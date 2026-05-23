from __future__ import annotations

from dataclasses import dataclass, asdict
from typing import Any


def _normalize_name(name: str) -> str:
    return (name or "unnamed").strip().replace(" ", "_")


def transform_activity_to_mnc(activity_diagram: dict[str, Any]) -> dict[str, Any]:
    name = activity_diagram.get("name", "UnnamedDiagram")
    activities = activity_diagram.get("activities", [])
    default_states = activity_diagram.get("defaultOperatingStates", ["IDLE", "RUNNING", "FAULT"])

    control_node = {
        "name": _normalize_name(name),
        "operatingStates": [{"name": s} for s in default_states],
        "actions": [],
        "eventBlock": [],
        "alarmBlock": [],
        "dataPointBlock": [],
        "commandResponseBlock": [],
    }

    for activity in activities:
        act_name = activity.get("name", "UnnamedActivity")
        action = {
            "name": _normalize_name(act_name),
            "type": "operation" if activity.get("requiresOperation", True) else "binding",
            "parameters": activity.get("parameters", []),
            "transitions": activity.get("transitions", []),
        }
        control_node["actions"].append(action)

        for c in activity.get("commands", []):
            control_node["commandResponseBlock"].append({
                "command": c,
                "response": f"ACK_{_normalize_name(c)}"
            })

        for e in activity.get("events", []):
            control_node["eventBlock"].append({"event": e})

        for a in activity.get("alarms", []):
            control_node["alarmBlock"].append({"alarm": a, "severity": "MEDIUM"})

        for d in activity.get("dataPoints", []):
            control_node["dataPointBlock"].append({"name": d, "type": "string"})

    return {
        "model": {
            "name": _normalize_name(name),
            "interfaceDescription": {
                "name": name,
                "controlNode": control_node,
            },
        }
    }
