from fastapi import APIRouter, Depends, HTTPException, status, Query
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy import select, func, desc
from typing import List, Optional
from datetime import datetime, timedelta, timezone
import secrets

from app.auth.dependencies import get_db, get_current_user, require_permission
from app.auth.rbac import UserRole, Permission, ROLE_WEIGHTS
from app.auth.password import hash_password
from app.models.user import User, Organization
from app.models.project import Project
from app.models.audit_log import AuditLog
from app.models.api_token import APIToken
from app.schemas.organization import (
    OrganizationDetailsResponse,
    MemberResponse,
    InviteMemberRequest,
    UpdateMemberRoleRequest,
    AuditLogResponse,
    CreateAPITokenRequest,
    CreateAPITokenResponse,
    APITokenResponse
)
from app.services.audit import AuditService
from app.services.api_token import APITokenService
from app.services.entitlements import EntitlementsService

router = APIRouter(prefix="/org", tags=["organization"])

def utcnow():
    return datetime.now(timezone.utc)

@router.get("/details", response_model=OrganizationDetailsResponse)
async def get_org_details(
    current_user: User = Depends(require_permission(Permission.ORG_READ)),
    db: AsyncSession = Depends(get_db)
):
    """Returns metadata, member counts, and project counts for current user's organization."""
    if not current_user.org_id:
        raise HTTPException(status_code=400, detail="User does not belong to an organization")

    org_res = await db.execute(select(Organization).where(Organization.id == current_user.org_id))
    org = org_res.scalars().first()
    if not org:
        raise HTTPException(status_code=404, detail="Organization not found")

    members_cnt_res = await db.execute(
        select(func.count(User.id)).where(User.org_id == current_user.org_id, User.is_active.is_(True))
    )
    members_count = members_cnt_res.scalar() or 0

    projects_cnt_res = await db.execute(
        select(func.count(Project.id)).where(Project.org_id == current_user.org_id)
    )
    projects_count = projects_cnt_res.scalar() or 0

    return OrganizationDetailsResponse(
        id=org.id,
        name=org.name,
        slug=org.slug,
        created_at=org.created_at,
        members_count=members_count,
        projects_count=projects_count
    )

@router.get("/members", response_model=List[MemberResponse])
async def list_members(
    current_user: User = Depends(require_permission(Permission.ORG_READ)),
    db: AsyncSession = Depends(get_db)
):
    """Lists all active and invited members of the organization."""
    if not current_user.org_id:
        raise HTTPException(status_code=400, detail="User does not belong to an organization")

    res = await db.execute(
        select(User).where(User.org_id == current_user.org_id).order_by(User.id.asc())
    )
    users = res.scalars().all()
    return users

@router.post("/members/invite", response_model=MemberResponse)
@router.post("/members", response_model=MemberResponse)
async def invite_member(
    invite: InviteMemberRequest,
    current_user: User = Depends(require_permission(Permission.ORG_MANAGE_MEMBERS)),
    db: AsyncSession = Depends(get_db)
):
    """
    Invites a new member to the organization with assigned role.
    Only OWNER can assign OWNER or ADMIN roles.
    """
    if not current_user.org_id:
        raise HTTPException(status_code=400, detail="User does not belong to an organization")

    # Check if email is already registered
    existing_res = await db.execute(select(User).where(User.email == invite.email.strip().lower()))
    if existing_res.scalars().first():
        raise HTTPException(status_code=400, detail="Email is already registered")

    # Privilege check: only OWNER can invite an OWNER or ADMIN
    caller_role = (current_user.role or "").lower()
    target_role = invite.role.value if isinstance(invite.role, UserRole) else str(invite.role).lower()

    if target_role in (UserRole.OWNER.value, UserRole.ADMIN.value) and caller_role != UserRole.OWNER.value:
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="Only organization Owners can invite Admin or Owner members"
        )

    # Entitlements check: Member seat limits
    await EntitlementsService.check_can_invite_member(db, current_user.org_id)

    initial_password = invite.password or secrets.token_urlsafe(12)
    new_user = User(
        email=invite.email.strip().lower(),
        hashed_password=hash_password(initial_password),
        full_name=invite.full_name,
        org_id=current_user.org_id,
        role=target_role,
        is_active=True,
        created_at=utcnow()
    )
    db.add(new_user)
    await db.commit()
    await db.refresh(new_user)

    # Immutable Audit Log
    await AuditService.log(
        db=db,
        org_id=current_user.org_id,
        action="member:invite",
        actor=current_user,
        target_type="user",
        target_id=str(new_user.id),
        details={"email": new_user.email, "role": new_user.role, "full_name": new_user.full_name}
    )

    return new_user

