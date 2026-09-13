from enum import Enum
from typing import Any, Dict, List, Optional
from pydantic import BaseModel, Field
from app.schemas.ai import ProposedPatch

class TraceLinkType(str, Enum):
    SPECIFIES = "specifies"
    REQUIRES_CAPABILITY = "requires_capability"
    REQUIRES_OPERATION = "requires_operation"
    CONTEXT_MODEL = "context_model"
    IMPLEMENTS_INTERFACE = "implements_interface"
    TRANSITIONS_TO = "transitions_to"
    SYNTHESIZES_STATE = "synthesizes_state"
    GENERATES_TARGET = "generates_target"

class ImpactRiskLevel(str, Enum):
    LOW = "LOW"
    MEDIUM = "MEDIUM"
    HIGH = "HIGH"
    CRITICAL = "CRITICAL"

class TraceabilityLink(BaseModel):
    source_symbol: str
    source_type: str
    source_file: str
    target_symbol: str
    target_type: str
    target_file: str
    link_type: TraceLinkType
    description: str

class TraceabilityMatrixRow(BaseModel):
    symbol: str
    symbol_type: str
    filename: str
    stage: int
    upstream_symbols: List[str] = Field(default_factory=list)
    downstream_symbols: List[str] = Field(default_factory=list)
    links: List[TraceabilityLink] = Field(default_factory=list)

class TraceabilityMatrixResponse(BaseModel):
    project_id: Optional[int] = None
    total_symbols: int = 0
    total_links: int = 0
    coverage_percentage: float = 0.0
    rows: List[TraceabilityMatrixRow] = Field(default_factory=list)
    layer_counts: Dict[str, int] = Field(default_factory=dict)

class ImpactedItem(BaseModel):
    symbol: str
    symbol_type: str
    filename: str
    stage: int
    impact_reason: str
    risk: ImpactRiskLevel

class ImpactAnalysisRequest(BaseModel):
    target_symbol: Optional[str] = None
    target_file: Optional[str] = None
    action: str = "modify" # "modify", "delete", "rename"
    new_name: Optional[str] = None

class ImpactAnalysisResponse(BaseModel):
    target_symbol: str
    target_type: str
    action: str
    risk_level: ImpactRiskLevel
    impacted_symbols_count: int
    impacted_items: List[ImpactedItem]
    affected_activities: List[str]
    affected_states: List[str]
    broken_transitions: List[str]
    affected_code_generators: List[str]
    breaking_hazards: List[str]
    recommended_mitigations: List[str]

class ReconfigurationRequest(BaseModel):
    deprecated_capability: str
    replacement_capability: str
    target_activities: Optional[List[str]] = None

class ReconfigurationProposal(BaseModel):
    status: str # "success", "no_change_needed", "incompatible"
    explanation: str
    deprecated_capability: str
    replacement_capability: str
    affected_files: List[str] = Field(default_factory=list)
    patches: List[ProposedPatch] = Field(default_factory=list)
    safety_verification: Dict[str, Any] = Field(default_factory=dict)

