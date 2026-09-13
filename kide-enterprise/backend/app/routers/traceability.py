from fastapi import APIRouter, Depends, HTTPException, Body, status
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy import select
from typing import Any, Dict, List, Optional

from app.auth.rbac import Permission
from app.auth.dependencies import get_current_user, get_db, require_permission
from app.models.user import User
from app.models.project import Project, ProjectFile
from app.schemas.traceability import (
    TraceabilityMatrixResponse,
    ImpactAnalysisRequest,
    ImpactAnalysisResponse,
    ReconfigurationRequest,
    ReconfigurationProposal
)
from app.services.traceability import TraceabilityService
from app.services.reconfiguration import SemanticReconfigurationService

router = APIRouter(prefix="/traceability", tags=["traceability"])

async def _verify_tenant_project(project_id: int, user: User, db: AsyncSession) -> Project:
    stmt = select(Project).where(Project.id == project_id, Project.org_id == user.org_id)
    res = await db.execute(stmt)
    proj = res.scalars().first()
    if not proj:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Project not found or belongs to another organization"
        )
    return proj

@router.get("/{project_id}/matrix", response_model=TraceabilityMatrixResponse)
async def get_project_traceability_matrix(
    project_id: int,
    current_user: User = Depends(require_permission(Permission.PROJECT_READ)),
    db: AsyncSession = Depends(get_db)
):
    """
    Constructs the multi-layer bidirectional Traceability Matrix for a persistent project.
    Correlates: Data Models -> Capabilities -> Operations -> Activities -> Operating States -> Code Targets.
    """
    await _verify_tenant_project(project_id, current_user, db)
    result = await db.execute(select(ProjectFile).where(ProjectFile.project_id == project_id))
    files = result.scalars().all()
    file_dicts = [{"id": str(f.id), "name": f.filename, "content": f.content} for f in files]
    return TraceabilityService.build_traceability_matrix(file_dicts, project_id=project_id)

@router.post("/{project_id}/impact", response_model=ImpactAnalysisResponse)
async def analyze_project_change_impact(
    project_id: int,
    request: ImpactAnalysisRequest,
    current_user: User = Depends(require_permission(Permission.PROJECT_READ)),
    db: AsyncSession = Depends(get_db)
):
    """
    Analyzes change impact and calculates blast radius (affected activities, states, broken transitions)
    when a symbol or file is modified, renamed, or deleted in a project.
    """
    await _verify_tenant_project(project_id, current_user, db)
    result = await db.execute(select(ProjectFile).where(ProjectFile.project_id == project_id))
    files = result.scalars().all()
    file_dicts = [{"id": str(f.id), "name": f.filename, "content": f.content} for f in files]
    return TraceabilityService.analyze_change_impact(file_dicts, request)

@router.post("/{project_id}/reconfigure", response_model=ReconfigurationProposal)
async def reconfigure_project_capability(
    project_id: int,
    request: ReconfigurationRequest,
    current_user: User = Depends(require_permission(Permission.PROJECT_EDIT)),
    db: AsyncSession = Depends(get_db)
):
    """
    Computes deterministic semantic reconfiguration patches when substituting a deprecated capability
    with an updated capability. Verifies that safety invariants and 0 deadlocks are maintained.
    """
    await _verify_tenant_project(project_id, current_user, db)
    result = await db.execute(select(ProjectFile).where(ProjectFile.project_id == project_id))
    files = result.scalars().all()
    file_dicts = [{"id": str(f.id), "name": f.filename, "content": f.content} for f in files]
    return SemanticReconfigurationService.compute_reconfiguration_plan(file_dicts, request)

# Direct buffer-based endpoints (for active editor buffers & CI/CD verification)

@router.post("/matrix", response_model=TraceabilityMatrixResponse)
async def compute_matrix_from_files(
    payload: Dict[str, Any] = Body(...)
):
    """
    Computes Traceability Matrix directly from raw file buffers:
    Accepts {"files": [...], "project_id": optional_int}
    """
    raw_files = payload.get("files", [])
    proj_id = payload.get("project_id")
    return TraceabilityService.build_traceability_matrix(raw_files, project_id=proj_id)

@router.post("/impact", response_model=ImpactAnalysisResponse)
async def compute_impact_from_files(
    payload: Dict[str, Any] = Body(...)
):
    """
    Computes Change Impact directly from raw file buffers:
    Accepts {"files": [...], "target_symbol": "...", "action": "delete|modify"}
    """
    raw_files = payload.get("files", [])
    req = ImpactAnalysisRequest(
        target_symbol=payload.get("target_symbol"),
        target_file=payload.get("target_file"),
        action=payload.get("action", "modify"),
        new_name=payload.get("new_name")
    )
    return TraceabilityService.analyze_change_impact(raw_files, req)

@router.post("/reconfigure", response_model=ReconfigurationProposal)
async def compute_reconfiguration_from_files(
    payload: Dict[str, Any] = Body(...)
):
    """
    Computes Reconfiguration Plan directly from raw file buffers:
    Accepts {"files": [...], "deprecated_capability": "...", "replacement_capability": "..."}
    """
    raw_files = payload.get("files", [])
    req = ReconfigurationRequest(
        deprecated_capability=payload.get("deprecated_capability", ""),
        replacement_capability=payload.get("replacement_capability", ""),
        target_activities=payload.get("target_activities")
    )
    return SemanticReconfigurationService.compute_reconfiguration_plan(raw_files, req)

