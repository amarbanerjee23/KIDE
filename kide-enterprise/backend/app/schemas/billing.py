from pydantic import BaseModel, ConfigDict
from typing import List, Optional
from datetime import datetime
from app.models.subscription import PlanTier, SubscriptionStatus

class PlanResponse(BaseModel):
    tier: str
    name: str
    description: Optional[str] = None
    price_monthly_cents: int
    price_annual_cents: int
    max_projects: int # -1 = unlimited
    max_members: int  # -1 = unlimited
    monthly_ai_tokens: int # -1 = unlimited
    allowed_generators: List[str]
    traceability_export: bool
    audit_retention_days: int

class UsageSummaryResponse(BaseModel):
    projects_count: int
    max_projects: int
    members_count: int
    max_members: int
    ai_tokens_used: int
    monthly_ai_tokens: int

class SubscriptionResponse(BaseModel):
    id: int
    org_id: int
    plan_tier: str
    plan_name: str
    status: str
    current_period_start: Optional[datetime] = None
    current_period_end: Optional[datetime] = None
    trial_start: Optional[datetime] = None
    trial_end: Optional[datetime] = None
    is_trial: bool = False
    trial_days_remaining: int = 0
    cancel_at_period_end: bool = False
    usage: UsageSummaryResponse

class CreateCheckoutRequest(BaseModel):
    tier: PlanTier
    billing_cycle: str = "monthly" # monthly or annual
    success_url: Optional[str] = None
    cancel_url: Optional[str] = None

class CheckoutResponse(BaseModel):
    session_id: str
    checkout_url: str

class CreatePortalRequest(BaseModel):
    return_url: Optional[str] = None

class PortalResponse(BaseModel):
    portal_url: str

class ActivateTrialResponse(BaseModel):
    status: str
    plan_tier: str
    trial_end: datetime
    message: str

