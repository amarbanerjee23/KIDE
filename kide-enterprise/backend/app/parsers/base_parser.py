import re
from enum import Enum
from typing import List, Optional

class TokenType(Enum):
    KEYWORD = "KEYWORD"
    ID = "ID"
    STRING = "STRING"
    NUMBER = "NUMBER"
    FLOAT = "FLOAT"
    SYMBOL = "SYMBOL"
    EOF = "EOF"

class Token:
    def __init__(self, type_: TokenType, value: str, line: int, column: int):
        self.type = type_
        self.value = value
        self.line = line
        self.column = column

    def __repr__(self):
        return f"Token({self.type}, {self.value!r})"

class Lexer:
    def __init__(self, text: str):
        self.text = text
        self.pos = 0
        self.line = 1
        self.column = 1
        self.current_char = self.text[self.pos] if self.text else None

    def advance(self):
        if self.current_char == '\n':
            self.line += 1
            self.column = 1
        else:
            self.column += 1
        self.pos += 1
        self.current_char = self.text[self.pos] if self.pos < len(self.text) else None

    def skip_whitespace(self):
        while self.current_char is not None and self.current_char.isspace():
            self.advance()

    def skip_comment(self):
        while self.current_char is not None and self.current_char != '\n':
            self.advance()

    def get_next_token(self) -> Token:
        while self.current_char is not None:
            if self.current_char.isspace():
                self.skip_whitespace()
                continue
            
            if self.current_char == '/' and self.pos + 1 < len(self.text) and self.text[self.pos+1] == '/':
                self.skip_comment()
                continue
                
            if self.current_char.isalpha() or self.current_char == '_':
                start_col = self.column
                val = ""
                while self.current_char is not None and (self.current_char.isalnum() or self.current_char == '_'):
                    val += self.current_char
                    self.advance()
                return Token(TokenType.ID, val, self.line, start_col)
            
            if self.current_char.isdigit() or self.current_char == '-':
                start_col = self.column
                val = ""
                if self.current_char == '-':
                    val += self.current_char
                    self.advance()
                while self.current_char is not None and self.current_char.isdigit():
                    val += self.current_char
                    self.advance()
                if self.current_char == '.':
                    val += '.'
                    self.advance()
                    while self.current_char is not None and self.current_char.isdigit():
                        val += self.current_char
                        self.advance()
                    return Token(TokenType.FLOAT, val, self.line, start_col)
                return Token(TokenType.NUMBER, val, self.line, start_col)
            
            if self.current_char in ("'", '"'):
                start_col = self.column
                quote = self.current_char
                val = ""
                self.advance()
                while self.current_char is not None and self.current_char != quote:
                    val += self.current_char
                    self.advance()
                if self.current_char == quote:
                    self.advance()
                return Token(TokenType.STRING, val, self.line, start_col)
                
            if self.current_char in '{}[](),.=><:!':
                start_col = self.column
                val = self.current_char
                self.advance()
                if val in ('=', '>', '<', '!') and self.current_char == '=':
                    val += '='
                    self.advance()
                elif val == '=' and self.current_char == '>':
                    val += '>'
                    self.advance()
                elif val == '-' and self.current_char == '>':
                    val += '>'
                    self.advance()
                return Token(TokenType.SYMBOL, val, self.line, start_col)
                
            # fallback
            val = self.current_char
            start_col = self.column
            self.advance()
            return Token(TokenType.SYMBOL, val, self.line, start_col)

        return Token(TokenType.EOF, "", self.line, self.column)

class BaseParser:
    def __init__(self, text: str):
        self.lexer = Lexer(text)
        self.current_token = self.lexer.get_next_token()

    def error(self, msg: str):
        raise Exception(f"Error at line {self.current_token.line}, col {self.current_token.column}: {msg}. Found '{self.current_token.value}'")

    def eat(self, token_type: TokenType, val: Optional[str] = None):
        if self.current_token.type == token_type:
            if val is not None and self.current_token.value != val:
                self.error(f"Expected '{val}', got '{self.current_token.value}'")
            tok = self.current_token
            self.current_token = self.lexer.get_next_token()
            return tok
        self.error(f"Expected token {token_type} {val or ''}, got {self.current_token.type} '{self.current_token.value}'")

    def eat_id(self) -> str:
        tok = self.eat(TokenType.ID)
        return tok.value

    def eat_string(self) -> str:
        tok = self.eat(TokenType.STRING)
        return tok.value
        
    def peek_val(self) -> str:
        return self.current_token.value
        
    def match(self, val: str) -> bool:
        return self.current_token.value == val
        
    def try_eat(self, val: str) -> bool:
        if self.match(val):
            self.eat(self.current_token.type)
            return True
        return False

    def advance_token(self):
        tok = self.current_token
        self.current_token = self.lexer.get_next_token()
        return tok