@router.put("/members/{user_id}/role", response_model=MemberResponse)
async def update_member_role(
    user_id: int,
    request: UpdateMemberRoleRequest,
    current_user: User = Depends(require_permission(Permission.ORG_CHANGE_ROLES)),
    db: AsyncSession = Depends(get_db)
):
    """
    Updates a member's role within the organization.
    Prevents demoting the last Owner and restricts non-Owners from modifying Admin/Owner roles.
    """
    target_res = await db.execute(
        select(User).where(User.id == user_id, User.org_id == current_user.org_id)
    )
    target_user = target_res.scalars().first()
    if not target_user:
        raise HTTPException(status_code=404, detail="Member not found in organization")

    caller_role = (current_user.role or "").lower()
    old_role = (target_user.role or "").lower()
    new_role = request.role.value if isinstance(request.role, UserRole) else str(request.role).lower()

    # If caller is not owner, they cannot assign owner/admin or modify existing owner/admin
    if caller_role != UserRole.OWNER.value:
        if new_role in (UserRole.OWNER.value, UserRole.ADMIN.value):
            raise HTTPException(
                status_code=status.HTTP_403_FORBIDDEN,
                detail="Only organization Owners can assign Admin or Owner roles"
            )
        if old_role in (UserRole.OWNER.value, UserRole.ADMIN.value):
            raise HTTPException(
                status_code=status.HTTP_403_FORBIDDEN,
                detail="Admins cannot modify roles of other Admins or Owners"
            )

    # Guard: Cannot demote the last Owner
    if old_role == UserRole.OWNER.value and new_role != UserRole.OWNER.value:
        owners_cnt_res = await db.execute(
            select(func.count(User.id)).where(
                User.org_id == current_user.org_id,
                User.role == UserRole.OWNER.value,
                User.is_active.is_(True)
            )
        )
        owners_count = owners_cnt_res.scalar() or 0
        if owners_count <= 1:
            raise HTTPException(
                status_code=400,
                detail="Cannot demote the last Owner of the organization"
            )

    target_user.role = new_role
    await db.commit()
    await db.refresh(target_user)

    # Immutable Audit Log
    await AuditService.log(
        db=db,
        org_id=current_user.org_id,
        action="member:role_updated",
        actor=current_user,
        target_type="user",
        target_id=str(target_user.id),
        details={"email": target_user.email, "old_role": old_role, "new_role": new_role}
    )

    return target_user

@router.delete("/members/{user_id}")
async def remove_member(
    user_id: int,
    current_user: User = Depends(require_permission(Permission.ORG_MANAGE_MEMBERS)),
    db: AsyncSession = Depends(get_db)
):
    """
    Removes a member from the organization.
    Prevents self-removal, removing the last Owner, and Admin deleting an Owner.
    """
    if target_user_id := user_id:
        if target_user_id == current_user.id:
            raise HTTPException(status_code=400, detail="Cannot remove yourself from the organization")

    target_res = await db.execute(
        select(User).where(User.id == user_id, User.org_id == current_user.org_id)
    )
    target_user = target_res.scalars().first()
    if not target_user:
        raise HTTPException(status_code=404, detail="Member not found in organization")

    caller_role = (current_user.role or "").lower()
    target_role = (target_user.role or "").lower()

    if caller_role != UserRole.OWNER.value and target_role in (UserRole.OWNER.value, UserRole.ADMIN.value):
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="Admins cannot remove other Admins or Owners"
        )

    # Guard: Cannot delete the last Owner
    if target_role == UserRole.OWNER.value:
        owners_cnt_res = await db.execute(
            select(func.count(User.id)).where(
                User.org_id == current_user.org_id,
                User.role == UserRole.OWNER.value,
                User.is_active.is_(True)
            )
        )
        owners_count = owners_cnt_res.scalar() or 0
        if owners_count <= 1:
            raise HTTPException(
                status_code=400,
                detail="Cannot remove the last Owner of the organization"
            )

    # Soft delete / deactivate user
    target_user.is_active = False
    await db.commit()

    # Immutable Audit Log
    await AuditService.log(
        db=db,
        org_id=current_user.org_id,
        action="member:removed",
        actor=current_user,
        target_type="user",
        target_id=str(target_user.id),
        details={"email": target_user.email, "role": target_role}
    )

    return {"status": "removed", "user_id": user_id}

