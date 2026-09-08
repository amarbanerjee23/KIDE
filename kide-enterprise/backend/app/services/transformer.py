from typing import Dict, Any
from app.services.validator import validate_activity_diagram
from app.services.synthesis import synthesize_interface_blocks
from app.services.composition import compose_control_node
from app.schemas.activity import ActivityDiagramSchema

class TransformResult:
    def __init__(self, model: Dict[str, Any], warnings: list, errors: list):
        self.model = model
        self.warnings = warnings
        self.errors = errors

def transform_activity_to_mnc(payload: Dict[str, Any], knowledge_base: Dict[str, Any] = None) -> TransformResult:
    errors = validate_activity_diagram(payload)
    if errors:
        return TransformResult({}, [], errors)
        
    try:
        diagram = ActivityDiagramSchema(**payload)
        blocks = synthesize_interface_blocks(diagram.activities, knowledge_base)
        cn = compose_control_node(diagram, blocks)
        
        iface = {
            "name": diagram.name + "_Interface",
            "commands": blocks.commands,
            "events": blocks.events,
            "responses": blocks.responses,
            "alarms": blocks.alarms,
            "data_points": blocks.data_points,
            "operating_states": cn["operating_states"]
        }
        
        model = {
            "name": diagram.name,
            "interface_description": iface,
            "control_node": cn
        }
        
        return TransformResult(model, [], [])
    except Exception as e:
        return TransformResult({}, [], [str(e)])
