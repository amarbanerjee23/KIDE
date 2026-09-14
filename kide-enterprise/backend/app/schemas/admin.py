from pydantic import BaseModel, ConfigDict
from typing import List, Optional, Dict
from datetime import datetime

class PlatformStatsResponse(BaseModel):
    mrr_cents: int
    mrr_usd: float
    arr_cents: int
    arr_usd: float
    active_subscribers: int
    active_trials: int
    trials_expiring_soon: int
    conversion_rate_pct: float
    churn_rate_pct: float
    tier_distribution: Dict[str, int]
    total_ai_tokens_used: int
    total_projects: int
    total_organizations: int
    total_users: int

class AdminOrganizationItem(BaseModel):
    id: int
    name: str
    slug: str
    owner_email: Optional[str] = None
    owner_name: Optional[str] = None
    plan_tier: str
    subscription_status: str
    is_trial: bool
    trial_days_remaining: int
    projects_count: int
    members_count: int
    ai_tokens_used: int
    created_at: Optional[datetime] = None

class AdminPlanOverrideRequest(BaseModel):
    plan_tier: str # community, team, enterprise
    extend_trial_days: Optional[int] = None

class FeatureFlagCreate(BaseModel):
    key: str
    name: str
    description: Optional[str] = None
    is_enabled: bool = True
    minimum_tier: str = "community"
    allowed_org_ids: List[int] = []
    rollout_percentage: int = 100

class FeatureFlagUpdate(BaseModel):
    name: Optional[str] = None
    description: Optional[str] = None
    is_enabled: Optional[bool] = None
    minimum_tier: Optional[str] = None
    allowed_org_ids: Optional[List[int]] = None
    rollout_percentage: Optional[int] = None

class FeatureFlagResponse(BaseModel):
    model_config = ConfigDict(from_attributes=True)

    id: int
    key: str
    name: str
    description: Optional[str] = None
    is_enabled: bool
    minimum_tier: str
    allowed_org_ids: List[int]
    rollout_percentage: int
    created_at: Optional[datetime] = None
    updated_at: Optional[datetime] = None

