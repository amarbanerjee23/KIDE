"""
Router for Project-Level Custom Code Generators and Synthesis Execution.
"""

import os
from typing import Any, Dict, List, Optional
from fastapi import APIRouter, Depends, HTTPException, Query, status
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy import select
from pydantic import BaseModel

from ..database import get_db
from ..models.project import Project, ProjectFile
from ..models.user import User
from ..auth.dependencies import get_current_user
from ..services.synthesis import synthesize
from ..services.composition import compose_mnc_model
from ..services.custom_generator import CustomGeneratorEngine, DEFAULT_GENERATOR_TEMPLATE
from ..parsers.activity_parser import parse as parse_activity
from ..parsers.capability_parser import parse as parse_capability
from ..parsers.operation_parser import parse as parse_operation

router = APIRouter(prefix="/projects", tags=["generators"])

class CreateGeneratorRequest(BaseModel):
    name: str
    code: Optional[str] = None

class GenerateRequest(BaseModel):
    generator_id: str
    save_to_project: bool = False
    activity_file_id: Optional[int] = None

@router.get("/{project_id}/generators/template")
async def get_generator_template(current_user: User = Depends(get_current_user)):
    return {"template": DEFAULT_GENERATOR_TEMPLATE}

@router.get("/{project_id}/generators/templates")
async def list_generator_templates(current_user: User = Depends(get_current_user)):
    return list(CustomGeneratorEngine.get_all_templates().values())

@router.get("/{project_id}/generators/templates/{generator_id}")
async def get_specific_generator_template(generator_id: str, current_user: User = Depends(get_current_user)):
    tpl = CustomGeneratorEngine.get_template_source(generator_id)
    if not tpl:
        raise HTTPException(status_code=404, detail="Generator template not found")
    return tpl

@router.get("/{project_id}/generators")
async def list_project_generators(
    project_id: int,
    db: AsyncSession = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    res = await db.execute(select(Project).where(Project.id == project_id, Project.org_id == current_user.org_id))
    project = res.scalars().first()
    if not project:
        raise HTTPException(status_code=404, detail="Project not found")
    
    files_res = await db.execute(select(ProjectFile).where(ProjectFile.project_id == project.id))
    files = files_res.scalars().all()
    project_files = [
        {"id": f.id, "name": f.filename, "filename": f.filename, "file_type": f.file_type}
        for f in files
    ]
    return CustomGeneratorEngine.list_generators(project_files)

@router.post("/{project_id}/generators", status_code=status.HTTP_201_CREATED)
async def create_custom_generator(
    project_id: int,
    payload: CreateGeneratorRequest,
    db: AsyncSession = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    res = await db.execute(select(Project).where(Project.id == project_id, Project.org_id == current_user.org_id))
    project = res.scalars().first()
    if not project:
        raise HTTPException(status_code=404, detail="Project not found")

    gen_name = payload.name.strip()
    if not gen_name.endswith(".generator.py"):
        gen_name = f"{gen_name}.generator.py"

    code_content = payload.code if payload.code is not None else DEFAULT_GENERATOR_TEMPLATE

    exist_res = await db.execute(select(ProjectFile).where(ProjectFile.project_id == project_id, ProjectFile.filename == gen_name))
    existing = exist_res.scalars().first()
    if existing:
        existing.content = code_content
        await db.commit()
        await db.refresh(existing)
        return {"message": "Custom generator updated", "file_id": existing.id, "name": existing.filename, "filename": existing.filename}

    new_file = ProjectFile(
        project_id=project_id,
        filename=gen_name,
        file_type="python",
        content=code_content
    )
    db.add(new_file)
    await db.commit()
    await db.refresh(new_file)
    return {"message": "Custom generator created", "file_id": new_file.id, "name": new_file.filename, "filename": new_file.filename}

@router.post("/{project_id}/generate")
async def generate_code(
    project_id: int,
    payload: GenerateRequest,
    db: AsyncSession = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    res = await db.execute(select(Project).where(Project.id == project_id, Project.org_id == current_user.org_id))
    project = res.scalars().first()
    if not project:
        raise HTTPException(status_code=404, detail="Project not found")

    files_res = await db.execute(select(ProjectFile).where(ProjectFile.project_id == project.id))
    files = files_res.scalars().all()

    # Find target activity file
    activity_file = None
    if payload.activity_file_id:
        for f in files:
            if f.id == payload.activity_file_id:
                activity_file = f
                break
    
    if not activity_file:
        for f in files:
            if f.filename.endswith(".activity") or f.file_type == "activity":
                activity_file = f
                break

    # Parse KB files (capabilities, operations)
    kb = {"capabilities": {}, "operations": {}}
    for f in files:
        if f.filename.endswith(".cap") or f.file_type == "capability":
            try:
                parsed_cap = parse_capability(f.content or "")
                cname = parsed_cap.get("name")
                if cname:
                    kb["capabilities"][cname] = parsed_cap
            except Exception:
                pass
        elif f.filename.endswith(".op") or f.file_type == "operation":
            try:
                parsed_op = parse_operation(f.content or "")
                for op in parsed_op.get("operations", []):
                    oname = op.get("name")
                    if oname:
                        kb["operations"][oname] = op
            except Exception:
                pass

    # Parse Activity AST
    activity_ast = None
    if activity_file and activity_file.content:
        try:
            activity_ast = parse_activity(activity_file.content)
        except Exception:
            pass

    if not activity_ast:
        activity_ast = {
            "name": project.name.replace(" ", "_"),
            "activities": [
                {"name": "InitProcess", "commands": [{"name": "INIT"}]},
                {"name": "RunProcess", "commands": [{"name": "START"}]}
            ]
        }

    # Synthesize & Compose formal MNC Model
    blocks = synthesize(activity_ast, kb)
    model = compose_mnc_model(activity_ast.get("name", project.name), blocks)

    # Execute selected generator
    custom_script = None
    if payload.generator_id.startswith("project_"):
        file_id_str = payload.generator_id.replace("project_", "")
        gen_file = None
        for f in files:
            if str(f.id) == file_id_str or f.filename == file_id_str or f.filename.endswith(".generator.py"):
                gen_file = f
                break
        if gen_file:
            custom_script = gen_file.content

    generated_files = CustomGeneratorEngine.run_generator(
        generator_id=payload.generator_id,
        model=model,
        custom_script=custom_script
    )

    # Optionally persist generated files in project
    if payload.save_to_project:
        for fname, fcontent in generated_files.items():
            existing_res = await db.execute(select(ProjectFile).where(ProjectFile.project_id == project_id, ProjectFile.filename == fname))
            existing_gen = existing_res.scalars().first()
            if existing_gen:
                existing_gen.content = fcontent
            else:
                new_gen = ProjectFile(
                    project_id=project_id,
                    filename=fname,
                    file_type="python" if fname.endswith(".py") else ("java" if fname.endswith(".java") else "text"),
                    content=fcontent
                )
                db.add(new_gen)
        await db.commit()

    return {
        "generator_id": payload.generator_id,
        "model_name": model.get("name"),
        "files": generated_files,
        "file_count": len(generated_files)
    }
