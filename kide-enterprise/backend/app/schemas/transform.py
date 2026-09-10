from pydantic import BaseModel
from typing import Any, Dict

class TransformRequest(BaseModel):
    text: str
    dsl_type: str

class TransformResponse(BaseModel):
    ast: Dict[str, Any]
