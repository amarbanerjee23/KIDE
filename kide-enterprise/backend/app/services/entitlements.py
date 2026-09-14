import json
import logging
from typing import Dict, Any, List, Optional, Tuple
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy import select, func
from datetime import datetime, timedelta, timezone
from fastapi import HTTPException, status

from app.models.subscription import (
    SubscriptionPlan, Subscription, UsageMeter,
    PlanTier, SubscriptionStatus
)
from app.models.user import User, Organization
from app.models.project import Project

logger = logging.getLogger("kide.entitlements")

def utcnow():
    return datetime.now(timezone.utc)

DEFAULT_PLANS: List[Dict[str, Any]] = [
    {
        "tier": PlanTier.COMMUNITY.value,
        "name": "Community Edition",
        "description": "Essential industrial modeling, DML validation, and state machine synthesis for individual engineers.",
        "price_monthly_cents": 0,
        "price_annual_cents": 0,
        "max_projects": 3,
        "max_members": 2,
        "monthly_ai_tokens": 50000,
        "allowed_generators": json.dumps(["python"]),
        "traceability_export": False,
        "audit_retention_days": 7
    },
    {
        "tier": PlanTier.TEAM.value,
        "name": "Professional Team",
        "description": "Multi-engineer collaboration, ROS2/PLC generators, 1M AI tokens, and change impact blast radius analysis.",
        "price_monthly_cents": 4900,
        "price_annual_cents": 49000,
        "max_projects": 25,
        "max_members": 10,
        "monthly_ai_tokens": 1000000,
        "allowed_generators": json.dumps(["python", "ros2", "java", "plc"]),
        "traceability_export": True,
        "audit_retention_days": 90
    },
    {
        "tier": PlanTier.ENTERPRISE.value,
        "name": "Industrial Enterprise",
        "description": "Unlimited projects, 10M tokens, custom embedded code generation sandbox, priority support, and compliance audit trail.",
        "price_monthly_cents": 29900,
        "price_annual_cents": 299000,
        "max_projects": -1,
        "max_members": -1,
        "monthly_ai_tokens": 10000000,
        "allowed_generators": json.dumps(["*"]),
        "traceability_export": True,
        "audit_retention_days": 365
    }
]

