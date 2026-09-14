import json
import hashlib
import logging
from typing import List, Dict, Optional, Any
from datetime import datetime, timezone
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy import select

from app.models.feature_flag import FeatureFlag
from app.models.subscription import PlanTier
from app.schemas.admin import FeatureFlagCreate, FeatureFlagUpdate

logger = logging.getLogger("kide.feature_flags")

def utcnow():
    return datetime.now(timezone.utc)

TIER_WEIGHTS = {
    PlanTier.COMMUNITY.value: 1,
    "community": 1,
    PlanTier.TEAM.value: 2,
    "team": 2,
    PlanTier.ENTERPRISE.value: 3,
    "enterprise": 3,
}

DEFAULT_FEATURE_FLAGS = [
    {
        "key": "deep_semantic_validator",
        "name": "Deep Semantic Model Validator",
        "description": "Deterministic multi-file semantic validation, detecting deadlocks, type hazards, and protocol mismatches.",
        "is_enabled": True,
        "minimum_tier": "community",
        "allowed_org_ids": [],
        "rollout_percentage": 100
    },
    {
        "key": "ai_copilot_v2",
        "name": "Real AI Engineering Copilot",
        "description": "Provider-neutral natural language requirements synthesis and contextual patch review engine.",
        "is_enabled": True,
        "minimum_tier": "community",
        "allowed_org_ids": [],
        "rollout_percentage": 100
    },
    {
        "key": "traceability_blast_radius",
        "name": "Traceability Matrix & Blast Radius Engine",
        "description": "Stage 1-5 Bidirectional Traceability and BFS change impact blast-radius traversal.",
        "is_enabled": True,
        "minimum_tier": "team",
        "allowed_org_ids": [],
        "rollout_percentage": 100
    },
    {
        "key": "ros2_workspace_generator",
        "name": "ROS2 Workspace Code Generator",
        "description": "Automated deterministic synthesis of ROS2 nodes, CMakeLists, and pub/sub message bridges.",
        "is_enabled": True,
        "minimum_tier": "team",
        "allowed_org_ids": [],
        "rollout_percentage": 100
    },
    {
        "key": "iec61499_xml_export",
        "name": "IEC 61499 Automation XML Generator",
        "description": "Deterministic industrial control FB network synthesis and XML package generation.",
        "is_enabled": True,
        "minimum_tier": "team",
        "allowed_org_ids": [],
        "rollout_percentage": 100
    },
    {
        "key": "enterprise_audit_stream",
        "name": "Real-time SIEM Audit Log Streaming",
        "description": "Cryptographically verified immutable audit events streaming to external SIEM / webhooks.",
        "is_enabled": True,
        "minimum_tier": "enterprise",
        "allowed_org_ids": [],
        "rollout_percentage": 100
    }
]

