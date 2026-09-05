from typing import Dict, Any, List
from app.schemas.activity import ActivityDiagramSchema

def validate_activity_diagram(diagram: Dict[str, Any]) -> List[str]:
    errors = []
    if "name" not in diagram or not diagram["name"]:
        errors.append("Diagram must have a name.")
    
    activity_names = set()
    for act in diagram.get("activities", []):
        name = act.get("name")
        if not name:
            errors.append("Activity must have a name.")
        elif name in activity_names:
            errors.append(f"Duplicate activity name: {name}")
        else:
            activity_names.add(name)
            
        for cmd in act.get("commands", []):
            if not isinstance(cmd.get("name"), str) or not cmd.get("name"):
                errors.append(f"Invalid command in activity {name}.")
        for evt in act.get("events", []):
            if not isinstance(evt.get("name"), str) or not evt.get("name"):
                errors.append(f"Invalid event in activity {name}.")
    
    return errors
