from typing import Any, Dict

def validate_model(diagram: Dict[str, Any], kb: Dict[str, Any]) -> Dict[str, Any]:
    errors = []
    warnings = []
    
    if not diagram.get("name"):
        errors.append("Diagram is missing a name")
        
    seen_activities = set()
    
    for act in diagram.get("activities", []):
        act_name = act.get("name")
        if not act_name:
            errors.append("Activity missing name")
            continue
            
        if act_name in seen_activities:
            errors.append(f"Duplicate activity name: {act_name}")
        seen_activities.add(act_name)
        
        cap = act.get("bindCapability") or act.get("requiredCapability")
        if cap and cap not in kb.get("capabilities", {}):
            errors.append(f"Capability '{cap}' not found in Knowledge Base")
            
        for op in act.get("requiresOperation", []):
            if op not in kb.get("operations", {}):
                errors.append(f"Operation '{op}' not found in Knowledge Base")
                
        for cond in act.get("conditionalActivity", []):
            if cond.get("onTrueNextActivity") == act_name:
                errors.append(f"Cycle detected: Activity '{act_name}' transitions to itself")
                
    return {"errors": errors, "warnings": warnings}
