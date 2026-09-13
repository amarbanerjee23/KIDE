from typing import Optional
from fastapi import Depends, HTTPException, status
from fastapi.security import OAuth2PasswordBearer
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy import select

from app.database import get_db
from app.auth.jwt import decode_token
from app.auth.rbac import (
    UserRole, Permission, 
    check_has_permission, check_min_role
)
from app.models.user import User
from app.services.api_token import APITokenService

oauth2_scheme = OAuth2PasswordBearer(tokenUrl="/api/v1/auth/login")
oauth2_scheme_optional = OAuth2PasswordBearer(tokenUrl="/api/v1/auth/login", auto_error=False)

async def get_current_user(
    token: str = Depends(oauth2_scheme), 
    db: AsyncSession = Depends(get_db)
) -> User:
    credentials_exception = HTTPException(
        status_code=status.HTTP_401_UNAUTHORIZED,
        detail="Could not validate credentials",
        headers={"WWW-Authenticate": "Bearer"},
    )

    # 1. Check if token is a customer Scoped API Token (e.g. kide_live_...)
    if token.startswith("kide_"):
        token_res = await APITokenService.verify_token(db, token)
        if not token_res:
            raise HTTPException(
                status_code=status.HTTP_401_UNAUTHORIZED,
                detail="Invalid or expired API token"
            )
        user, api_token = token_res
        if not user.is_active:
            raise HTTPException(status_code=status.HTTP_403_FORBIDDEN, detail="Inactive user account")
        # Attach token metadata to user object for request lifecycle
        user.current_api_token = api_token
        return user

    # 2. Otherwise verify as JWT bearer token
    payload = decode_token(token)
    if payload is None:
        raise credentials_exception
    email: str = payload.get("sub")
    if email is None:
        raise credentials_exception

    result = await db.execute(select(User).where(User.email == email))
    user = result.scalars().first()
    if user is None:
        raise credentials_exception
    if not user.is_active:
        raise HTTPException(status_code=status.HTTP_403_FORBIDDEN, detail="Inactive user account")

    user.current_api_token = None
    return user

async def get_current_user_optional(
    token: str = Depends(oauth2_scheme_optional), 
    db: AsyncSession = Depends(get_db)
) -> Optional[User]:
    if not token:
        return None
    try:
        # Check API token
        if token.startswith("kide_"):
            token_res = await APITokenService.verify_token(db, token)
            if token_res:
                user, api_token = token_res
                user.current_api_token = api_token
                return user
            return None

        payload = decode_token(token)
        if payload is None:
            return None
        email: str = payload.get("sub")
        if email is None:
            return None
        result = await db.execute(select(User).where(User.email == email))
        user = result.scalars().first()
        if user:
            user.current_api_token = None
        return user
    except Exception:
        return None

def require_role(role: str):
    """Legacy and simple role check helper."""
    async def role_checker(current_user: User = Depends(get_current_user)) -> User:
        user_role = (current_user.role or "").lower()
        required_role = role.lower()
        if user_role != required_role and user_role not in ("admin", "owner"):
            raise HTTPException(status_code=status.HTTP_403_FORBIDDEN, detail="Not enough permissions")
        return current_user
    return role_checker

def require_min_role(min_role: UserRole):
    """Enforces minimum hierarchical role: OWNER > ADMIN > ENGINEER > VIEWER."""
    async def min_role_checker(current_user: User = Depends(get_current_user)) -> User:
        if not check_min_role(current_user, min_role):
            raise HTTPException(
                status_code=status.HTTP_403_FORBIDDEN,
                detail=f"Operation requires minimum role: '{min_role.value}'. Current role: '{current_user.role}'"
            )
        return current_user
    return min_role_checker

def require_permission(permission: Permission):
    """Enforces specific granular RBAC permission."""
    async def permission_checker(current_user: User = Depends(get_current_user)) -> User:
        # Check if caller authenticated via scoped API token
        api_token = getattr(current_user, "current_api_token", None)
        if api_token:
            scopes = [s.strip() for s in (api_token.scopes or "").split(",")]
            if "admin" not in scopes:
                if permission in (Permission.PROJECT_READ, Permission.ORG_READ) and "read" not in scopes:
                    raise HTTPException(status_code=status.HTTP_403_FORBIDDEN, detail=f"API token lacks 'read' scope")
                if permission not in (Permission.PROJECT_READ, Permission.ORG_READ) and "write" not in scopes:
                    raise HTTPException(status_code=status.HTTP_403_FORBIDDEN, detail=f"API token lacks 'write' scope")

        if not check_has_permission(current_user, permission):
            raise HTTPException(
                status_code=status.HTTP_403_FORBIDDEN,
                detail=f"User role '{current_user.role}' lacks permission: '{permission.value}'"
            )
        return current_user
    return permission_checker
