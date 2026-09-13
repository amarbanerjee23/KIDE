from sqlalchemy import Column, Integer, String, Boolean, ForeignKey, DateTime, Text
from sqlalchemy.orm import relationship
from datetime import datetime, timezone
from enum import Enum
from app.database import Base

def utcnow():
    return datetime.now(timezone.utc)

class PlanTier(str, Enum):
    COMMUNITY = "community"
    TEAM = "team"
    ENTERPRISE = "enterprise"

class SubscriptionStatus(str, Enum):
    TRIALING = "trialing"
    ACTIVE = "active"
    PAST_DUE = "past_due"
    CANCELED = "canceled"
    UNPAID = "unpaid"

class SubscriptionPlan(Base):
    __tablename__ = "subscription_plans"

    id = Column(Integer, primary_key=True, index=True)
    tier = Column(String, unique=True, index=True, nullable=False) # community, team, enterprise
    name = Column(String, nullable=False)
    description = Column(String, nullable=True)
    price_monthly_cents = Column(Integer, default=0)
    price_annual_cents = Column(Integer, default=0)
    max_projects = Column(Integer, default=3) # -1 = unlimited
    max_members = Column(Integer, default=2)  # -1 = unlimited
    monthly_ai_tokens = Column(Integer, default=50000) # -1 = unlimited
    allowed_generators = Column(Text, default='["python"]') # JSON list or ["*"]
    traceability_export = Column(Boolean, default=False)
    audit_retention_days = Column(Integer, default=7)
    is_active = Column(Boolean, default=True)

class Subscription(Base):
    __tablename__ = "subscriptions"

    id = Column(Integer, primary_key=True, index=True)
    org_id = Column(Integer, ForeignKey("organizations.id"), unique=True, index=True, nullable=False)
    plan_tier = Column(String, default=PlanTier.COMMUNITY.value, nullable=False)
    status = Column(String, default=SubscriptionStatus.ACTIVE.value, nullable=False)
    stripe_customer_id = Column(String, index=True, nullable=True)
    stripe_subscription_id = Column(String, index=True, nullable=True)
    current_period_start = Column(DateTime, default=datetime.utcnow)
    current_period_end = Column(DateTime, default=datetime.utcnow)
    trial_start = Column(DateTime, nullable=True)
    trial_end = Column(DateTime, nullable=True)
    cancel_at_period_end = Column(Boolean, default=False)
    created_at = Column(DateTime, default=datetime.utcnow)
    updated_at = Column(DateTime, default=datetime.utcnow)

    organization = relationship("Organization")

class UsageMeter(Base):
    __tablename__ = "usage_meters"

    id = Column(Integer, primary_key=True, index=True)
    org_id = Column(Integer, ForeignKey("organizations.id"), index=True, nullable=False)
    period_month = Column(String, index=True, nullable=False) # e.g. "2026-09"
    ai_tokens_used = Column(Integer, default=0)
    simulations_run = Column(Integer, default=0)
    syntheses_run = Column(Integer, default=0)
    created_at = Column(DateTime, default=datetime.utcnow)
    updated_at = Column(DateTime, default=datetime.utcnow)

class WebhookEvent(Base):
    __tablename__ = "webhook_events"

    id = Column(Integer, primary_key=True, index=True)
    event_id = Column(String, unique=True, index=True, nullable=False)
    event_type = Column(String, index=True, nullable=False)
    processed_at = Column(DateTime, default=datetime.utcnow)
    payload = Column(Text, nullable=True)

