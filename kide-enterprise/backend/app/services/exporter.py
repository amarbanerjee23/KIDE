from typing import Any, Dict

def export_mnc(model: Dict[str, Any]) -> str:
    lines = []
    name = model.get("name", "Model")
    lines.append(f"Model {name}")
    
    systems = model.get("systems") or ([model.get("interface_description")] if model.get("interface_description") else [])
    for sys in systems:
        if not sys:
            continue
        ifname = sys.get("name") or f"{name}_Interface"
        uses = ", ".join(sys.get("uses", []))
        uses_str = f" uses {uses}" if uses else ""
        lines.append(f"InterfaceDescription {ifname}{uses_str} {{")
        
        commands = sys.get("commands", [])
        if commands:
            lines.append("  commands {")
            for c in commands:
                cname = c.get("name") if isinstance(c, dict) else str(c)
                lines.append(f"    Command {cname} []")
            lines.append("  }")
            
        events = sys.get("events", [])
        if events:
            lines.append("  events {")
            for e in events:
                ename = e.get("name") if isinstance(e, dict) else str(e)
                lines.append(f"    Event {ename} []")
            lines.append("  }")
            
        responses = sys.get("responses", [])
        if responses:
            lines.append("  responses {")
            for r in responses:
                rname = r.get("name") if isinstance(r, dict) else str(r)
                lines.append(f"    Response {rname} []")
            lines.append("  }")
            
        alarms = sys.get("alarms", [])
        if alarms:
            lines.append("  alarms {")
            for a in alarms:
                aname = a.get("name") if isinstance(a, dict) else str(a)
                lines.append(f"    Alarm {aname} []")
            lines.append("  }")
            
        dps = sys.get("dataPoints") or sys.get("data_points")
        if dps:
            lines.append("  dataPoints {")
            for dp in dps:
                dpname = dp.get("name") if isinstance(dp, dict) else str(dp)
                lines.append(f"    DataPoint {dpname} []")
            lines.append("  }")
            
        ops = sys.get("operatingStatesUtility") or sys.get("operating_states")
        if isinstance(ops, dict) and ops.get("operatingStates"):
            lines.append("  operatingStates {")
            for st in ops.get("operatingStates", []):
                sname = st.get("name") if isinstance(st, dict) else str(st)
                lines.append(f"    {sname} []")
            if ops.get("startStates"):
                lines.append(f"    startStates : {', '.join(ops['startStates'])}")
            if ops.get("endStates"):
                lines.append(f"    endStates : {', '.join(ops['endStates'])}")
            lines.append("  }")
            
        sub = sys.get("subscribedItems") or sys.get("subscribed_items")
        if isinstance(sub, dict):
            lines.append("  SubscribableItemList {")
            if sub.get("subscribedEvents") or sub.get("events"):
                evs = sub.get("subscribedEvents") or sub.get("events")
                lines.append(f"    subscribedEvents : {', '.join(evs)}")
            if sub.get("subscribedAlarms") or sub.get("alarms"):
                als = sub.get("subscribedAlarms") or sub.get("alarms")
                lines.append(f"    subscribedAlarms : {', '.join(als)}")
            if sub.get("subscribedDataPoints") or sub.get("data_points"):
                dpts = sub.get("subscribedDataPoints") or sub.get("data_points")
                lines.append(f"    subscribedDataPoints : {', '.join(dpts)}")
            lines.append("  }")
            
        lines.append("}")
        
    cn = model.get("controlNode") or model.get("control_node")
    if cn:
        cnname = cn.get("name") or f"{name}CN"
        ifref = cn.get("interfaceDescription") or cn.get("interface_ref") or f"{name}_Interface"
        lines.append(f"ControlNode {cnname} implements interface {ifref} {{")
        
        cnodes = cn.get("childNodes") or cn.get("child_nodes")
        if cnodes:
            lines.append(f"  childNodes ({', '.join(cnodes)})")
            
        crbs = cn.get("commandResponseBlocks") or cn.get("command_response_blocks") or cn.get("command_response_block") or []
        for crb in crbs:
            cmd = crb.get("command") or crb.get("command_ref") or crb.get("name")
            lines.append("  CommandResponseBlock {")
            lines.append(f"    Command {cmd} {{")
            lines.append("    }")
            lines.append("  }")
            
        for eb in cn.get("eventBlocks") or cn.get("event_blocks") or []:
            evt = eb.get("event") or eb.get("event_ref")
            lines.append("  EventBlock {")
            lines.append(f"    Event {evt} {{")
            lines.append("    }")
            lines.append("  }")
            
        for ab in cn.get("alarmBlocks") or cn.get("alarm_blocks") or []:
            alm = ab.get("alarm") or ab.get("alarm_ref")
            lines.append("  AlarmBlock {")
            lines.append(f"    Alarm {alm} {{")
            lines.append("    }")
            lines.append("  }")
            
        lines.append("}")
        
    return "\n".join(lines)

def export_python(model: Dict[str, Any]) -> str:
    name = model.get("name", "Model")
    lines = [
        f"class {name}Handler:",
        f"    \"\"\"Generated Python Handler for {name}\"\"\"",
        "    def __init__(self):",
        "        pass",
    ]
    return "\n".join(lines)

def export_format(model: Dict[str, Any], fmt: str) -> str:
    fmt_lower = fmt.lower()
    if fmt_lower in ("mnc", "mncml", "dsl"):
        return export_mnc(model)
    elif fmt_lower == "json":
        import json
        return json.dumps(model, indent=2)
    elif fmt_lower == "python":
        return export_python(model)
    elif fmt_lower == "activity":
        return f"ActivityDiagram {model.get('name', 'Model')} {{\n  // Synthesized Activity DSL\n}}"
    elif fmt_lower == "capability":
        return f"Capability {model.get('name', 'Model')}Cap {{\n  // Synthesized Capability DSL\n}}"
    elif fmt_lower == "operation":
        return f"Operation {model.get('name', 'Model')}Op() {{\n  // Synthesized Operation DSL\n}}"
    elif fmt_lower == "dml":
        return f"Package {model.get('name', 'Model')}Package {{\n  // Synthesized DML\n}}"
    return export_mnc(model)
