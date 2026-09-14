from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy import select
from app.schemas.auth import UserCreate, UserLogin, Token, UserResponse
from app.models.user import User, Organization
from app.auth.password import hash_password, verify_password
from app.auth.jwt import create_access_token, create_refresh_token
from app.services.audit import AuditService
from app.auth.dependencies import get_db, get_current_user
import uuid

router = APIRouter()

@router.post("/register", response_model=Token)
async def register(user_data: UserCreate, db: AsyncSession = Depends(get_db)):
    result = await db.execute(select(User).where(User.email == user_data.email))
    if result.scalars().first():
        raise HTTPException(status_code=400, detail="Email already registered")
        
    org = None
    if user_data.org_name:
        org = Organization(name=user_data.org_name, slug=str(uuid.uuid4()))
        db.add(org)
        await db.commit()
        await db.refresh(org)
        
    user = User(
        email=user_data.email,
        hashed_password=hash_password(user_data.password),
        full_name=user_data.full_name,
        org_id=org.id if org else None,
        role="owner" if org else "member"
    )
    db.add(user)
    await db.commit()
    await db.refresh(user)

    if org:
        await AuditService.log(
            db=db,
            org_id=org.id,
            action="org:created",
            actor=user,
            target_type="organization",
            target_id=str(org.id),
            details={"name": org.name, "owner": user.email}
        )
    
    access_token = create_access_token({"sub": user.email})
    refresh_token = create_refresh_token({"sub": user.email})
    return {"access_token": access_token, "refresh_token": refresh_token, "token_type": "bearer"}

@router.post("/login", response_model=Token)
async def login(user_data: UserLogin, db: AsyncSession = Depends(get_db)):
    result = await db.execute(select(User).where(User.email == user_data.email))
    user = result.scalars().first()
    if not user or not verify_password(user_data.password, user.hashed_password):
        raise HTTPException(status_code=401, detail="Invalid credentials")
        
    if user.org_id:
        await AuditService.log(
            db=db,
            org_id=user.org_id,
            action="user:login",
            actor=user,
            target_type="user",
            target_id=str(user.id),
            details={"email": user.email}
        )

    access_token = create_access_token({"sub": user.email})
    refresh_token = create_refresh_token({"sub": user.email})
    return {"access_token": access_token, "refresh_token": refresh_token, "token_type": "bearer"}

@router.post("/refresh", response_model=Token)
async def refresh():
    pass # Implementation requires tracking refresh tokens, returning dummy for now
    return {"access_token": "", "refresh_token": "", "token_type": "bearer"}

@router.get("/me", response_model=UserResponse)
async def get_me(current_user: User = Depends(get_current_user), db: AsyncSession = Depends(get_db)):
    if current_user.org_id:
        res = await db.execute(select(Organization).where(Organization.id == current_user.org_id))
        org = res.scalars().first()
        org_name = org.name if org else None
    else:
        org_name = None
    return UserResponse(
        id=current_user.id,
        email=current_user.email,
        full_name=current_user.full_name,
        role=current_user.role,
        org_name=org_name,
        created_at=current_user.created_at
    )
