from ..core.models import ActivityDiagram, Activity

def parse_activity_json(payload: dict) -> ActivityDiagram:
    acts = []
    for a in payload.get("activities", []):
        acts.append(Activity(
            name=a.get("name", "UnnamedActivity"),
            requires_operation=a.get("requiresOperation", True),
            parameters=a.get("parameters", []),
            commands=a.get("commands", []),
            events=a.get("events", []),
            alarms=a.get("alarms", []),
            datapoints=a.get("dataPoints", []),
            transitions=a.get("transitions", []),
        ))
    return ActivityDiagram(
        name=payload.get("name", "UnnamedDiagram"),
        default_operating_states=payload.get("defaultOperatingStates", ["IDLE","RUNNING","STOPPED","FAULT"]),
        activities=acts,
    )
