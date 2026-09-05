from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy import select
from typing import List
from app.schemas.project import ProjectCreate, ProjectUpdate, ProjectResponse, ProjectFileCreate, ProjectFileResponse, ProjectFileContent
from app.models.project import Project, ProjectFile
from app.models.user import User
from app.auth.dependencies import get_db, get_current_user

router = APIRouter()

@router.get("/", response_model=List[ProjectResponse])
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
    await db.delete(p)
    await db.commit()
    return {"status": "deleted"}

@router.post("/{id}/files", response_model=ProjectFileResponse)
async def add_file(id: int, file: ProjectFileCreate, current_user: User = Depends(get_current_user), db: AsyncSession = Depends(get_db)):
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

@router.delete("/{id}/files/{file_id}")
async def delete_file(id: int, file_id: int, current_user: User = Depends(get_current_user), db: AsyncSession = Depends(get_db)):
    result = await db.execute(select(ProjectFile).where(ProjectFile.id == file_id, ProjectFile.project_id == id))
    f = result.scalars().first()
    if not f:
        raise HTTPException(status_code=404)
    await db.delete(f)
    await db.commit()
    return {"status": "deleted"}
