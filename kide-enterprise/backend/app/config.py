from typing import List
from pydantic_settings import BaseSettings

class Settings(BaseSettings):
    DATABASE_URL: str = "sqlite+aiosqlite:///./kide_data.db"
    SECRET_KEY: str = "secret-key-for-dev-only"
    ALGORITHM: str = "HS256"
    ACCESS_TOKEN_EXPIRE_MINUTES: int = 30
    REFRESH_TOKEN_EXPIRE_DAYS: int = 7
    CORS_ORIGINS: List[str] = ["*"]
    DEBUG: bool = True
    UPLOAD_DIR: str = "./uploads"
    EXPORT_DIR: str = "./exports"

    class Config:
        env_file = ".env"

settings = Settings()
