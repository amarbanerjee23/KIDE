from typing import List, Optional
from enum import Enum
from pydantic import BaseModel, Field
from .dml import Parameter, PrimitiveValue

class UnitTime(str, Enum):
    secs = 'secs'
    mins = 'mins'
    hrs = 'hrs'
    days = 'days'

class ActivityCheckParameterCondition(BaseModel):
    parameter: str
    checkMaxValue: Optional[PrimitiveValue] = None
    checkMinValue: Optional[PrimitiveValue] = None
    checkValues: List[PrimitiveValue] = Field(default_factory=list)

class Outcome(BaseModel):
    capabilityOutcome: Optional[str] = None
    outcomeValidation: List[ActivityCheckParameterCondition] = Field(default_factory=list)

class ConditionalActivity(BaseModel):
    outcomes: List[Outcome] = Field(default_factory=list)
    bOps: List[str] = Field(default_factory=list)
    onTrueNextActivity: Optional[str] = None
    onTrueFinalResult: Optional[str] = None

class Activity(BaseModel):
    name: str
    description: Optional[str] = None
    inputParameters: List[str] = Field(default_factory=list)
    requiredCapability: Optional[str] = None
    bindCapability: Optional[str] = None
    useControlCapabilities: List[str] = Field(default_factory=list)
    requiresOperation: List[str] = Field(default_factory=list)
    childActivityDiagram: Optional[str] = None
    conditionalActivity: List[ConditionalActivity] = Field(default_factory=list)
    nextActivity: Optional[str] = None
    nextActivityDiagram: Optional[str] = None
    time: Optional[float] = None
    unit: Optional[UnitTime] = None
    interruptedBy: List[str] = Field(default_factory=list)
    interrupts: List[str] = Field(default_factory=list)

class ActivityDiagram(BaseModel):
    name: str
    dataObjects: List[str] = Field(default_factory=list)
    contextDataModel: List[str] = Field(default_factory=list)
    physicalContexts: List[str] = Field(default_factory=list)
    producesResults: List[str] = Field(default_factory=list)
    activities: List[Activity] = Field(default_factory=list)
