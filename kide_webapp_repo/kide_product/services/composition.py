def compose_control_node(diagram, synthesized_blocks):
    cblock, eblock, ablock, dblock = synthesized_blocks
    actions=[]
    for a in diagram.activities:
        actions.append({
            "name": a.name.replace(" ", "_"),
            "type": "operation" if a.requires_operation else "binding",
            "parameters": a.parameters,
            "transitions": a.transitions,
        })
    return {
        "name": diagram.name.replace(" ", "_"),
        "operatingStates": [{"name": s} for s in diagram.default_operating_states],
        "actions": actions,
        "eventBlock": eblock,
        "alarmBlock": ablock,
        "dataPointBlock": dblock,
        "commandResponseBlock": cblock,
    }
