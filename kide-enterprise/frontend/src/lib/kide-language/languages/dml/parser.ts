/**
 * Native Resilient DML Parser with Exact Source Ranges and Syntax Diagnostics.
 */

import { Lexer, Token, TokenType } from '../../lexer';
import { DmlPackageNode, DmlDataModelNode, DmlParameterNode, SourceRange, SymbolReference } from '../../ast';

export interface ParseSyntaxError {
  message: string;
  range: SourceRange;
  severity: 'error' | 'warning';
  code: string;
}

export interface DmlParseResult {
  ast: DmlPackageNode;
  errors: ParseSyntaxError[];
  tokens: Token[];
}

export class DmlParser {
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

  public parse(): DmlParseResult {
    const lexer = new Lexer(this.text);
    this.tokens = lexer.tokenize();
    this.idx = 0;
    this.errors = [];

    const startToken = this.peek();
    let packageName = 'DefaultPackage';
    let pkgRange: SourceRange = { ...startToken.range };

    if (this.match('Package')) {
      if (this.peek().type === TokenType.IDENTIFIER || this.peek().type === TokenType.STRING) {
        packageName = this.advance().value;
      } else {
        this.errors.push({
          message: "Expected package identifier after 'Package'",
          range: this.peek().range,
          severity: 'error',
          code: 'KIDE-DML-SYN-001'
        });
      }
    }

    const models: DmlDataModelNode[] = [];

    while (this.peek().type !== TokenType.EOF) {
      if (this.match('DataModel')) {
        const modelNode = this.parseDataModel();
        if (modelNode) models.push(modelNode);
      } else {
        // Skip unexpected token
        const errTok = this.advance();
        if (errTok.type !== TokenType.EOF) {
          this.errors.push({
            message: `Unexpected token '${errTok.value}' outside DataModel definition`,
            range: errTok.range,
            severity: 'error',
            code: 'KIDE-DML-SYN-002'
          });
        }
      }
    }

    const lastToken = this.tokens[Math.max(0, this.tokens.length - 2)] || startToken;
    pkgRange.endLine = lastToken.range.endLine;
    pkgRange.endColumn = lastToken.range.endColumn;

    const ast: DmlPackageNode = {
      type: 'DataPackage',
      id: `dml_pkg_${packageName}`,
      name: packageName,
      range: pkgRange,
      children: models,
      references: [],
      models
    };

    return { ast, errors: this.errors, tokens: this.tokens };
  }

  private parseDataModel(): DmlDataModelNode | null {
    const nameToken = this.peek();
    let modelName = 'UnnamedModel';

    if (nameToken.type === TokenType.IDENTIFIER || nameToken.type === TokenType.STRING) {
      modelName = this.advance().value;
    } else {
      this.errors.push({
        message: "Expected DataModel name identifier",
        range: nameToken.range,
        severity: 'error',
        code: 'KIDE-DML-SYN-003'
      });
    }

    const startRange = { ...nameToken.range };

    if (!this.match('{')) {
      this.errors.push({
        message: `Expected '{' after DataModel '${modelName}'`,
        range: this.peek().range,
        severity: 'error',
        code: 'KIDE-DML-SYN-004'
      });
    }

    const primitives: DmlParameterNode[] = [];
    const composites: SymbolReference[] = [];

    while (this.peek().value !== '}' && this.peek().type !== TokenType.EOF) {
      if (this.match('primitives')) {
        this.parsePrimitivesBlock(primitives);
      } else if (this.match('composites')) {
        this.parseCompositesBlock(composites);
      } else {
        const skipped = this.advance();
        this.errors.push({
          message: `Unexpected block '${skipped.value}'. Expected 'primitives' or 'composites'`,
          range: skipped.range,
          severity: 'error',
          code: 'KIDE-DML-SYN-005'
        });
      }
    }

    let endRange = this.peek().range;
    if (this.match('}')) {
      endRange = this.tokens[this.idx - 1].range;
    } else {
      this.errors.push({
        message: `Unclosed DataModel '${modelName}', missing '}'`,
        range: this.peek().range,
        severity: 'error',
        code: 'KIDE-DML-SYN-006'
      });
    }

    const fullRange: SourceRange = {
      startLine: startRange.startLine,
      startColumn: startRange.startColumn,
      endLine: endRange.endLine,
      endColumn: endRange.endColumn
    };

    return {
      type: 'DataModel',
      id: `dml_model_${modelName}`,
      name: modelName,
      range: fullRange,
      children: primitives,
      references: composites,
      primitives,
      composites
    };
  }

  private parsePrimitivesBlock(outParams: DmlParameterNode[]) {
    if (!this.match('{')) {
      this.errors.push({
        message: "Expected '{' to open primitives block",
        range: this.peek().range,
        severity: 'error',
        code: 'KIDE-DML-SYN-007'
      });
      return;
    }

    while (this.peek().value !== '}' && this.peek().type !== TokenType.EOF) {
      const typeTok = this.advance();
      const valType = typeTok.value;
      const isArray = this.match('[') && this.match(']');

      let pName = 'unnamed';
      if (this.peek().type === TokenType.IDENTIFIER || this.peek().type === TokenType.STRING) {
        pName = this.advance().value;
      } else {
        this.errors.push({
          message: `Expected parameter identifier after type '${valType}'`,
          range: this.peek().range,
          severity: 'error',
          code: 'KIDE-DML-SYN-008'
        });
      }

      let defVal: string | undefined;
      if (this.match('=')) {
        defVal = this.advance().value;
      }

      const paramNode: DmlParameterNode = {
        type: 'Parameter',
        id: `param_${pName}`,
        name: pName,
        range: {
          startLine: typeTok.range.startLine,
          startColumn: typeTok.range.startColumn,
          endLine: this.tokens[this.idx - 1].range.endLine,
          endColumn: this.tokens[this.idx - 1].range.endColumn
        },
        children: [],
        references: [],
        paramType: isArray ? 'array' : 'simple',
        valueType: valType,
        defaultValue: defVal
      };
      outParams.push(paramNode);

      this.match(','); // optional comma
    }

    this.match('}');
  }

  private parseCompositesBlock(outComposites: SymbolReference[]) {
    if (!this.match('{')) return;

    while (this.peek().value !== '}' && this.peek().type !== TokenType.EOF) {
      if (this.peek().type === TokenType.IDENTIFIER || this.peek().type === TokenType.STRING) {
        const compTok = this.advance();
        outComposites.push({
          name: compTok.value,
          kind: 'DataModel',
          range: compTok.range
        });
      } else {
        this.advance();
      }
      this.match(',');
    }
    this.match('}');
  }
}

export function parseDml(text: string): DmlParseResult {
  return new DmlParser(text).parse();
}

