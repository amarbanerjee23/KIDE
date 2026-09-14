import json
import math
from fastapi import APIRouter, Depends, HTTPException, Request, Header, status
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy import select
from typing import List, Optional
from datetime import datetime, timezone

from app.auth.dependencies import get_db, get_current_user, require_permission
from app.auth.rbac import Permission
from app.models.user import User, Organization
from app.models.subscription import PlanTier, SubscriptionStatus
from app.schemas.billing import (
    PlanResponse,
    SubscriptionResponse,
    UsageSummaryResponse,
    CreateCheckoutRequest,
    CheckoutResponse,
    CreatePortalRequest,
    PortalResponse,
    ActivateTrialResponse
)
from app.services.entitlements import EntitlementsService
from app.services.billing import BillingService

router = APIRouter(prefix="/billing", tags=["billing"])

def utcnow():
    return datetime.now(timezone.utc)

@router.get("/plans", response_model=List[PlanResponse])
async def list_plans(db: AsyncSession = Depends(get_db)):
    """Returns all available commercial subscription tiers and entitlement limits."""
    plans = await EntitlementsService.get_all_plans(db)
    out = []
    for p in plans:
        allowed = json.loads(p.allowed_generators or '["python"]')
        out.append(PlanResponse(
            tier=p.tier,
            name=p.name,
            description=p.description,
            price_monthly_cents=p.price_monthly_cents,
            price_annual_cents=p.price_annual_cents,
            max_projects=p.max_projects,
            max_members=p.max_members,
            monthly_ai_tokens=p.monthly_ai_tokens,
            allowed_generators=allowed,
            traceability_export=p.traceability_export,
            audit_retention_days=p.audit_retention_days
        ))
    return out

@router.get("/subscription", response_model=SubscriptionResponse)
async def get_org_subscription(
    current_user: User = Depends(get_current_user),
    db: AsyncSession = Depends(get_db)
):
    """
    Returns current organization's active plan, status, trial countdown,
    and real-time usage meters (projects, members, AI tokens).
    """
    if not current_user.org_id:
        raise HTTPException(status_code=400, detail="User does not belong to an organization")

    sub, plan = await EntitlementsService.get_effective_subscription_and_plan(db, current_user.org_id)
    usage = await EntitlementsService.get_usage_summary(db, current_user.org_id)

    now = utcnow()
    trial_end = sub.trial_end
    if trial_end and trial_end.tzinfo is None:
        trial_end = trial_end.replace(tzinfo=timezone.utc)

    is_trial = sub.status == SubscriptionStatus.TRIALING.value and trial_end is not None and now <= trial_end
    trial_days_remaining = max(0, int(math.ceil((trial_end - now).total_seconds() / 86400.0))) if is_trial and trial_end else 0

    return SubscriptionResponse(
        id=sub.id,
        org_id=sub.org_id,
        plan_tier=sub.plan_tier,
        plan_name=plan.name,
        status=sub.status,
        current_period_start=sub.current_period_start,
        current_period_end=sub.current_period_end,
        trial_start=sub.trial_start,
        trial_end=sub.trial_end,
        is_trial=is_trial,
        trial_days_remaining=trial_days_remaining,
        cancel_at_period_end=sub.cancel_at_period_end,
        usage=UsageSummaryResponse(
            projects_count=usage["projects_count"],
            max_projects=plan.max_projects,
            members_count=usage["members_count"],
            max_members=plan.max_members,
            ai_tokens_used=usage["ai_tokens_used"],
            monthly_ai_tokens=plan.monthly_ai_tokens
        )
    )

@router.post("/checkout", response_model=CheckoutResponse)
async def create_checkout_session(
    request: CreateCheckoutRequest,
    current_user: User = Depends(require_permission(Permission.ORG_BILLING)),
    db: AsyncSession = Depends(get_db)
):
    """
    Initiates Stripe Checkout session for upgrading or changing subscription plans.
    """
    org_res = await db.execute(select(Organization).where(Organization.id == current_user.org_id))
    org = org_res.scalars().first()
    if not org:
        raise HTTPException(status_code=404, detail="Organization not found")

    session_data = await BillingService.create_checkout_session(
        db=db,
        org=org,
        tier=request.tier,
        billing_cycle=request.billing_cycle,
        success_url=request.success_url,
        cancel_url=request.cancel_url
    )
    return CheckoutResponse(**session_data)

@router.post("/portal", response_model=PortalResponse)
async def create_customer_portal(
    request: CreatePortalRequest,
    current_user: User = Depends(require_permission(Permission.ORG_BILLING)),
    db: AsyncSession = Depends(get_db)
):
    """Returns Stripe Customer Portal session for managing invoices and payment methods."""
    org_res = await db.execute(select(Organization).where(Organization.id == current_user.org_id))
    org = org_res.scalars().first()
    if not org:
        raise HTTPException(status_code=404, detail="Organization not found")

    portal_url = await BillingService.create_customer_portal_session(
        db=db,
        org=org,
        return_url=request.return_url
    )
    return PortalResponse(portal_url=portal_url)

@router.post("/trial/activate", response_model=ActivateTrialResponse)
async def activate_trial(
    current_user: User = Depends(require_permission(Permission.ORG_BILLING)),
    db: AsyncSession = Depends(get_db)
):
    """Activates a fresh 14-day Professional Team trial for the organization."""
    sub = await EntitlementsService.activate_trial(db, current_user.org_id, days=14)
    return ActivateTrialResponse(
        status=sub.status,
        plan_tier=sub.plan_tier,
        trial_end=sub.trial_end,
        message="14-day Professional Team trial successfully activated with full entitlements!"
    )

@router.post("/webhook")
async def handle_stripe_webhook(
    request: Request,
    stripe_signature: Optional[str] = Header(None, alias="stripe-signature"),
    db: AsyncSession = Depends(get_db)
):
    """
    Cryptographic webhook receiver for Stripe event callbacks.
    Guarantees idempotency and updates subscription state.
    """
    body = await request.body()
    res = await BillingService.process_webhook(
        db=db,
        payload_bytes=body,
        signature_header=stripe_signature
    )
    return res
