from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy import select
from typing import List, Dict, Optional

from app.models.user import User
from app.auth.dependencies import get_db, get_current_user, require_permission
from app.auth.rbac import Permission
from app.schemas.auth import UserResponse
from app.schemas.admin import (
    PlatformStatsResponse,
    AdminOrganizationItem,
    AdminPlanOverrideRequest,
    FeatureFlagCreate,
    FeatureFlagUpdate,
    FeatureFlagResponse
)
from app.services.admin_metrics import AdminMetricsService
from app.services.feature_flags import FeatureFlagService
from app.services.entitlements import EntitlementsService

router = APIRouter(prefix="/admin", tags=["admin"])

# ============================================================================
# 1. Platform & Business Analytics
# ============================================================================

@router.get("/analytics", response_model=PlatformStatsResponse)
async def get_platform_analytics(
    current_user: User = Depends(require_permission(Permission.PLATFORM_ADMIN)),
    db: AsyncSession = Depends(get_db)
):
    """
    Returns executive financial metrics, customer acquisition stats,
    MRR, ARR, churn rate, and AI token consumption.
    """
    return await AdminMetricsService.get_platform_stats(db)

# ============================================================================
# 2. Organization Tenant Directory & Overrides
# ============================================================================

@router.get("/organizations", response_model=List[AdminOrganizationItem])
async def list_organizations_directory(
    current_user: User = Depends(require_permission(Permission.PLATFORM_ADMIN)),
    db: AsyncSession = Depends(get_db)
):
    """
    Returns comprehensive multi-tenant directory across the platform,
    including owner contact, active subscription, and live resource meters.
    """
    return await AdminMetricsService.get_organizations_directory(db)

@router.put("/organizations/{org_id}/tier", response_model=AdminOrganizationItem)
async def override_organization_plan(
    org_id: int,
    req: AdminPlanOverrideRequest,
    current_user: User = Depends(require_permission(Permission.PLATFORM_ADMIN)),
    db: AsyncSession = Depends(get_db)
):
    """
    Administrative override: Immediately modify any organization's commercial tier
    or extend trial duration.
    """
    try:
        return await AdminMetricsService.override_organization_plan(
            db=db,
            org_id=org_id,
            new_tier=req.plan_tier,
            extend_trial_days=req.extend_trial_days,
            actor=current_user
        )
    except ValueError as ve:
        raise HTTPException(status_code=404, detail=str(ve))

# ============================================================================
# 3. Enterprise Feature Flags Management
# ============================================================================

def _to_flag_response(flag) -> FeatureFlagResponse:
    return FeatureFlagResponse(
        id=flag.id,
        key=flag.key,
        name=flag.name,
        description=flag.description,
        is_enabled=flag.is_enabled,
        minimum_tier=flag.minimum_tier,
        allowed_org_ids=flag.get_allowed_org_ids(),
        rollout_percentage=flag.rollout_percentage,
        created_at=flag.created_at,
        updated_at=flag.updated_at
    )

@router.get("/feature-flags", response_model=List[FeatureFlagResponse])
async def list_feature_flags(
    current_user: User = Depends(require_permission(Permission.PLATFORM_ADMIN)),
    db: AsyncSession = Depends(get_db)
):
    """Lists all feature flags and rollout rules."""
    flags = await FeatureFlagService.list_flags(db)
    return [_to_flag_response(f) for f in flags]

@router.post("/feature-flags", response_model=FeatureFlagResponse, status_code=status.HTTP_201_CREATED)
async def create_feature_flag(
    flag_in: FeatureFlagCreate,
    current_user: User = Depends(require_permission(Permission.PLATFORM_ADMIN)),
    db: AsyncSession = Depends(get_db)
):
    """Creates a new enterprise feature flag."""
    try:
        flag = await FeatureFlagService.create_flag(db, flag_in)
        return _to_flag_response(flag)
    except ValueError as ve:
        raise HTTPException(status_code=400, detail=str(ve))

@router.put("/feature-flags/{flag_id}", response_model=FeatureFlagResponse)
async def update_feature_flag(
    flag_id: int,
    flag_in: FeatureFlagUpdate,
    current_user: User = Depends(require_permission(Permission.PLATFORM_ADMIN)),
    db: AsyncSession = Depends(get_db)
):
    """Updates an existing feature flag."""
    try:
        flag = await FeatureFlagService.update_flag(db, flag_id, flag_in)
        return _to_flag_response(flag)
    except ValueError as ve:
        raise HTTPException(status_code=404, detail=str(ve))

@router.delete("/feature-flags/{flag_id}", status_code=status.HTTP_204_NO_CONTENT)
async def delete_feature_flag(
    flag_id: int,
    current_user: User = Depends(require_permission(Permission.PLATFORM_ADMIN)),
    db: AsyncSession = Depends(get_db)
):
    """Deletes a feature flag."""
    success = await FeatureFlagService.delete_flag(db, flag_id)
    if not success:
        raise HTTPException(status_code=404, detail="Feature flag not found")
    return None

# ============================================================================
# 4. Client Feature Flags Evaluation Endpoint
# ============================================================================

@router.get("/feature-flags/eval", response_model=Dict[str, bool])
async def evaluate_client_flags(
    current_user: User = Depends(get_current_user),
    db: AsyncSession = Depends(get_db)
):
    """
    Evaluates and returns all active feature flags for the calling user's organization.
    Available to any authenticated user.
    """
    tier = "community"
    if current_user.org_id:
        sub, _ = await EntitlementsService.get_effective_subscription_and_plan(db, current_user.org_id)
        tier = sub.plan_tier

    return await FeatureFlagService.get_client_flags(db, current_user.org_id, tier)

# ============================================================================
# 5. Legacy Endpoints (Maintained for Backward Compatibility)
# ============================================================================

@router.get("/users", response_model=List[UserResponse])
async def list_users(
    current_user: User = Depends(require_permission(Permission.ORG_MANAGE_MEMBERS)),
    db: AsyncSession = Depends(get_db)
):
    result = await db.execute(select(User).where(User.org_id == current_user.org_id))
    users = result.scalars().all()
    return [
        UserResponse(
            id=u.id,
            email=u.email,
            full_name=u.full_name,
            role=u.role,
            org_name=None,
            created_at=u.created_at
        )
        for u in users
    ]

@router.put("/users/{id}/role")
async def update_user_role(
    id: int,
    role: str,
    current_user: User = Depends(require_permission(Permission.ORG_CHANGE_ROLES)),
    db: AsyncSession = Depends(get_db)
):
    result = await db.execute(select(User).where(User.id == id, User.org_id == current_user.org_id))
    u = result.scalars().first()
    if not u:
        raise HTTPException(status_code=404, detail="User not found")
    u.role = role
    await db.commit()
    return {"status": "updated"}

@router.get("/stats")
async def get_stats(
    current_user: User = Depends(require_permission(Permission.PLATFORM_ADMIN)),
    db: AsyncSession = Depends(get_db)
):
    from app.models.project import Project
    from app.models.transform import TransformHistory
    p_res = await db.execute(select(Project).where(Project.org_id == current_user.org_id))
    projects = p_res.scalars().all()
    th_res = await db.execute(select(TransformHistory).join(User).where(User.org_id == current_user.org_id))
    transforms = th_res.scalars().all()
    return {"projects_count": len(projects), "transforms_count": len(transforms)}
