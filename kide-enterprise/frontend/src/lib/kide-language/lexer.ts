/**
 * High-performance, resilient Lexer for KIDE Enterprise DSLs.
 * Produces tokens with exact line, column, and character offset ranges.
 */

import { SourceRange } from './ast';

export enum TokenType {
  KEYWORD = 'KEYWORD',
  IDENTIFIER = 'IDENTIFIER',
  STRING = 'STRING',
  NUMBER = 'NUMBER',
  COMMENT = 'COMMENT',
  OPERATOR = 'OPERATOR',
  PUNCTUATION = 'PUNCTUATION',
  EOF = 'EOF',
  ERROR = 'ERROR'
}

export interface Token {
  type: TokenType;
  value: string;
  range: SourceRange;
  offset: number;
}

export class Lexer {
  private text: string;
  private pos: number = 0;
  private line: number = 1;
  private column: number = 1;

  constructor(text: string) {
    this.text = text;
  }

  private peek(): string | null {
    if (this.pos >= this.text.length) return null;
    return this.text[this.pos];
  }

  private peekNext(): string | null {
    if (this.pos + 1 >= this.text.length) return null;
    return this.text[this.pos + 1];
  }

  private advance(): string | null {
    if (this.pos >= this.text.length) return null;
    const ch = this.text[this.pos];
    this.pos++;
    if (ch === '\n') {
      this.line++;
      this.column = 1;
    } else {
      this.column++;
    }
    return ch;
  }

  public tokenize(includeComments: boolean = false): Token[] {
    const tokens: Token[] = [];
    
    while (this.pos < this.text.length) {
      const ch = this.peek();
      if (!ch) break;

      // Skip whitespace
      if (/\s/.test(ch)) {
        this.advance();
        continue;
      }

      // Single line comment // ...
      if (ch === '/' && this.peekNext() === '/') {
        const startLine = this.line;
        const startCol = this.column;
        const startOffset = this.pos;
        let commentText = '';
        while (this.peek() !== null && this.peek() !== '\n') {
          commentText += this.advance();
        }
        if (includeComments) {
          tokens.push({
            type: TokenType.COMMENT,
            value: commentText,
            range: { startLine, startColumn: startCol, endLine: this.line, endColumn: this.column },
            offset: startOffset
          });
        }
        continue;
      }

      // Multi-line comment /* ... */
      if (ch === '/' && this.peekNext() === '*') {
        const startLine = this.line;
        const startCol = this.column;
        const startOffset = this.pos;
        let commentText = this.advance()! + this.advance()!;
        while (this.pos < this.text.length) {
          if (this.peek() === '*' && this.peekNext() === '/') {
            commentText += this.advance()! + this.advance()!;
            break;
          }
          commentText += this.advance()!;
        }
        if (includeComments) {
          tokens.push({
            type: TokenType.COMMENT,
            value: commentText,
            range: { startLine, startColumn: startCol, endLine: this.line, endColumn: this.column },
            offset: startOffset
          });
        }
        continue;
      }

      // String literal
      if (ch === '"' || ch === "'") {
        const quote = ch;
        const startLine = this.line;
        const startCol = this.column;
        const startOffset = this.pos;
        this.advance(); // consume open quote
        let strVal = '';
        let closed = false;

        while (this.pos < this.text.length) {
          const cur = this.peek();
          if (cur === '\\') {
            this.advance(); // escape
            strVal += this.advance() || '';
          } else if (cur === quote) {
            this.advance(); // consume close quote
            closed = true;
            break;
          } else if (cur === '\n') {
            // Unterminated string on newline
            break;
          } else {
            strVal += this.advance()!;
          }
        }

        tokens.push({
          type: closed ? TokenType.STRING : TokenType.ERROR,
          value: strVal,
          range: { startLine, startColumn: startCol, endLine: this.line, endColumn: this.column },
          offset: startOffset
        });
        continue;
      }

      // Identifiers / Keywords
      if (/[A-Za-z_]/.test(ch)) {
        const startLine = this.line;
        const startCol = this.column;
        const startOffset = this.pos;
        let word = '';
        while (this.peek() !== null && /[A-Za-z0-9_]/.test(this.peek()!)) {
          word += this.advance()!;
        }

        tokens.push({
          type: TokenType.IDENTIFIER,
          value: word,
          range: { startLine, startColumn: startCol, endLine: this.line, endColumn: this.column },
          offset: startOffset
        });
        continue;
      }

      // Numbers
      if (/[0-9]/.test(ch) || (ch === '-' && /[0-9]/.test(this.peekNext() || ''))) {
        const startLine = this.line;
        const startCol = this.column;
        const startOffset = this.pos;
        let numStr = this.advance()!;
        let isFloat = false;

        while (this.peek() !== null && (/[0-9]/.test(this.peek()!) || (this.peek() === '.' && !isFloat))) {
          if (this.peek() === '.') isFloat = true;
          numStr += this.advance()!;
        }

        tokens.push({
          type: TokenType.NUMBER,
          value: numStr,
          range: { startLine, startColumn: startCol, endLine: this.line, endColumn: this.column },
          offset: startOffset
        });
        continue;
      }

      // Multi-character operators: =>, ->, ::, ==, !=, <=, >=
      const twoChar = ch + (this.peekNext() || '');
      if (['=>', '->', '::', '==', '!=', '<=', '>='].includes(twoChar)) {
        const startLine = this.line;
        const startCol = this.column;
        const startOffset = this.pos;
        this.advance();
        this.advance();
        tokens.push({
          type: TokenType.OPERATOR,
          value: twoChar,
          range: { startLine, startColumn: startCol, endLine: this.line, endColumn: this.column },
          offset: startOffset
        });
        continue;
      }

      // Single character delimiters / operators
      const startLine = this.line;
      const startCol = this.column;
      const startOffset = this.pos;
      const single = this.advance()!;
      const isBrace = ['{', '}', '[', ']', '(', ')', '<', '>', ';', ':', ',', '.'].includes(single);

      tokens.push({
        type: isBrace ? TokenType.PUNCTUATION : TokenType.OPERATOR,
        value: single,
        range: { startLine, startColumn: startCol, endLine: this.line, endColumn: this.column },
        offset: startOffset
      });
    }

    tokens.push({
      type: TokenType.EOF,
      value: '',
      range: { startLine: this.line, startColumn: this.column, endLine: this.line, endColumn: this.column },
      offset: this.pos
    });

    return tokens;
  }
}

