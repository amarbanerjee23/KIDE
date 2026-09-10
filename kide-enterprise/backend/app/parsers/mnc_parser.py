from typing import Any, Dict
from .dml_parser import DmlParser
from .base_parser import TokenType
from ..schemas.mnc import Model

class MncParser(DmlParser):
    def parse_model(self) -> Dict[str, Any]:
        imports = []
        while self.try_eat("import"):
            imports.append({"importedNamespace": self.parse_qualified_name()})
            
        self.eat(TokenType.ID, "Model")
        name = self.parse_estring()
        
        systems = []
        # simplified interface description parsing
        while self.match("InterfaceDescription"):
            systems.append(self.parse_interface_description())
            
        control_node = None
        if self.match("ControlNode"):
            control_node = self.parse_control_node()
            
        return {
            "importSection": imports,
            "name": name,
            "systems": systems,
            "controlNode": control_node
        }

    def parse_interface_description(self) -> Dict[str, Any]:
        self.eat(TokenType.ID, "InterfaceDescription")
        name = self.parse_estring()
        
        uses = []
        if self.try_eat("uses"):
            uses.append(self.parse_qualified_name())
            while self.try_eat(","):
                uses.append(self.parse_qualified_name())
                
        self.eat(TokenType.SYMBOL, "{")
        
        # Simplified parsing of contents
        res = {"name": name, "uses": uses, "commands": [], "events": [], "alarms": [], "dataPoints": [], "responses": []}
        
        while not self.match("}"):
            if self.try_eat("commands"):
                self.eat(TokenType.SYMBOL, "{")
                while not self.try_eat("}"):
                    self.lexer.advance()
            else:
                self.lexer.advance()
                
        self.eat(TokenType.SYMBOL, "}")
        return res

    def parse_control_node(self) -> Dict[str, Any]:
        self.eat(TokenType.ID, "ControlNode")
        name = self.parse_estring()
        self.eat(TokenType.ID, "implements")
        self.eat(TokenType.ID, "interface")
        interface = self.parse_qualified_name()
        
        self.eat(TokenType.SYMBOL, "{")
        while not self.match("}"):
            self.lexer.advance()
        self.eat(TokenType.SYMBOL, "}")
        
        return {
            "name": name,
            "interfaceDescription": interface,
            "childNodes": [],
            "commandResponseBlocks": [],
            "eventBlocks": [],
            "alarmBlocks": [],
            "dataPointBlocks": []
        }

    def parse(self) -> Dict[str, Any]:
        return self.parse_model()

    def parse_to_model(self) -> Model:
        return Model(**self.parse())

def parse(text: str) -> Dict[str, Any]:
    parser = MncParser(text)
    return parser.parse()
