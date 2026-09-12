from fastapi import APIRouter
from pydantic import BaseModel
from typing import Dict, Any, List
from fastapi import APIRouter, Body
from pydantic import BaseModel, Field
from typing import Dict, Any, List, Optional, Union
from ..services.validator import validate_model
from ..services.semantic_validator import validate_project_semantics
from ..schemas.engineering_ir import ProjectIR

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
    id: Optional[str] = None
    name: Optional[str] = None
    filename: Optional[str] = None
    content: str = ""
    language: Optional[str] = None

class SemanticValidationRequest(BaseModel):
    files: List[FileItem]

class ValidationErrorItem(BaseModel):
    fileId: str
    fileId: str = ""
    filename: str = ""
    line: Optional[int] = 1
    column: Optional[int] = 1
    message: str
    severity: str = "error"
    ruleId: str = "SEM-000"
    symbol: Optional[str] = None
    suggestion: Optional[str] = None
    quickFix: Optional[str] = None

@router.post("/semantic", response_model=List[ValidationErrorItem])
def validate_semantic(req: SemanticValidationRequest):
    # Semantic validation stub - returns empty errors when clean
    return []
def validate_semantic(payload: Any = Body(...)):
    """
    Executes multi-file canonical semantic validation across DSL files.
    Accepts either {"files": [...]} or [...] directly.
    """
    raw_files = []
    if isinstance(payload, dict):
        if "files" in payload and isinstance(payload["files"], list):
            raw_files = payload["files"]
        else:
            # Map of filename -> content
            raw_files = [{"id": k, "name": k, "content": str(v)} for k, v in payload.items()]
    elif isinstance(payload, list):
        raw_files = payload

    # Normalize file items to dicts
    file_dicts = []
    for f in raw_files:
        if isinstance(f, dict):
            fid = f.get("id") or f.get("fileId") or f.get("filename") or f.get("name") or "unnamed"
            fname = f.get("name") or f.get("filename") or f.get("id") or "unnamed"
            cnt = f.get("content", "")
            lang = f.get("language", "")
            file_dicts.append({"id": str(fid), "name": str(fname), "content": str(cnt), "language": str(lang)})
        elif hasattr(f, "model_dump"):
            dump = f.model_dump()
            fid = dump.get("id") or dump.get("name") or "unnamed"
            fname = dump.get("name") or dump.get("filename") or "unnamed"
            file_dicts.append({"id": str(fid), "name": str(fname), "content": str(dump.get("content", "")), "language": str(dump.get("language", ""))})

    _, diagnostics = validate_project_semantics(file_dicts)

    results = []
    for d in diagnostics:
        results.append(
            ValidationErrorItem(
                fileId=d.file_id or d.fileId or d.filename or "",
                filename=d.filename or "",
                line=d.line or 1,
                column=d.column or 1,
                message=d.message,
                severity=d.severity.value if hasattr(d.severity, "value") else str(d.severity),
                ruleId=d.rule_id or d.ruleId or "SEM-000",
                symbol=d.symbol,
                suggestion=d.suggestion,
                quickFix=d.quick_fix or d.quickFix
            )
        )
    return results

@router.post("/ir")
def compile_ir_endpoint(payload: Any = Body(...)):
    """
    Compiles and returns the full Project Canonical Engineering IR and Symbol Table.
    """
    raw_files = []
    if isinstance(payload, dict) and "files" in payload:
        raw_files = payload["files"]
    elif isinstance(payload, list):
        raw_files = payload
    else:
        raw_files = [{"id": k, "name": k, "content": str(v)} for k, v in payload.items()] if isinstance(payload, dict) else []

    file_dicts = []
    for f in raw_files:
        if isinstance(f, dict):
            fid = f.get("id") or f.get("name") or "unnamed"
            fname = f.get("name") or f.get("filename") or "unnamed"
            cnt = f.get("content", "")
            file_dicts.append({"id": str(fid), "name": str(fname), "content": str(cnt)})

    ir, diagnostics = validate_project_semantics(file_dicts)
    return {
        "ir": ir.model_dump(),
        "summary": ir.summary.model_dump(),
        "diagnostics": [d.model_dump() for d in diagnostics]
    }
