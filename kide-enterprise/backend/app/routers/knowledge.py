"""
Router for External Knowledge Hub and Catalog Management.
"""

from typing import Any, Dict, List, Optional
from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy import select
from pydantic import BaseModel

from ..database import get_db
from ..models.project import Project
from ..models.user import User
from ..auth.dependencies import get_current_user
from ..services.knowledge_hub import KnowledgeHubService

router = APIRouter(prefix="", tags=["knowledge"])

class ImportKnowledgeRequest(BaseModel):
    catalog_id: str

class ConvertSpecRequest(BaseModel):
    spec_type: str
    content: str
    name: str = "ImportedDevice"

@router.get("/knowledge/catalog")
async def get_knowledge_catalog(current_user: User = Depends(get_current_user)):
    return KnowledgeHubService.get_catalog()

@router.get("/knowledge/catalog/{catalog_id}")
async def get_catalog_item(catalog_id: str, current_user: User = Depends(get_current_user)):
    item = KnowledgeHubService.get_catalog_item(catalog_id)
    if not item:
        raise HTTPException(status_code=404, detail="Equipment catalog item not found")
    return item

@router.post("/projects/{project_id}/import-knowledge", status_code=status.HTTP_200_OK)
async def import_knowledge_into_project(
    project_id: int,
    payload: ImportKnowledgeRequest,
    db: AsyncSession = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    res = await db.execute(select(Project).where(Project.id == project_id, Project.org_id == current_user.org_id))
    project = res.scalars().first()
    if not project:
        raise HTTPException(status_code=404, detail="Project not found")

    try:
        imported = await KnowledgeHubService.import_to_project(db, project_id, payload.catalog_id)
        return {
            "message": f"Successfully imported equipment knowledge into project {project.name}",
            "catalog_id": payload.catalog_id,
            "imported_files": imported
        }
    except ValueError as e:
        raise HTTPException(status_code=400, detail=str(e))

@router.post("/knowledge/convert-spec")
async def convert_external_specification(
    payload: ConvertSpecRequest,
    current_user: User = Depends(get_current_user)
):
    converted = KnowledgeHubService.convert_external_spec(
        spec_type=payload.spec_type,
        raw_content=payload.content,
        name=payload.name
    )
    return {
        "spec_type": payload.spec_type,
        "name": payload.name,
        "files": converted
    }
