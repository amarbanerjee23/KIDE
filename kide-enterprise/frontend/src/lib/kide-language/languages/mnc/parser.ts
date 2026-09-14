/**
 * Native Resilient MNC-ML Parser with Exact Source Ranges, State Machines, and Interfaces.
 */

import { Lexer, Token, TokenType } from '../../lexer';
import { 
  MncModelNode, 
  MncInterfaceNode, 
  MncControlNodeNode, 
  MncOperatingStateNode, 
  MncTransitionNode, 
  SourceRange, 
  SymbolReference,
  AstNode 
} from '../../ast';
import { ParseSyntaxError } from '../dml/parser';

export interface MncParseResult {
  ast: MncModelNode | null;
  errors: ParseSyntaxError[];
  tokens: Token[];
}

export class MncParser {
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

  public parse(): MncParseResult {
    const lexer = new Lexer(this.text);
    this.tokens = lexer.tokenize();
    this.idx = 0;
    this.errors = [];

    const startTok = this.peek();
    let modelName = 'UnnamedMncModel';

    if (this.match('Model')) {
      if (this.peek().type === TokenType.IDENTIFIER || this.peek().type === TokenType.STRING) {
        modelName = this.advance().value;
      }
    }

    let ifaceNode: MncInterfaceNode | undefined;
    let controlNode: MncControlNodeNode | undefined;

    while (this.peek().type !== TokenType.EOF) {
      if (this.match('InterfaceDescription')) {
        ifaceNode = this.parseInterface();
      } else if (this.match('ControlNode')) {
        controlNode = this.parseControlNode();
      } else {
        this.advance();
      }
    }

    const lastTok = this.tokens[Math.max(0, this.tokens.length - 2)] || startTok;
    const overallRange: SourceRange = {
      startLine: startTok.range.startLine,
      startColumn: startTok.range.startColumn,
      endLine: lastTok.range.endLine,
      endColumn: lastTok.range.endColumn
    };

    const ast: MncModelNode = {
      type: 'Model',
      id: `mnc_model_${modelName}`,
      name: modelName,
      range: overallRange,
      children: [],
      references: [],
      interfaceDescription: ifaceNode,
      controlNode
    };

    return { ast, errors: this.errors, tokens: this.tokens };
  }

  private parseInterface(): MncInterfaceNode {
    const nameTok = this.peek();
    let ifName = 'UnnamedInterface';
    if (nameTok.type === TokenType.IDENTIFIER || nameTok.type === TokenType.STRING) {
      ifName = this.advance().value;
    }

    const uses: SymbolReference[] = [];
    if (this.match('uses')) {
      while (this.peek().value !== '{' && this.peek().type !== TokenType.EOF) {
        if (this.peek().type === TokenType.IDENTIFIER || this.peek().type === TokenType.STRING) {
          const uTok = this.advance();
          uses.push({ name: uTok.value, kind: 'InterfaceDescription', range: uTok.range });
        } else {
          this.advance();
        }
        this.match(',');
      }
    }

    this.match('{');

    const commands: AstNode[] = [];
    const events: AstNode[] = [];
    const alarms: AstNode[] = [];
    const dataPoints: AstNode[] = [];
    const responses: AstNode[] = [];
    const operatingStates: MncOperatingStateNode[] = [];

    while (this.peek().value !== '}' && this.peek().type !== TokenType.EOF) {
      if (this.match('commands')) {
        this.parseBlockEntities(commands, 'Command');
      } else if (this.match('events')) {
        this.parseBlockEntities(events, 'Event');
      } else if (this.match('alarms')) {
        this.parseBlockEntities(alarms, 'Alarm');
      } else if (this.match('dataPoints')) {
        this.parseBlockEntities(dataPoints, 'DataPoint');
      } else if (this.match('responses')) {
        this.parseBlockEntities(responses, 'Response');
      } else if (this.match('operatingStates')) {
        this.parseOperatingStates(operatingStates);
      } else {
        this.advance();
      }
    }

    this.match('}');

    const lastTok = this.tokens[this.idx - 1] || nameTok;
    return {
      type: 'InterfaceDescription',
      id: `iface_${ifName}`,
      name: ifName,
      range: {
        startLine: nameTok.range.startLine,
        startColumn: nameTok.range.startColumn,
        endLine: lastTok.range.endLine,
        endColumn: lastTok.range.endColumn
      },
      children: [...operatingStates, ...commands, ...events, ...alarms, ...dataPoints],
      references: uses,
      uses,
      commands,
      events,
      alarms,
      dataPoints,
      responses,
      operatingStates
    };
  }

