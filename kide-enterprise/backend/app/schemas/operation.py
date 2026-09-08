from pydantic import BaseModel
from typing import List, Optional
from app.schemas.dml import ParameterSchema

class OperationSchema(BaseModel):
    name: str
    input_parameters: List[ParameterSchema] = []
    output_parameters: List[ParameterSchema] = []
    executable_script: Optional[str] = None

class OperationDescriptionsSchema(BaseModel):
    operations: List[OperationSchema] = []

