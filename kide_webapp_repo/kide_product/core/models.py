from dataclasses import dataclass, field, asdict
from typing import Any

@dataclass
class Activity:
    name: str
    requires_operation: bool = True
    parameters: list[str] = field(default_factory=list)
    commands: list[str] = field(default_factory=list)
    events: list[str] = field(default_factory=list)
    alarms: list[str] = field(default_factory=list)
    datapoints: list[str] = field(default_factory=list)
    transitions: list[dict[str, str]] = field(default_factory=list)

@dataclass
class ActivityDiagram:
    name: str
    default_operating_states: list[str] = field(default_factory=lambda: ["IDLE","RUNNING","STOPPED","FAULT"])
    activities: list[Activity] = field(default_factory=list)

@dataclass
class ProductModel:
    model: dict[str, Any]

    def to_dict(self) -> dict[str, Any]:
        return self.model
