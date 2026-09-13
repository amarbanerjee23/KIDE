"""
Router for Knowledge Ingestion Pipeline, Document Extraction, Staged Artifacts,
and Transactional Notifications (PhD Requirements 5, 24, 50).
"""

import json
from typing import Any, Dict, List, Optional
from fastapi import APIRouter, Depends, HTTPException, BackgroundTasks, UploadFile, File, Form, Query, status
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy import select, desc

from ..database import get_db
from ..models.user import User
from ..models.knowledge_ingestion import IngestionJob, StagedKnowledgeArtifact, NotificationLog
from ..schemas.ingestion import (
    IngestionJobResponse, IngestionJobCreate, SampleIngestRequest,
    StagedArtifactResponse, ArtifactReviewActionRequest,
    NotificationResponse, SampleTemplate
)
from ..auth.dependencies import get_current_user
from ..services.document_extractor import DocumentExtractor
from ..services.ingestion_worker import IngestionWorker
from ..services.notification_service import NotificationService

router = APIRouter(prefix="", tags=["knowledge-ingestion"])

# =============================================================================
# Document Ingestion Endpoints
# =============================================================================

@router.get("/knowledge/ingest/templates", response_model=List[SampleTemplate])
async def list_sample_templates(current_user: User = Depends(get_current_user)):
    """Returns the pre-configured industrial equipment datasheet templates."""
    return DocumentExtractor.get_sample_templates()