  private parseBlockEntities(outList: AstNode[], kind: string) {
    if (!this.match('{')) return;
    while (this.peek().value !== '}' && this.peek().type !== TokenType.EOF) {
      this.match('Publish'); // optional keyword
      this.match('async');

      // skip optional type for DataPoints: float, int, boolean, etc.
      if (['float', 'int', 'boolean', 'string', 'date'].includes(this.peek().value)) {
        this.advance();
      }

      if (this.peek().type === TokenType.IDENTIFIER || this.peek().type === TokenType.STRING) {
        const itemTok = this.advance();
        const startRange = itemTok.range;

        // Skip [ ... ] and level = N
        while (this.peek().value !== '}' && this.peek().value !== ';' && this.peek().type !== TokenType.EOF) {
          if (this.peek().value === 'level') {
            this.advance();
            this.match('=');
            this.advance();
            break;
          }
          if (this.peek().value === '[') {
            while (this.peek().value !== ']' && this.peek().type !== TokenType.EOF) {
              this.advance();
            }
            this.match(']');
            break;
          }
          if (this.peek().type === TokenType.IDENTIFIER || this.peek().value === 'Publish') {
            break;
          }
          this.advance();
        }

        outList.push({
          type: kind,
          id: `${kind.toLowerCase()}_${itemTok.value}`,
          name: itemTok.value,
          range: {
            startLine: startRange.startLine,
            startColumn: startRange.startColumn,
            endLine: this.tokens[this.idx - 1].range.endLine,
            endColumn: this.tokens[this.idx - 1].range.endColumn
          },
          children: [],
          references: []
        });
      } else {
        this.advance();
      }
    }
    this.match('}');
  }

  private parseOperatingStates(outStates: MncOperatingStateNode[]) {
    if (!this.match('{')) return;
    while (this.peek().value !== '}' && this.peek().type !== TokenType.EOF) {
      if (this.peek().type === TokenType.IDENTIFIER || this.peek().type === TokenType.STRING) {
        const stateTok = this.advance();
        const startRange = stateTok.range;

        if (this.match('[')) {
          while (this.peek().value !== ']' && this.peek().type !== TokenType.EOF) {
            this.advance();
          }
          this.match(']');
        }

        outStates.push({
          type: 'OperatingState',
          id: `state_${stateTok.value}`,
          name: stateTok.value,
          range: {
            startLine: startRange.startLine,
            startColumn: startRange.startColumn,
            endLine: this.tokens[this.idx - 1].range.endLine,
            endColumn: this.tokens[this.idx - 1].range.endColumn
          },
          children: [],
          references: [],
          parameters: []
        });
      } else {
        this.advance();
      }
    }
    this.match('}');
  }

  private parseControlNode(): MncControlNodeNode {
    const nameTok = this.peek();
    let cnName = 'UnnamedControlNode';
    if (nameTok.type === TokenType.IDENTIFIER || nameTok.type === TokenType.STRING) {
      cnName = this.advance().value;
    }

    let implIface: SymbolReference | undefined;
    if (this.match('implements') && this.match('interface')) {
      if (this.peek().type === TokenType.IDENTIFIER || this.peek().type === TokenType.STRING) {
        const ifTok = this.advance();
        implIface = { name: ifTok.value, kind: 'InterfaceDescription', range: ifTok.range };
      }
    }

    this.match('{');
    const transitions: MncTransitionNode[] = [];
    const references: SymbolReference[] = [];
    if (implIface) references.push(implIface);

    while (this.peek().value !== '}' && this.peek().type !== TokenType.EOF) {
      if (this.match('currentState')) {
        const curTok = this.advance();
        this.match('=>');
        this.match('nextState');
        const nextTok = this.advance();

        const trNode: MncTransitionNode = {
          type: 'Transition',
          id: `trans_${curTok.value}_${nextTok.value}`,
          range: {
            startLine: curTok.range.startLine,
            startColumn: curTok.range.startColumn,
            endLine: nextTok.range.endLine,
            endColumn: nextTok.range.endColumn
          },
          children: [],
          references: [
            { name: curTok.value, kind: 'State', range: curTok.range },
            { name: nextTok.value, kind: 'State', range: nextTok.range }
          ],
          currentStates: [curTok.value],
          nextState: nextTok.value
        };
        transitions.push(trNode);
        references.push(...trNode.references);
      } else {
        this.advance();
      }
    }

    this.match('}');

    const lastTok = this.tokens[this.idx - 1] || nameTok;
    return {
      type: 'ControlNode',
      id: `cn_${cnName}`,
      name: cnName,
      range: {
        startLine: nameTok.range.startLine,
        startColumn: nameTok.range.startColumn,
        endLine: lastTok.range.endLine,
        endColumn: lastTok.range.endColumn
      },
      children: transitions,
      references,
      implementedInterface: implIface,
      childNodes: [],
      transitions,
      commandBlocks: [],
      eventBlocks: [],
      alarmBlocks: []
    };
  }
}

export function parseMnc(text: string): MncParseResult {
  return new MncParser(text).parse();
}

