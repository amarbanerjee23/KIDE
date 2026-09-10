from typing import Any, Dict, List, Optional

class SynthesizedBlocks:
    def __init__(self):
        self.used_interfaces: List[str] = []
        self.init_actions: List[Dict[str, Any]] = []
        self.child_nodes: List[Dict[str, Any]] = []
        self.command_response_blocks: List[Dict[str, Any]] = []
        self.event_blocks: List[Dict[str, Any]] = []
        self.alarm_blocks: List[Dict[str, Any]] = []
        self.data_point_blocks: List[Dict[str, Any]] = []
        self.operating_states: List[Dict[str, Any]] = []
        self.end_states: List[str] = []
        self.start_states: List[str] = ["INITIALIZED"]
        self.subscribed_items: Dict[str, List[str]] = {
            "events": [],
            "alarms": [],
            "data_points": []
        }
        self.commands: List[Dict[str, Any]] = []
        self.events: List[Dict[str, Any]] = []
        self.alarms: List[Dict[str, Any]] = []
        self.data_points: List[Dict[str, Any]] = []
        self.responses: List[Dict[str, Any]] = []
        self.actions: List[Dict[str, Any]] = []
        self.transitions: List[Dict[str, Any]] = []

def _extract_name(item: Any) -> str:
    if isinstance(item, str):
        return item
    if isinstance(item, dict):
        return item.get("name") or item.get("alarm") or item.get("event") or item.get("command") or item.get("dataPoint") or ""
    return str(item)

def _extract_parameters(item: Any) -> List[Dict[str, Any]]:
    if isinstance(item, dict):
        params = item.get("parameters", [])
        if isinstance(params, list):
            return params
    return []

