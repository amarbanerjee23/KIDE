from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy import select
from typing import List, Optional
from datetime import datetime
from app.schemas.project import (
    ProjectCreate, ProjectUpdate, ProjectResponse, 
    ProjectFileCreate, ProjectFileUpdate, ProjectFileResponse, ProjectFileContent,
    ProjectFromTemplateCreate
)
from app.models.project import Project, ProjectFile
from app.models.user import User
from app.auth.dependencies import get_db, get_current_user
from app.services.thesis_examples import get_template, list_templates

router = APIRouter()

@router.get("/templates")
async def get_project_templates():
    return list_templates()

@router.post("/from-template", response_model=ProjectResponse)
async def create_project_from_template(
    payload: ProjectFromTemplateCreate,
    current_user: User = Depends(get_current_user),
    db: AsyncSession = Depends(get_db)
):
    template = get_template(payload.template)
    if not template:
        raise HTTPException(status_code=400, detail=f"Unknown template '{payload.template}'")
    
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
    
    return ProjectResponse(
        id=p.id, name=p.name, description=p.description,
        created_by_name=current_user.full_name, file_count=len(template["files"]),
        created_at=p.created_at, updated_at=p.updated_at
    )

@router.get("")
@router.get("/")
async def list_projects(current_user: User = Depends(get_current_user), db: AsyncSession = Depends(get_db)):
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
async def create_project(proj: ProjectCreate, current_user: User = Depends(get_current_user), db: AsyncSession = Depends(get_db)):
    p = Project(name=proj.name, description=proj.description, org_id=current_user.org_id, created_by=current_user.id)
    db.add(p)
    await db.commit()
    await db.refresh(p)
    return ProjectResponse(
        id=p.id, name=p.name, description=p.description,
        created_by_name=current_user.full_name, file_count=0,
        created_at=p.created_at, updated_at=p.updated_at
    )

@router.get("/{id}", response_model=ProjectResponse)
async def get_project(id: int, current_user: User = Depends(get_current_user), db: AsyncSession = Depends(get_db)):
    result = await db.execute(select(Project).where(Project.id == id, Project.org_id == current_user.org_id))
    p = result.scalars().first()
    if not p:
        raise HTTPException(status_code=404)
    file_count_res = await db.execute(select(ProjectFile).where(ProjectFile.project_id == p.id))
    return ProjectResponse(
        id=p.id, name=p.name, description=p.description,
        created_by_name=current_user.full_name, file_count=len(file_count_res.scalars().all()),
        created_at=p.created_at, updated_at=p.updated_at
    )

@router.put("/{id}", response_model=ProjectResponse)
async def update_project(id: int, proj: ProjectUpdate, current_user: User = Depends(get_current_user), db: AsyncSession = Depends(get_db)):
    result = await db.execute(select(Project).where(Project.id == id, Project.org_id == current_user.org_id))
    p = result.scalars().first()
    if not p:
        raise HTTPException(status_code=404)
    if proj.name is not None:
        p.name = proj.name
    if proj.description is not None:
        p.description = proj.description
    await db.commit()
    return await get_project(id, current_user, db)

@router.delete("/{id}")
async def delete_project(id: int, current_user: User = Depends(get_current_user), db: AsyncSession = Depends(get_db)):
    result = await db.execute(select(Project).where(Project.id == id, Project.org_id == current_user.org_id))
    p = result.scalars().first()
    if not p:
        raise HTTPException(status_code=404)
    # delete files first
    files_res = await db.execute(select(ProjectFile).where(ProjectFile.project_id == id))
    for f in files_res.scalars().all():
        await db.delete(f)
    await db.delete(p)
    await db.commit()
    return {"status": "deleted"}

@router.get("/{id}/files", response_model=List[ProjectFileContent])
async def list_files(id: int, current_user: User = Depends(get_current_user), db: AsyncSession = Depends(get_db)):
    result = await db.execute(select(Project).where(Project.id == id, Project.org_id == current_user.org_id))
    p = result.scalars().first()
    if not p:
        raise HTTPException(status_code=404, detail="Project not found")
    files_res = await db.execute(select(ProjectFile).where(ProjectFile.project_id == id).order_by(ProjectFile.filename))
    return files_res.scalars().all()

@router.post("/{id}/seed-examples", response_model=List[ProjectFileContent])
async def seed_examples(
    id: int, 
    template_key: Optional[str] = "industrial_cooling", 
    current_user: User = Depends(get_current_user), 
    db: AsyncSession = Depends(get_db)
):
    result = await db.execute(select(Project).where(Project.id == id, Project.org_id == current_user.org_id))
    p = result.scalars().first()
    if not p:
        raise HTTPException(status_code=404, detail="Project not found")
    
    template = get_template(template_key or "industrial_cooling")
    if not template:
        raise HTTPException(status_code=400, detail="Unknown template key")
        
    created = []
    for file_info in template["files"]:
        # Check if already exists
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
    files_res = await db.execute(select(ProjectFile).where(ProjectFile.project_id == id).order_by(ProjectFile.filename))
    return files_res.scalars().all()

@router.post("/{id}/files", response_model=ProjectFileContent)
async def add_file(id: int, file: ProjectFileCreate, current_user: User = Depends(get_current_user), db: AsyncSession = Depends(get_db)):
    result = await db.execute(select(Project).where(Project.id == id, Project.org_id == current_user.org_id))
    p = result.scalars().first()
    if not p:
        raise HTTPException(status_code=404, detail="Project not found")
    
    # If file with same name exists, update it or raise error
    existing_res = await db.execute(
        select(ProjectFile).where(ProjectFile.project_id == id, ProjectFile.filename == file.filename)
    )
    existing = existing_res.scalars().first()
    if existing:
        existing.content = file.content
        existing.file_type = file.file_type or existing.file_type
        existing.version += 1
        existing.updated_at = datetime.utcnow()
        await db.commit()
        await db.refresh(existing)
        return existing

    f = ProjectFile(project_id=id, filename=file.filename, file_type=file.file_type, content=file.content)
    db.add(f)
    await db.commit()
    await db.refresh(f)
    return f

@router.get("/{id}/files/{file_id}", response_model=ProjectFileContent)
async def get_file(id: int, file_id: int, current_user: User = Depends(get_current_user), db: AsyncSession = Depends(get_db)):
    result = await db.execute(select(ProjectFile).where(ProjectFile.id == file_id, ProjectFile.project_id == id))
    f = result.scalars().first()
    if not f:
        raise HTTPException(status_code=404)
    return f

@router.put("/{id}/files/{file_id}", response_model=ProjectFileContent)
async def update_file(
    id: int, 
    file_id: int, 
    update_data: ProjectFileUpdate, 
    current_user: User = Depends(get_current_user), 
    db: AsyncSession = Depends(get_db)
):
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
    f.version += 1
    f.updated_at = datetime.utcnow()
    await db.commit()
    await db.refresh(f)
    return f

@router.delete("/{id}/files/{file_id}")
async def delete_file(id: int, file_id: int, current_user: User = Depends(get_current_user), db: AsyncSession = Depends(get_db)):
    result = await db.execute(select(ProjectFile).where(ProjectFile.id == file_id, ProjectFile.project_id == id))
    f = result.scalars().first()
    if not f:
        raise HTTPException(status_code=404)
    await db.delete(f)
    await db.commit()
    return {"status": "deleted"}

