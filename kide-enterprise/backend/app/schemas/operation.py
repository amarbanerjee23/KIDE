from typing import List, Optional
from pydantic import BaseModel, Field
from .dml import Parameter

class Operation(BaseModel):
    name: str
    inputParameters: List[Parameter] = Field(default_factory=list)
    executableScript: Optional[str] = None
    outputParameters: Optional[Parameter] = None

class OperationDescriptions(BaseModel):
    operations: List[Operation] = Field(default_factory=list)
