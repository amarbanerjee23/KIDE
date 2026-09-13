from typing import List, Optional
from pydantic import BaseModel, ConfigDict
from datetime import datetime
from app.auth.rbac import UserRole

class MemberResponse(BaseModel):
    id: int
    email: str
    full_name: Optional[str] = None
    role: str
    is_active: bool
    created_at: Optional[datetime] = None

    model_config = ConfigDict(from_attributes=True)

class InviteMemberRequest(BaseModel):
    email: str
    full_name: str
    role: UserRole = UserRole.ENGINEER
    password: Optional[str] = None

class UpdateMemberRoleRequest(BaseModel):
    role: UserRole

class AuditLogResponse(BaseModel):
    id: int
    org_id: int
    actor_id: Optional[int] = None
    actor_email: Optional[str] = None
    action: str
    target_type: Optional[str] = None
    target_id: Optional[str] = None
    details: Optional[str] = None
    ip_address: Optional[str] = None
    created_at: Optional[datetime] = None

    model_config = ConfigDict(from_attributes=True)

class CreateAPITokenRequest(BaseModel):
    name: str
    scopes: List[str] = ["read", "write"]
    expires_days: Optional[int] = 30

class CreateAPITokenResponse(BaseModel):
    id: int
    name: str
    prefix: str
    raw_token: str
    scopes: str
    expires_at: Optional[datetime] = None
    created_at: Optional[datetime] = None

class APITokenResponse(BaseModel):
    id: int
    name: str
    prefix: str
    scopes: str
    is_active: bool
    expires_at: Optional[datetime] = None
    created_at: Optional[datetime] = None
    last_used_at: Optional[datetime] = None

    model_config = ConfigDict(from_attributes=True)

class OrganizationDetailsResponse(BaseModel):
    id: int
    name: str
    slug: str
    created_at: Optional[datetime] = None
    members_count: int = 0
    projects_count: int = 0

    model_config = ConfigDict(from_attributes=True)

