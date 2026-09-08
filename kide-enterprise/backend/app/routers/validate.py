from fastapi import APIRouter, HTTPException
from typing import List, Dict, Any
from pydantic import BaseModel
import json

router = APIRouter()

class FileItem(BaseModel):
    id: str
    name: str
    content: str
    language: str

class ValidationRequest(BaseModel):
    files: List[FileItem]

class ValidationError(BaseModel):
    fileId: str
    message: str

@router.post("/semantic", response_model=List[ValidationError])
async def validate_semantic(request: ValidationRequest):
    errors = []
    
    # 1. Parse all files into a knowledge base
    capabilities = {}
    operations = {}
    activities = []
    
    for f in request.files:
        try:
            parsed = json.loads(f.content)
            if f.name.endswith('.capability'):
                # Handle single or list
                if isinstance(parsed, list):
                    for c in parsed:
                        if 'name' in c:
                            capabilities[c['name']] = c
                else:
                    if 'name' in parsed:
                        capabilities[parsed['name']] = parsed
            elif f.name.endswith('.operation'):
                if isinstance(parsed, list):
                    for o in parsed:
                        if 'name' in o:
                            operations[o['name']] = o
                else:
                    if 'name' in parsed:
                        operations[parsed['name']] = parsed
            elif f.name.endswith('.activity'):
                activities.append((f, parsed))
        except Exception:
            # Syntax errors are handled by Monaco or format action, we only care about semantic ones here
            continue

    # 2. Semantic Rules
    # Rule 1: Operations must reference valid capabilities
    for op_name, op in operations.items():
        if 'capabilityRef' in op:
            if op['capabilityRef'] not in capabilities:
                # Find the file id that contains this operation
                file_id = next(f.id for f in request.files if f.name.endswith('.operation') and op_name in f.content)
                errors.append(ValidationError(
                    fileId=file_id,
                    message=f"Semantic Error: Operation '{op_name}' references unknown Capability '{op['capabilityRef']}'"
                ))
    
    # Rule 2: Activities must reference valid capabilities or operations
    for f, act_model in activities:
        for activity in act_model.get('activities', []):
            if activity.get('requiresOperation'):
                # It must have either require_operation or require_capability
                req_op = activity.get('require_operation')
                req_cap = activity.get('require_capability')
                
                if req_op and req_op not in operations:
                    errors.append(ValidationError(
                        fileId=f.id,
                        message=f"Semantic Error: Activity state '{activity.get('name')}' references unknown Operation '{req_op}'"
                    ))
                
                if req_cap and req_cap not in capabilities:
                    errors.append(ValidationError(
                        fileId=f.id,
                        message=f"Semantic Error: Activity state '{activity.get('name')}' references unknown Capability '{req_cap}'"
                    ))

    return errors

