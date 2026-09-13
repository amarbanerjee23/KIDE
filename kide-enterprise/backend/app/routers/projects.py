from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy import select
from typing import List, Optional
from datetime import datetime, timezone

from app.schemas.project import (
    ProjectCreate, ProjectUpdate, ProjectResponse, 
    ProjectFileCreate, ProjectFileUpdate, ProjectFileResponse, ProjectFileContent,
    ProjectFromTemplateCreate
)
from app.models.project import Project, ProjectFile
from app.models.user import User
from app.auth.dependencies import get_db, get_current_user, require_permission
from app.auth.rbac import Permission
from app.services.thesis_examples import get_template, list_templates
from app.services.audit import AuditService
from app.services.entitlements import EntitlementsService

router = APIRouter()

def utcnow():
    return datetime.now(timezone.utc)

async def _get_project_with_tenant_check(project_id: int, user: User, db: AsyncSession) -> Project:
    stmt = select(Project).where(Project.id == project_id, Project.org_id == user.org_id)
    res = await db.execute(stmt)
    proj = res.scalars().first()
    if not proj:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Project not found or belongs to another organization"
        )
    return proj

@router.get("/templates")
async def get_project_templates():
    return list_templates()

@router.post("/from-template", response_model=ProjectResponse)
async def create_project_from_template(
    payload: ProjectFromTemplateCreate,
    current_user: User = Depends(require_permission(Permission.PROJECT_CREATE)),
    db: AsyncSession = Depends(get_db)
):
    template = get_template(payload.template)
    if not template:
        raise HTTPException(status_code=400, detail=f"Unknown template '{payload.template}'")

    if current_user.org_id:
        await EntitlementsService.check_can_create_project(db, current_user.org_id)
    
    p = Project(
        name=payload.name,
        description=payload.description or template["description"],
        org_id=current_user.org_id,
        created_by=current_user.id
    )
    db.add(p)
    await db.commit()
    await db.refresh(p)
    
    # Add files from template
    for file_info in template["files"]:
        f = ProjectFile(
            project_id=p.id,
            filename=file_info["filename"],
            file_type=file_info["file_type"],
            content=file_info["content"]
        )
        db.add(f)
    
    await db.commit()

    # Immutable Audit Log
    await AuditService.log(
        db=db,
        org_id=current_user.org_id,
        action="project:create_from_template",
        actor=current_user,
        target_type="project",
        target_id=str(p.id),
        details={"name": p.name, "template": payload.template, "files_count": len(template["files"])}
    )
    
    return ProjectResponse(
        id=p.id, name=p.name, description=p.description,
        created_by_name=current_user.full_name, file_count=len(template["files"]),
        created_at=p.created_at, updated_at=p.updated_at
    )

@router.get("")
@router.get("/")
async def list_projects(
    current_user: User = Depends(require_permission(Permission.PROJECT_READ)), 
    db: AsyncSession = Depends(get_db)
):
    result = await db.execute(select(Project).where(Project.org_id == current_user.org_id))
    projects = result.scalars().all()
    out = []
    for p in projects:
        file_count_res = await db.execute(select(ProjectFile).where(ProjectFile.project_id == p.id))
        out.append(ProjectResponse(
            id=p.id, name=p.name, description=p.description,
            created_by_name=current_user.full_name, file_count=len(file_count_res.scalars().all()),
            created_at=p.created_at, updated_at=p.updated_at
        ))
    return out

@router.post("", response_model=ProjectResponse)
@router.post("/", response_model=ProjectResponse)
async def create_project(
    proj: ProjectCreate, 
    current_user: User = Depends(require_permission(Permission.PROJECT_CREATE)), 
    db: AsyncSession = Depends(get_db)
):
    if current_user.org_id:
        await EntitlementsService.check_can_create_project(db, current_user.org_id)

    p = Project(name=proj.name, description=proj.description, org_id=current_user.org_id, created_by=current_user.id)
    db.add(p)
    await db.commit()
    await db.refresh(p)

    # Immutable Audit Log
    await AuditService.log(
        db=db,
        org_id=current_user.org_id,
        action="project:create",
        actor=current_user,
        target_type="project",
        target_id=str(p.id),
        details={"name": p.name}
    )

    return ProjectResponse(
        id=p.id, name=p.name, description=p.description,
        created_by_name=current_user.full_name, file_count=0,
        created_at=p.created_at, updated_at=p.updated_at
    )

