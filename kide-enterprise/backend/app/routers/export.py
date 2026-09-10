from fastapi import APIRouter, Response, Body
from typing import Dict, Any
from ..services.exporter import export_format

router = APIRouter(prefix="/export", tags=["export"])

@router.post("/{fmt}")
@router.post("/{fmt}/")
async def export_model(fmt: str, payload: Dict[str, Any] = Body(...)):
    model = payload.get("model") if "model" in payload and isinstance(payload["model"], dict) and "name" in payload["model"] else payload
    content = export_format(model, fmt)
    return Response(content=content, media_type="text/plain")