@router.get("/audit-logs", response_model=List[AuditLogResponse])
async def list_audit_logs(
    limit: int = Query(50, ge=1, le=200),
    offset: int = Query(0, ge=0),
    action: Optional[str] = None,
    current_user: User = Depends(require_permission(Permission.ORG_VIEW_AUDIT_LOGS)),
    db: AsyncSession = Depends(get_db)
):
    """
    Queries immutable audit logs for the current organization.
    Supports filtering by action, ordered by most recent first.
    """
    if not current_user.org_id:
        raise HTTPException(status_code=400, detail="User does not belong to an organization")

    stmt = select(AuditLog).where(AuditLog.org_id == current_user.org_id)
    if action:
        stmt = stmt.where(AuditLog.action == action)
    stmt = stmt.order_by(desc(AuditLog.created_at)).offset(offset).limit(limit)

    res = await db.execute(stmt)
    return res.scalars().all()

@router.post("/api-tokens", response_model=CreateAPITokenResponse)
async def create_api_token(
    request: CreateAPITokenRequest,
    current_user: User = Depends(require_permission(Permission.ORG_MANAGE_API_TOKENS)),
    db: AsyncSession = Depends(get_db)
):
    """
    Generates a new scoped API token (kide_live_...).
    The raw token is only returned ONCE upon creation.
    """
    if not current_user.org_id:
        raise HTTPException(status_code=400, detail="User does not belong to an organization")

    expires_at = None
    if request.expires_days and request.expires_days > 0:
        expires_at = utcnow() + timedelta(days=request.expires_days)

    raw_token, token_record = await APITokenService.create_token(
        db=db,
        user=current_user,
        name=request.name.strip(),
        scopes=request.scopes,
        expires_at=expires_at
    )

    # Immutable Audit Log
    await AuditService.log(
        db=db,
        org_id=current_user.org_id,
        action="api_token:create",
        actor=current_user,
        target_type="api_token",
        target_id=str(token_record.id),
        details={"name": token_record.name, "prefix": token_record.prefix, "scopes": token_record.scopes}
    )

    return CreateAPITokenResponse(
        id=token_record.id,
        name=token_record.name,
        prefix=token_record.prefix,
        raw_token=raw_token,
        scopes=token_record.scopes,
        expires_at=token_record.expires_at,
        created_at=token_record.created_at
    )

@router.get("/api-tokens", response_model=List[APITokenResponse])
async def list_api_tokens(
    current_user: User = Depends(require_permission(Permission.ORG_MANAGE_API_TOKENS)),
    db: AsyncSession = Depends(get_db)
):
    """Lists all active scoped API tokens for the organization."""
    if not current_user.org_id:
        raise HTTPException(status_code=400, detail="User does not belong to an organization")

    stmt = select(APIToken).where(
        APIToken.org_id == current_user.org_id,
        APIToken.is_active.is_(True)
    ).order_by(desc(APIToken.created_at))

    res = await db.execute(stmt)
    return res.scalars().all()

@router.delete("/api-tokens/{token_id}")
async def revoke_api_token(
    token_id: int,
    current_user: User = Depends(require_permission(Permission.ORG_MANAGE_API_TOKENS)),
    db: AsyncSession = Depends(get_db)
):
    """Revokes an API token, immediately disabling its access."""
    if not current_user.org_id:
        raise HTTPException(status_code=400, detail="User does not belong to an organization")

    success = await APITokenService.revoke_token(db, token_id=token_id, org_id=current_user.org_id)
    if not success:
        raise HTTPException(status_code=404, detail="API token not found or already revoked")

    # Immutable Audit Log
    await AuditService.log(
        db=db,
        org_id=current_user.org_id,
        action="api_token:revoke",
        actor=current_user,
        target_type="api_token",
        target_id=str(token_id)
    )

    return {"status": "revoked", "token_id": token_id}

