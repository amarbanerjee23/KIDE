from sqlalchemy import Column, Integer, String, Boolean, DateTime, Text
from datetime import datetime, timezone
import json
from app.database import Base

def utcnow():
    return datetime.now(timezone.utc)

class FeatureFlag(Base):
    __tablename__ = "feature_flags"

    id = Column(Integer, primary_key=True, index=True, autoincrement=True)
    key = Column(String(100), unique=True, index=True, nullable=False)
    name = Column(String(150), nullable=False)
    description = Column(Text, nullable=True)
    is_enabled = Column(Boolean, default=True, nullable=False)
    minimum_tier = Column(String(50), default="community", nullable=False) # community, team, enterprise
    allowed_org_ids = Column(Text, default="[]", nullable=False) # JSON array of tenant IDs
    rollout_percentage = Column(Integer, default=100, nullable=False) # 0 to 100
    created_at = Column(DateTime(timezone=True), default=utcnow, nullable=False)
    updated_at = Column(DateTime(timezone=True), default=utcnow, onupdate=utcnow, nullable=False)

    def get_allowed_org_ids(self) -> list[int]:
        try:
            return json.loads(self.allowed_org_ids or "[]")
        except Exception:
            return []

    def set_allowed_org_ids(self, org_ids: list[int]) -> None:
        self.allowed_org_ids = json.dumps(org_ids or [])