@router.get("/{id}", response_model=ProjectResponse)
async def get_project(
    id: int, 
    current_user: User = Depends(require_permission(Permission.PROJECT_READ)), 
    db: AsyncSession = Depends(get_db)
):
    p = await _get_project_with_tenant_check(id, current_user, db)
    file_count_res = await db.execute(select(ProjectFile).where(ProjectFile.project_id == p.id))
    return ProjectResponse(
        id=p.id, name=p.name, description=p.description,
        created_by_name=current_user.full_name, file_count=len(file_count_res.scalars().all()),
        created_at=p.created_at, updated_at=p.updated_at
    )

@router.put("/{id}", response_model=ProjectResponse)
async def update_project(
    id: int, 
    proj: ProjectUpdate, 
    current_user: User = Depends(require_permission(Permission.PROJECT_EDIT)), 
    db: AsyncSession = Depends(get_db)
):
    p = await _get_project_with_tenant_check(id, current_user, db)
    if proj.name is not None:
        p.name = proj.name
    if proj.description is not None:
        p.description = proj.description
    p.updated_at = utcnow()
    await db.commit()

    # Immutable Audit Log
    await AuditService.log(
        db=db,
        org_id=current_user.org_id,
        action="project:update",
        actor=current_user,
        target_type="project",
        target_id=str(p.id),
        details={"name": p.name}
    )

    return await get_project(id, current_user, db)

@router.delete("/{id}")
async def delete_project(
    id: int, 
    current_user: User = Depends(require_permission(Permission.PROJECT_DELETE)), 
    db: AsyncSession = Depends(get_db)
):
    p = await _get_project_with_tenant_check(id, current_user, db)
    proj_name = p.name
    # delete files first
    files_res = await db.execute(select(ProjectFile).where(ProjectFile.project_id == id))
    for f in files_res.scalars().all():
        await db.delete(f)
    await db.delete(p)
    await db.commit()

    # Immutable Audit Log
    await AuditService.log(
        db=db,
        org_id=current_user.org_id,
        action="project:delete",
        actor=current_user,
        target_type="project",
        target_id=str(id),
        details={"name": proj_name}
    )

    return {"status": "deleted"}

@router.get("/{id}/files", response_model=List[ProjectFileContent])
async def list_files(
    id: int, 
    current_user: User = Depends(require_permission(Permission.PROJECT_READ)), 
    db: AsyncSession = Depends(get_db)
):
    await _get_project_with_tenant_check(id, current_user, db)
    files_res = await db.execute(select(ProjectFile).where(ProjectFile.project_id == id).order_by(ProjectFile.filename))
    return files_res.scalars().all()

@router.post("/{id}/seed-examples", response_model=List[ProjectFileContent])
async def seed_examples(
    id: int, 
    template_key: Optional[str] = "industrial_cooling", 
    current_user: User = Depends(require_permission(Permission.PROJECT_EDIT)), 
    db: AsyncSession = Depends(get_db)
):
    p = await _get_project_with_tenant_check(id, current_user, db)
    template = get_template(template_key or "industrial_cooling")
    if not template:
        raise HTTPException(status_code=400, detail="Unknown template key")
        
    created = []
    for file_info in template["files"]:
        exists_res = await db.execute(
            select(ProjectFile).where(ProjectFile.project_id == id, ProjectFile.filename == file_info["filename"])
        )
        existing = exists_res.scalars().first()
        if not existing:
            f = ProjectFile(
                project_id=id,
                filename=file_info["filename"],
                file_type=file_info["file_type"],
                content=file_info["content"]
            )
            db.add(f)
            created.append(f)
    
    await db.commit()

    # Immutable Audit Log
    await AuditService.log(
        db=db,
        org_id=current_user.org_id,
        action="project:seed_examples",
        actor=current_user,
        target_type="project",
        target_id=str(p.id),
        details={"template": template_key, "files_created": len(created)}
    )

    files_res = await db.execute(select(ProjectFile).where(ProjectFile.project_id == id).order_by(ProjectFile.filename))
    return files_res.scalars().all()