@router.post("/knowledge/ingest/sample", response_model=IngestionJobResponse, status_code=status.HTTP_201_CREATED)
async def ingest_sample_datasheet(
    payload: SampleIngestRequest,
    background_tasks: BackgroundTasks,
    db: AsyncSession = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    """Enqueues and processes one of the built-in industrial equipment datasheets."""
    if payload.sample_id not in DocumentExtractor.SAMPLE_TEMPLATES:
        raise HTTPException(status_code=400, detail=f"Unknown sample template ID: {payload.sample_id}")

    sample_meta = DocumentExtractor.SAMPLE_TEMPLATES[payload.sample_id]
    sample_content = json.dumps(sample_meta, indent=2)

    job = IngestionJob(
        org_id=current_user.org_id,
        user_id=current_user.id,
        filename=f"{sample_meta['name'].replace(' ', '_')}.{sample_meta['file_type']}",
        file_type=sample_meta["file_type"],
        file_size_bytes=len(sample_content),
        raw_content=sample_content,
        status="pending",
        progress_pct=0,
        stage_message="Sample datasheet queued for extraction"
    )
    db.add(job)
    await db.commit()
    await db.refresh(job)

    # Process immediately in worker pipeline
    await IngestionWorker.process_job(job.id, db=db)
    await db.refresh(job)
    return job


@router.post("/knowledge/ingest/upload", response_model=IngestionJobResponse, status_code=status.HTTP_201_CREATED)
async def upload_document_for_ingestion(
    file: UploadFile = File(...),
    background_tasks: BackgroundTasks = None,
    db: AsyncSession = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    """
    Uploads a technical datasheet / manual (PDF, TXT, CSV, JSON) and runs
    the automated extraction and DSL synthesis pipeline.
    """
    raw_bytes = await file.read()
    filename = file.filename or "uploaded_datasheet.txt"
    file_type = filename.rsplit(".", 1)[-1].lower() if "." in filename else "txt"
    
    try:
        content_text = raw_bytes.decode("utf-8")
    except UnicodeDecodeError:
        content_text = raw_bytes.decode("latin-1", errors="replace")

    job = IngestionJob(
        org_id=current_user.org_id,
        user_id=current_user.id,
        filename=filename,
        file_type=file_type,
        file_size_bytes=len(raw_bytes),
        raw_content=content_text,
        status="pending",
        progress_pct=0,
        stage_message="Document uploaded and registered"
    )
    db.add(job)
    await db.commit()
    await db.refresh(job)

    # Run extraction
    await IngestionWorker.process_job(job.id, db=db)
    await db.refresh(job)
    return job


@router.post("/knowledge/ingest/submit-raw", response_model=IngestionJobResponse, status_code=status.HTTP_201_CREATED)
async def submit_raw_document(
    payload: IngestionJobCreate,
    db: AsyncSession = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    """Submits raw text/JSON/CSV specification directly for knowledge ingestion."""
    job = IngestionJob(
        org_id=current_user.org_id,
        user_id=current_user.id,
        filename=payload.filename,
        file_type=payload.file_type,
        file_size_bytes=len(payload.content),
        raw_content=payload.content,
        status="pending",
        progress_pct=0,
        stage_message="Raw specification queued"
    )
    db.add(job)
    await db.commit()
    await db.refresh(job)

    await IngestionWorker.process_job(job.id, db=db)
    await db.refresh(job)
    return job


@router.get("/knowledge/ingest/jobs", response_model=List[IngestionJobResponse])
async def list_ingestion_jobs(
    limit: int = Query(50, ge=1, le=100),
    db: AsyncSession = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    """Lists all knowledge ingestion jobs for the current organization."""
    query = select(IngestionJob).where(
        IngestionJob.org_id == current_user.org_id
    ).order_by(desc(IngestionJob.created_at)).limit(limit)
    res = await db.execute(query)
    return list(res.scalars().all())


@router.get("/knowledge/ingest/jobs/{job_id}", response_model=IngestionJobResponse)
async def get_ingestion_job(
    job_id: int,
    db: AsyncSession = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    """Gets status and metrics for a specific ingestion job."""
    res = await db.execute(select(IngestionJob).where(
        IngestionJob.id == job_id,
        IngestionJob.org_id == current_user.org_id
    ))
    job = res.scalars().first()
    if not job:
        raise HTTPException(status_code=404, detail="Ingestion job not found")
    return job


# =============================================================================
# Staged Knowledge Artifacts Endpoints
# =============================================================================

def _format_staged_artifact(a: StagedKnowledgeArtifact) -> Dict[str, Any]:
    return {
        "id": a.id,
        "artifact_uuid": a.artifact_uuid,
        "job_id": a.job_id,
        "org_id": a.org_id,
        "name": a.name,
        "category": a.category,
        "protocol": a.protocol,
        "status": a.status,
        "device_metadata": json.loads(a.device_metadata or "{}"),
        "extracted_datapoints": json.loads(a.extracted_datapoints or "[]"),
        "extracted_commands": json.loads(a.extracted_commands or "[]"),
        "extracted_alarms": json.loads(a.extracted_alarms or "[]"),
        "extracted_capabilities": json.loads(a.extracted_capabilities or "[]"),
        "generated_dml": a.generated_dml,
        "generated_mnc": a.generated_mnc,
        "generated_cap": a.generated_cap,
        "generated_op": a.generated_op,
        "review_notes": a.review_notes,
        "reviewed_by_user_id": a.reviewed_by_user_id,
        "reviewed_at": a.reviewed_at,
        "created_at": a.created_at
    }


@router.get("/knowledge/ingest/jobs/{job_id}/staged", response_model=List[StagedArtifactResponse])
async def list_job_staged_artifacts(
    job_id: int,
    db: AsyncSession = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    """Lists all staged entities extracted by a specific ingestion job."""
    res = await db.execute(select(StagedKnowledgeArtifact).where(
        StagedKnowledgeArtifact.job_id == job_id,
        StagedKnowledgeArtifact.org_id == current_user.org_id
    ))
    artifacts = res.scalars().all()
    return [_format_staged_artifact(a) for a in artifacts]


@router.get("/knowledge/ingest/staged", response_model=List[StagedArtifactResponse])
async def list_all_staged_artifacts(
    status_filter: Optional[str] = Query(None, alias="status"),
    db: AsyncSession = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    """Lists all staged artifacts across the organization, optionally filtered by status."""
    query = select(StagedKnowledgeArtifact).where(
        StagedKnowledgeArtifact.org_id == current_user.org_id
    )
    if status_filter:
        query = query.where(StagedKnowledgeArtifact.status == status_filter)
    query = query.order_by(desc(StagedKnowledgeArtifact.created_at))
    res = await db.execute(query)
    artifacts = res.scalars().all()
    return [_format_staged_artifact(a) for a in artifacts]


@router.get("/knowledge/ingest/staged/{artifact_id}", response_model=StagedArtifactResponse)
async def get_staged_artifact(
    artifact_id: int,
    db: AsyncSession = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    """Retrieves full details of a staged artifact including synthesized DSL code."""
    res = await db.execute(select(StagedKnowledgeArtifact).where(
        StagedKnowledgeArtifact.id == artifact_id,
        StagedKnowledgeArtifact.org_id == current_user.org_id
    ))
    artifact = res.scalars().first()
    if not artifact:
        raise HTTPException(status_code=404, detail="Staged knowledge artifact not found")
    return _format_staged_artifact(artifact)


@router.post("/knowledge/ingest/staged/{artifact_id}/approve", response_model=StagedArtifactResponse)
async def approve_staged_artifact(
    artifact_id: int,
    payload: ArtifactReviewActionRequest = None,
    db: AsyncSession = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    """
    Approves a staged artifact, promoting it into the active Knowledge Hub catalog
    so it becomes immediately available for capability matching and synthesis.
    """
    notes = payload.notes if payload else None
    try:
        updated = await IngestionWorker.approve_and_promote_artifact(
            db=db,
            artifact_id=artifact_id,
            user=current_user,
            notes=notes
        )
        return _format_staged_artifact(updated)
    except ValueError as e:
        raise HTTPException(status_code=400, detail=str(e))


@router.post("/knowledge/ingest/staged/{artifact_id}/reject", response_model=StagedArtifactResponse)
async def reject_staged_artifact(
    artifact_id: int,
    payload: ArtifactReviewActionRequest = None,
    db: AsyncSession = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    """Rejects a staged artifact with optional engineer review feedback."""
    notes = payload.notes if payload else None
    try:
        updated = await IngestionWorker.reject_artifact(
            db=db,
            artifact_id=artifact_id,
            user=current_user,
            notes=notes
        )
        return _format_staged_artifact(updated)
    except ValueError as e:
        raise HTTPException(status_code=400, detail=str(e))


@router.post("/knowledge/ingest/staged/{artifact_id}/import-to-project/{project_id}")
async def import_staged_to_project(
    artifact_id: int,
    project_id: int,
    db: AsyncSession = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    """Directly imports the synthesized DSL files of a staged artifact into a project workspace."""
    try:
        imported = await IngestionWorker.import_artifact_to_project(
            db=db,
            artifact_id=artifact_id,
            project_id=project_id,
            user=current_user
        )
        return {
            "message": f"Successfully imported {len(imported)} DSL files into project {project_id}",
            "imported_files": imported
        }
    except ValueError as e:
        raise HTTPException(status_code=400, detail=str(e))


# =============================================================================
# Transactional Notifications Endpoints (PhD Requirement 50)
# =============================================================================

@router.get("/notifications", response_model=List[NotificationResponse])
async def list_notifications(
    limit: int = Query(50, ge=1, le=100),
    db: AsyncSession = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    """Lists recent transactional notifications for the organization."""
    return await NotificationService.get_notifications(
        db=db,
        org_id=current_user.org_id,
        user_id=current_user.id,
        limit=limit
    )


@router.get("/notifications/stats")
async def get_notification_stats(
    db: AsyncSession = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    """Returns notification delivery statistics for current user/organization."""
    res = await db.execute(
        select(NotificationLog).where(
            (NotificationLog.org_id == current_user.org_id) | (NotificationLog.user_id == current_user.id)
        )
    )
    all_notifs = res.scalars().all()
    total_count = len(all_notifs)
    sent_count = sum(1 for n in all_notifs if n.status == "sent")
    failed_count = sum(1 for n in all_notifs if n.status == "failed")
    return {
        "total_notifications": total_count,
        "sent_count": sent_count,
        "failed_count": failed_count,
        "latest_notification": all_notifs[0].notification_type if all_notifs else None
    }
