from pydantic import BaseModel
from typing import List, Optional, Dict, Any
from datetime import datetime

class TransformRequest(BaseModel):
    activity_diagram: Dict[str, Any]
    knowledge_base: Optional[Dict[str, Any]] = None

class TransformResponse(BaseModel):
    model: Dict[str, Any]
    warnings: List[str]
    validation_errors: List[str]

class TransformHistoryResponse(BaseModel):
    id: int
    status: str
    created_at: datetime
    input_summary: str
    class Config:
        from_attributes = True
