from typing import Any, Dict
from .base_parser import BaseParser, TokenType
from ..schemas.dml import DataPackage, DataModelDef, SimpleType, AbstractType, ArrayType, PrimitiveValueType

class DmlParser(BaseParser):
    def parse_package(self) -> Dict[str, Any]:
        pkg = {}
        if self.try_eat("Package"):
            pkg["name"] = self.parse_estring()
        
        models = []
        while self.match("DataModel"):
            models.append(self.parse_datamodel())
        
        pkg["dataModelCollections"] = models
        return pkg

    def parse_datamodel(self) -> Dict[str, Any]:
        self.eat(TokenType.ID, "DataModel")
        name = self.parse_estring()
        self.eat(TokenType.SYMBOL, "{")
        
        primitives = []
        if self.try_eat("primitives"):
            self.eat(TokenType.SYMBOL, "{")
            primitives.append(self.parse_parameter())
            while self.try_eat(","):
                primitives.append(self.parse_parameter())
            self.eat(TokenType.SYMBOL, "}")
            
        composites = []
        if self.try_eat("composites"):
            self.eat(TokenType.SYMBOL, "{")
            composites.append(self.parse_qualified_name())
            while self.try_eat(","):
                composites.append(self.parse_qualified_name())
            self.eat(TokenType.SYMBOL, "}")
            
        self.eat(TokenType.SYMBOL, "}")
        
        return {
            "name": name,
            "primitives": primitives,
            "composites": composites
        }

    def parse_parameter(self) -> Dict[str, Any]:
        # Peek to determine parameter type
        typ_name = self.peek_val()
        is_primitive = typ_name in ("int", "boolean", "float", "string", "object", "date")
        
        # Check if it's an array type
        is_array = False
        saved_pos = self.lexer.pos
        # Note: A real implementation would save lexer state properly. For this prompt, 
        # we will distinguish based on basic parsing since this is a simplified descent.
        
        # We will parse SimpleType for now if primitive, Abstract if not
        # Let's simplify and build the structure:
        typ = self.parse_qualified_name()
        
        if self.try_eat("["):
            self.eat(TokenType.SYMBOL, "]")
            name = self.parse_estring()
            param = {"name": name, "primitiveType": typ if is_primitive else None, "dataModelType": typ if not is_primitive else None}
            if self.try_eat("="):
                self.eat(TokenType.SYMBOL, "[")
                vals = []
                if not self.match("]"):
                    vals.append(self.parse_primitive_value())
                    while self.try_eat(","):
                        vals.append(self.parse_primitive_value())
                self.eat(TokenType.SYMBOL, "]")
                param["values"] = vals
            return {"ArrayType": param}
        else:
            name = self.parse_estring()
            val = None
            if self.try_eat("="):
                val = self.parse_primitive_value()
                
            if is_primitive:
                return {"SimpleType": {"type": typ, "name": name, "value": val}}
            else:
                return {"AbstractType": {"type": typ, "name": name, "value": val}}

    def parse_qualified_name(self) -> str:
        name = self.eat(TokenType.ID).value
        while self.try_eat("."):
            if self.try_eat("*"):
                name += ".*"
                break
            name += "." + self.eat(TokenType.ID).value
        return name

    def parse_estring(self) -> str:
        if self.current_token.type == TokenType.STRING:
            return self.eat_string()
        return self.eat_id()

    def parse_primitive_value(self) -> Any:
        if self.current_token.type == TokenType.NUMBER:
            return {"IntValue": {"value": int(self.eat(TokenType.NUMBER).value)}}
        elif self.current_token.type == TokenType.FLOAT:
            return {"FloatValue": {"value": float(self.eat(TokenType.FLOAT).value)}}
        elif self.current_token.type == TokenType.STRING:
            return {"StringValue": {"value": self.eat_string()}}
        elif self.current_token.value in ("true", "false"):
            val = self.eat(TokenType.ID).value == "true"
            return {"BoolValue": {"value": val}}
        else:
            return {"AbstractObjectValue": {"abstractValue": self.eat_id()}}

    def parse(self) -> Dict[str, Any]:
        pkg = self.parse_package()
        pkg["data_models"] = pkg.get("dataModelCollections", [])
        return pkg

    def parse_to_model(self) -> DataPackage:
        return DataPackage(**self.parse())

def parse(text: str) -> Dict[str, Any]:
    parser = DmlParser(text)
    return parser.parse()
