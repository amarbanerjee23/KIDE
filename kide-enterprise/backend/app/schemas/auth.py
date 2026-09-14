from typing import Optional
from datetime import datetime
from pydantic import BaseModel, ConfigDict

try:
    import email_validator  # noqa: F401
    from pydantic import EmailStr
except (ImportError, ModuleNotFoundError):
    # Resilient fallback if email-validator package is not installed in bare runtime environments
    EmailStr = str  # type: ignore

class UserCreate(BaseModel):
    email: EmailStr
    password: str
    full_name: str
    org_name: Optional[str] = None

class UserLogin(BaseModel):
    email: EmailStr
    password: str

class Token(BaseModel):
    access_token: str
    refresh_token: str
    token_type: str

class UserResponse(BaseModel):
    id: int
    email: EmailStr
    full_name: str
    role: str
    org_name: Optional[str] = None
    created_at: datetime
    model_config = ConfigDict(from_attributes=True)

