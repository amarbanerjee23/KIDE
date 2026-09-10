from typing import Any, Dict
from .dml_parser import DmlParser
from .base_parser import TokenType
from ..schemas.operation import OperationDescriptions, Operation

class OperationParser(DmlParser):
    def parse_operations(self) -> Dict[str, Any]:
        operations = []
        while self.match("Operation"):
            operations.append(self.parse_operation())
        return {"operations": operations}

    def parse_operation(self) -> Dict[str, Any]:
        self.eat(TokenType.ID, "Operation")
        name = self.parse_estring()
        self.eat(TokenType.SYMBOL, "(")
        
        inputs = []
        if not self.match(")"):
            inputs.append(self.parse_parameter())
            while self.try_eat(","):
                inputs.append(self.parse_parameter())
        self.eat(TokenType.SYMBOL, ")")
        
        self.eat(TokenType.SYMBOL, "{")
        
        exec_script = None
        if self.try_eat("execute"):
            exec_script = self.parse_estring()
            
        outputs = None
        if self.try_eat("return"):
            outputs = self.parse_parameter()
            
        self.eat(TokenType.SYMBOL, "}")
        
        return {
            "name": name,
            "inputParameters": inputs,
            "input_parameters": inputs,
            "executableScript": exec_script,
            "executable_script": exec_script,
            "outputParameters": outputs,
            "output_parameters": outputs
        }

    def parse(self) -> Dict[str, Any]:
        return self.parse_operations()

    def parse_to_model(self) -> OperationDescriptions:
        return OperationDescriptions(**self.parse())

def parse(text: str) -> Dict[str, Any]:
    parser = OperationParser(text)
    return parser.parse()
