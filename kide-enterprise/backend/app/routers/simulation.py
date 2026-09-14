"""
Router for Live Controller Simulation, Hardware-in-the-Loop Gateway, and WebSockets (PhD Requirements 31, 35, 37, 45).
"""

import asyncio
import json
import logging
from typing import Any, Dict, List, Optional
from fastapi import APIRouter, Depends, HTTPException, WebSocket, WebSocketDisconnect, Response, Query, status

logger = logging.getLogger(__name__)
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy import select
from pydantic import BaseModel

from ..database import get_db
from ..models.project import Project, ProjectFile
from ..models.user import User
from ..auth.dependencies import get_current_user
from ..services.synthesis import synthesize
from ..services.composition import compose_mnc_model
from ..services.simulation_runner import SimulationService, SimulationSession, SimulationMode, ExecutionState
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

class ExecutionStateRequest(BaseModel):
    state: str  # RUNNING, PAUSED, STOPPED

class SimulationModeRequest(BaseModel):
    mode: str  # VIRTUAL_EMULATION, HIL_MODBUS_TCP, HIL_MQTT, HIL_OPC_UA, REPLAY
    protocol_config: Optional[Dict[str, Any]] = None

class BreakpointRequest(BaseModel):
    break_on_states: List[str] = []
    break_on_alarms: bool = False

class TelemetryOverrideRequest(BaseModel):
    datapoint: str
    value: float


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

    cmd_res = session.execute_command(payload.command, payload.payload)
    return {
        "result": cmd_res,
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

    evt_res = session.inject_event(payload.event, payload.payload)
    return {
        "result": evt_res,
        "session": session.get_status()
    }


@router.post("/{project_id}/simulate/execution-state")
async def set_execution_state(
    project_id: int,
    payload: ExecutionStateRequest,
    db: AsyncSession = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    session = SimulationService.get_session(project_id)
    if not session:
        raise HTTPException(status_code=404, detail="Simulation session not found")
    session.set_execution_state(payload.state)
    return session.get_status()


@router.post("/{project_id}/simulate/mode")
async def set_simulation_mode(
    project_id: int,
    payload: SimulationModeRequest,
    db: AsyncSession = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    session = SimulationService.get_session(project_id)
    if not session:
        raise HTTPException(status_code=404, detail="Simulation session not found")
    session.set_mode(payload.mode, payload.protocol_config)
    return session.get_status()


@router.post("/{project_id}/simulate/breakpoints")
async def set_breakpoints(
    project_id: int,
    payload: BreakpointRequest,
    db: AsyncSession = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    session = SimulationService.get_session(project_id)
    if not session:
        raise HTTPException(status_code=404, detail="Simulation session not found")
    session.set_breakpoints(payload.break_on_states, payload.break_on_alarms)
    return session.get_status()


@router.post("/{project_id}/simulate/telemetry/override")
async def set_telemetry_override(
    project_id: int,
    payload: TelemetryOverrideRequest,
    db: AsyncSession = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    session = SimulationService.get_session(project_id)
    if not session:
        raise HTTPException(status_code=404, detail="Simulation session not found")
    res = session.set_telemetry_override(payload.datapoint, payload.value)
    return {
        "result": res,
        "session": session.get_status()
    }


@router.get("/{project_id}/simulate/telemetry/history")
async def get_telemetry_history(
    project_id: int,
    db: AsyncSession = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    session = SimulationService.get_session(project_id)
    if not session:
        raise HTTPException(status_code=404, detail="Simulation session not found")
    return session.telemetry_history


@router.get("/{project_id}/simulate/telemetry/export")
async def export_simulation_telemetry(
    project_id: int,
    format: str = Query("csv", pattern="^(csv|json)$"),
    db: AsyncSession = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    session = SimulationService.get_session(project_id)
    if not session:
        raise HTTPException(status_code=404, detail="Simulation session not found")

    content = session.export_telemetry(format=format)
    media_type = "text/csv" if format == "csv" else "application/json"
    filename = f"telemetry_trace_{session.model_name}.{format}"
    return Response(content=content, media_type=media_type, headers={
        "Content-Disposition": f'attachment; filename="{filename}"'
    })


# =============================================================================
# Real-Time WebSocket Streaming Endpoint (PhD Requirement 31)
# =============================================================================

@router.websocket("/{project_id}/ws")
async def simulation_websocket_endpoint(websocket: WebSocket, project_id: int):
    """
    Bidirectional WebSocket connection for live telemetry streaming,
    instant command dispatching, and hardware event injection.
    """
    await websocket.accept()
    session = SimulationService.get_session(project_id)
    if not session:
        # Create default fallback session
        session = SimulationService.get_or_create_session(project_id, {"name": f"Project_{project_id}"})

    session.subscribers.add(websocket)
    # Send initial connection confirmation & current status
    await websocket.send_json({
        "type": "connection_established",
        "project_id": project_id,
        "status": session.get_status()
    })

    try:
        while True:
            data = await websocket.receive_json()
            msg_type = data.get("type", "")

            if msg_type == "command":
                cmd = data.get("command", "")
                payload = data.get("payload")
                session.execute_command(cmd, payload)
            elif msg_type == "inject_event":
                evt = data.get("event", "")
                payload = data.get("payload")
                session.inject_event(evt, payload)
            elif msg_type == "telemetry_override":
                dp = data.get("datapoint", "")
                val = float(data.get("value", 0.0))
                session.set_telemetry_override(dp, val)
            elif msg_type == "set_execution_state":
                st = data.get("state", "PAUSED")
                session.set_execution_state(st)
            elif msg_type == "set_mode":
                md = data.get("mode", "VIRTUAL_EMULATION")
                cfg = data.get("protocol_config")
                session.set_mode(md, cfg)
            elif msg_type == "set_breakpoints":
                states = data.get("break_on_states", [])
                alarm_break = data.get("break_on_alarms", False)
                session.set_breakpoints(states, alarm_break)
            elif msg_type == "step":
                session.step_simulation()

            # Echo updated status
            await websocket.send_json({
                "type": "status_update",
                "status": session.get_status()
            })

    except WebSocketDisconnect:
        session.subscribers.discard(websocket)
    except Exception as e:
        session.subscribers.discard(websocket)
        logger.error(f"WebSocket error for project {project_id}: {e}")
