from fastapi import APIRouter, HTTPException, Request
from pydantic import BaseModel
from typing import Dict, Any, Optional

from ..parsers import dml_parser, operation_parser, capability_parser, activity_parser, mnc_parser

router = APIRouter(prefix="/parse", tags=["parse"])

class ParseRequest(BaseModel):
    content: str

class ParseResponse(BaseModel):
    ast: Dict[str, Any]
    errors: list = []

def normalize_language(lang: str) -> str:
    cleaned = lang.lower().lstrip(".").strip()
    if cleaned in ("activity", "activitydsl"):
        return "activity"
    if cleaned in ("mnc", "mncml"):
        return "mnc"
    if cleaned in ("dml", "dmldsl"):
        return "dml"
    if cleaned in ("capability", "capabilitydsl", "cap"):
        return "capability"
    if cleaned in ("operation", "operationdsl", "op"):
        return "operation"
    return cleaned

@router.post("/{language}", response_model=ParseResponse)
async def parse_dsl(language: str, request: Request, req: Optional[ParseRequest] = None):
    # Support both JSON ParseRequest and raw text body
    content = ""
    if req and req.content:
        content = req.content
    else:
        try:
            body = await request.body()
            text = body.decode("utf-8")
            if text:
                import json
                try:
                    data = json.loads(text)
                    if isinstance(data, dict) and "content" in data:
                        content = data["content"]
                    else:
                        content = text
                except Exception:
                    content = text
        except Exception:
            pass

    norm_lang = normalize_language(language)
    try:
        if norm_lang == "dml":
            ast = dml_parser.parse(content)
        elif norm_lang == "operation":
            ast = operation_parser.parse(content)
        elif norm_lang == "capability":
            ast = capability_parser.parse(content)
        elif norm_lang == "activity":
            ast = activity_parser.parse(content)
        elif norm_lang == "mnc":
            ast = mnc_parser.parse(content)
        else:
            raise HTTPException(status_code=400, detail=f"Unsupported language: {language}")
            
        return ParseResponse(ast=ast, errors=[])
    except HTTPException:
        raise
    except Exception as e:
        return ParseResponse(ast={}, errors=[str(e)])

