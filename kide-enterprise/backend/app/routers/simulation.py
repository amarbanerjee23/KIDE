"""
Router for Live Controller Simulation & Execution.
"""

from typing import Any, Dict, List, Optional
from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy import select
from pydantic import BaseModel

from ..database import get_db
from ..models.project import Project, ProjectFile
from ..models.user import User
from ..auth.dependencies import get_current_user
from ..services.synthesis import synthesize
from ..services.composition import compose_mnc_model
from ..services.simulation_runner import SimulationService
from ..parsers.activity_parser import parse as parse_activity
from ..parsers.capability_parser import parse as parse_capability
from ..parsers.operation_parser import parse as parse_operation

router = APIRouter(prefix="/projects", tags=["simulation"])

class StartSimulationRequest(BaseModel):
    force_reset: bool = False
    activity_file_id: Optional[int] = None

class CommandExecRequest(BaseModel):
    command: str
    payload: Optional[Dict[str, Any]] = None

class EventInjectRequest(BaseModel):
    event: str
    payload: Optional[Dict[str, Any]] = None

async def _resolve_project_model(project: Project, db: AsyncSession, activity_file_id: Optional[int] = None) -> Dict[str, Any]:
    files_res = await db.execute(select(ProjectFile).where(ProjectFile.project_id == project.id))
    files = files_res.scalars().all()

    activity_file = None
    if activity_file_id:
        for f in files:
            if f.id == activity_file_id:
                activity_file = f
                break
    if not activity_file:
        for f in files:
            fname = f.filename or ""
            ftype = f.file_type or ""
            if fname.endswith(".activity") or ftype == "activity":
                activity_file = f
                break

    kb = {"capabilities": {}, "operations": {}}
    for f in files:
        fname = f.filename or ""
        ftype = f.file_type or ""
        if fname.endswith(".cap") or ftype == "capability":
            try:
                cap_ast = parse_capability(f.content or "")
                if cap_ast.get("name"):
                    kb["capabilities"][cap_ast["name"]] = cap_ast
            except Exception:
                pass
        elif fname.endswith(".op") or ftype == "operation":
            try:
                op_ast = parse_operation(f.content or "")
                for op in op_ast.get("operations", []):
                    if op.get("name"):
                        kb["operations"][op["name"]] = op
            except Exception:
                pass

    act_ast = None
    if activity_file and activity_file.content:
        try:
            act_ast = parse_activity(activity_file.content)
        except Exception:
            pass

    if not act_ast:
        act_ast = {
            "name": project.name.replace(" ", "_"),
            "activities": [
                {"name": "InitProcess", "commands": [{"name": "INIT"}]},
                {"name": "RunProcess", "commands": [{"name": "START"}]}
            ]
        }

    blocks = synthesize(act_ast, kb)
    return compose_mnc_model(act_ast.get("name", project.name), blocks)

@router.post("/{project_id}/simulate")
async def start_or_reset_simulation(
    project_id: int,
    payload: Optional[StartSimulationRequest] = None,
    db: AsyncSession = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    res = await db.execute(select(Project).where(Project.id == project_id, Project.org_id == current_user.org_id))
    project = res.scalars().first()
    if not project:
        raise HTTPException(status_code=404, detail="Project not found")

    force_reset = payload.force_reset if payload else False
    act_file_id = payload.activity_file_id if payload else None
    model = await _resolve_project_model(project, db, act_file_id)

    session = SimulationService.get_or_create_session(project_id, model, force_reset=force_reset)
    return session.get_status()

@router.get("/{project_id}/simulate")
async def get_simulation_status(
    project_id: int,
    db: AsyncSession = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    session = SimulationService.get_session(project_id)
    if not session:
        res = await db.execute(select(Project).where(Project.id == project_id, Project.org_id == current_user.org_id))
        project = res.scalars().first()
        if not project:
            raise HTTPException(status_code=404, detail="Project not found")
        model = await _resolve_project_model(project, db)
        session = SimulationService.get_or_create_session(project_id, model)
    return session.get_status()

@router.post("/{project_id}/simulate/command")
async def execute_simulation_command(
    project_id: int,
    payload: CommandExecRequest,
    db: AsyncSession = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    session = SimulationService.get_session(project_id)
    if not session:
        res = await db.execute(select(Project).where(Project.id == project_id, Project.org_id == current_user.org_id))
        project = res.scalars().first()
        if not project:
            raise HTTPException(status_code=404, detail="Project not found")
        model = await _resolve_project_model(project, db)
        session = SimulationService.get_or_create_session(project_id, model)

    res = session.execute_command(payload.command, payload.payload)
    return {
        "result": res,
        "session": session.get_status()
    }

@router.post("/{project_id}/simulate/event")
async def inject_simulation_event(
    project_id: int,
    payload: EventInjectRequest,
    db: AsyncSession = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    session = SimulationService.get_session(project_id)
    if not session:
        res = await db.execute(select(Project).where(Project.id == project_id, Project.org_id == current_user.org_id))
        project = res.scalars().first()
        if not project:
            raise HTTPException(status_code=404, detail="Project not found")
        model = await _resolve_project_model(project, db)
        session = SimulationService.get_or_create_session(project_id, model)

    res = session.inject_event(payload.event, payload.payload)
    return {
        "result": res,
        "session": session.get_status()
    }

@router.post("/{project_id}/simulate/step")
async def step_simulation(
    project_id: int,
    db: AsyncSession = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    session = SimulationService.get_session(project_id)
    if not session:
        res = await db.execute(select(Project).where(Project.id == project_id, Project.org_id == current_user.org_id))
        project = res.scalars().first()
        if not project:
            raise HTTPException(status_code=404, detail="Project not found")
        model = await _resolve_project_model(project, db)
        session = SimulationService.get_or_create_session(project_id, model)

    return session.step_simulation()
