/**
 * Native Resilient Capability DSL Parser with Exact Source Ranges and Interface Bindings.
 */

import { Lexer, Token, TokenType } from '../../lexer';
import { 
  CapabilityNode, 
  ControlCapabilitiesNode, 
  CapabilityOutcomeNode, 
  CapabilityActionNode, 
  SourceRange, 
  SymbolReference 
} from '../../ast';
import { ParseSyntaxError } from '../dml/parser';

export interface CapabilityParseResult {
  ast: CapabilityNode | null;
  errors: ParseSyntaxError[];
  tokens: Token[];
}

export class CapabilityParser {
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

  public parse(): CapabilityParseResult {
    const lexer = new Lexer(this.text);
    this.tokens = lexer.tokenize();
    this.idx = 0;
    this.errors = [];

    let capNode: CapabilityNode | null = null;

    while (this.peek().type !== TokenType.EOF) {
      if (this.match('Capability')) {
        capNode = this.parseCapability();
        break;
      } else {
        const skipped = this.advance();
        if (skipped.type !== TokenType.EOF) {
          this.errors.push({
            message: `Unexpected token '${skipped.value}'. Expected 'Capability'`,
            range: skipped.range,
            severity: 'error',
            code: 'KIDE-CAP-SYN-001'
          });
        }
      }
    }

    return { ast: capNode, errors: this.errors, tokens: this.tokens };
  }

  private parseCapability(): CapabilityNode {
    const nameTok = this.peek();
    let capName = 'UnnamedCapability';
    if (nameTok.type === TokenType.IDENTIFIER || nameTok.type === TokenType.STRING) {
      capName = this.advance().value;
    } else {
      this.errors.push({
        message: "Expected Capability name identifier",
        range: nameTok.range,
        severity: 'error',
        code: 'KIDE-CAP-SYN-002'
      });
    }

    const interfaces: SymbolReference[] = [];

    // 'compatible component interface' <Interfaces>
    if (this.match('compatible')) {
      this.match('component');
      this.match('interface');

      while (this.peek().value !== '{' && this.peek().type !== TokenType.EOF) {
        if (this.peek().type === TokenType.IDENTIFIER || this.peek().type === TokenType.STRING) {
          const ifTok = this.advance();
          interfaces.push({
            name: ifTok.value,
            kind: 'InterfaceDescription',
            range: ifTok.range
          });
        } else {
          this.advance();
        }
        this.match(',');
      }
    }

    this.match('{');

    let initAction: CapabilityActionNode | undefined;
    let controlCaps: ControlCapabilitiesNode | undefined;
    let outcomes: CapabilityOutcomeNode | undefined;

    while (this.peek().value !== '}' && this.peek().type !== TokenType.EOF) {
      if (this.match('Init')) {
        initAction = this.parseInitBlock();
      } else if (this.match('providesControlCapabilities')) {
        controlCaps = this.parseControlCapabilities();
      } else if (this.match('providesOutcomes')) {
        outcomes = this.parseOutcomes();
      } else {
        const skipped = this.advance();
        this.errors.push({
          message: `Unexpected block '${skipped.value}' in Capability`,
          range: skipped.range,
          severity: 'error',
          code: 'KIDE-CAP-SYN-003'
        });
      }
    }

    this.match('}');

    const allRefs: SymbolReference[] = [...interfaces];
    if (controlCaps) {
      allRefs.push(...controlCaps.commands, ...controlCaps.events, ...controlCaps.alarms, ...controlCaps.dataPoints);
    }
    if (initAction) {
      allRefs.push(...initAction.fireCommands, ...initAction.raiseAlarms, ...initAction.publishEvents, ...initAction.triggerDataPoints, ...initAction.executeOperations);
    }
    if (outcomes) {
      allRefs.push(...outcomes.responses, ...outcomes.events, ...outcomes.alarms, ...outcomes.dataPoints);
    }

    const lastTok = this.tokens[Math.max(0, this.tokens.length - 2)] || nameTok;
    const fullRange: SourceRange = {
      startLine: nameTok.range.startLine,
      startColumn: nameTok.range.startColumn,
      endLine: lastTok.range.endLine,
      endColumn: lastTok.range.endColumn
    };

    return {
      type: 'Capability',
      id: `cap_${capName}`,
      name: capName,
      range: fullRange,
      children: [],
      references: allRefs,
      componentInterfaces: interfaces,
      initAction,
      controlCapabilities: controlCaps,
      outcomes
    };
  }

