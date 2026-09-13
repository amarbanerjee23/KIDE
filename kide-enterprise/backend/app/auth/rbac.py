from enum import Enum
from typing import Set
from fastapi import HTTPException, status
from app.models.user import User

class UserRole(str, Enum):
    OWNER = "owner"
    ADMIN = "admin"
    ENGINEER = "engineer"
    VIEWER = "viewer"

ROLE_WEIGHTS = {
    UserRole.VIEWER.value: 10,
    "viewer": 10,
    UserRole.ENGINEER.value: 20,
    "engineer": 20,
    "member": 20, # legacy alias for engineer
    UserRole.ADMIN.value: 30,
    "admin": 30,
    UserRole.OWNER.value: 40,
    "owner": 40,
}

class Permission(str, Enum):
    # Project permissions
    PROJECT_READ = "project:read"
    PROJECT_CREATE = "project:create"
    PROJECT_EDIT = "project:edit"
    PROJECT_DELETE = "project:delete"
    PROJECT_EXPORT = "project:export"

    # Engineering permissions
    MODEL_VALIDATE = "model:validate"
    MODEL_SYNTHESIZE = "model:synthesize"
    MODEL_SIMULATE = "model:simulate"
    CODE_GENERATE = "code:generate"
    PATCH_APPLY = "patch:apply"

    # Organization & Member management
    ORG_READ = "org:read"
    ORG_MANAGE_MEMBERS = "org:manage_members"
    ORG_CHANGE_ROLES = "org:change_roles"
    ORG_VIEW_AUDIT_LOGS = "org:view_audit_logs"
    ORG_MANAGE_API_TOKENS = "org:manage_api_tokens"
    ORG_BILLING = "org:billing"
    PLATFORM_ADMIN = "platform:admin"

ROLE_PERMISSIONS: dict[str, Set[Permission]] = {
    UserRole.VIEWER.value: {
        Permission.PROJECT_READ,
        Permission.ORG_READ,
    },
    UserRole.ENGINEER.value: {
        Permission.PROJECT_READ,
        Permission.PROJECT_CREATE,
        Permission.PROJECT_EDIT,
        Permission.PROJECT_EXPORT,
        Permission.MODEL_VALIDATE,
        Permission.MODEL_SYNTHESIZE,
        Permission.MODEL_SIMULATE,
        Permission.CODE_GENERATE,
        Permission.PATCH_APPLY,
        Permission.ORG_READ,
    },
    UserRole.ADMIN.value: {
        Permission.PROJECT_READ,
        Permission.PROJECT_CREATE,
        Permission.PROJECT_EDIT,
        Permission.PROJECT_DELETE,
        Permission.PROJECT_EXPORT,
        Permission.MODEL_VALIDATE,
        Permission.MODEL_SYNTHESIZE,
        Permission.MODEL_SIMULATE,
        Permission.CODE_GENERATE,
        Permission.PATCH_APPLY,
        Permission.ORG_READ,
        Permission.ORG_MANAGE_MEMBERS,
        Permission.ORG_VIEW_AUDIT_LOGS,
        Permission.ORG_MANAGE_API_TOKENS,
    },
    UserRole.OWNER.value: set(Permission),
}

# Legacy alias
ROLE_PERMISSIONS["member"] = ROLE_PERMISSIONS[UserRole.ENGINEER.value]

def normalize_role(role: str) -> str:
    r = (role or "engineer").lower()
    if r == "member":
        return UserRole.ENGINEER.value
    return r

def check_has_permission(user: User, permission: Permission) -> bool:
    norm_role = normalize_role(user.role)
    perms = ROLE_PERMISSIONS.get(norm_role, set())
    return permission in perms

def check_min_role(user: User, min_role: UserRole) -> bool:
    user_weight = ROLE_WEIGHTS.get((user.role or "").lower(), 10)
    min_weight = ROLE_WEIGHTS.get(min_role.value if isinstance(min_role, UserRole) else str(min_role).lower(), 20)
    return user_weight >= min_weight

def verify_tenant_access(user: User, resource_org_id: int, resource_name: str = "Resource") -> None:
    if user.org_id != resource_org_id:
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail=f"Access denied: {resource_name} belongs to another organization"
        )