class EntitlementsService:
    @classmethod
    async def ensure_plans_seeded(cls, db: AsyncSession) -> None:
        """Seeds the 3 canonical subscription plans if table is empty."""
        res = await db.execute(select(SubscriptionPlan))
        existing = res.scalars().all()
        if not existing:
            for p_data in DEFAULT_PLANS:
                plan = SubscriptionPlan(**p_data)
                db.add(plan)
            await db.commit()

    seed_plans_if_needed = ensure_plans_seeded

    @classmethod
    async def get_plan_by_tier(cls, db: AsyncSession, tier: str) -> SubscriptionPlan:
        await cls.ensure_plans_seeded(db)
        res = await db.execute(select(SubscriptionPlan).where(SubscriptionPlan.tier == tier.lower()))
        plan = res.scalars().first()
        if not plan:
            # Fallback to community
            res = await db.execute(select(SubscriptionPlan).where(SubscriptionPlan.tier == PlanTier.COMMUNITY.value))
            plan = res.scalars().first()
        return plan

    @classmethod
    async def get_all_plans(cls, db: AsyncSession) -> List[SubscriptionPlan]:
        await cls.ensure_plans_seeded(db)
        res = await db.execute(select(SubscriptionPlan).where(SubscriptionPlan.is_active.is_(True)).order_by(SubscriptionPlan.id.asc()))
        return res.scalars().all()

    @classmethod
    async def get_or_create_subscription(cls, db: AsyncSession, org_id: int) -> Subscription:
        """
        Retrieves subscription or creates an initial 14-day full TEAM trial for new organizations.
        """
        res = await db.execute(select(Subscription).where(Subscription.org_id == org_id))
        sub = res.scalars().first()
        if not sub:
            now = utcnow()
            sub = Subscription(
                org_id=org_id,
                plan_tier=PlanTier.TEAM.value,
                status=SubscriptionStatus.TRIALING.value,
                trial_start=now,
                trial_end=now + timedelta(days=14),
                current_period_start=now,
                current_period_end=now + timedelta(days=14),
                created_at=now,
                updated_at=now
            )
            db.add(sub)
            await db.commit()
            await db.refresh(sub)
        return sub

    @classmethod
    async def get_effective_subscription_and_plan(
        cls, db: AsyncSession, org_id: int
    ) -> Tuple[Subscription, SubscriptionPlan]:
        """
        Calculates effective plan, handling automated trial expiration degradation.
        """
        sub = await cls.get_or_create_subscription(db, org_id)
        now = utcnow()

        # Handle naive datetime from SQLite
        trial_end = sub.trial_end
        if trial_end and trial_end.tzinfo is None:
            trial_end = trial_end.replace(tzinfo=timezone.utc)

        # Automated trial expiry check
        if sub.status == SubscriptionStatus.TRIALING.value and trial_end and now > trial_end:
            sub.status = SubscriptionStatus.ACTIVE.value
            sub.plan_tier = PlanTier.COMMUNITY.value
            sub.updated_at = now
            await db.commit()
            await db.refresh(sub)

        plan = await cls.get_plan_by_tier(db, sub.plan_tier)
        return sub, plan

    @classmethod
    async def get_usage_summary(
        cls, db: AsyncSession, org_id: int
    ) -> Dict[str, int]:
        """Returns live counts: projects, members, and monthly AI tokens used."""
        proj_cnt_res = await db.execute(
            select(func.count(Project.id)).where(Project.org_id == org_id)
        )
        projects_count = proj_cnt_res.scalar() or 0

        mem_cnt_res = await db.execute(
            select(func.count(User.id)).where(User.org_id == org_id, User.is_active.is_(True))
        )
        members_count = mem_cnt_res.scalar() or 0

        # Current month usage meter
        month_key = utcnow().strftime("%Y-%m")
        meter_res = await db.execute(
            select(UsageMeter).where(UsageMeter.org_id == org_id, UsageMeter.period_month == month_key)
        )
        meter = meter_res.scalars().first()
        ai_tokens_used = meter.ai_tokens_used if meter else 0

        return {
            "projects_count": projects_count,
            "members_count": members_count,
            "ai_tokens_used": ai_tokens_used
        }

    @classmethod
    async def check_can_create_project(cls, db: AsyncSession, org_id: int) -> None:
        sub, plan = await cls.get_effective_subscription_and_plan(db, org_id)
        if plan.max_projects != -1:
            usage = await cls.get_usage_summary(db, org_id)
            if usage["projects_count"] >= plan.max_projects:
                raise HTTPException(
                    status_code=status.HTTP_402_PAYMENT_REQUIRED,
                    detail=f"Plan limit reached: '{plan.name}' allows up to {plan.max_projects} projects. Upgrade to Team or Enterprise to create additional projects."
                )

    @classmethod
    async def check_can_invite_member(cls, db: AsyncSession, org_id: int) -> None:
        sub, plan = await cls.get_effective_subscription_and_plan(db, org_id)
        if plan.max_members != -1:
            usage = await cls.get_usage_summary(db, org_id)
            if usage["members_count"] >= plan.max_members:
                raise HTTPException(
                    status_code=status.HTTP_402_PAYMENT_REQUIRED,
                    detail=f"Seat limit reached: '{plan.name}' allows up to {plan.max_members} team members. Upgrade to Team or Enterprise to add more seats."
                )

    @classmethod
    async def check_can_use_ai(cls, db: AsyncSession, org_id: int, estimated_tokens: int = 1000) -> None:
        sub, plan = await cls.get_effective_subscription_and_plan(db, org_id)
        if plan.monthly_ai_tokens != -1:
            usage = await cls.get_usage_summary(db, org_id)
            if usage["ai_tokens_used"] + estimated_tokens > plan.monthly_ai_tokens:
                raise HTTPException(
                    status_code=status.HTTP_402_PAYMENT_REQUIRED,
                    detail=f"Monthly AI token limit reached ({plan.monthly_ai_tokens:,} tokens). Upgrade your plan for higher monthly AI quotas."
                )

    @classmethod
    async def record_ai_usage(cls, db: AsyncSession, org_id: int, tokens_used: int) -> None:
        if not org_id or tokens_used <= 0:
            return
        month_key = utcnow().strftime("%Y-%m")
        meter_res = await db.execute(
            select(UsageMeter).where(UsageMeter.org_id == org_id, UsageMeter.period_month == month_key)
        )
        meter = meter_res.scalars().first()
        if not meter:
            meter = UsageMeter(
                org_id=org_id,
                period_month=month_key,
                ai_tokens_used=tokens_used,
                created_at=utcnow(),
                updated_at=utcnow()
            )
            db.add(meter)
        else:
            meter.ai_tokens_used += tokens_used
            meter.updated_at = utcnow()
        await db.commit()

    @classmethod
    async def check_can_generate_target(cls, db: AsyncSession, org_id: int, target: str) -> None:
        sub, plan = await cls.get_effective_subscription_and_plan(db, org_id)
        allowed = json.loads(plan.allowed_generators or '["*"]')
        if "*" not in allowed and target.lower() not in [t.lower() for t in allowed]:
            raise HTTPException(
                status_code=status.HTTP_402_PAYMENT_REQUIRED,
                detail=f"Code generation target '{target}' requires Team or Enterprise tier. Current plan: '{plan.name}'."
            )

    @classmethod
    async def check_can_export_traceability(cls, db: AsyncSession, org_id: int) -> None:
        sub, plan = await cls.get_effective_subscription_and_plan(db, org_id)
        if not plan.traceability_export:
            raise HTTPException(
                status_code=status.HTTP_402_PAYMENT_REQUIRED,
                detail=f"Traceability matrix export is reserved for Team and Enterprise tiers."
            )

    @classmethod
    async def activate_trial(cls, db: AsyncSession, org_id: int, days: int = 14) -> Subscription:
        sub = await cls.get_or_create_subscription(db, org_id)
        now = utcnow()
        sub.plan_tier = PlanTier.TEAM.value
        sub.status = SubscriptionStatus.TRIALING.value
        sub.trial_start = now
        sub.trial_end = now + timedelta(days=days)
        sub.current_period_start = now
        sub.current_period_end = now + timedelta(days=days)
        sub.updated_at = now
        await db.commit()
        await db.refresh(sub)
        return sub
