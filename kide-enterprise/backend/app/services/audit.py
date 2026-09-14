import json
import logging
from typing import Any, Optional
from sqlalchemy.ext.asyncio import AsyncSession
from datetime import datetime, timezone

from app.models.audit_log import AuditLog
from app.models.user import User

logger = logging.getLogger("kide.audit")

def utcnow():
    return datetime.now(timezone.utc)

class AuditService:
    @staticmethod
    async def log(
        db: AsyncSession,
        org_id: int,
        action: str,
        actor: Optional[User] = None,
        target_type: Optional[str] = None,
        target_id: Optional[str] = None,
        details: Optional[Any] = None,
        ip_address: Optional[str] = None
    ) -> Optional[AuditLog]:
        """
        Records an immutable audit event to the audit_logs table.
        """
        try:
            details_str = None
            if details is not None:
                if isinstance(details, (dict, list)):
                    details_str = json.dumps(details)
                else:
                    details_str = str(details)

            entry = AuditLog(
                org_id=org_id,
                actor_id=actor.id if actor else None,
                actor_email=actor.email if actor else "system",
                action=action,
                target_type=target_type,
                target_id=str(target_id) if target_id is not None else None,
                details=details_str,
                ip_address=ip_address,
                created_at=utcnow()
            )
            db.add(entry)
            await db.commit()
            await db.refresh(entry)
            return entry
        except Exception as e:
            logger.error(f"Failed to record audit log: {e}", exc_info=True)
            return None

