from pydantic import BaseModel
from typing import List, Optional, Any, Union

class ParameterSchema(BaseModel):
    name: str
    type: str # int, boolean, float, string, object, date
    value: Optional[Any] = None

class DataModelSchema(BaseModel):
    name: str
    primitives: List[ParameterSchema] = []
    composites: List[str] = [] # References to other DataModels

class DataPackageSchema(BaseModel):
    name: Optional[str] = None
    data_models: List[DataModelSchema] = []

