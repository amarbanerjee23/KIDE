from typing import List, Optional
from pydantic import BaseModel, Field
from .mnc import ActionAlarm, ActionCommand, ActionEvent, ActionDataPoint, ActionOperation

class CapabilityAction(BaseModel):
    raiseAlarms: List[ActionAlarm] = Field(default_factory=list)
    fireCommands: List[ActionCommand] = Field(default_factory=list)
    publishEvents: List[ActionEvent] = Field(default_factory=list)
    triggerDataPoints: List[ActionDataPoint] = Field(default_factory=list)
    executeOperations: List[ActionOperation] = Field(default_factory=list)

class ControlCapabilities(BaseModel):
    commands: List[str] = Field(default_factory=list)
    events: List[str] = Field(default_factory=list)
    alarms: List[str] = Field(default_factory=list)
    dataPoints: List[str] = Field(default_factory=list)

class CapabilitiesOutcome(BaseModel):
    responses: List[str] = Field(default_factory=list)
    events: List[str] = Field(default_factory=list)
    alarms: List[str] = Field(default_factory=list)
    dataPoints: List[str] = Field(default_factory=list)

class Capability(BaseModel):
    name: str
    componentInterface: List[str] = Field(default_factory=list)
    requiredINITProcess: Optional[CapabilityAction] = None
    providesControlCapabilities: Optional[ControlCapabilities] = None
    providesOutcomes: Optional[CapabilitiesOutcome] = None
