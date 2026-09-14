import math
import logging
from typing import List, Dict, Any, Optional
from datetime import datetime, timezone, timedelta
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy import select, func, desc

from app.models.user import Organization, User
from app.models.project import Project
from app.models.subscription import (
    Subscription, SubscriptionPlan, UsageMeter, PlanTier, SubscriptionStatus
)
from app.schemas.admin import PlatformStatsResponse, AdminOrganizationItem
from app.services.audit import AuditService
from app.services.entitlements import EntitlementsService

logger = logging.getLogger("kide.admin_metrics")

def utcnow():
    return datetime.now(timezone.utc)

class AdminMetricsService:
    """
    Business & Administrative Intelligence Engine.
    Aggregates financial KPIs, tenant activity, and resource saturation.
    """

    @classmethod
    async def get_platform_stats(cls, db: AsyncSession) -> PlatformStatsResponse:
        now = utcnow()

        # Seed plans if not yet seeded
        await EntitlementsService.seed_plans_if_needed(db)

        # 1. Total counts
        total_orgs_res = await db.execute(select(func.count(Organization.id)))
        total_organizations = total_orgs_res.scalar() or 0

        total_users_res = await db.execute(select(func.count(User.id)))
        total_users = total_users_res.scalar() or 0

        total_projects_res = await db.execute(select(func.count(Project.id)))
        total_projects = total_projects_res.scalar() or 0

        # Total AI tokens
        tokens_res = await db.execute(select(func.sum(UsageMeter.ai_tokens_used)))
        total_ai_tokens_used = tokens_res.scalar() or 0

        # 2. Subscriptions & Plans
        subs_res = await db.execute(select(Subscription))
        subscriptions = subs_res.scalars().all()

        plans_res = await db.execute(select(SubscriptionPlan))
        plans_map = {p.tier: p for p in plans_res.scalars().all()}

        mrr_cents = 0
        active_subscribers = 0
        active_trials = 0
        trials_expiring_soon = 0
        canceled_count = 0
        tier_distribution: Dict[str, int] = {
            PlanTier.COMMUNITY.value: 0,
            PlanTier.TEAM.value: 0,
            PlanTier.ENTERPRISE.value: 0
        }

        for sub in subscriptions:
            # Reconcile naive datetime from SQLite
            trial_end = sub.trial_end
            if trial_end and trial_end.tzinfo is None:
                trial_end = trial_end.replace(tzinfo=timezone.utc)

            tier = sub.plan_tier
            tier_distribution[tier] = tier_distribution.get(tier, 0) + 1

            # Trial checking
            if sub.status == SubscriptionStatus.TRIALING.value and trial_end and trial_end > now:
                active_trials += 1
                days_left = max(0, (trial_end - now).days)
                if days_left <= 3:
                    trials_expiring_soon += 1

            # Active paying subscribers
            elif sub.status == SubscriptionStatus.ACTIVE.value and tier != PlanTier.COMMUNITY.value:
                active_subscribers += 1
                plan = plans_map.get(tier)
                if plan:
                    mrr_cents += plan.price_monthly_cents

            elif sub.status == SubscriptionStatus.CANCELED.value:
                canceled_count += 1

        mrr_usd = round(mrr_cents / 100.0, 2)
        arr_cents = mrr_cents * 12
        arr_usd = round(arr_cents / 100.0, 2)

        # Conversion rate: paying orgs / total orgs
        conversion_rate_pct = round((active_subscribers / max(1, total_organizations)) * 100.0, 1)

        # Churn rate: canceled / (active + canceled)
        total_paid_pool = active_subscribers + canceled_count
        churn_rate_pct = round((canceled_count / max(1, total_paid_pool)) * 100.0, 1)

        return PlatformStatsResponse(
            mrr_cents=mrr_cents,
            mrr_usd=mrr_usd,
            arr_cents=arr_cents,
            arr_usd=arr_usd,
            active_subscribers=active_subscribers,
            active_trials=active_trials,
            trials_expiring_soon=trials_expiring_soon,
            conversion_rate_pct=conversion_rate_pct,
            churn_rate_pct=churn_rate_pct,
            tier_distribution=tier_distribution,
            total_ai_tokens_used=total_ai_tokens_used,
            total_projects=total_projects,
            total_organizations=total_organizations,
            total_users=total_users
        )

    @classmethod
    async def get_organizations_directory(cls, db: AsyncSession) -> List[AdminOrganizationItem]:
        now = utcnow()
        orgs_res = await db.execute(select(Organization).order_by(Organization.created_at.desc()))
        organizations = orgs_res.scalars().all()

        items: List[AdminOrganizationItem] = []
        for org in organizations:
            # Find owner
            owner_res = await db.execute(
                select(User).where(User.org_id == org.id, User.role == "owner")
            )
            owner = owner_res.scalars().first()
            if not owner:
                # Fallback to any user in org
                any_user_res = await db.execute(
                    select(User).where(User.org_id == org.id).order_by(User.id.asc())
                )
                owner = any_user_res.scalars().first()

            sub, plan = await EntitlementsService.get_effective_subscription_and_plan(db, org.id)
            usage = await EntitlementsService.get_usage_summary(db, org.id)

            trial_end = sub.trial_end
            if trial_end and trial_end.tzinfo is None:
                trial_end = trial_end.replace(tzinfo=timezone.utc)

            is_trial = sub.status == SubscriptionStatus.TRIALING.value and trial_end is not None and now <= trial_end
            trial_days_remaining = max(0, int(math.ceil((trial_end - now).total_seconds() / 86400.0))) if is_trial and trial_end else 0

            items.append(AdminOrganizationItem(
                id=org.id,
                name=org.name,
                slug=org.slug,
                owner_email=owner.email if owner else None,
                owner_name=owner.full_name if owner else None,
                plan_tier=sub.plan_tier,
                subscription_status=sub.status,
                is_trial=is_trial,
                trial_days_remaining=trial_days_remaining,
                projects_count=usage["projects_count"],
                members_count=usage["members_count"],
                ai_tokens_used=usage["ai_tokens_used"],
                created_at=org.created_at
            ))

        return items

    @classmethod
    async def override_organization_plan(
        cls,
        db: AsyncSession,
        org_id: int,
        new_tier: str,
        extend_trial_days: Optional[int] = None,
        actor: Optional[User] = None
    ) -> AdminOrganizationItem:
        org_res = await db.execute(select(Organization).where(Organization.id == org_id))
        org = org_res.scalars().first()
        if not org:
            raise ValueError(f"Organization #{org_id} not found")

        sub = await EntitlementsService.get_or_create_subscription(db, org_id)
        prev_tier = sub.plan_tier
        sub.plan_tier = new_tier
        now = utcnow()

        if new_tier == PlanTier.COMMUNITY.value:
            sub.is_trial = False
            sub.trial_end = None
            sub.status = SubscriptionStatus.ACTIVE.value
        else:
            sub.status = SubscriptionStatus.ACTIVE.value
            if extend_trial_days and extend_trial_days > 0:
                sub.status = SubscriptionStatus.TRIALING.value
                current_end = sub.trial_end or now
                if current_end.tzinfo is None:
                    current_end = current_end.replace(tzinfo=timezone.utc)
                sub.trial_end = max(now, current_end) + timedelta(days=extend_trial_days)

        sub.updated_at = now
        await db.commit()
        await db.refresh(sub)

        # Audit log
        await AuditService.log(
            db=db,
            org_id=org_id,
            action="admin:plan_override",
            actor=actor,
            target_type="organization",
            target_id=str(org_id),
            details={
                "previous_tier": prev_tier,
                "new_tier": new_tier,
                "extended_days": extend_trial_days
            }
        )

        # Return refreshed directory item
        items = await cls.get_organizations_directory(db)
        for it in items:
            if it.id == org_id:
                return it

        raise ValueError("Failed to retrieve updated organization item")

