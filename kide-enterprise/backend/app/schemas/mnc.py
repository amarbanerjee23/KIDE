from typing import List, Optional, Union
from pydantic import BaseModel, Field
from .dml import Parameter, PrimitiveValue, PrimitiveValueType
from .operation import Operation

class ActionParameter(BaseModel):
    parameterValues: List[PrimitiveValue] = Field(default_factory=list)
    parameterMappings: List['ParameterTranslation'] = Field(default_factory=list)

class ActionCommand(BaseModel):
    command: str
    actionParameter: Optional[ActionParameter] = None
    responseHandling: List['ResponseBlock'] = Field(default_factory=list)

class ActionAlarm(BaseModel):
    alarm: str
    actionParameter: Optional[ActionParameter] = None

class ActionEvent(BaseModel):
    event: str
    actionParameter: Optional[ActionParameter] = None

class ActionDataPoint(BaseModel):
    dataPoint: str
    actionParameter: Optional[ActionParameter] = None

class ActionOperation(BaseModel):
    operation: str
    actionParameter: Optional[ActionParameter] = None

class Transition(BaseModel):
    currentState: Union[List[str], str]
    exitAction: Optional['Action'] = None
    nextState: str
    entryAction: Optional['Action'] = None

class Action(BaseModel):
    raiseAlarms: List[ActionAlarm] = Field(default_factory=list)
    fireCommands: List[ActionCommand] = Field(default_factory=list)
    generateEvents: List[ActionEvent] = Field(default_factory=list)
    triggerDataPoints: List[ActionDataPoint] = Field(default_factory=list)
    executeOperations: List[ActionOperation] = Field(default_factory=list)
    transitionStates: List[Transition] = Field(default_factory=list)

class ParameterTranslation(BaseModel):
    inputParameters: List[str] = Field(default_factory=list)
    translatedParameters: str

class CheckParameterCondition(BaseModel):
    parameters: Optional[List[str]] = Field(default_factory=list)
    operation: Optional[str] = None
    checkMaxValue: Optional[PrimitiveValue] = None
    checkMinValue: Optional[PrimitiveValue] = None
    checkValues: List[PrimitiveValue] = Field(default_factory=list)

class Validation(BaseModel):
    parametersValidationRules: List[CheckParameterCondition] = Field(default_factory=list)
    onFail: Optional[Action] = None
    onSuccess: Optional[Action] = None

class ResponseAggregationRule(BaseModel):
    inputResponses: List[str] = Field(default_factory=list)
    booleanOps: List[str] = Field(default_factory=list)
    parameterTranslations: List[ParameterTranslation] = Field(default_factory=list)

class ResponseBlock(BaseModel):
    response: str
    action: Optional[Action] = None
    validationRules: List[Validation] = Field(default_factory=list)
    responseAggregationRules: List[ResponseAggregationRule] = Field(default_factory=list)

class CommandResponseBlock(BaseModel):
    command: str
    action: Optional[Action] = None
    validationRules: List[Validation] = Field(default_factory=list)
    responseBlock: List[ResponseBlock] = Field(default_factory=list)

class EventBlock(BaseModel):
    event: str
    action: Optional[Action] = None
    validationRules: List[Validation] = Field(default_factory=list)

class AlarmBlock(BaseModel):
    alarm: str
    action: Optional[Action] = None
    validationRules: List[Validation] = Field(default_factory=list)

class DataPointBlock(BaseModel):
    dataPoints: List[str] = Field(default_factory=list)
    action: Optional[Action] = None
    validationRules: List[Validation] = Field(default_factory=list)

class ControlNode(BaseModel):
    name: str
    interfaceDescription: str
    childNodes: List[str] = Field(default_factory=list)
    commandResponseBlocks: List[CommandResponseBlock] = Field(default_factory=list)
    eventBlocks: List[EventBlock] = Field(default_factory=list)
    alarmBlocks: List[AlarmBlock] = Field(default_factory=list)
    dataPointBlocks: List[DataPointBlock] = Field(default_factory=list)

class Address(BaseModel):
    ipaddress: str

class Port(BaseModel):
    name: str
    value: Optional[int] = None

class Command(BaseModel):
    is_async: bool = False
    name: str
    parameters: List[Parameter] = Field(default_factory=list)

class Event(BaseModel):
    is_publish: bool = False
    name: str
    parameters: List[Parameter] = Field(default_factory=list)

class Response(BaseModel):
    name: str
    parameters: List[Parameter] = Field(default_factory=list)

class Alarm(BaseModel):
    is_publish: bool = False
    name: str
    parameters: List[Parameter] = Field(default_factory=list)
    level: Optional[int] = None

class DataPoint(BaseModel):
    is_publish: bool = False
    type: Optional[PrimitiveValueType] = None
    name: str
    value: Optional[PrimitiveValue] = None
    parameters: List[Parameter] = Field(default_factory=list)

class OperatingState(BaseModel):
    name: str
    parameters: List[Parameter] = Field(default_factory=list)

class OperatingStateUtility(BaseModel):
    operatingStates: List[OperatingState] = Field(default_factory=list)
    startStates: List[str] = Field(default_factory=list)
    endStates: List[str] = Field(default_factory=list)

class SubscribableItemList(BaseModel):
    subscribedEvents: List[str] = Field(default_factory=list)
    subscribedAlarms: List[str] = Field(default_factory=list)
    subscribedDataPoints: List[str] = Field(default_factory=list)

class InterfaceDescription(BaseModel):
    name: str
    uses: List[str] = Field(default_factory=list)
    port: Optional[Port] = None
    dataPoints: List[DataPoint] = Field(default_factory=list)
    alarms: List[Alarm] = Field(default_factory=list)
    commands: List[Command] = Field(default_factory=list)
    events: List[Event] = Field(default_factory=list)
    responses: List[Response] = Field(default_factory=list)
    operatingStatesUtility: Optional[OperatingStateUtility] = None
    subscribedItems: Optional[SubscribableItemList] = None
    ipaddress: Optional[Address] = None

class Import(BaseModel):
    importedNamespace: str

class Model(BaseModel):
    importSection: List[Import] = Field(default_factory=list)
    name: str
    systems: List[InterfaceDescription] = Field(default_factory=list)
    controlNode: Optional[ControlNode] = None

ActionCommand.model_rebuild()
Action.model_rebuild()
Transition.model_rebuild()
