from typing import Dict, Any, List, Optional
from app.schemas.activity import ActivitySchema
from app.schemas.capability import CapabilitySchema

class SynthesizedBlocks:
    def __init__(self):
        self.commands = []
        self.events = []
        self.alarms = []
        self.data_points = []
        self.responses = []

def resolve_capability(knowledge_base: Dict[str, Any], operation_ref: str = None, capability_ref: str = None) -> Optional[CapabilitySchema]:
    if not knowledge_base:
        return None
        
    capabilities = knowledge_base.get("capabilities", [])
    
    # If capability is directly referenced
    if capability_ref:
        for cap in capabilities:
            if cap.get("name") == capability_ref:
                return CapabilitySchema(**cap)
                
    # If operation is referenced, in a real knowledge base, we'd query an ontology 
    # to find which capability provides this operation.
    # Here we mock that ontology resolution:
    if operation_ref:
        for cap in capabilities:
            # check if capability init process executes this operation
            init_proc = cap.get("required_init_process", {})
            if init_proc:
                exec_ops = init_proc.get("execute_operations", [])
                for op in exec_ops:
                    if op.get("operation") == operation_ref:
                        return CapabilitySchema(**cap)
    return None

def synthesize_interface_blocks(activities: List[ActivitySchema], knowledge_base: Dict[str, Any] = None) -> SynthesizedBlocks:
    blocks = SynthesizedBlocks()
    # Add default lifecycle blocks
    blocks.commands.append({"name": "INIT", "type": "lifecycle"})
    blocks.responses.append({"name": "INIT_RES", "type": "lifecycle"})
    blocks.events.append({"name": "Started", "type": "lifecycle"})
    blocks.events.append({"name": "Ready", "type": "lifecycle"})
    blocks.events.append({"name": "Stopped", "type": "lifecycle"})
    blocks.alarms.append({"name": "Aborted", "severity": "MEDIUM"})
    
    for act in activities:
        resolved = False
        
        # Knowledge-driven synthesis
        if knowledge_base and (act.require_operation or act.require_capability):
            cap = resolve_capability(knowledge_base, act.require_operation, act.require_capability)
            if cap:
                resolved = True
                if cap.provides_control_capabilities:
                    for cmd in cap.provides_control_capabilities.commands:
                        blocks.commands.append(cmd)
                        blocks.responses.append({"name": f"ACK_{cmd.get('name', 'cmd')}", "type": "ack"})
                    for evt in cap.provides_control_capabilities.events:
                        blocks.events.append(evt)
                    for alarm in cap.provides_control_capabilities.alarms:
                        blocks.alarms.append(alarm)
                    for dp in cap.provides_control_capabilities.data_points:
                        blocks.data_points.append(dp)
        
        # Fallback to direct properties if knowledge resolution failed or was not requested
        if not resolved:
            for cmd in act.commands:
                blocks.commands.append(cmd)
                blocks.responses.append({"name": f"ACK_{cmd.get('name', 'cmd')}", "type": "ack"})
            for evt in act.events:
                blocks.events.append(evt)
            for alarm in act.alarms:
                al = dict(alarm)
                if "severity" not in al:
                    al["severity"] = "MEDIUM"
                blocks.alarms.append(al)
            for dp in act.data_points:
                d = dict(dp)
                if "type" not in d:
                    d["type"] = "string"
                blocks.data_points.append(d)
                
    return blocks
