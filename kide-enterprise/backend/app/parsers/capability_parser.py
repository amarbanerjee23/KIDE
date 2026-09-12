from typing import Any, Dict
from .dml_parser import DmlParser
from .base_parser import TokenType
from ..schemas.capability import Capability

class CapabilityParser(DmlParser):
    def parse_capability(self) -> Dict[str, Any]:
        self.eat(TokenType.ID, "Capability")
        name = self.parse_estring()
        self.eat(TokenType.ID, "compatible")
        self.eat(TokenType.ID, "component")
        self.eat(TokenType.ID, "interface")
        
        interfaces = [self.parse_qualified_name()]
        while self.try_eat(","):
            interfaces.append(self.parse_qualified_name())
            
        self.eat(TokenType.SYMBOL, "{")
        
        req_init = None
        if self.match("Init"):
            req_init = self.parse_init_action()
            
        control_cap = None
        if self.try_eat("providesControlCapabilities"):
            control_cap = self.parse_control_capabilities()
            
        outcome = None
        if self.try_eat("providesOutcomes"):
            outcome = self.parse_capabilities_outcome()
            
        self.eat(TokenType.SYMBOL, "}")
        
        return {
            "name": name,
            "componentInterface": interfaces,
            "compatible_component_interfaces": interfaces,
            "requiredINITProcess": req_init,
            "required_init_process": req_init,
            "providesControlCapabilities": control_cap,
            "provides_control_capabilities": control_cap,
            "providesOutcomes": outcome,
            "provides_outcomes": outcome
        }

    def parse_action_ref(self) -> str:
        name = self.parse_qualified_name()
        if self.try_eat("("):
            while not self.match(")") and not self.match(TokenType.EOF):
                self.advance_token()
            self.try_eat(")")
        return name

    def parse_init_action(self) -> Dict[str, Any]:
        self.eat(TokenType.ID, "Init")
        self.eat(TokenType.SYMBOL, "{")
        
        action = {
            "raiseAlarms": [],
            "fireCommands": [],
            "publishEvents": [],
            "triggerDataPoints": [],
            "executeOperations": []
        }
        
        while not self.match("}"):
            if self.try_eat("subscribe"):
                if self.try_eat("alarms"):
                    self.eat(TokenType.SYMBOL, "[")
                    while not self.try_eat("]"):
                        action["raiseAlarms"].append({"alarm": self.parse_action_ref()})
                        self.try_eat(",")
                elif self.try_eat("events"):
                    self.eat(TokenType.SYMBOL, "[")
                    while not self.try_eat("]"):
                        action["publishEvents"].append({"event": self.parse_action_ref()})
                        self.try_eat(",")
                elif self.try_eat("data"):
                    self.eat(TokenType.SYMBOL, "[")
                    while not self.try_eat("]"):
                        action["triggerDataPoints"].append({"dataPoint": self.parse_action_ref()})
                        self.try_eat(",")
            elif self.try_eat("fire"):
                if self.current_token.value and self.current_token.value.lower() == "commands":
                    self.advance_token()
                self.eat(TokenType.SYMBOL, "[")
                while not self.try_eat("]"):
                    action["fireCommands"].append({"command": self.parse_action_ref()})
                    self.try_eat(",")
            elif self.try_eat("execute"):
                if self.current_token.value and self.current_token.value.lower() == "operations":
                    self.advance_token()
                self.eat(TokenType.SYMBOL, "[")
                while not self.try_eat("]"):
                    action["executeOperations"].append({"operation": self.parse_action_ref()})
                    self.try_eat(",")
            else:
                self.advance_token()

        self.eat(TokenType.SYMBOL, "}")
        return action

    def parse_control_capabilities(self) -> Dict[str, Any]:
        self.eat(TokenType.SYMBOL, "{")
        res = {"commands": [], "events": [], "alarms": [], "dataPoints": []}
        while not self.match("}"):
            if self.try_eat("fireable"):
                self.eat(TokenType.ID, "commands")
                self.eat(TokenType.SYMBOL, ":")
                res["commands"].append(self.parse_qualified_name())
                while self.try_eat(","):
                    res["commands"].append(self.parse_qualified_name())
            elif self.try_eat("receivable"):
                self.eat(TokenType.ID, "events")
                self.eat(TokenType.SYMBOL, ":")
                res["events"].append(self.parse_qualified_name())
                while self.try_eat(","):
                    res["events"].append(self.parse_qualified_name())
            elif self.try_eat("raised"):
                self.eat(TokenType.ID, "alarms")
                self.eat(TokenType.SYMBOL, ":")
                res["alarms"].append(self.parse_qualified_name())
                while self.try_eat(","):
                    res["alarms"].append(self.parse_qualified_name())
            elif self.try_eat("subscribable"):
                self.eat(TokenType.ID, "DataPoints")
                self.eat(TokenType.SYMBOL, ":")
                res["dataPoints"].append(self.parse_qualified_name())
                while self.try_eat(","):
                    res["dataPoints"].append(self.parse_qualified_name())
        self.eat(TokenType.SYMBOL, "}")
        return res

    def parse_capabilities_outcome(self) -> Dict[str, Any]:
        self.eat(TokenType.SYMBOL, "{")
        res = {"responses": [], "events": [], "alarms": [], "dataPoints": []}
        while not self.match("}"):
            if self.try_eat("receivable"):
                if self.try_eat("responses"):
                    self.try_eat(":")
                    res["responses"].append(self.parse_qualified_name())
                    while self.try_eat(","):
                        res["responses"].append(self.parse_qualified_name())
                elif self.try_eat("events"):
                    self.try_eat(":")
                    res["events"].append(self.parse_qualified_name())
                    while self.try_eat(","):
                        res["events"].append(self.parse_qualified_name())
                elif self.try_eat("alarms"):
                    self.try_eat(":")
                    res["alarms"].append(self.parse_qualified_name())
                    while self.try_eat(","):
                        res["alarms"].append(self.parse_qualified_name())
                elif self.try_eat("dataPoints"):
                    self.try_eat(":")
                    res["dataPoints"].append(self.parse_qualified_name())
                    while self.try_eat(","):
                        res["dataPoints"].append(self.parse_qualified_name())
            else:
                self.advance_token()
        self.eat(TokenType.SYMBOL, "}")
        return res

    def parse(self) -> Dict[str, Any]:
        return self.parse_capability()

    def parse_to_model(self) -> Capability:
        return Capability(**self.parse())

def parse(text: str) -> Dict[str, Any]:
    parser = CapabilityParser(text)
    return parser.parse()
