/**
 * Native Resilient Activity DSL Parser with Exact Source Ranges and Hierarchical Activities.
 */

import { Lexer, Token, TokenType } from '../../lexer';
import { ActivityDiagramNode, ActivityNode, SymbolReference } from '../../ast';
import { ParseSyntaxError } from '../dml/parser';

export interface ActivityParseResult {
  ast: ActivityDiagramNode | null;
  errors: ParseSyntaxError[];
  tokens: Token[];
}

export class ActivityParser {
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

  public parse(): ActivityParseResult {
    const lexer = new Lexer(this.text);
    this.tokens = lexer.tokenize();
    this.idx = 0;
    this.errors = [];

    let diagramNode: ActivityDiagramNode | null = null;

    while (this.peek().type !== TokenType.EOF) {
      if (this.match('ActivityDiagram')) {
        diagramNode = this.parseActivityDiagram();
        break;
      } else {
        const skipped = this.advance();
        if (skipped.type !== TokenType.EOF) {
          this.errors.push({
            message: `Unexpected token '${skipped.value}'. Expected 'ActivityDiagram'`,
            range: skipped.range,
            severity: 'error',
            code: 'KIDE-ACT-SYN-001'
          });
        }
      }
    }

    return { ast: diagramNode, errors: this.errors, tokens: this.tokens };
  }

  private parseActivityDiagram(): ActivityDiagramNode {
    const nameTok = this.peek();
    let diagName = 'UnnamedDiagram';
    if (nameTok.type === TokenType.IDENTIFIER || nameTok.type === TokenType.STRING) {
      diagName = this.advance().value;
    } else {
      this.errors.push({
        message: "Expected ActivityDiagram name identifier",
        range: nameTok.range,
        severity: 'error',
        code: 'KIDE-ACT-SYN-002'
      });
    }

    let contextDataModel: SymbolReference | undefined;
    const physicalContexts: string[] = [];

    // 'on' 'context' <DataModel>
    if (this.match('on') && this.match('context')) {
      if (this.peek().type === TokenType.IDENTIFIER || this.peek().type === TokenType.STRING) {
        const ctxTok = this.advance();
        contextDataModel = {
          name: ctxTok.value,
          kind: 'DataModel',
          range: ctxTok.range
        };
      }
    }

    // 'has' 'activities' '{'
    while (this.peek().value !== '{' && this.peek().type !== TokenType.EOF) {
      if (this.match('has') && this.match('activities')) {
        this.match('{');
        break;
      }
      this.advance();
    }

    const activities: ActivityNode[] = [];
    while (this.peek().value !== '}' && this.peek().type !== TokenType.EOF) {
      if (this.match('Activity')) {
        const act = this.parseActivity();
        if (act) activities.push(act);
      } else {
        this.advance();
      }
    }

    this.match('}');

    const lastTok = this.tokens[Math.max(0, this.tokens.length - 2)] || nameTok;
    const allRefs: SymbolReference[] = [];
    if (contextDataModel) allRefs.push(contextDataModel);
    for (const act of activities) {
      allRefs.push(...act.references);
    }

    return {
      type: 'ActivityDiagram',
      id: `act_diag_${diagName}`,
      name: diagName,
      range: {
        startLine: nameTok.range.startLine,
        startColumn: nameTok.range.startColumn,
        endLine: lastTok.range.endLine,
        endColumn: lastTok.range.endColumn
      },
      children: activities,
      references: allRefs,
      contextDataModel,
      physicalContexts,
      results: [],
      activities
    };
  }

  private parseActivity(): ActivityNode | null {
    const nameTok = this.peek();
    let actName = 'UnnamedActivity';
    if (nameTok.type === TokenType.IDENTIFIER || nameTok.type === TokenType.STRING) {
      actName = this.advance().value;
    } else {
      this.errors.push({
        message: "Expected Activity name identifier",
        range: nameTok.range,
        severity: 'error',
        code: 'KIDE-ACT-SYN-003'
      });
    }

    this.match('{');

    let description: string | undefined;
    let reqCap: SymbolReference | undefined;
    const reqOps: SymbolReference[] = [];
    let nextAct: SymbolReference | undefined;
    const conditions: any[] = [];
    const references: SymbolReference[] = [];

    while (this.peek().value !== '}' && this.peek().type !== TokenType.EOF) {
      if (this.match('description')) {
        this.match(':');
        if (this.peek().type === TokenType.STRING || this.peek().type === TokenType.IDENTIFIER) {
          description = this.advance().value;
        }
      } else if (this.match('requireCapability')) {
        this.match(':');
        if (this.peek().type === TokenType.IDENTIFIER || this.peek().type === TokenType.STRING) {
          const capTok = this.advance();
          reqCap = { name: capTok.value, kind: 'Capability', range: capTok.range };
          references.push(reqCap);
        }
      } else if (this.match('requireOperation')) {
        this.match('(');
        while (this.peek().value !== ')' && this.peek().type !== TokenType.EOF) {
          if (this.peek().type === TokenType.IDENTIFIER || this.peek().type === TokenType.STRING) {
            const opTok = this.advance();
            const opRef: SymbolReference = { name: opTok.value, kind: 'Operation', range: opTok.range };
            reqOps.push(opRef);
            references.push(opRef);
          } else {
            this.advance();
          }
          this.match(',');
        }
        this.match(')');
      } else if (this.match('nextActivity')) {
        this.match(':');
        if (this.peek().type === TokenType.IDENTIFIER || this.peek().type === TokenType.STRING) {
          const nextTok = this.advance();
          nextAct = { name: nextTok.value, kind: 'Activity', range: nextTok.range };
          references.push(nextAct);
        }
      } else if (this.match('conditions')) {
        this.match('{');
        while (this.peek().value !== '}' && this.peek().type !== TokenType.EOF) {
          if (this.match('=>') && this.match('nextActivity')) {
            this.match(':');
            if (this.peek().type === TokenType.IDENTIFIER) {
              const condTok = this.advance();
              const condRef: SymbolReference = { name: condTok.value, kind: 'Activity', range: condTok.range };
              references.push(condRef);
              conditions.push({ target: condTok.value, range: condTok.range });
            }
          } else {
            this.advance();
          }
        }
        this.match('}');
      } else {
        this.advance();
      }
    }

    this.match('}');

    const lastTok = this.tokens[this.idx - 1] || nameTok;
    return {
      type: 'Activity',
      id: `act_${actName}`,
      name: actName,
      range: {
        startLine: nameTok.range.startLine,
        startColumn: nameTok.range.startColumn,
        endLine: lastTok.range.endLine,
        endColumn: lastTok.range.endColumn
      },
      children: [],
      references,
      description,
      inputParameters: [],
      requiredCapability: reqCap,
      requiredOperations: reqOps,
      nextActivity: nextAct,
      conditions
    };
  }
}

export function parseActivity(text: string): ActivityParseResult {
  return new ActivityParser(text).parse();
}
