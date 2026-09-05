from fastapi import APIRouter, Depends
from typing import Dict, Any
from app.models.user import User
from app.auth.dependencies import get_current_user
from app.services.exporter import export_json, export_mnc_dsl, export_python_stub
from fastapi.responses import PlainTextResponse

router = APIRouter()

@router.post("/json")
async def do_export_json(model: Dict[str, Any], current_user: User = Depends(get_current_user)):
    return PlainTextResponse(export_json(model), media_type="application/json")

@router.post("/dsl")
async def do_export_dsl(model: Dict[str, Any], current_user: User = Depends(get_current_user)):
    return PlainTextResponse(export_mnc_dsl(model), media_type="text/plain")

@router.post("/python")
async def do_export_python(model: Dict[str, Any], current_user: User = Depends(get_current_user)):
    return PlainTextResponse(export_python_stub(model), media_type="text/plain")
