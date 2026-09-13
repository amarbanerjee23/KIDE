from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy import select, desc
from typing import List
from datetime import datetime, timezone

from app.auth.dependencies import get_current_user, get_db, require_permission
from app.auth.rbac import Permission
from app.services.audit import AuditService
from app.services.entitlements import EntitlementsService
from app.models.user import User
from app.models.project import Project, ProjectFile
from app.models.ai_provenance import AIProvenance
from app.schemas.ai import (
    AIChatRequest,
    AIChatResponse,
    ApplyPatchRequest,
    ApplyPatchResponse,
    AIProvenanceResponse,
    AIProvidersResponse
)
from app.services.ai_gateway import AIGatewayService
from app.config import settings

router = APIRouter(prefix="/ai", tags=["ai"])

def utcnow():
    return datetime.now(timezone.utc)

@router.get("/providers", response_model=AIProvidersResponse)
async def get_providers(current_user: User = Depends(get_current_user)):
    """Returns available and configured AI providers."""
    available = ["deterministic"]
    has_gemini = bool(settings.GEMINI_API_KEY)
    has_openai = bool(settings.OPENAI_API_KEY)
    has_anthropic = bool(settings.ANTHROPIC_API_KEY)

    if has_gemini:
        available.insert(0, "gemini")
    if has_openai:
        available.insert(1, "openai")
    if has_anthropic:
        available.append("anthropic")

    return AIProvidersResponse(
        available_providers=available,
        active_provider=settings.DEFAULT_AI_PROVIDER if (has_gemini or has_openai) else "deterministic",
        active_model=settings.DEFAULT_AI_MODEL if has_gemini else "kide-deterministic-copilot",
        has_gemini_key=has_gemini,
        has_openai_key=has_openai,
        has_anthropic_key=has_anthropic
    )

@router.post("/chat", response_model=AIChatResponse)
async def chat(
    request: AIChatRequest,
    current_user: User = Depends(get_current_user),
    db: AsyncSession = Depends(get_db)
):
    """
    Multi-turn engineering chat endpoint with real-time tool execution,
    strict server-side authorization, and provenance recording.
    """
    if current_user.org_id:
        await EntitlementsService.check_can_use_ai(db, current_user.org_id, estimated_tokens=1000)

    try:
        response = await AIGatewayService.chat(
            project_id=request.project_id,
            messages=request.messages,
            current_user=current_user,
            db=db,
            context_file=request.context_file,
            provider=request.provider,
            model=request.model
        )

        if current_user.org_id:
            last_msg_len = len(request.messages[-1].content) if request.messages else 50
            token_count = max(250, (last_msg_len + len(response.message or "")) // 3)
            await EntitlementsService.record_ai_usage(db, current_user.org_id, token_count)

        return response
    except ValueError as ve:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail=str(ve))
    except Exception as ex:
        raise HTTPException(status_code=status.HTTP_500_INTERNAL_SERVER_ERROR, detail=f"AI chat error: {str(ex)}")

@router.post("/apply-patch", response_model=ApplyPatchResponse)
async def apply_patch(
    request: ApplyPatchRequest,
    current_user: User = Depends(require_permission(Permission.PATCH_APPLY)),
    db: AsyncSession = Depends(get_db)
):
    """
    Applies approved patches to project files with automatic version increment
    and marks AI provenance status as 'applied'.
    """
    # Verify user has access to project
    proj_stmt = select(Project).where(Project.id == request.project_id, Project.org_id == current_user.org_id)
    proj_res = await db.execute(proj_stmt)
    project = proj_res.scalars().first()
    if not project:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Project not found or unauthorized")

    applied_files: List[str] = []

    for patch in request.patches:
        filename = patch.filename
        file_stmt = select(ProjectFile).where(
            ProjectFile.project_id == project.id,
            ProjectFile.filename == filename
        )
        file_res = await db.execute(file_stmt)
        existing_file = file_res.scalars().first()

        if patch.action == "delete":
            if existing_file:
                await db.delete(existing_file)
                applied_files.append(f"{filename} (deleted)")
        else:
            ext = filename.split(".")[-1].lower() if "." in filename else "text"
            if existing_file:
                existing_file.content = patch.new_content
                existing_file.version = (existing_file.version or 1) + 1
                existing_file.updated_at = utcnow()
                applied_files.append(f"{filename} (v{existing_file.version})")
            else:
                new_file = ProjectFile(
                    project_id=project.id,
                    filename=filename,
                    file_type=ext,
                    content=patch.new_content,
                    version=1,
                    created_at=utcnow(),
                    updated_at=utcnow()
                )
                db.add(new_file)
                applied_files.append(f"{filename} (created v1)")

    # Update provenance status
    if request.provenance_id:
        prov_stmt = select(AIProvenance).where(AIProvenance.id == request.provenance_id)
        prov_res = await db.execute(prov_stmt)
        prov = prov_res.scalars().first()
        if prov:
            prov.status = "applied"
            prov.applied_at = utcnow()

    project.updated_at = utcnow()
    await db.commit()

    # Immutable Audit Log
    await AuditService.log(
        db=db,
        org_id=current_user.org_id,
        action="project:patch_applied",
        actor=current_user,
        target_type="project",
        target_id=str(project.id),
        details={"applied_files": applied_files, "provenance_id": request.provenance_id}
    )

    return ApplyPatchResponse(
        status="applied",
        applied_files=applied_files,
        timestamp=utcnow().isoformat()
    )

@router.get("/provenance/{project_id}", response_model=List[AIProvenanceResponse])
async def get_provenance(
    project_id: int,
    current_user: User = Depends(get_current_user),
    db: AsyncSession = Depends(get_db)
):
    """Fetches full AI decision and proposal provenance audit trail for the project."""
    proj_stmt = select(Project).where(Project.id == project_id, Project.org_id == current_user.org_id)
    proj_res = await db.execute(proj_stmt)
    project = proj_res.scalars().first()
    if not project:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Project not found or unauthorized")

    stmt = select(AIProvenance).where(
        AIProvenance.project_id == project_id
    ).order_by(desc(AIProvenance.created_at)).limit(50)
    res = await db.execute(stmt)
    records = res.scalars().all()

    import json
    result = []
    for r in records:
        tool_calls_parsed = json.loads(r.tool_calls) if r.tool_calls else None
        proposed_patches_parsed = json.loads(r.proposed_patches) if r.proposed_patches else None
        result.append(AIProvenanceResponse(
            id=r.id,
            project_id=r.project_id,
            session_id=r.session_id,
            provider=r.provider,
            model_name=r.model_name,
            user_prompt=r.user_prompt,
            tool_calls=tool_calls_parsed,
            assistant_response=r.assistant_response,
            proposed_patches=proposed_patches_parsed,
            status=r.status,
            created_at=r.created_at.isoformat() if r.created_at else ""
        ))
    return result

