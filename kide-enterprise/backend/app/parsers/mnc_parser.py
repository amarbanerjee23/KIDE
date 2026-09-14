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
        while self.match("InterfaceDescription"):
            systems.append(self.parse_interface_description())
            
        control_node = None
        if self.match("ControlNode"):
            control_node = self.parse_control_node()
            
        iface = systems[0] if systems else None
        return {
            "importSection": imports,
            "imports": imports,
            "name": name,
            "systems": systems,
            "interface_description": iface,
            "interfaceDescription": iface,
            "controlNode": control_node,
            "control_node": control_node
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
        
        res = {
            "name": name,
            "uses": uses,
            "commands": [],
            "events": [],
            "alarms": [],
            "dataPoints": [],
            "data_points": [],
            "responses": [],
            "operatingStates": [],
            "operating_states": [],
            "ip_address": None,
            "ipaddress": None,
            "port": None
        }
        
        while not self.match("}"):
            if self.try_eat("IPaddress") or self.try_eat("ipaddress"):
                self.eat(TokenType.SYMBOL, ":")
                ip_parts = []
                while self.current_token.type in (TokenType.NUMBER, TokenType.FLOAT) or self.current_token.value == ".":
                    ip_parts.append(str(self.current_token.value))
                    self.advance_token()
                ip_str = "".join(ip_parts)
                res["ip_address"] = ip_str
                res["ipaddress"] = ip_str
            elif self.try_eat("port"):
                pname = self.parse_estring()
                val = None
                if self.try_eat("="):
                    val = int(self.eat(TokenType.NUMBER).value)
                res["port"] = {"name": pname, "value": val}
            elif self.try_eat("commands"):
                self.eat(TokenType.SYMBOL, "{")
                while not self.try_eat("}"):
                    asynch = bool(self.try_eat("async"))
                    cname = self.parse_estring()
                    params = []
                    if self.try_eat("["):
                        if not self.match("]"):
                            params.append(self.parse_parameter())
                            while self.try_eat(","):
                                params.append(self.parse_parameter())
                        self.eat(TokenType.SYMBOL, "]")
                    res["commands"].append({"name": cname, "asynch": asynch, "parameters": params})
                    self.try_eat(",")
            elif self.try_eat("events"):
                self.eat(TokenType.SYMBOL, "{")
                while not self.try_eat("}"):
                    pub = bool(self.try_eat("Publish") or self.try_eat("publish"))
                    ename = self.parse_estring()
                    params = []
                    if self.try_eat("["):
                        if not self.match("]"):
                            params.append(self.parse_parameter())
                            while self.try_eat(","):
                                params.append(self.parse_parameter())
                        self.eat(TokenType.SYMBOL, "]")
                    res["events"].append({"name": ename, "publish": pub, "parameters": params})
                    self.try_eat(",")
            elif self.try_eat("alarms"):
                self.eat(TokenType.SYMBOL, "{")
                while not self.try_eat("}"):
                    pub = bool(self.try_eat("Publish") or self.try_eat("publish"))
                    aname = self.parse_estring()
                    params = []
                    if self.try_eat("["):
                        if not self.match("]"):
                            params.append(self.parse_parameter())
                            while self.try_eat(","):
                                params.append(self.parse_parameter())
                        self.eat(TokenType.SYMBOL, "]")
                    level = 0
                    if self.try_eat("level"):
                        self.eat(TokenType.SYMBOL, "=")
                        level = int(self.eat(TokenType.NUMBER).value)
                    res["alarms"].append({"name": aname, "publish": pub, "parameters": params, "level": level})
                    self.try_eat(",")
            elif self.try_eat("dataPoints"):
                self.eat(TokenType.SYMBOL, "{")
                while not self.try_eat("}"):
                    pub = bool(self.try_eat("Publish") or self.try_eat("publish"))
                    typ = None
                    if self.peek_val() in ("int", "boolean", "float", "string", "object", "date"):
                        typ = self.eat_id()
                    dpname = self.parse_estring()
                    val = None
                    if self.try_eat("="):
                        val = self.parse_primitive_value()
                    params = []
                    if self.try_eat("["):
                        if not self.match("]"):
                            params.append(self.parse_parameter())
                            while self.try_eat(","):
                                params.append(self.parse_parameter())
                        self.eat(TokenType.SYMBOL, "]")
                    dp_dict = {"name": dpname, "publish": pub, "type": typ, "value": val, "parameters": params}
                    res["dataPoints"].append(dp_dict)
                    res["data_points"].append(dp_dict)
                    self.try_eat(",")
            elif self.try_eat("operatingStates"):
                self.eat(TokenType.SYMBOL, "{")
                op_states = []
                start_states = []
                end_states = []
                while not self.try_eat("}"):
                    if self.try_eat("startStates"):
                        self.eat(TokenType.SYMBOL, ":")
                        start_states.append(self.parse_estring())
                        while self.try_eat(","):
                            start_states.append(self.parse_estring())
                    elif self.try_eat("endStates"):
                        self.eat(TokenType.SYMBOL, ":")
                        end_states.append(self.parse_estring())
                        while self.try_eat(","):
                            end_states.append(self.parse_estring())
                    else:
                        st_name = self.parse_estring()
                        if self.try_eat("["):
                            self.eat(TokenType.SYMBOL, "]")
                        op_states.append({"name": st_name})
                        self.try_eat(",")
                res["operatingStates"] = op_states
                res["operating_states"] = [s["name"] for s in op_states]
                res["operatingStatesUtility"] = {
                    "operatingStates": op_states,
                    "startStates": start_states,
                    "endStates": end_states
                }
            else:
                self.advance_token()
                
        self.eat(TokenType.SYMBOL, "}")
        return res

    def parse_control_node(self) -> Dict[str, Any]:
        self.eat(TokenType.ID, "ControlNode")
        name = self.parse_estring()
        self.eat(TokenType.ID, "implements")
        self.eat(TokenType.ID, "interface")
        interface = self.parse_qualified_name()
        
        child_nodes = []
        crbs = []
        
        self.eat(TokenType.SYMBOL, "{")
        while not self.match("}"):
            if self.try_eat("childNodes"):
                self.eat(TokenType.SYMBOL, "(")
                child_nodes.append(self.parse_qualified_name())
                while self.try_eat(","):
                    child_nodes.append(self.parse_qualified_name())
                self.eat(TokenType.SYMBOL, ")")
            elif self.try_eat("CommandResponseBlock"):
                self.eat(TokenType.SYMBOL, "{")
                crb_cmd = None
                if self.try_eat("Command"):
                    crb_cmd = self.parse_qualified_name()
                # Skip inside Command block
                if self.try_eat("{"):
                    nest = 1
                    while nest > 0 and self.current_token.type != TokenType.EOF:
                        if self.current_token.value == "{":
                            nest += 1
                        elif self.current_token.value == "}":
                            nest -= 1
                        self.advance_token()
                self.try_eat("}")
                crbs.append({"command": crb_cmd, "name": crb_cmd})
            else:
                self.advance_token()
        self.eat(TokenType.SYMBOL, "}")
        
        return {
            "name": name,
            "interfaceDescription": interface,
            "interface_ref": interface,
            "childNodes": child_nodes,
            "child_nodes": child_nodes,
            "commandResponseBlocks": crbs,
            "command_response_blocks": crbs,
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
