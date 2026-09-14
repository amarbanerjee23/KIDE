import hashlib
import secrets
from typing import List, Optional, Tuple
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy import select
from datetime import datetime, timezone

from app.models.api_token import APIToken
from app.models.user import User

def utcnow():
    return datetime.now(timezone.utc)

class APITokenService:
    @staticmethod
    def hash_token(raw_token: str) -> str:
        return hashlib.sha256(raw_token.encode("utf-8")).hexdigest()

    @classmethod
    async def create_token(
        cls,
        db: AsyncSession,
        user: User,
        name: str,
        scopes: List[str],
        expires_at: Optional[datetime] = None
    ) -> Tuple[str, APIToken]:
        """
        Creates a new scoped API token and returns the (raw_token, APIToken_model).
        The raw_token is only visible during creation.
        """
        random_part = secrets.token_hex(24)
        raw_token = f"kide_live_{random_part}"
        prefix = f"kide_live_{random_part[:8]}..."
        token_hash = cls.hash_token(raw_token)
        scopes_str = ",".join(scopes) if scopes else "read,write"

        api_token = APIToken(
            org_id=user.org_id,
            user_id=user.id,
            name=name,
            prefix=prefix,
            token_hash=token_hash,
            scopes=scopes_str,
            is_active=True,
            expires_at=expires_at,
            created_at=utcnow()
        )
        db.add(api_token)
        await db.commit()
        await db.refresh(api_token)
        return raw_token, api_token

    @classmethod
    async def verify_token(
        cls,
        db: AsyncSession,
        raw_token: str
    ) -> Optional[Tuple[User, APIToken]]:
        """
        Verifies a raw API token and returns (User, APIToken) if valid and not expired.
        """
        if not raw_token or not raw_token.startswith("kide_"):
            return None

        token_hash = cls.hash_token(raw_token)
        stmt = (
            select(APIToken, User)
            .join(User, APIToken.user_id == User.id)
            .where(APIToken.token_hash == token_hash, APIToken.is_active.is_(True))
        )
        res = await db.execute(stmt)
        record = res.first()
        if not record:
            return None

        api_token, user = record
        if api_token.expires_at and api_token.expires_at.tzinfo is None:
            # handle naive datetime from SQLite/PostgreSQL
            now = datetime.utcnow()
        else:
            now = utcnow()

        if api_token.expires_at and now > api_token.expires_at:
            return None

        # Update last used timestamp
        try:
            api_token.last_used_at = utcnow()
            await db.commit()
        except Exception:
            pass

        return user, api_token

    @classmethod
    async def revoke_token(
        cls,
        db: AsyncSession,
        token_id: int,
        org_id: int
    ) -> bool:
        stmt = select(APIToken).where(APIToken.id == token_id, APIToken.org_id == org_id)
        res = await db.execute(stmt)
        token = res.scalars().first()
        if not token:
            return False
        token.is_active = False
        await db.commit()
        return True

