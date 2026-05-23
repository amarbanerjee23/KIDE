def synthesize_interface_blocks(activities):
    command_response_block=[]
    event_block=[]
    alarm_block=[]
    datapoint_block=[]
    for activity in activities:
        for c in activity.commands:
            command_response_block.append({"command": c, "response": f"ACK_{c.upper()}"})
        for e in activity.events:
            event_block.append({"event": e})
        for a in activity.alarms:
            alarm_block.append({"alarm": a, "severity": "MEDIUM"})
        for d in activity.datapoints:
            datapoint_block.append({"name": d, "type": "string"})
    return command_response_block, event_block, alarm_block, datapoint_block
