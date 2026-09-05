import json
from typing import Dict, Any

def export_json(model: Dict[str, Any]) -> str:
    return json.dumps(model, indent=2)

def export_mnc_dsl(model: Dict[str, Any]) -> str:
    name = model.get("name", "Unknown")
    iface = model.get("interface_description", {})
    cn = model.get("control_node", {})
    
    lines = [f"Model {name}"]
    lines.append(f"InterfaceDescription {iface.get('name', name + '_IF')} {{")
    lines.append("  commands {")
    for cmd in iface.get("commands", []):
        lines.append(f"    Command {cmd.get('name')} []")
    lines.append("  }")
    lines.append("  events {")
    for evt in iface.get("events", []):
        lines.append(f"    Event {evt.get('name')} []")
    lines.append("  }")
    lines.append("  alarms {")
    for alm in iface.get("alarms", []):
        lines.append(f"    Alarm {alm.get('name')} []")
    lines.append("  }")
    lines.append("  dataPoints {")
    for dp in iface.get("data_points", []):
        lines.append(f"    DataPoint {dp.get('name')} []")
    lines.append("  }")
    lines.append("  operatingStates {")
    for st in iface.get("operating_states", []):
        lines.append(f"    State {st.get('name', st) if isinstance(st, dict) else st} []")
    lines.append("  }")
    lines.append("}")
    
    lines.append(f"ControlNode {cn.get('name', name + '_CN')} implements interface {iface.get('name', name + '_IF')} {{")
    lines.append("  CommandResponseBlock {")
    for cr in cn.get("command_response_block", []):
        lines.append(f"    {cr.get('name')}")
    lines.append("  }")
    lines.append("  EventBlock {")
    for ev in cn.get("event_block", []):
        lines.append(f"    {ev.get('name')}")
    lines.append("  }")
    lines.append("}")
    
    return "\n".join(lines)

def export_python_stub(model: Dict[str, Any]) -> str:
    name = model.get("name", "Unknown")
    cn = model.get("control_node", {})
    
    lines = [f"class {name}Handler:"]
    lines.append(f"    def __init__(self):")
    lines.append(f"        self.state = 'IDLE'")
    
    for cmd in cn.get("command_response_block", []):
        if cmd.get("name", "").startswith("ACK_"):
            continue
        lines.append(f"")
        lines.append(f"    def handle_{cmd.get('name').lower()}(self, payload):")
        lines.append(f"        # TODO: Implement {cmd.get('name')} logic")
        lines.append(f"        pass")
        
    return "\n".join(lines)
