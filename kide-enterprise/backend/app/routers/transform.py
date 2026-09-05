from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy import select
from typing import List
import json
from app.schemas.transform import TransformRequest, TransformResponse, TransformHistoryResponse
from app.models.transform import TransformHistory
from app.models.user import User
from app.auth.dependencies import get_db, get_current_user
from app.services.transformer import transform_activity_to_mnc

router = APIRouter()

@router.post("/", response_model=TransformResponse)
async def transform(req: TransformRequest, current_user: User = Depends(get_current_user), db: AsyncSession = Depends(get_db)):
    result = transform_activity_to_mnc(req.activity_diagram)
    
    if result.errors:
        status = "error"
    elif result.warnings:
        status = "warning"
    else:
        status = "success"
        
    th = TransformHistory(
        user_id=current_user.id,
        input_json=json.dumps(req.activity_diagram),
        output_json=json.dumps(result.model),
        status=status,
        errors=json.dumps(result.errors) if result.errors else None
    )
    db.add(th)
    await db.commit()
    
    if result.errors:
        raise HTTPException(status_code=400, detail={"errors": result.errors})
        
    return TransformResponse(
        model=result.model,
        warnings=result.warnings,
        validation_errors=result.errors
    )

@router.get("/history", response_model=List[TransformHistoryResponse])
async def get_history(current_user: User = Depends(get_current_user), db: AsyncSession = Depends(get_db)):
    result = await db.execute(select(TransformHistory).where(TransformHistory.user_id == current_user.id))
    histories = result.scalars().all()
    return [
        TransformHistoryResponse(
            id=h.id, status=h.status, created_at=h.created_at, input_summary="Diagram"
        ) for h in histories
    ]

@router.get("/history/{id}")
async def get_history_detail(id: int, current_user: User = Depends(get_current_user), db: AsyncSession = Depends(get_db)):
    result = await db.execute(select(TransformHistory).where(TransformHistory.id == id, TransformHistory.user_id == current_user.id))
    h = result.scalars().first()
    if not h:
        raise HTTPException(status_code=404)
    return {
        "id": h.id,
        "input": json.loads(h.input_json),
        "output": json.loads(h.output_json) if h.output_json else None,
        "status": h.status,
        "errors": json.loads(h.errors) if h.errors else []
    }
