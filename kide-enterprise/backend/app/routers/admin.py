from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy import select
from typing import List
from app.models.user import User
from app.schemas.auth import UserResponse
from app.auth.dependencies import get_db, require_role

router = APIRouter()

@router.get("/users", response_model=List[UserResponse])
async def list_users(current_user: User = Depends(require_role("admin")), db: AsyncSession = Depends(get_db)):
    result = await db.execute(select(User).where(User.org_id == current_user.org_id))
    users = result.scalars().all()
    return [UserResponse(id=u.id, email=u.email, full_name=u.full_name, role=u.role, org_name=None, created_at=u.created_at) for u in users]

@router.put("/users/{id}/role")
async def update_user_role(id: int, role: str, current_user: User = Depends(require_role("admin")), db: AsyncSession = Depends(get_db)):
    result = await db.execute(select(User).where(User.id == id, User.org_id == current_user.org_id))
    u = result.scalars().first()
    if not u:
        raise HTTPException(status_code=404)
    u.role = role
    await db.commit()
    return {"status": "updated"}

@router.get("/stats")
async def get_stats(current_user: User = Depends(require_role("admin")), db: AsyncSession = Depends(get_db)):
    from app.models.project import Project
    from app.models.transform import TransformHistory
    p_res = await db.execute(select(Project).where(Project.org_id == current_user.org_id))
    projects = p_res.scalars().all()
    th_res = await db.execute(select(TransformHistory).join(User).where(User.org_id == current_user.org_id))
    transforms = th_res.scalars().all()
    return {"projects_count": len(projects), "transforms_count": len(transforms)}
