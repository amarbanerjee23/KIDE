from pydantic import BaseModel, Field
from typing import List, Optional, Dict, Any

class TransitionSchema(BaseModel):
    from_state: str = Field(alias="from")
    to_state: str = Field(alias="to")
    condition: Optional[str] = None

class ActivitySchema(BaseModel):
    name: str
    requires_operation: bool = True
    require_operation: Optional[str] = None
    require_capability: Optional[str] = None
    parameters: List[Dict[str, Any]] = []
    # Legacy fallbacks or overrides
    commands: List[Dict[str, Any]] = []
    events: List[Dict[str, Any]] = []
    alarms: List[Dict[str, Any]] = []
    data_points: List[Dict[str, Any]] = []
    transitions: List[TransitionSchema] = []

class ActivityDiagramSchema(BaseModel):
    name: str
    default_operating_states: List[str] = ["IDLE", "RUNNING", "STOPPED", "FAULT"]
    activities: List[ActivitySchema] = []
    data_objects: List[Dict[str, Any]] = []
    context: Optional[Dict[str, Any]] = None

class ControlNodeSchema(BaseModel):
    name: str
    operating_states: List[str]
    actions: List[Dict[str, Any]]
    event_block: List[Dict[str, Any]]
    alarm_block: List[Dict[str, Any]]
    data_point_block: List[Dict[str, Any]]
    command_response_block: List[Dict[str, Any]]

class InterfaceDescriptionSchema(BaseModel):
    name: str
    commands: List[Dict[str, Any]]
    events: List[Dict[str, Any]]
    responses: List[Dict[str, Any]]
    alarms: List[Dict[str, Any]]
    data_points: List[Dict[str, Any]]
    operating_states: List[Dict[str, Any]]

class MncModelSchema(BaseModel):
    name: str
    interface_description: InterfaceDescriptionSchema
    control_node: ControlNodeSchema
