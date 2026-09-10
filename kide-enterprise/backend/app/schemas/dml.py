from typing import List, Optional, Union, Any, ForwardRef
from enum import Enum
from pydantic import BaseModel, Field

class PrimitiveValueType(str, Enum):
    int = 'int'
    boolean = 'boolean'
    float = 'float'
    string = 'string'
    object = 'object'
    date = 'date'

class BaseValue(BaseModel):
    pass

class IntValue(BaseValue):
    value: int

class FloatValue(BaseValue):
    value: float

class StringValue(BaseValue):
    value: str

class BoolValue(BaseValue):
    value: bool

class DateValue(BaseValue):
    day: int
    month: int
    year: int

class AbstractObjectValue(BaseValue):
    abstractValue: str

class ArrayValues(BaseValue):
    values: List[Union['IntValue', 'FloatValue', 'StringValue', 'BoolValue', 'DateValue', 'AbstractObjectValue', 'ArrayValues']] = Field(default_factory=list)

PrimitiveValue = Union[IntValue, FloatValue, StringValue, BoolValue, DateValue, ArrayValues, AbstractObjectValue]

class SimpleType(BaseModel):
    type: PrimitiveValueType
    name: str
    value: Optional[PrimitiveValue] = None

class AbstractType(BaseModel):
    type: str
    name: str
    value: Optional[AbstractObjectValue] = None

class ArrayType(BaseModel):
    primitiveType: Optional[PrimitiveValueType] = None
    dataModelType: Optional[str] = None
    name: str
    values: List[PrimitiveValue] = Field(default_factory=list)

Parameter = Union[SimpleType, AbstractType, ArrayType]

DataModel = ForwardRef('DataModel')

class DataModelDef(BaseModel):
    name: str
    primitives: List[Parameter] = Field(default_factory=list)
    composites: List[str] = Field(default_factory=list)

class DataPackage(BaseModel):
    name: Optional[str] = None
    dataModelCollections: List[DataModelDef] = Field(default_factory=list)

ArrayValues.model_rebuild()