@router.post("/{id}/files", response_model=ProjectFileContent)
async def add_file(
    id: int, 
    file: ProjectFileCreate, 
    current_user: User = Depends(require_permission(Permission.PROJECT_EDIT)), 
    db: AsyncSession = Depends(get_db)
):
    p = await _get_project_with_tenant_check(id, current_user, db)
    
    existing_res = await db.execute(
        select(ProjectFile).where(ProjectFile.project_id == id, ProjectFile.filename == file.filename)
    )
    existing = existing_res.scalars().first()
    if existing:
        existing.content = file.content
        existing.file_type = file.file_type or existing.file_type
        existing.version = (existing.version or 1) + 1
        existing.updated_at = utcnow()
        await db.commit()
        await db.refresh(existing)

        await AuditService.log(
            db=db,
            org_id=current_user.org_id,
            action="file:update",
            actor=current_user,
            target_type="file",
            target_id=str(existing.id),
            details={"project_id": id, "filename": existing.filename, "version": existing.version}
        )
        return existing

    f = ProjectFile(
        project_id=id, 
        filename=file.filename, 
        file_type=file.file_type, 
        content=file.content,
        version=1,
        created_at=utcnow(),
        updated_at=utcnow()
    )
    db.add(f)
    await db.commit()
    await db.refresh(f)

    await AuditService.log(
        db=db,
        org_id=current_user.org_id,
        action="file:create",
        actor=current_user,
        target_type="file",
        target_id=str(f.id),
        details={"project_id": id, "filename": f.filename}
    )

    return f

@router.get("/{id}/files/{file_id}", response_model=ProjectFileContent)
async def get_file(
    id: int, 
    file_id: int, 
    current_user: User = Depends(require_permission(Permission.PROJECT_READ)), 
    db: AsyncSession = Depends(get_db)
):
    await _get_project_with_tenant_check(id, current_user, db)
    result = await db.execute(select(ProjectFile).where(ProjectFile.id == file_id, ProjectFile.project_id == id))
    f = result.scalars().first()
    if not f:
        raise HTTPException(status_code=404, detail="File not found")
    return f

@router.put("/{id}/files/{file_id}", response_model=ProjectFileContent)
async def update_file(
    id: int, 
    file_id: int, 
    update_data: ProjectFileUpdate, 
    current_user: User = Depends(require_permission(Permission.PROJECT_EDIT)), 
    db: AsyncSession = Depends(get_db)
):
    await _get_project_with_tenant_check(id, current_user, db)
    result = await db.execute(select(ProjectFile).where(ProjectFile.id == file_id, ProjectFile.project_id == id))
    f = result.scalars().first()
    if not f:
        raise HTTPException(status_code=404, detail="File not found")
    if update_data.content is not None:
        f.content = update_data.content
    if update_data.filename is not None:
        f.filename = update_data.filename
    if update_data.file_type is not None:
        f.file_type = update_data.file_type
    f.version = (f.version or 1) + 1
    f.updated_at = utcnow()
    await db.commit()
    await db.refresh(f)

    await AuditService.log(
        db=db,
        org_id=current_user.org_id,
        action="file:update",
        actor=current_user,
        target_type="file",
        target_id=str(f.id),
        details={"project_id": id, "filename": f.filename, "version": f.version}
    )

    return f

@router.delete("/{id}/files/{file_id}")
async def delete_file(
    id: int, 
    file_id: int, 
    current_user: User = Depends(require_permission(Permission.PROJECT_EDIT)), 
    db: AsyncSession = Depends(get_db)
):
    await _get_project_with_tenant_check(id, current_user, db)
    result = await db.execute(select(ProjectFile).where(ProjectFile.id == file_id, ProjectFile.project_id == id))
    f = result.scalars().first()
    if not f:
        raise HTTPException(status_code=404, detail="File not found")
    fname = f.filename
    await db.delete(f)
    await db.commit()

    await AuditService.log(
        db=db,
        org_id=current_user.org_id,
        action="file:delete",
        actor=current_user,
        target_type="file",
        target_id=str(file_id),
        details={"project_id": id, "filename": fname}
    )

    return {"status": "deleted"}

@router.get("/{id}/ir")
async def get_project_ir(
    id: int, 
    current_user: User = Depends(require_permission(Permission.PROJECT_READ)), 
    db: AsyncSession = Depends(get_db)
):
    await _get_project_with_tenant_check(id, current_user, db)
    result = await db.execute(select(ProjectFile).where(ProjectFile.project_id == id))
    files = result.scalars().all()
    from app.services.semantic_validator import validate_project_semantics
    file_dicts = [{"id": str(f.id), "name": f.filename, "content": f.content} for f in files]
    ir, diagnostics = validate_project_semantics(file_dicts, project_id=str(id))
    return {
        "project_id": id,
        "ir": ir.model_dump(),
        "summary": ir.summary.model_dump(),
        "diagnostics": [d.model_dump() for d in diagnostics]
    }
