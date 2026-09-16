from typing import Any, Dict, List
from .synthesis import SynthesizedBlocks

def _dedup_by_name(items: List[Any]) -> List[Dict[str, Any]]:
    seen = set()
    result = []
    for item in items:
        name = item.get("name") if isinstance(item, dict) else str(item)
        if name and name not in seen:
            seen.add(name)
            if isinstance(item, dict):
                result.append(item)
            else:
                result.append({"name": item, "parameters": []})
    return result

def compose_mnc_model(diagram_name: str, blocks: SynthesizedBlocks) -> Dict[str, Any]:
    """
    Composes the formal MNC-ML supervisory model according to the KIDE metamodel specifications:
    - Model <Name>
    - InterfaceDescription <Name>_Interface uses <Interfaces>
    - ControlNode <Name> implements interface <Name>_Interface
    - SubscribableItemList with subscribedEvents, subscribedAlarms, subscribedDataPoints
    - OperatingStates with startState INITIALIZED and endStates derived from Activity Diagram
    - CommandResponseBlocks with validation and state transitions
    """
    uses = list(dict.fromkeys(blocks.used_interfaces))
    
    seen_states = set()
    states = []
    for st in blocks.operating_states:
        name = st.get("name") if isinstance(st, dict) else str(st)
        if name and name not in seen_states:
            seen_states.add(name)
            states.append(st if isinstance(st, dict) else {"name": name, "parameters": []})

    commands = _dedup_by_name(blocks.commands)
    events = _dedup_by_name(blocks.events)
    alarms = _dedup_by_name(blocks.alarms)
    data_points = _dedup_by_name(blocks.data_points)
    responses = _dedup_by_name(blocks.responses)

    # Ensure all commands have CommandResponseBlocks
    crb_list = list(blocks.command_response_blocks)
    existing_cmd_refs = {b.get("command_ref") or b.get("name") for b in crb_list}
    for c in commands:
        cname = c["name"]
        if cname not in existing_cmd_refs:
            crb_list.append({
                "name": cname,
                "command": cname,
                "command_ref": cname,
                "action": {
                    "fireCommands": [{"command": cname}],
                    "transitionStates": []
                },
                "responseBlock": [],
                "validationRules": []
            })
            existing_cmd_refs.add(cname)

    interface_name = f"{diagram_name}_Interface"
    state_names = [s["name"] if isinstance(s, dict) else str(s) for s in states]

    end_states = ["READY"] + [s for s in blocks.end_states if s != "READY"]
    start_states = blocks.start_states or ["INITIALIZED"]

    interface = {
        "name": interface_name,
        "uses": uses,
        "commands": commands,
        "events": events,
        "responses": responses,
        "alarms": alarms,
        "dataPoints": data_points,
        "data_points": data_points,
        "operatingStatesUtility": {
            "operatingStates": states,
            "startStates": start_states,
            "endStates": end_states
        },
        "operating_states": states,
        "operatingStates": states,
        "subscribedItems": {
            "subscribedEvents": list(dict.fromkeys(blocks.subscribed_items["events"])),
            "subscribedAlarms": list(dict.fromkeys(blocks.subscribed_items["alarms"])),
            "subscribedDataPoints": list(dict.fromkeys(blocks.subscribed_items["data_points"]))
        },
        "subscribed_items": {
            "events": list(dict.fromkeys(blocks.subscribed_items["events"])),
            "alarms": list(dict.fromkeys(blocks.subscribed_items["alarms"])),
            "data_points": list(dict.fromkeys(blocks.subscribed_items["data_points"]))
        }
    }
    
    control_node = {
        "name": f"{diagram_name}CN",
        "interfaceDescription": interface_name,
        "interface_ref": interface_name,
        "actions": blocks.actions,
        "childNodes": blocks.child_nodes,
        "child_nodes": blocks.child_nodes,
        "operating_states": state_names,
        "operatingStates": state_names,
        "transitions": blocks.transitions,
        "commandResponseBlocks": crb_list,
        "command_response_blocks": crb_list,
        "command_response_block": crb_list,
        "eventBlocks": blocks.event_blocks,
        "event_blocks": blocks.event_blocks,
        "alarmBlocks": blocks.alarm_blocks,
        "alarm_blocks": blocks.alarm_blocks,
        "dataPointBlocks": blocks.data_point_blocks,
        "data_point_blocks": blocks.data_point_blocks
    }
    
    return {
        "name": diagram_name,
        "importSection": [],
        "systems": [interface],
        "controlNode": control_node,
        "control_node": control_node,
        "interfaceDescription": interface,
        "interface_description": interface,
        "subscribableItemList": interface["subscribedItems"]
    }
