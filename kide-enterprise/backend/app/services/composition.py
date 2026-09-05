from typing import Dict, Any
from app.schemas.activity import ActivityDiagramSchema
from app.services.synthesis import SynthesizedBlocks

def compose_control_node(diagram: ActivityDiagramSchema, blocks: SynthesizedBlocks) -> dict:
    states = list(set(diagram.default_operating_states))
    for act in diagram.activities:
        for trans in act.transitions:
            if trans.from_state not in states:
                states.append(trans.from_state)
            if trans.to_state not in states:
                states.append(trans.to_state)
    
    actions = []
    for act in diagram.activities:
        actions.append({
            "name": act.name,
            "requires_operation": act.requires_operation,
            "parameters": act.parameters,
            "transitions": [t.dict(by_alias=True) for t in act.transitions]
        })
        
    return {
        "name": diagram.name,
        "operating_states": states,
        "actions": actions,
        "event_block": blocks.events,
        "alarm_block": blocks.alarms,
        "data_point_block": blocks.data_points,
        "command_response_block": blocks.commands + blocks.responses
    }
