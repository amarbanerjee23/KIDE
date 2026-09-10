from fastapi import APIRouter
from pydantic import BaseModel
from typing import Dict, Any, List
from ..services.validator import validate_model

router = APIRouter(prefix="/validate", tags=["validate"])

class ValidateRequest(BaseModel):
    model: Dict[str, Any]
    kb: Dict[str, Any] = {}

class ValidateResponse(BaseModel):
    errors: list
    warnings: list

@router.post("", response_model=ValidateResponse)
def validate(req: ValidateRequest):
    res = validate_model(req.model, req.kb)
    return res

class FileItem(BaseModel):
    id: str
    name: str
    content: str
    language: str

class SemanticValidationRequest(BaseModel):
    files: List[FileItem]

class ValidationErrorItem(BaseModel):
    fileId: str
    message: str

@router.post("/semantic", response_model=List[ValidationErrorItem])
def validate_semantic(req: SemanticValidationRequest):
    # Semantic validation stub - returns empty errors when clean
    return []
