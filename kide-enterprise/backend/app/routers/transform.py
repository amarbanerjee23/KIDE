import json
from fastapi import APIRouter, Depends, HTTPException
from pydantic import BaseModel
from typing import Dict, Any, Optional
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy import select
from ..services.transformer import transform_workspace, transform_activity_to_mnc
from ..auth.dependencies import get_db, get_current_user, get_current_user_optional
from ..models.user import User
from ..models.transform import TransformHistory

router = APIRouter(prefix="/transform", tags=["transform"])

class TransformRequest(BaseModel):
    workspace: Optional[Dict[str, str]] = None
    activity_diagram: Optional[Dict[str, Any]] = None
    knowledge_base: Optional[Dict[str, Any]] = None
    name: Optional[str] = None
    activities: Optional[list] = None

@router.post("")
@router.post("/")
async def transform(
    req: TransformRequest,
    current_user: Optional[User] = Depends(get_current_user_optional),
    db: Optional[AsyncSession] = Depends(get_db)
):
    if req.workspace:
        res = transform_workspace(req.workspace)
    elif req.activity_diagram:
        res = transform_activity_to_mnc(req.activity_diagram, req.knowledge_base or {})
    elif req.name and req.activities is not None:
        # User passed the activity diagram directly at root of request
        res = transform_activity_to_mnc(req.model_dump(), req.knowledge_base or {})
    else:
        raise HTTPException(status_code=400, detail="No activity_diagram or workspace provided")

        
    if "error" in res and not res.get("validation_errors"):
        raise HTTPException(status_code=400, detail={"errors": [res["error"]]})
        
    mnc_model = res.get("model") or res.get("mnc_model") or res
    warnings = res.get("warnings", [])
    validation_errors = res.get("validation_errors", []) or res.get("errors", [])
    
    if validation_errors and not mnc_model.get("name"):
        raise HTTPException(status_code=400, detail={"errors": validation_errors})

    if current_user and db:
        try:
            history_entry = TransformHistory(
                user_id=current_user.id,
                input_json=json.dumps(req.workspace or req.activity_diagram or {}),
                output_json=json.dumps(mnc_model),
                status="success" if not validation_errors else "error",
                errors="; ".join(validation_errors) if validation_errors else None
            )
            db.add(history_entry)
            await db.commit()
        except Exception:
            pass

    return {
        "model": mnc_model,
        "mnc_model": mnc_model,
        "warnings": warnings,
        "validation_errors": validation_errors,
        "errors": validation_errors
    }

@router.get("/history")
@router.get("/history/")
async def get_history(
    current_user: User = Depends(get_current_user),
    db: AsyncSession = Depends(get_db)
):
    result = await db.execute(select(TransformHistory).where(TransformHistory.user_id == current_user.id))
    history = result.scalars().all()
    return history
