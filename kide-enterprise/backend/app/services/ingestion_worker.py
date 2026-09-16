"""
Ingestion Background Worker & Knowledge Staging Pipeline (Enterprise Specifications 5 & 24).
Orchestrates document extraction, staged entity lifecycle (draft -> approved -> promoted),
and project workspace integration.
"""

import json
import logging
from typing import Any, Dict, List, Optional
from datetime import datetime, timezone
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy import select, desc

from ..database import AsyncSessionLocal
from ..models.knowledge_ingestion import IngestionJob, StagedKnowledgeArtifact
from ..models.project import Project, ProjectFile
from ..models.user import User
from .document_extractor import DocumentExtractor
from .notification_service import NotificationService
from .knowledge_hub import KnowledgeHubService, EQUIPMENT_CATALOG

logger = logging.getLogger("kide.ingestion_worker")

class IngestionWorker:
    """
    Asynchronous document ingestion worker and staging manager.
    """

    @classmethod
    async def process_job(cls, job_id: int, db: Optional[AsyncSession] = None) -> None:
        """
        Processes an ingestion job through extraction, DSL synthesis, and staging.
        Can run as a FastAPI BackgroundTask or direct coroutine.
        """
        if db is not None:
            await cls._run_processing(job_id, db)
        else:
            async with AsyncSessionLocal() as session:
                await cls._run_processing(job_id, session)

    @classmethod
    async def _run_processing(cls, job_id: int, db: AsyncSession) -> None:
        res = await db.execute(select(IngestionJob).where(IngestionJob.id == job_id))
        job = res.scalars().first()
        if not job:
            logger.error(f"IngestionJob id={job_id} not found")
            return

        # Fetch user email for notifications
        user_email = "engineer@kide.enterprise"
        if job.user_id:
            u_res = await db.execute(select(User).where(User.id == job.user_id))
            u = u_res.scalars().first()
            if u and u.email:
                user_email = u.email

        try:
            # Stage 1: Update status to processing
            job.status = "processing"
            job.progress_pct = 20
            job.stage_message = "Analyzing document structure and format..."
            await db.commit()

            # Stage 2: Extraction
            job.progress_pct = 50
            job.stage_message = "Extracting device metadata, registers, parameters, and control points..."
            await db.commit()

            raw_content = job.raw_content or ""
            artifacts_data = DocumentExtractor.extract_document(
                filename=job.filename,
                file_type=job.file_type,
                content=raw_content
            )

            # Stage 3: DSL Synthesis & DB Staging
            job.progress_pct = 80
            job.stage_message = "Synthesizing Canonical DML, MNC-ML, Capability, and Operation DSL code..."
            await db.commit()

            total_params = 0
            total_commands = 0
            total_alarms = 0

            for art in artifacts_data:
                dps = art.get("datapoints", [])
                cmds = art.get("commands", [])
                alms = art.get("alarms", [])
                caps = art.get("capabilities", [])

                total_params += len(dps)
                total_commands += len(cmds)
                total_alarms += len(alms)

                staged_entry = StagedKnowledgeArtifact(
                    job_id=job.id,
                    org_id=job.org_id,
                    name=art.get("name", "Extracted Device"),
                    category=art.get("category", "Sensors & Actuators"),
                    protocol=art.get("protocol", "Modbus-RTU"),
                    status="draft",
                    device_metadata=json.dumps(art.get("device_metadata", {})),
                    extracted_datapoints=json.dumps(dps),
                    extracted_commands=json.dumps(cmds),
                    extracted_alarms=json.dumps(alms),
                    extracted_capabilities=json.dumps(caps),
                    generated_dml=art.get("generated_dml", ""),
                    generated_mnc=art.get("generated_mnc", ""),
                    generated_cap=art.get("generated_cap", ""),
                    generated_op=art.get("generated_op", "")
                )
                db.add(staged_entry)

            # Stage 4: Mark Complete
            job.status = "completed"
            job.progress_pct = 100
            job.stage_message = f"Ingestion complete. Staged {len(artifacts_data)} device models ready for review."
            job.extracted_devices_count = len(artifacts_data)
            job.extracted_parameters_count = total_params
            job.extracted_capabilities_count = len(artifacts_data)
            job.extracted_operations_count = total_commands
            job.completed_at = datetime.now(timezone.utc)
            await db.commit()

            # Dispatch transactional notification
            await NotificationService.notify_ingestion_completed(
                db=db,
                recipient_email=user_email,
                org_id=job.org_id,
                user_id=job.user_id,
                filename=job.filename,
                artifacts_count=len(artifacts_data),
                parameters_count=total_params
            )
            logger.info(f"IngestionJob id={job_id} successfully completed and staged {len(artifacts_data)} artifacts.")

        except Exception as e:
            logger.exception(f"Error processing IngestionJob id={job_id}: {e}")
            job.status = "failed"
            job.progress_pct = 0
            job.stage_message = f"Extraction failed: {str(e)}"
            job.error_message = str(e)
            job.completed_at = datetime.now(timezone.utc)
            await db.commit()

            await NotificationService.notify_ingestion_failed(
                db=db,
                recipient_email=user_email,
                org_id=job.org_id,
                user_id=job.user_id,
                filename=job.filename,
                error_message=str(e)
            )

    # =========================================================================
    # Staged Artifact Approval & Promotion
    # =========================================================================

    @classmethod
    async def approve_and_promote_artifact(
        cls,
        db: AsyncSession,
        artifact_id: int,
        user: User,
        notes: Optional[str] = None
    ) -> StagedKnowledgeArtifact:
        """
        Approves a staged artifact, marks it approved, and registers it into
        the active Knowledge Hub catalog (EQUIPMENT_CATALOG) for global access.
        """
        res = await db.execute(
            select(StagedKnowledgeArtifact).where(
                StagedKnowledgeArtifact.id == artifact_id,
                StagedKnowledgeArtifact.org_id == user.org_id
            )
        )
        artifact = res.scalars().first()
        if not artifact:
            raise ValueError(f"Staged knowledge artifact id={artifact_id} not found in this organization")

        artifact.status = "approved"
        artifact.reviewed_by_user_id = user.id
        artifact.reviewed_at = datetime.now(timezone.utc)
        if notes:
            artifact.review_notes = notes

        await db.commit()
        await db.refresh(artifact)

        # Register in live Equipment Catalog so it becomes immediately searchable
        catalog_entry_id = f"ingested_{artifact.id}_{artifact.name.lower().replace(' ', '_')}"
        clean_name = artifact.name.replace(" ", "")

        new_catalog_entry = {
            "id": catalog_entry_id,
            "name": artifact.name,
            "category": artifact.category,
            "specification_reference": f"Ingested from {artifact.protocol} datasheet (Artifact #{artifact.id})",
            "description": f"Extracted {artifact.protocol} industrial device specification with automated KIDE DSL synthesis.",
            "tags": [artifact.protocol, artifact.category, "Ingested", "Enterprise"],
            "devices": [artifact.name],
            "files": [
                {
                    "filename": f"{clean_name}_DML.dml",
                    "file_type": "dml",
                    "content": artifact.generated_dml
                },
                {
                    "filename": f"{clean_name}_MNC.mnc",
                    "file_type": "mnc",
                    "content": artifact.generated_mnc
                },
                {
                    "filename": f"{clean_name}_Cap.cap",
                    "file_type": "capability",
                    "content": artifact.generated_cap
                },
                {
                    "filename": f"{clean_name}_Ops.op",
                    "file_type": "operation",
                    "content": artifact.generated_op
                }
            ]
        }

        # Check if already present in EQUIPMENT_CATALOG, replace or append
        existing_idx = next((i for i, c in enumerate(EQUIPMENT_CATALOG) if c["id"] == catalog_entry_id), -1)
        if existing_idx >= 0:
            EQUIPMENT_CATALOG[existing_idx] = new_catalog_entry
        else:
            EQUIPMENT_CATALOG.append(new_catalog_entry)

        # Trigger notification
        await NotificationService.notify_artifact_approved(
            db=db,
            recipient_email=user.email,
            org_id=user.org_id,
            user_id=user.id,
            artifact_name=artifact.name,
            category=artifact.category
        )

        return artifact

    @classmethod
    async def reject_artifact(
        cls,
        db: AsyncSession,
        artifact_id: int,
        user: User,
        notes: Optional[str] = None
    ) -> StagedKnowledgeArtifact:
        """Marks a staged artifact as rejected."""
        res = await db.execute(
            select(StagedKnowledgeArtifact).where(
                StagedKnowledgeArtifact.id == artifact_id,
                StagedKnowledgeArtifact.org_id == user.org_id
            )
        )
        artifact = res.scalars().first()
        if not artifact:
            raise ValueError(f"Staged artifact id={artifact_id} not found")

        artifact.status = "rejected"
        artifact.reviewed_by_user_id = user.id
        artifact.reviewed_at = datetime.now(timezone.utc)
        if notes:
            artifact.review_notes = notes

        await db.commit()
        await db.refresh(artifact)
        return artifact

    @classmethod
    async def import_artifact_to_project(
        cls,
        db: AsyncSession,
        artifact_id: int,
        project_id: int,
        user: User
    ) -> List[Dict[str, Any]]:
        """
        Directly imports the synthesized DSL files of a staged artifact into a project workspace.
        """
        # Validate project belongs to org
        p_res = await db.execute(select(Project).where(Project.id == project_id, Project.org_id == user.org_id))
        project = p_res.scalars().first()
        if not project:
            raise ValueError(f"Project id={project_id} not found in this organization")

        # Validate artifact belongs to org
        a_res = await db.execute(select(StagedKnowledgeArtifact).where(
            StagedKnowledgeArtifact.id == artifact_id,
            StagedKnowledgeArtifact.org_id == user.org_id
        ))
        artifact = a_res.scalars().first()
        if not artifact:
            raise ValueError(f"Staged artifact id={artifact_id} not found")

        clean_name = artifact.name.replace(" ", "")
        files_to_import = [
            (f"{clean_name}_DML.dml", "dml", artifact.generated_dml),
            (f"{clean_name}_MNC.mnc", "mnc", artifact.generated_mnc),
            (f"{clean_name}_Cap.cap", "capability", artifact.generated_cap),
            (f"{clean_name}_Ops.op", "operation", artifact.generated_op),
        ]

        imported_records = []
        for fname, ftype, content in files_to_import:
            if not content.strip():
                continue
            # Check if file already exists in project
            f_res = await db.execute(
                select(ProjectFile).where(
                    ProjectFile.project_id == project_id,
                    ProjectFile.filename == fname
                )
            )
            pf = f_res.scalars().first()
            if pf:
                pf.content = content
            else:
                pf = ProjectFile(
                    project_id=project_id,
                    filename=fname,
                    file_type=ftype,
                    content=content
                )
                db.add(pf)
            imported_records.append({"filename": fname, "file_type": ftype, "bytes": len(content)})

        await db.commit()
        return imported_records
