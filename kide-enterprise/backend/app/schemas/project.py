from pydantic import BaseModel
from typing import Optional, List
from datetime import datetime

class ProjectCreate(BaseModel):
    name: str
    description: Optional[str] = None

class ProjectUpdate(BaseModel):
    name: Optional[str] = None
    description: Optional[str] = None

class ProjectResponse(BaseModel):
    id: int
    name: str
    description: Optional[str] = None
    created_by_name: str
    file_count: int
    created_at: datetime
    updated_at: datetime
    class Config:
        from_attributes = True

class ProjectFileCreate(BaseModel):
    filename: str
    file_type: str
    content: str

class ProjectFileResponse(BaseModel):
    id: int
    filename: str
    file_type: str
    version: int
    created_at: datetime
    updated_at: datetime
    class Config:
        from_attributes = True

class ProjectFileContent(ProjectFileResponse):
    content: str