  private parseInitBlock(): CapabilityActionNode {
    const startTok = this.peek();
    this.match('{');

    const fireCommands: SymbolReference[] = [];
    const raiseAlarms: SymbolReference[] = [];
    const publishEvents: SymbolReference[] = [];
    const triggerDataPoints: SymbolReference[] = [];
    const executeOperations: SymbolReference[] = [];

    while (this.peek().value !== '}' && this.peek().type !== TokenType.EOF) {
      if (this.match('fire') && this.match('Commands')) {
        this.parseBracketedRefs(fireCommands, 'Command');
      } else if (this.match('subscribe') && this.match('alarms')) {
        this.parseBracketedRefs(raiseAlarms, 'Alarm');
      } else if (this.match('subscribe') && this.match('events')) {
        this.parseBracketedRefs(publishEvents, 'Event');
      } else if (this.match('subscribe') && this.match('data')) {
        this.parseBracketedRefs(triggerDataPoints, 'DataPoint');
      } else if (this.match('execute') && this.match('Operations')) {
        this.parseBracketedRefs(executeOperations, 'Operation');
      } else {
        this.advance();
      }
    }
    this.match('}');

    return {
      type: 'Action',
      id: `cap_init_${startTok.range.startLine}`,
      range: { startLine: startTok.range.startLine, startColumn: startTok.range.startColumn, endLine: this.peek().range.endLine, endColumn: this.peek().range.endColumn },
      children: [],
      references: [...fireCommands, ...raiseAlarms, ...publishEvents, ...triggerDataPoints, ...executeOperations],
      fireCommands,
      raiseAlarms,
      publishEvents,
      triggerDataPoints,
      executeOperations
    };
  }

  private parseBracketedRefs(outList: SymbolReference[], kind: any) {
    if (!this.match('[')) return;
    while (this.peek().value !== ']' && this.peek().type !== TokenType.EOF) {
      if (this.peek().type === TokenType.IDENTIFIER || this.peek().type === TokenType.STRING) {
        const tok = this.advance();
        outList.push({ name: tok.value, kind, range: tok.range });

        // Handle optional arguments like `()` or `(param1, param2)`
        if (this.match('(')) {
          while (this.peek().value !== ')' && this.peek().type !== TokenType.EOF) {
            this.advance();
          }
          this.match(')');
        }

        // Handle optional response block: -> expected ...
        if (this.match('->')) {
          while (this.peek().value !== ',' && this.peek().value !== ']' && this.peek().type !== TokenType.EOF) {
            this.advance();
          }
        }
      } else {
        this.advance();
      }
      this.match(',');
    }
    this.match(']');
  }

  private parseControlCapabilities(): ControlCapabilitiesNode {
    const startTok = this.peek();
    this.match('{');
    const commands: SymbolReference[] = [];
    const events: SymbolReference[] = [];
    const alarms: SymbolReference[] = [];
    const dataPoints: SymbolReference[] = [];

    while (this.peek().value !== '}' && this.peek().type !== TokenType.EOF) {
      if (this.match('fireable') && this.match('commands')) {
        this.match(':');
        this.parseColonRefs(commands, 'Command');
      } else if (this.match('receivable') && this.match('events')) {
        this.match(':');
        this.parseColonRefs(events, 'Event');
      } else if (this.match('raised') && this.match('alarms')) {
        this.match(':');
        this.parseColonRefs(alarms, 'Alarm');
      } else if (this.match('subscribable') && (this.match('DataPoints') || this.match('dataPoints'))) {
        this.match(':');
        this.parseColonRefs(dataPoints, 'DataPoint');
      } else {
        this.advance();
      }
    }
    this.match('}');

    return {
      type: 'ControlCapabilities',
      id: `cap_ctrl_${startTok.range.startLine}`,
      range: { startLine: startTok.range.startLine, startColumn: startTok.range.startColumn, endLine: this.peek().range.endLine, endColumn: this.peek().range.endColumn },
      children: [],
      references: [...commands, ...events, ...alarms, ...dataPoints],
      commands,
      events,
      alarms,
      dataPoints
    };
  }

  private parseOutcomes(): CapabilityOutcomeNode {
    const startTok = this.peek();
    this.match('{');
    const responses: SymbolReference[] = [];
    const events: SymbolReference[] = [];
    const alarms: SymbolReference[] = [];
    const dataPoints: SymbolReference[] = [];

    while (this.peek().value !== '}' && this.peek().type !== TokenType.EOF) {
      if (this.match('receivable') && this.match('responses')) {
        this.parseColonRefs(responses, 'Command');
      } else if (this.match('receivable') && this.match('events')) {
        this.parseColonRefs(events, 'Event');
      } else if (this.match('receivable') && this.match('alarms')) {
        this.parseColonRefs(alarms, 'Alarm');
      } else if (this.match('receivable') && this.match('dataPoints')) {
        this.parseColonRefs(dataPoints, 'DataPoint');
      } else {
        this.advance();
      }
    }
    this.match('}');

    return {
      type: 'CapabilitiesOutcome',
      id: `cap_outcome_${startTok.range.startLine}`,
      range: { startLine: startTok.range.startLine, startColumn: startTok.range.startColumn, endLine: this.peek().range.endLine, endColumn: this.peek().range.endColumn },
      children: [],
      references: [...responses, ...events, ...alarms, ...dataPoints],
      responses,
      events,
      alarms,
      dataPoints
    };
  }

  private parseColonRefs(outList: SymbolReference[], kind: any) {
    while (this.peek().value !== '}' && this.peek().type !== TokenType.EOF) {
      if (this.peek().value === 'fireable' || this.peek().value === 'receivable' || this.peek().value === 'raised' || this.peek().value === 'subscribable') {
        break;
      }
      if (this.peek().type === TokenType.IDENTIFIER || this.peek().type === TokenType.STRING) {
        const tok = this.advance();
        outList.push({ name: tok.value, kind, range: tok.range });
      } else {
        this.advance();
      }
      this.match(',');
    }
  }
}

export function parseCapability(text: string): CapabilityParseResult {
  return new CapabilityParser(text).parse();
}

