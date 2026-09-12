from typing import List, Optional, Dict, Any
from pydantic import BaseModel, ConfigDict

class ChatMessage(BaseModel):
    role: str # user, assistant, system
    content: str

class ToolCallRecord(BaseModel):
    id: str
    tool: str
    input: Dict[str, Any]
    output: Any
    status: str = "success"

class ProposedPatch(BaseModel):
    filename: str
    action: str = "modify" # modify, create, delete
    diff: str
    new_content: str
    rationale: str

class AIChatRequest(BaseModel):
    project_id: int
    messages: List[ChatMessage]
    context_file: Optional[str] = None
    provider: Optional[str] = None
    model: Optional[str] = None

class AIChatResponse(BaseModel):
    session_id: str
    provider: str
    model: str
    message: str
    tool_calls: List[ToolCallRecord] = []
    proposed_patches: List[ProposedPatch] = []
    provenance_id: Optional[int] = None

class ApplyPatchRequest(BaseModel):
    project_id: int
    provenance_id: Optional[int] = None
    patches: List[ProposedPatch]

class ApplyPatchResponse(BaseModel):
    status: str
    applied_files: List[str]
    timestamp: str

class AIProvenanceResponse(BaseModel):
    id: int
    project_id: int
    session_id: str
    provider: str
    model_name: str
    user_prompt: str
    tool_calls: Optional[List[Dict[str, Any]]] = None
    assistant_response: str
    proposed_patches: Optional[List[Dict[str, Any]]] = None
    status: str
    created_at: str

    model_config = ConfigDict(from_attributes=True)

class AIProvidersResponse(BaseModel):
    available_providers: List[str]
    active_provider: str
    active_model: str
    has_gemini_key: bool
    has_openai_key: bool
    has_anthropic_key: bool

