"""
Router for External Knowledge Hub, Equipment Catalog, and Knowledge Graph.
"""

from typing import Any, Dict, List, Optional
from fastapi import APIRouter, Depends, HTTPException, Query, Response, status
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy import select
from pydantic import BaseModel

from ..database import get_db
from ..models.project import Project, ProjectFile
from ..models.user import User
from ..auth.dependencies import get_current_user
from ..services.knowledge_hub import KnowledgeHubService
from ..services.knowledge_graph import KnowledgeGraphService
from ..services.synthesis import synthesize
from ..services.composition import compose_mnc_model
from ..parsers.activity_parser import parse as parse_activity
from ..parsers.capability_parser import parse as parse_capability
from ..parsers.operation_parser import parse as parse_operation

router = APIRouter(prefix="", tags=["knowledge"])

class ImportKnowledgeRequest(BaseModel):
    catalog_id: str

class ConvertSpecRequest(BaseModel):
    spec_type: str
    content: str
    name: str = "ImportedDevice"

class MatchCapabilityRequest(BaseModel):
    query: str

@router.get("/knowledge/catalog")
async def get_knowledge_catalog(current_user: User = Depends(get_current_user)):
    return KnowledgeHubService.get_catalog()

@router.get("/knowledge/catalog/{catalog_id}")
async def get_catalog_item(catalog_id: str, current_user: User = Depends(get_current_user)):
    item = KnowledgeHubService.get_catalog_item(catalog_id)
    if not item:
        raise HTTPException(status_code=404, detail="Equipment catalog item not found")
    return item

@router.get("/knowledge/graph")
async def get_global_knowledge_graph(current_user: User = Depends(get_current_user)):
    """Returns the global cross-domain Knowledge Repository Graph."""
    return KnowledgeGraphService.build_global_catalog_graph()

@router.post("/knowledge/match")
async def match_capabilities(
    payload: MatchCapabilityRequest,
    current_user: User = Depends(get_current_user)
):
    """Recommends equipment and capabilities based on activity queries or task keywords."""
    return KnowledgeGraphService.match_capabilities(payload.query)

@router.get("/projects/{project_id}/knowledge-graph")
async def get_project_knowledge_graph(
    project_id: int,
    db: AsyncSession = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    """Returns the live connected Knowledge Graph for a specific project."""
    res = await db.execute(select(Project).where(Project.id == project_id, Project.org_id == current_user.org_id))
    project = res.scalars().first()
    if not project:
        raise HTTPException(status_code=404, detail="Project not found")

    files_res = await db.execute(select(ProjectFile).where(ProjectFile.project_id == project.id))
    files = files_res.scalars().all()
    files_list = [
        {"id": f.id, "filename": f.filename, "file_type": f.file_type, "content": f.content or ""}
        for f in files
    ]

    # Synthesize MNC model if activity and capabilities are present
    synthesized_model = None
    act_file = next((f for f in files if (f.filename or "").endswith(".activity") or f.file_type == "activity"), None)
    if act_file and act_file.content:
        try:
            act_ast = parse_activity(act_file.content)
            kb = {"capabilities": {}, "operations": {}}
            for f in files:
                fname = f.filename or ""
                ftype = f.file_type or ""
                if fname.endswith(".cap") or ftype == "capability":
                    try:
                        c_ast = parse_capability(f.content or "")
                        if c_ast.get("name"):
                            kb["capabilities"][c_ast["name"]] = c_ast
                    except Exception:
                        pass
                elif fname.endswith(".op") or ftype == "operation":
                    try:
                        o_ast = parse_operation(f.content or "")
                        for op in o_ast.get("operations", []):
                            if op.get("name"):
                                kb["operations"][op["name"]] = op
                    except Exception:
                        pass
            blocks = synthesize(act_ast, kb)
            synthesized_model = compose_mnc_model(act_ast.get("name", project.name), blocks)
        except Exception:
            pass

    return KnowledgeGraphService.build_project_knowledge_graph(
        project_id=project.id,
        project_name=project.name,
        files=files_list,
        synthesized_model=synthesized_model
    )

@router.get("/projects/{project_id}/knowledge-graph/export")
async def export_project_knowledge_graph(
    project_id: int,
    format: str = Query("json", pattern="^(json|turtle|rdf)$"),
    db: AsyncSession = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    """Exports the project knowledge graph in JSON or W3C RDF Turtle ontology syntax."""
    res = await db.execute(select(Project).where(Project.id == project_id, Project.org_id == current_user.org_id))
    project = res.scalars().first()
    if not project:
        raise HTTPException(status_code=404, detail="Project not found")

    files_res = await db.execute(select(ProjectFile).where(ProjectFile.project_id == project.id))
    files = files_res.scalars().all()
    files_list = [
        {"id": f.id, "filename": f.filename, "file_type": f.file_type, "content": f.content or ""}
        for f in files
    ]

    graph = KnowledgeGraphService.build_project_knowledge_graph(
        project_id=project.id,
        project_name=project.name,
        files=files_list
    )

    if format in ("turtle", "rdf"):
        ttl_content = KnowledgeGraphService.export_graph_to_rdf_turtle(graph)
        return Response(content=ttl_content, media_type="text/turtle", headers={
            "Content-Disposition": f'attachment; filename="{project.name}_ontology.ttl"'
        })

    return graph

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
