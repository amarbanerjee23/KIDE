from typing import Any, Dict
from .base_parser import BaseParser, TokenType
from ..schemas.dml import DataPackage, DataModelDef, SimpleType, AbstractType, ArrayType, PrimitiveValueType

class DmlParser(BaseParser):
    def parse_package(self) -> Dict[str, Any]:
        pkg = {}
        if self.try_eat("Package"):
            pkg["name"] = self.parse_estring()
        
        has_brace = bool(self.try_eat("{"))
        models = []
        while self.match("DataModel"):
            models.append(self.parse_datamodel())
            self.try_eat(",")
        
        if has_brace:
            self.try_eat("}")

        pkg["dataModelCollections"] = models
        pkg["data_models"] = models
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
        typ_name = self.peek_val()
        is_primitive = typ_name in ("int", "boolean", "float", "string", "object", "date")
        
        typ = self.parse_qualified_name()
        
        if self.try_eat("["):
            self.eat(TokenType.SYMBOL, "]")
            name = self.parse_estring()
            param = {
                "name": name,
                "primitiveType": typ if is_primitive else None,
                "dataModelType": typ if not is_primitive else None,
                "kind": "ArrayType"
            }
            if self.try_eat("="):
                self.eat(TokenType.SYMBOL, "[")
                vals = []
                if not self.match("]"):
                    vals.append(self.parse_primitive_value())
                    while self.try_eat(","):
                        vals.append(self.parse_primitive_value())
                self.eat(TokenType.SYMBOL, "]")
                param["values"] = vals
            return param
        else:
            name = self.parse_estring()
            val = None
            if self.try_eat("="):
                val = self.parse_primitive_value()
                
            return {
                "type": typ,
                "name": name,
                "value": val,
                "kind": "SimpleType" if is_primitive else "AbstractType"
            }

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
            return int(self.eat(TokenType.NUMBER).value)
        elif self.current_token.type == TokenType.FLOAT:
            return float(self.eat(TokenType.FLOAT).value)
        elif self.current_token.type == TokenType.STRING:
            return self.eat_string()
        elif self.current_token.value in ("true", "false"):
            return self.eat(TokenType.ID).value == "true"
        else:
            return self.eat_id()

    def parse(self) -> Dict[str, Any]:
        pkg = self.parse_package()
        pkg["data_models"] = pkg.get("dataModelCollections", [])
        return pkg

    def parse_to_model(self) -> DataPackage:
        return DataPackage(**self.parse())

def parse(text: str) -> Dict[str, Any]:
    parser = DmlParser(text)
    return parser.parse()
