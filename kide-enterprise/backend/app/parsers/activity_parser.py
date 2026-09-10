from typing import Any, Dict
from .dml_parser import DmlParser
from .base_parser import TokenType
from ..schemas.activity import ActivityDiagram

class ActivityParser(DmlParser):
    def parse_activity_diagram(self) -> Dict[str, Any]:
        self.eat(TokenType.ID, "ActivityDiagram")
        name = self.parse_estring()
        
        diag = {
            "name": name,
            "dataObjects": [],
            "contextDataModel": [],
            "physicalContexts": [],
            "producesResults": [],
            "activities": []
        }
        
        if self.try_eat("uses"):
            self.eat(TokenType.ID, "Objects")
            self.eat(TokenType.SYMBOL, "[")
            if not self.match("]"):
                if self.peek_val() in ("string", "int", "float", "boolean", "date", "object"):
                    self.parse_qualified_name()
                diag["dataObjects"].append(self.parse_qualified_name())
                while self.try_eat(","):
                    if self.peek_val() in ("string", "int", "float", "boolean", "date", "object"):
                        self.parse_qualified_name()
                    diag["dataObjects"].append(self.parse_qualified_name())
            self.eat(TokenType.SYMBOL, "]")
            
        if self.try_eat("on"):
            self.eat(TokenType.ID, "context")
            diag["contextDataModel"].append(self.parse_qualified_name())
            while self.try_eat(","):
                diag["contextDataModel"].append(self.parse_qualified_name())
                
        if self.try_eat("physical"):
            self.eat(TokenType.ID, "contexts")
            diag["physicalContexts"].append(self.parse_estring())
            while self.try_eat(","):
                diag["physicalContexts"].append(self.parse_estring())
                
        if self.try_eat("produces"):
            self.eat(TokenType.ID, "results")
            self.eat(TokenType.SYMBOL, "(")
            if self.peek_val() in ("string", "int", "float", "boolean", "date", "object"):
                self.parse_qualified_name()
            diag["producesResults"].append(self.parse_qualified_name())
            while self.try_eat(","):
                if self.peek_val() in ("string", "int", "float", "boolean", "date", "object"):
                    self.parse_qualified_name()
                diag["producesResults"].append(self.parse_qualified_name())
            self.eat(TokenType.SYMBOL, ")")
            
        if self.try_eat("has"):
            self.eat(TokenType.ID, "activities")
            self.eat(TokenType.SYMBOL, "{")
            while self.match("Activity"):
                diag["activities"].append(self.parse_activity())
                self.try_eat(",")
            self.eat(TokenType.SYMBOL, "}")
            
        return diag

    def parse_activity(self) -> Dict[str, Any]:
        self.eat(TokenType.ID, "Activity")
        name = self.parse_estring()
        self.eat(TokenType.SYMBOL, "{")
        
        act = {"name": name, "inputParameters": [], "useControlCapabilities": [], "requiresOperation": [], "interruptedBy": [], "interrupts": [], "conditionalActivity": []}
        
        if self.try_eat("description"):
            self.eat(TokenType.SYMBOL, ":")
            act["description"] = self.parse_estring()
            
        if self.try_eat("inputData"):
            self.eat(TokenType.SYMBOL, "{")
            if not self.match("}"):
                act["inputParameters"].append(self.parse_qualified_name())
                while self.try_eat(","):
                    act["inputParameters"].append(self.parse_qualified_name())
            self.eat(TokenType.SYMBOL, "}")
            
        if self.try_eat("requireCapability"):
            self.eat(TokenType.SYMBOL, ":")
            req_cap = self.parse_estring()
            act["requiredCapability"] = req_cap
            act["require_capability"] = req_cap
            if self.try_eat("{"):
                ctrl_items = []
                if not self.match("}"):
                    ctrl_items.append(self.parse_qualified_name())
                    while self.try_eat(","):
                        ctrl_items.append(self.parse_qualified_name())
                self.eat(TokenType.SYMBOL, "}")
                act["useControlCapabilities"] = ctrl_items
            
        if self.try_eat("requireOperation"):
            self.eat(TokenType.SYMBOL, "(")
            act["requiresOperation"].append(self.parse_qualified_name())
            while self.try_eat(","):
                act["requiresOperation"].append(self.parse_qualified_name())
            self.eat(TokenType.SYMBOL, ")")
            
        if self.try_eat("childActivityDiagram"):
            self.eat(TokenType.SYMBOL, ":")
            act["childActivityDiagram"] = self.parse_qualified_name()
            
        if self.try_eat("conditions"):
            self.eat(TokenType.SYMBOL, "{")
            while not self.match("}"):
                self.advance_token() # Simplified skip
            self.eat(TokenType.SYMBOL, "}")
            
        if self.try_eat("nextActivity"):
            self.eat(TokenType.SYMBOL, ":")
            nxt = self.parse_qualified_name()
            act["nextActivity"] = nxt
            act["next_activity"] = nxt

        if self.try_eat("nextActivityDiagram"):
            self.eat(TokenType.SYMBOL, ":")
            nxt_diag = self.parse_qualified_name()
            act["nextActivityDiagram"] = nxt_diag
            act["next_activity_diagram"] = nxt_diag
            
        if self.try_eat("time"):
            self.eat(TokenType.SYMBOL, ":")
            act["time"] = float(self.eat(TokenType.FLOAT).value if self.current_token.type == TokenType.FLOAT else self.eat(TokenType.NUMBER).value)
            act["unit"] = self.eat(TokenType.ID).value
            
        if self.try_eat("interruptedBy"):
            self.eat(TokenType.SYMBOL, "(")
            act["interruptedBy"].append(self.parse_qualified_name())
            while self.try_eat(","):
                act["interruptedBy"].append(self.parse_qualified_name())
            self.eat(TokenType.SYMBOL, ")")
            
        if self.try_eat("interrupts"):
            self.eat(TokenType.SYMBOL, "(")
            act["interrupts"].append(self.parse_qualified_name())
            while self.try_eat(","):
                act["interrupts"].append(self.parse_qualified_name())
            self.eat(TokenType.SYMBOL, ")")
            
        self.eat(TokenType.SYMBOL, "}")
        return act

    def parse(self) -> Dict[str, Any]:
        return self.parse_activity_diagram()

    def parse_to_model(self) -> ActivityDiagram:
        return ActivityDiagram(**self.parse())

def parse(text: str) -> Dict[str, Any]:
    parser = ActivityParser(text)
    return parser.parse()
