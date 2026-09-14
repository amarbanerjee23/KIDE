from enum import Enum
from typing import Any, Dict, List, Optional
from pydantic import BaseModel, Field

class SymbolType(str, Enum):
    DATA_PACKAGE = "data_package"
    DATA_MODEL = "data_model"
    PRIMITIVE = "primitive"
    COMPOSITE = "composite"
    ARRAY = "array"
    CAPABILITY = "capability"
    OPERATION = "operation"
    ACTIVITY_DIAGRAM = "activity_diagram"
    ACTIVITY = "activity"
    INTERFACE_DESCRIPTION = "interface_description"
    CONTROL_NODE = "control_node"
    COMMAND = "command"
    EVENT = "event"
    RESPONSE = "response"
    ALARM = "alarm"
    DATA_POINT = "data_point"
    OPERATING_STATE = "operating_state"
    TRANSITION = "transition"

class DiagnosticSeverity(str, Enum):
    ERROR = "error"
    WARNING = "warning"
    INFO = "info"

class Diagnostic(BaseModel):
    file_id: Optional[str] = None
    fileId: Optional[str] = None
    filename: str = ""
    line: Optional[int] = None
    column: Optional[int] = None
    message: str
    severity: DiagnosticSeverity = DiagnosticSeverity.ERROR
    rule_id: str = "SEM-000"
    ruleId: Optional[str] = None
    symbol: Optional[str] = None
    suggestion: Optional[str] = None
    quick_fix: Optional[str] = None
    quickFix: Optional[str] = None

    def model_post_init(self, __context: Any) -> None:
        if self.file_id and not self.fileId:
            self.fileId = self.file_id
        elif self.fileId and not self.file_id:
            self.file_id = self.fileId
        if self.rule_id and not self.ruleId:
            self.ruleId = self.rule_id
        elif self.ruleId and not self.rule_id:
            self.rule_id = self.ruleId
        if self.quick_fix and not self.quickFix:
            self.quickFix = self.quick_fix
        elif self.quickFix and not self.quick_fix:
            self.quick_fix = self.quickFix

class SymbolRecord(BaseModel):
    name: str
    fqn: str
    symbol_type: SymbolType
    file_id: Optional[str] = None
    filename: str = ""
    line: Optional[int] = 1
    column: Optional[int] = 1
    signature: Optional[str] = None
    properties: Dict[str, Any] = Field(default_factory=dict)
    dependencies: List[str] = Field(default_factory=list)

class CrossReference(BaseModel):
    source_symbol: str
    target_symbol: str
    reference_kind: str
    file_id: Optional[str] = None
    filename: str = ""
    line: Optional[int] = None

class AutomataHazard(BaseModel):
    hazard_type: str
    state: str
    description: str
    severity: DiagnosticSeverity = DiagnosticSeverity.ERROR
    rule_id: str = "HAZ-000"

class ProjectIRSummary(BaseModel):
    total_files: int = 0
    total_symbols: int = 0
    total_interfaces: int = 0
    total_states: int = 0
    total_transitions: int = 0
    total_activities: int = 0
    total_cross_references: int = 0
    error_count: int = 0
    warning_count: int = 0
    health_score: float = 100.0

class ProjectIR(BaseModel):
    project_id: Optional[str] = None
    project_name: str = "Project"
    symbols: Dict[str, SymbolRecord] = Field(default_factory=dict)
    data_models: Dict[str, Any] = Field(default_factory=dict)
    capabilities: Dict[str, Any] = Field(default_factory=dict)
    operations: Dict[str, Any] = Field(default_factory=dict)
    activity_diagrams: Dict[str, Any] = Field(default_factory=dict)
    mnc_models: Dict[str, Any] = Field(default_factory=dict)
    cross_references: List[CrossReference] = Field(default_factory=list)
    diagnostics: List[Diagnostic] = Field(default_factory=list)
    summary: ProjectIRSummary = Field(default_factory=ProjectIRSummary)