class FeatureFlagService:
    """
    Enterprise Feature Flagging & Progressive Rollout Engine.
    Handles global kill-switches, tier gates, tenant whitelists, and deterministic percentage rollouts.
    """

    @classmethod
    async def seed_flags_if_needed(cls, db: AsyncSession) -> None:
        res = await db.execute(select(FeatureFlag))
        existing = {f.key: f for f in res.scalars().all()}

        for flag_def in DEFAULT_FEATURE_FLAGS:
            if flag_def["key"] not in existing:
                flag = FeatureFlag(
                    key=flag_def["key"],
                    name=flag_def["name"],
                    description=flag_def["description"],
                    is_enabled=flag_def["is_enabled"],
                    minimum_tier=flag_def["minimum_tier"],
                    allowed_org_ids=json.dumps(flag_def["allowed_org_ids"]),
                    rollout_percentage=flag_def["rollout_percentage"],
                    created_at=utcnow(),
                    updated_at=utcnow()
                )
                db.add(flag)
        await db.commit()

    @classmethod
    def evaluate_flag(cls, flag: FeatureFlag, org_id: Optional[int], org_tier: str) -> bool:
        # 1. Global kill switch
        if not flag.is_enabled:
            return False

        # 2. Explicit tenant whitelist (bypass tier and percentage)
        allowed_orgs = flag.get_allowed_org_ids()
        if org_id is not None and org_id in allowed_orgs:
            return True

        # 3. Minimum tier gate
        user_weight = TIER_WEIGHTS.get(org_tier.lower(), 1)
        required_weight = TIER_WEIGHTS.get(flag.minimum_tier.lower(), 1)
        if user_weight < required_weight:
            return False

        # 4. Percentage rollout
        if flag.rollout_percentage >= 100:
            return True
        if flag.rollout_percentage <= 0:
            return False

        # Deterministic hash based on org_id + flag key
        identifier = f"{flag.key}:{org_id or 0}"
        digest = hashlib.md5(identifier.encode("utf-8")).hexdigest()
        bucket = int(digest[:8], 16) % 100
        return bucket < flag.rollout_percentage

    @classmethod
    async def get_client_flags(
        cls, db: AsyncSession, org_id: Optional[int], org_tier: str
    ) -> Dict[str, bool]:
        await cls.seed_flags_if_needed(db)
        res = await db.execute(select(FeatureFlag))
        flags = res.scalars().all()

        results: Dict[str, bool] = {}
        for f in flags:
            results[f.key] = cls.evaluate_flag(f, org_id, org_tier)
        return results

    @classmethod
    async def list_flags(cls, db: AsyncSession) -> List[FeatureFlag]:
        await cls.seed_flags_if_needed(db)
        res = await db.execute(select(FeatureFlag).order_by(FeatureFlag.id.asc()))
        return list(res.scalars().all())

    @classmethod
    async def create_flag(cls, db: AsyncSession, data: FeatureFlagCreate) -> FeatureFlag:
        existing_res = await db.execute(select(FeatureFlag).where(FeatureFlag.key == data.key.strip()))
        if existing_res.scalars().first():
            raise ValueError(f"Feature flag with key '{data.key}' already exists")

        flag = FeatureFlag(
            key=data.key.strip(),
            name=data.name.strip(),
            description=data.description,
            is_enabled=data.is_enabled,
            minimum_tier=data.minimum_tier,
            allowed_org_ids=json.dumps(data.allowed_org_ids or []),
            rollout_percentage=max(0, min(100, data.rollout_percentage)),
            created_at=utcnow(),
            updated_at=utcnow()
        )
        db.add(flag)
        await db.commit()
        await db.refresh(flag)
        return flag

    @classmethod
    async def update_flag(cls, db: AsyncSession, flag_id: int, data: FeatureFlagUpdate) -> FeatureFlag:
        res = await db.execute(select(FeatureFlag).where(FeatureFlag.id == flag_id))
        flag = res.scalars().first()
        if not flag:
            raise ValueError(f"Feature flag #{flag_id} not found")

        if data.name is not None:
            flag.name = data.name.strip()
        if data.description is not None:
            flag.description = data.description
        if data.is_enabled is not None:
            flag.is_enabled = data.is_enabled
        if data.minimum_tier is not None:
            flag.minimum_tier = data.minimum_tier
        if data.allowed_org_ids is not None:
            flag.allowed_org_ids = json.dumps(data.allowed_org_ids)
        if data.rollout_percentage is not None:
            flag.rollout_percentage = max(0, min(100, data.rollout_percentage))

        flag.updated_at = utcnow()
        await db.commit()
        await db.refresh(flag)
        return flag

    @classmethod
    async def delete_flag(cls, db: AsyncSession, flag_id: int) -> bool:
        res = await db.execute(select(FeatureFlag).where(FeatureFlag.id == flag_id))
        flag = res.scalars().first()
        if not flag:
            return False
        await db.delete(flag)
        await db.commit()
        return True

