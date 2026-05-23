from ..parsers.json_parser import parse_activity_json
from .synthesis import synthesize_interface_blocks
from .composition import compose_control_node

def transform_activity_to_mnc(payload: dict) -> dict:
    diagram = parse_activity_json(payload)
    blocks = synthesize_interface_blocks(diagram.activities)
    control_node = compose_control_node(diagram, blocks)
    return {
        "model": {
            "name": diagram.name.replace(" ", "_"),
            "interfaceDescription": {
                "name": diagram.name,
                "controlNode": control_node,
            },
        }
    }
