/**
 * Native Resilient Operation DSL Parser with Exact Source Ranges and Parameters.
 */

import { Lexer, Token, TokenType } from '../../lexer';
import { OperationNode, OperationDescriptionsNode, DmlParameterNode, SourceRange } from '../../ast';
import { ParseSyntaxError } from '../dml/parser';

export interface OperationParseResult {
  ast: OperationDescriptionsNode;
  errors: ParseSyntaxError[];
  tokens: Token[];
}

export class OperationParser {
  private tokens: Token[] = [];
  private idx: number = 0;
  private errors: ParseSyntaxError[] = [];

  constructor(private text: string) {}

  private peek(): Token {
    return this.tokens[this.idx] || this.tokens[this.tokens.length - 1];
  }

  private advance(): Token {
    const t = this.peek();
    if (this.idx < this.tokens.length - 1) this.idx++;
    return t;
  }

  private match(val: string): boolean {
    if (this.peek().value === val) {
      this.advance();
      return true;
    }
    return false;
  }

  public parse(): OperationParseResult {
    const lexer = new Lexer(this.text);
    this.tokens = lexer.tokenize();
    this.idx = 0;
    this.errors = [];

    const operations: OperationNode[] = [];
    const startTok = this.peek();

    while (this.peek().type !== TokenType.EOF) {
      if (this.match('Operation')) {
        const op = this.parseOperation();
        if (op) operations.push(op);
      } else {
        const skipped = this.advance();
        if (skipped.type !== TokenType.EOF) {
          this.errors.push({
            message: `Unexpected token '${skipped.value}'. Expected 'Operation'`,
            range: skipped.range,
            severity: 'error',
            code: 'KIDE-OP-SYN-001'
          });
        }
      }
    }

    const lastTok = this.tokens[Math.max(0, this.tokens.length - 2)] || startTok;
    const overallRange: SourceRange = {
      startLine: startTok.range.startLine,
      startColumn: startTok.range.startColumn,
      endLine: lastTok.range.endLine,
      endColumn: lastTok.range.endColumn
    };

    return {
      ast: {
        type: 'OperationDescriptions',
        id: 'op_descriptions',
        operations,
        range: overallRange,
        children: operations,
        references: []
      },
      errors: this.errors,
      tokens: this.tokens
    };
  }

  private parseOperation(): OperationNode | null {
    const nameTok = this.peek();
    let opName = 'UnnamedOperation';

    if (nameTok.type === TokenType.IDENTIFIER || nameTok.type === TokenType.STRING) {
      opName = this.advance().value;
    } else {
      this.errors.push({
        message: "Expected Operation name identifier",
        range: nameTok.range,
        severity: 'error',
        code: 'KIDE-OP-SYN-002'
      });
    }

    const inputs: DmlParameterNode[] = [];
    if (this.match('(')) {
      while (this.peek().value !== ')' && this.peek().type !== TokenType.EOF) {
        if (this.peek().type === TokenType.IDENTIFIER || this.peek().type === TokenType.STRING) {
          const pTypeTok = this.advance();
          let pNameTok = pTypeTok;
          if (this.peek().value !== ',' && this.peek().value !== ')' && this.peek().type !== TokenType.EOF) {
            pNameTok = this.advance();
          }
          inputs.push({
            type: 'Parameter',
            id: `op_param_${pNameTok.value}`,
            name: pNameTok.value,
            range: {
              startLine: pTypeTok.range.startLine,
              startColumn: pTypeTok.range.startColumn,
              endLine: pNameTok.range.endLine,
              endColumn: pNameTok.range.endColumn
            },
            children: [],
            references: [],
            paramType: 'simple',
            valueType: pTypeTok.value
          });
        } else {
          this.advance();
        }
        this.match(',');
      }
      this.match(')');
    }

    this.match('{');

    let execScript: string | undefined;
    let outputParam: DmlParameterNode | undefined;

    while (this.peek().value !== '}' && this.peek().type !== TokenType.EOF) {
      if (this.match('execute')) {
        if (this.peek().type === TokenType.STRING || this.peek().type === TokenType.IDENTIFIER) {
          execScript = this.advance().value;
        }
      } else if (this.match('return')) {
        const rType = this.advance();
        let rName = 'result';
        if (this.peek().type === TokenType.IDENTIFIER || this.peek().type === TokenType.STRING) {
          rName = this.advance().value;
        }
        outputParam = {
          type: 'Parameter',
          id: `op_ret_${opName}`,
          name: rName,
          range: {
            startLine: rType.range.startLine,
            startColumn: rType.range.startColumn,
            endLine: this.tokens[this.idx - 1].range.endLine,
            endColumn: this.tokens[this.idx - 1].range.endColumn
          },
          children: [],
          references: [],
          paramType: 'simple',
          valueType: rType.value
        };
      } else {
        this.advance();
      }
    }

    this.match('}');

    const lastTok = this.tokens[this.idx - 1] || nameTok;
    return {
      type: 'Operation',
      id: `op_${opName}`,
      name: opName,
      range: {
        startLine: nameTok.range.startLine,
        startColumn: nameTok.range.startColumn,
        endLine: lastTok.range.endLine,
        endColumn: lastTok.range.endColumn
      },
      children: [],
      references: [],
      inputParameters: inputs,
      outputParameters: outputParam,
      executableScript: execScript
    };
  }
}

export function parseOperation(text: string): OperationParseResult {
  return new OperationParser(text).parse();
}
