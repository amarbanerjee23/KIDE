from typing import List, Union
from pydantic_settings import BaseSettings, SettingsConfigDict
from pydantic import field_validator

class Settings(BaseSettings):
    DATABASE_URL: str = "sqlite+aiosqlite:///./kide_data.db"
    SECRET_KEY: str = "secret-key-for-dev-only"
    ALGORITHM: str = "HS256"
    ACCESS_TOKEN_EXPIRE_MINUTES: int = 30
    REFRESH_TOKEN_EXPIRE_DAYS: int = 7
    CORS_ORIGINS: List[str] = ["*"]
    DEBUG: Union[bool, str] = True
    UPLOAD_DIR: str = "./uploads"
    EXPORT_DIR: str = "./exports"

    @field_validator("DEBUG", mode="before")
    @classmethod
    def parse_debug(cls, v):
        if isinstance(v, str):
            if v.lower() in ("true", "1", "yes", "on"):
                return True
            return False
        return bool(v)

    model_config = SettingsConfigDict(env_file=".env", extra="ignore")

settings = Settings()