def synthesize(activity_diagram: Dict[str, Any], kb: Optional[Dict[str, Any]] = None) -> SynthesizedBlocks:
    """
    Faithfully implements the thesis synthesis algorithm from:
    - GenerateMnCDesignFromActivityDiagram.xtend
    - MncProvider.xtend
    - ECREGeneratorUtils.xtend
    """
    if kb is None:
        kb = {}
    blocks = SynthesizedBlocks()
    
    # 1. Default lifecycle commands and responses (GenerateMnCDesignFromActivityDiagram.xtend)
    blocks.commands.append({"name": "INIT", "parameters": []})
    blocks.responses.append({"name": "INIT_RES", "parameters": []})
    
    # Default events: Started, Ready, Stopped
    for ev in ["Started", "Ready", "Stopped"]:
        blocks.events.append({"name": ev, "parameters": []})
        blocks.event_blocks.append({"event": ev, "event_ref": ev, "validationRules": []})
        
    # Default operating states: INITIALIZED, READY
    blocks.operating_states.extend([
        {"name": "INITIALIZED", "parameters": []},
        {"name": "READY", "parameters": []}
    ])
    
    # Results / Produces Results derived end-states & Aborted alarm parameters
    # As per: defaultAlarmBlocks = activityDiagram.results.defaultAlarmsForController
    diagram_results = (
        activity_diagram.get("results") or 
        activity_diagram.get("produces_results") or 
        activity_diagram.get("producesResults") or 
        []
    )
    aborted_params = []
    for res_item in diagram_results:
        res_name = _extract_name(res_item)
        if res_name:
            blocks.operating_states.append({"name": res_name, "parameters": []})
            blocks.end_states.append(res_name)
            param_obj = {"name": res_name, "type": res_item.get("type", "string") if isinstance(res_item, dict) else "string"}
            aborted_params.append(param_obj)
            
    # Default alarm: Aborted (includes result parameters if any)
    blocks.alarms.append({"name": "Aborted", "parameters": aborted_params})
    blocks.alarm_blocks.append({
        "alarm": "Aborted",
        "alarm_ref": "Aborted",
        "parameters": aborted_params,
        "validationRules": []
    })

    # Custom default operating states defined on diagram
    custom_states = (
        activity_diagram.get("default_operating_states") or 
        activity_diagram.get("defaultOperatingStates") or 
        []
    )
    for st in custom_states:
        name = st.get("name") if isinstance(st, dict) else str(st)
        if name and name not in [s["name"] for s in blocks.operating_states]:
            blocks.operating_states.append({"name": name, "parameters": []})

    # Collections to feed the supervisory INIT command block
    to_be_fired_commands: List[str] = []
    to_be_executed_operations: List[str] = []

    # Process all activities in the diagram
    activities = activity_diagram.get("activities", [])
    for idx, act in enumerate(activities):
        act_name = act.get("name", f"Activity_{idx+1}")
        req_op = bool(act.get("requires_operation") or act.get("requireOperation"))
        act_params = act.get("parameters") or act.get("inputData") or []

        # Activity becomes an operating state and supervisory action
        if act_name not in [s["name"] for s in blocks.operating_states]:
            blocks.operating_states.append({"name": act_name, "parameters": []})

        action_entry = {
            "name": act_name,
            "requires_operation": req_op,
            "requiresOperation": req_op,
            "type": "operation" if req_op else "standard",
            "parameters": act_params,
            "transitions": act.get("transitions", [])
        }
        blocks.actions.append(action_entry)

        # Direct commands, events, alarms, data points defined on the activity
        for c in act.get("commands", []):
            name = _extract_name(c)
            if name:
                blocks.commands.append({"name": name, "parameters": _extract_parameters(c)})
                to_be_fired_commands.append(name)
        for e in act.get("events", []):
            name = _extract_name(e)
            if name:
                blocks.events.append({"name": name, "parameters": _extract_parameters(e)})
        for a in act.get("alarms", []):
            name = _extract_name(a)
            if name:
                blocks.alarms.append({"name": name, "parameters": _extract_parameters(a)})
        for d in act.get("dataPoints", []) or act.get("data_points", []):
            name = _extract_name(d)
            if name:
                blocks.data_points.append({"name": name, "type": d.get("type", "string") if isinstance(d, dict) else "string", "parameters": []})

        # Operations required by activity
        requires_op_list = act.get("requiresOperation") or act.get("requires_operation_list") or []
        for op in requires_op_list:
            op_name = _extract_name(op)
            if op_name:
                to_be_executed_operations.append(op_name)

        # Capability Binding: activity.bindCapability or activity.requireCapability
        cap_name = act.get("bindCapability") or act.get("requiredCapability")
        cap = None
        if cap_name and isinstance(kb, dict):
            cap = kb.get("capabilities", {}).get(cap_name)
            if not cap and "capabilities" in kb:
                for k, v in kb["capabilities"].items():
                    if k.lower() == str(cap_name).lower():
                        cap = v
                        break

        if cap:
            comp_ifaces = cap.get("componentInterface", [])
            if isinstance(comp_ifaces, str):
                comp_ifaces = [comp_ifaces]
            blocks.used_interfaces.extend([_extract_name(ci) for ci in comp_ifaces])

            req_init = cap.get("requiredINITProcess") or cap.get("required_init_process") or {}
            if req_init:
                for ev in req_init.get("publishEvents") or req_init.get("subscribeEvents") or []:
                    ev_name = _extract_name(ev)
                    if ev_name:
                        blocks.subscribed_items["events"].append(ev_name)
                for al in req_init.get("raiseAlarms") or req_init.get("subscribeAlarms") or []:
                    al_name = _extract_name(al)
                    if al_name:
                        blocks.subscribed_items["alarms"].append(al_name)
                for dp in req_init.get("triggerDataPoints") or req_init.get("subscribeData") or []:
                    dp_name = _extract_name(dp)
                    if dp_name:
                        blocks.subscribed_items["data_points"].append(dp_name)
                for fc in req_init.get("fireCommands") or req_init.get("fire_commands") or []:
                    fc_name = _extract_name(fc)
                    if fc_name:
                        to_be_fired_commands.append(fc_name)
                for op in req_init.get("executeOperations") or []:
                    op_name = _extract_name(op)
                    if op_name:
                        to_be_executed_operations.append(op_name)

            ctrl = cap.get("providesControlCapabilities") or cap.get("provides_control_capabilities") or {}
            cap_commands = ctrl.get("commands", [])
            for cmd in cap_commands:
                cname = _extract_name(cmd)
                if cname:
                    blocks.commands.append({"name": cname, "parameters": _extract_parameters(cmd)})
                    to_be_fired_commands.append(cname)
                    
                    crb = {
                        "name": cname,
                        "command": cname,
                        "command_ref": cname,
                        "action": {
                            "fireCommands": [{"command": cname}],
                            "transitionStates": []
                        },
                        "responseBlock": [],
                        "validationRules": []
                    }
                    blocks.command_response_blocks.append(crb)

            for ev in ctrl.get("events", []):
                ename = _extract_name(ev)
                if ename:
                    blocks.events.append({"name": ename, "parameters": _extract_parameters(ev)})
                    blocks.event_blocks.append({"event": ename, "event_ref": ename, "validationRules": []})
                    blocks.subscribed_items["events"].append(ename)

            for al in ctrl.get("alarms", []):
                aname = _extract_name(al)
                if aname:
                    blocks.alarms.append({"name": aname, "parameters": _extract_parameters(al)})
                    blocks.alarm_blocks.append({"alarm": aname, "alarm_ref": aname, "validationRules": []})
                    blocks.subscribed_items["alarms"].append(aname)

            for dp in ctrl.get("dataPoints") or ctrl.get("data_points") or []:
                dname = _extract_name(dp)
                if dname:
                    dp_type = dp.get("type", "string") if isinstance(dp, dict) else "string"
                    blocks.data_points.append({"name": dname, "type": dp_type, "parameters": []})
                    blocks.data_point_blocks.append({"dataPoints": [dname], "data_point_refs": [dname], "validationRules": []})
                    blocks.subscribed_items["data_points"].append(dname)

        # Handling Conditional Activity
        cond_activities = act.get("conditionalActivity") or act.get("conditional_activities") or []
        for cond in cond_activities:
            target_next = cond.get("onTrueNextActivity") or cond.get("on_true_next_activity")
            final_res = cond.get("onTrueFinalResult") or cond.get("on_true_final_result")
            
            target_state = target_next or final_res
            if target_state:
                if target_state not in [s["name"] for s in blocks.operating_states]:
                    blocks.operating_states.append({"name": target_state, "parameters": []})
                
                transition = {
                    "currentState": act_name,
                    "nextState": target_state,
                    "condition": cond.get("condition", "onTrue")
                }
                blocks.transitions.append(transition)

            outcomes = cond.get("outcome", [])
            if isinstance(outcomes, dict):
                outcomes = [outcomes]
            for outcome in outcomes:
                outcome_item = outcome.get("capabilityOutcome") or outcome.get("outcome") or ""
                validation_rules = outcome.get("outcomeValidation") or []
                
                for crb in blocks.command_response_blocks:
                    crb["responseBlock"].append({
                        "response": outcome_item or "CMD_RES",
                        "validationRules": validation_rules,
                        "onSuccessAction": {
                            "transitionStates": [{"currentState": act_name, "nextState": target_state}] if target_state else []
                        }
                    })

        # Explicit transitions
        explicit_transitions = act.get("transitions", [])
        for trans in explicit_transitions:
            from_st = trans.get("from") or act_name
            to_st = trans.get("to") or trans.get("next")
            cond = trans.get("condition", "")
            if to_st:
                blocks.transitions.append({"currentState": from_st, "nextState": to_st, "condition": cond})
                if to_st not in [s["name"] for s in blocks.operating_states]:
                    blocks.operating_states.append({"name": to_st, "parameters": []})

        next_act = act.get("nextActivity") or act.get("next_activity")
        if next_act:
            blocks.transitions.append({"currentState": act_name, "nextState": next_act, "condition": "always"})
            if next_act not in [s["name"] for s in blocks.operating_states]:
                blocks.operating_states.append({"name": next_act, "parameters": []})

        # Child Activity Diagram handling
        child_diag = act.get("childActivityDiagram") or act.get("child_activity_diagram")
        if child_diag:
            if isinstance(child_diag, dict):
                blocks.child_nodes.append(child_diag)
            elif isinstance(child_diag, str) and isinstance(kb, dict) and "activity_diagrams" in kb:
                child_ast = kb["activity_diagrams"].get(child_diag)
                if child_ast:
                    child_blocks = synthesize(child_ast, kb)
                    blocks.child_nodes.append({
                        "name": f"{child_diag}CN",
                        "diagram": child_diag,
                        "commands": [c["name"] for c in child_blocks.commands]
                    })
            else:
                blocks.child_nodes.append({"name": str(child_diag), "diagram": str(child_diag)})

    unique_init_commands = list(dict.fromkeys(to_be_fired_commands))
    unique_init_ops = list(dict.fromkeys(to_be_executed_operations))
    
    init_crb = {
        "name": "INIT",
        "command": "INIT",
        "command_ref": "INIT",
        "action": {
            "fireCommands": [{"command": cmd} for cmd in unique_init_commands],
            "executeOperation": unique_init_ops,
            "transitionStates": [{"currentState": "INITIALIZED", "nextState": "READY"}]
        },
        "responseBlock": [
            {
                "response": "INIT_RES",
                "validationRules": [],
                "onSuccessAction": {
                    "transitionStates": [{"currentState": "INITIALIZED", "nextState": "READY"}]
                }
            }
        ],
        "validationRules": []
    }
    blocks.command_response_blocks.insert(0, init_crb)

    return blocks
