from pydantic import BaseModel, Field
from typing import List, Optional, Dict, Any

class ActionParameterSchema(BaseModel):
    values: List[Any] = []

class ActionCommandSchema(BaseModel):
    command: str
    action_parameters: Optional[ActionParameterSchema] = None
    responses: List[str] = [] # Response block refs

class ActionAlarmSchema(BaseModel):
    alarm: str
    action_parameters: Optional[ActionParameterSchema] = None

class ActionEventSchema(BaseModel):
    event: str
    action_parameters: Optional[ActionParameterSchema] = None

class ActionDataPointSchema(BaseModel):
    data_point: str
    action_parameters: Optional[ActionParameterSchema] = None

class ActionOperationSchema(BaseModel):
    operation: str
    action_parameters: Optional[ActionParameterSchema] = None

class ActionSchema(BaseModel):
    # init process representation
    raise_alarms: List[ActionAlarmSchema] = []
    fire_commands: List[ActionCommandSchema] = []
    publish_events: List[ActionEventSchema] = []
    trigger_data_points: List[ActionDataPointSchema] = []
    execute_operations: List[ActionOperationSchema] = []

class ControlCapabilitiesSchema(BaseModel):
    commands: List[Dict[str, Any]] = []  # Can just store the raw definitions or names
    events: List[Dict[str, Any]] = []
    alarms: List[Dict[str, Any]] = []
    data_points: List[Dict[str, Any]] = []

class CapabilitiesOutcomeSchema(BaseModel):
    responses: List[Dict[str, Any]] = []
    events: List[Dict[str, Any]] = []
    alarms: List[Dict[str, Any]] = []
    data_points: List[Dict[str, Any]] = []

class CapabilitySchema(BaseModel):
    name: str
    compatible_component_interfaces: List[str] = [] # references to MNC interfaces
    required_init_process: Optional[ActionSchema] = None
    provides_control_capabilities: Optional[ControlCapabilitiesSchema] = None
    provides_outcomes: Optional[CapabilitiesOutcomeSchema] = None

