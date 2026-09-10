from pydantic import BaseModel, ConfigDict
from typing import Optional, List
from datetime import datetime

class ProjectCreate(BaseModel):
    name: str
    description: Optional[str] = None

class ProjectFromTemplateCreate(BaseModel):
    name: str
    template: str
    description: Optional[str] = None

class ProjectUpdate(BaseModel):
    name: Optional[str] = None
    description: Optional[str] = None

class ProjectResponse(BaseModel):
    model_config = ConfigDict(from_attributes=True)
    id: int
    name: str
    description: Optional[str] = None
    created_by_name: str
    file_count: int
    created_at: datetime
    updated_at: datetime

class ProjectFileCreate(BaseModel):
    filename: str
    file_type: str
    content: str

class ProjectFileUpdate(BaseModel):
    filename: Optional[str] = None
    file_type: Optional[str] = None
    content: Optional[str] = None

class ProjectFileResponse(BaseModel):
    model_config = ConfigDict(from_attributes=True)
    id: int
    filename: str
    file_type: str
    version: int
    created_at: datetime
    updated_at: datetime

class ProjectFileContent(ProjectFileResponse):
    model_config = ConfigDict(from_attributes=True)
    content: str

