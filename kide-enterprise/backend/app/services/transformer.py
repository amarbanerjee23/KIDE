from typing import Any, Dict
import json
from .synthesis import synthesize
from .composition import compose_mnc_model
from .validator import validate_model
from ..parsers.activity_parser import parse as parse_activity
from ..parsers.capability_parser import parse as parse_capability
from ..parsers.operation_parser import parse as parse_operation

def transform_workspace(payload: Dict[str, Any]) -> Dict[str, Any]:
    kb = {"capabilities": {}, "operations": {}}
    
    diagram = None
    
    for file_name, content in payload.items():
        if isinstance(content, dict):
            if "activities" in content:
                diagram = content
            elif "provides_control_capabilities" in content or "compatible_component_interfaces" in content:
                kb["capabilities"][content.get("name", file_name)] = content
            elif "executable_script" in content:
                kb["operations"][content.get("name", file_name)] = content
        elif file_name.endswith('.activity'):
            try:
                diagram = parse_activity(content)
            except Exception as e:
                print(f"[Transformer] Error parsing {file_name}: {e}")
        elif file_name.endswith('.capability') or file_name.endswith('.cap'):
            try:
                cap = parse_capability(content)
                if "name" in cap:
                    kb["capabilities"][cap["name"]] = cap
            except Exception as e:
                print(f"[Transformer] Error parsing {file_name}: {e}")
        elif file_name.endswith('.operation') or file_name.endswith('.op'):
            try:
                op = parse_operation(content)
                if "name" in op:
                    kb["operations"][op["name"]] = op
            except Exception as e:
                print(f"[Transformer] Error parsing {file_name}: {e}")
        elif file_name.endswith('.json'):
            try:
                data = json.loads(content)
                if "activities" in data:
                    diagram = data
            except Exception:
                pass

                
    if not diagram:
        return {"error": "No valid Activity Diagram found"}
        
    validation_results = validate_model(diagram, kb)
    
    blocks = synthesize(diagram, kb)
    mnc_model = compose_mnc_model(diagram.get("name", "Model"), blocks)
    
    return {
        "model": mnc_model,
        "mnc_model": mnc_model,
        "warnings": validation_results.get("warnings", []),
        "validation_errors": validation_results.get("errors", []),
        "errors": validation_results.get("errors", [])
    }

def transform_activity_to_mnc(activity_diagram: Dict[str, Any], knowledge_base: Dict[str, Any] = None) -> Dict[str, Any]:
    kb = knowledge_base or {"capabilities": {}, "operations": {}}
    validation_results = validate_model(activity_diagram, kb)
    blocks = synthesize(activity_diagram, kb)
    mnc_model = compose_mnc_model(activity_diagram.get("name", "Model"), blocks)
    return {
        "model": mnc_model,
        "mnc_model": mnc_model,
        "warnings": validation_results.get("warnings", []),
        "validation_errors": validation_results.get("errors", []),
        "errors": validation_results.get("errors", [])
    }
