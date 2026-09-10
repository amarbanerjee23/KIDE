from typing import Any, Dict, List

class SynthesizedBlocks:
    def __init__(self):
        self.used_interfaces = []
        self.init_actions = []
        self.child_nodes = []
        self.command_response_blocks = []
        self.event_blocks = []
        self.alarm_blocks = []
        self.data_point_blocks = []
        self.operating_states = []
        self.subscribed_items = {"events": [], "alarms": [], "data_points": []}
        self.commands = []
        self.events = []
        self.alarms = []
        self.data_points = []
        self.responses = []
        self.actions = []

def _extract_name(item: Any) -> str:
    if isinstance(item, str):
        return item
    if isinstance(item, dict):
        return item.get("name") or item.get("alarm") or item.get("event") or item.get("command") or ""
    return str(item)

def synthesize(activity_diagram: Dict[str, Any], kb: Dict[str, Any]) -> SynthesizedBlocks:
    blocks = SynthesizedBlocks()
    
    # 1. Default lifecycle blocks
    blocks.commands.append({"name": "INIT", "parameters": []})
    blocks.responses.append({"name": "INIT_RES", "parameters": []})
    for ev in ["Started", "Ready", "Stopped"]:
        blocks.events.append({"name": ev, "parameters": []})
    blocks.alarms.append({"name": "Aborted", "parameters": []})
    
    # 2. Default operating states
    blocks.operating_states.extend([
        {"name": "INITIALIZED", "parameters": []},
        {"name": "READY", "parameters": []}
    ])
    
    for st in activity_diagram.get("default_operating_states", []) or activity_diagram.get("defaultOperatingStates", []):
        name = st.get("name") if isinstance(st, dict) else str(st)
        blocks.operating_states.append({"name": name, "parameters": []})
    
    # Process activities
    for act in activity_diagram.get("activities", []):
        act_name = act.get("name", "")
        if act_name:
            req_op = bool(act.get("requires_operation") or act.get("requireOperation"))
            blocks.actions.append({
                "name": act_name,
                "requires_operation": req_op,
                "requiresOperation": req_op,
                "type": "operation" if req_op else "standard",
                "parameters": act.get("parameters", []),
                "transitions": act.get("transitions", [])
            })
            blocks.operating_states.append({"name": act_name, "parameters": []})
            
        # Direct commands, events, alarms, data points on activity
        for c in act.get("commands", []):
            name = _extract_name(c)
            if name:
                blocks.commands.append({"name": name, "parameters": []})
        for e in act.get("events", []):
            name = _extract_name(e)
            if name:
                blocks.events.append({"name": name, "parameters": []})
        for a in act.get("alarms", []):
            name = _extract_name(a)
            if name:
                blocks.alarms.append({"name": name, "parameters": []})
        for d in act.get("dataPoints", []) or act.get("data_points", []):
            name = _extract_name(d)
            if name:
                blocks.data_points.append({"name": name, "type": "string", "parameters": []})

        # Bound capability
        cap_name = act.get("bindCapability") or act.get("requiredCapability")
        cap = kb.get("capabilities", {}).get(cap_name) if isinstance(kb, dict) else None
        if cap:
            blocks.used_interfaces.extend(cap.get("componentInterface", []))
            
            req_init = cap.get("requiredINITProcess", {})
            if req_init:
                for ev in req_init.get("publishEvents", []):
                    blocks.subscribed_items["events"].append(_extract_name(ev))
                for al in req_init.get("raiseAlarms", []):
                    blocks.subscribed_items["alarms"].append(_extract_name(al))
                for dp in req_init.get("triggerDataPoints", []):
                    blocks.subscribed_items["data_points"].append(_extract_name(dp))
                    
            ctrl = cap.get("providesControlCapabilities", {})
            for cmd in ctrl.get("commands", []):
                cname = _extract_name(cmd)
                blocks.commands.append({"name": cname, "parameters": []})
                crb = {
                    "name": cname,
                    "command": cname,
                    "command_ref": cname,
                    "action": {"fireCommands": [{"command": cname}]},
                    "responseBlock": [],
                    "validationRules": []
                }
                blocks.command_response_blocks.append(crb)

            for ev in ctrl.get("events", []):
                ename = _extract_name(ev)
                blocks.events.append({"name": ename, "parameters": []})
                blocks.event_blocks.append({"event": ename, "event_ref": ename, "validationRules": []})

            for al in ctrl.get("alarms", []):
                aname = _extract_name(al)
                blocks.alarms.append({"name": aname, "parameters": []})
                blocks.alarm_blocks.append({"alarm": aname, "alarm_ref": aname, "validationRules": []})

            for dp in ctrl.get("dataPoints", []):
                dname = _extract_name(dp)
                blocks.data_points.append({"name": dname, "type": "string", "parameters": []})
                blocks.data_point_blocks.append({"dataPoints": [dname], "data_point_refs": [dname], "validationRules": []})

        if act.get("childActivityDiagram"):
            blocks.child_nodes.append(act["childActivityDiagram"])

        for cond in act.get("conditionalActivity", []):
            if cond.get("onTrueNextActivity"):
                blocks.operating_states.append({"name": cond.get("onTrueNextActivity"), "parameters": []})
            if cond.get("onTrueFinalResult"):
                blocks.operating_states.append({"name": cond.get("onTrueFinalResult"), "parameters": []})

    return blocks
