from fastapi import APIRouter, HTTPException
from pydantic import BaseModel
from typing import Dict, Any

from ..parsers import dml_parser, operation_parser, capability_parser, activity_parser, mnc_parser

router = APIRouter(prefix="/parse", tags=["parse"])

class ParseRequest(BaseModel):
    content: str

class ParseResponse(BaseModel):
    ast: Dict[str, Any]
    errors: list = []

@router.post("/{language}", response_model=ParseResponse)
def parse_dsl(language: str, req: ParseRequest):
    try:
        if language == "dml":
            ast = dml_parser.parse(req.content)
        elif language == "operation":
            ast = operation_parser.parse(req.content)
        elif language == "capability":
            ast = capability_parser.parse(req.content)
        elif language == "activity":
            ast = activity_parser.parse(req.content)
        elif language == "mnc":
            ast = mnc_parser.parse(req.content)
        else:
            raise HTTPException(status_code=400, detail="Unsupported language")
            
        return ParseResponse(ast=ast, errors=[])
    except Exception as e:
        return ParseResponse(ast={}, errors=[str(e)])
