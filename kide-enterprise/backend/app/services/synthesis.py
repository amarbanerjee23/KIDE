from typing import Dict, Any, List
from app.schemas.activity import ActivitySchema

class SynthesizedBlocks:
    def __init__(self):
        self.commands = []
        self.events = []
        self.alarms = []
        self.data_points = []
        self.responses = []

def synthesize_interface_blocks(activities: List[ActivitySchema]) -> SynthesizedBlocks:
    blocks = SynthesizedBlocks()
    # Add default lifecycle blocks
    blocks.commands.append({"name": "INIT", "type": "lifecycle"})
    blocks.responses.append({"name": "INIT_RES", "type": "lifecycle"})
    blocks.events.append({"name": "Started", "type": "lifecycle"})
    blocks.events.append({"name": "Ready", "type": "lifecycle"})
    blocks.events.append({"name": "Stopped", "type": "lifecycle"})
    blocks.alarms.append({"name": "Aborted", "severity": "MEDIUM"})
    
    for act in activities:
        for cmd in act.commands:
            blocks.commands.append(cmd)
            blocks.responses.append({"name": f"ACK_{cmd.get('name', 'cmd')}", "type": "ack"})
        for evt in act.events:
            blocks.events.append(evt)
        for alarm in act.alarms:
            al = dict(alarm)
            if "severity" not in al:
                al["severity"] = "MEDIUM"
            blocks.alarms.append(al)
        for dp in act.data_points:
            d = dict(dp)
            if "type" not in d:
                d["type"] = "string"
            blocks.data_points.append(d)
    return blocks
