/**
 * Semantic Tokens Highlighting Provider for KIDE Enterprise.
 * Implements Monaco's DocumentSemanticTokensProvider.
 * Distinguishes AST declarations, references, interfaces, types, parameters, and events.
 */

import * as monaco from 'monaco-editor';
import { getLanguageForArtifact } from './languages';
import { parseAst } from './languages';
import { projectSymbolIndex } from './symbolIndex';

const TOKEN_TYPES = [
  'class',      // 0: DataModel
  'interface',  // 1: Capability, InterfaceDescription
  'function',   // 2: Operation, Command
  'event',      // 3: Event, Alarm
  'variable',   // 4: Parameter, State
  'property',   // 5: DataPoint, Field
  'type',       // 6: Primitive types
  'keyword',    // 7: DSL keywords
  'string',     // 8: Strings
  'number'      // 9: Numbers
];

const TOKEN_MODIFIERS = [
  'declaration', // 1 << 0 = 1
  'definition',  // 1 << 1 = 2
  'readonly',    // 1 << 2 = 4
  'defaultLibrary' // 1 << 3 = 8
];

export const semanticTokensLegend: monaco.languages.SemanticTokensLegend = {
  tokenTypes: TOKEN_TYPES,
  tokenModifiers: TOKEN_MODIFIERS
};

export class KideSemanticTokensProvider implements monaco.languages.DocumentSemanticTokensProvider {
  public getLegend(): monaco.languages.SemanticTokensLegend {
    return semanticTokensLegend;
  }

  public provideDocumentSemanticTokens(
    model: monaco.editor.ITextModel,
    _lastResultId: string | null,
    _token: monaco.CancellationToken
  ): monaco.languages.ProviderResult<monaco.languages.SemanticTokens> {
    const text = model.getValue();
    const lang = getLanguageForArtifact(model.uri.path || model.uri.fsPath);
    const ast = parseAst(lang, text);
    if (!ast) return null;

    // Collect all semantic spans in document: { line (0-based), startCol (0-based), length, typeIndex, modBitmask }
    interface RawToken {
      line: number;
      startChar: number;
      length: number;
      tokenType: number;
      tokenModifiers: number;
    }

    const rawTokens: RawToken[] = [];
    const symbols = projectSymbolIndex.getAllSymbols();
    const symbolMap = new Map(symbols.map(s => [s.name, s]));

    // Scan words in text and classify based on AST / SymbolIndex
    const lines = text.split('\n');
    for (let l = 0; l < lines.length; l++) {
      const line = lines[l];
      const wordRegex = /\b[A-Za-z_][A-Za-z0-9_]*\b/g;
      let match: RegExpExecArray | null;

      while ((match = wordRegex.exec(line)) !== null) {
        const word = match[0];
        const startChar = match.index;
        const sym = symbolMap.get(word);

        if (sym) {
          let tIndex = 4; // variable
          if (sym.kind === 'DataModel') tIndex = 0; // class
          else if (sym.kind === 'Capability' || sym.kind === 'InterfaceDescription') tIndex = 1; // interface
          else if (sym.kind === 'Operation' || sym.kind === 'Command') tIndex = 2; // function
          else if (sym.kind === 'Event' || sym.kind === 'Alarm') tIndex = 3; // event
          else if (sym.kind === 'DataPoint') tIndex = 5; // property

          const isDecl = sym.range.startLine === (l + 1) && (sym.range.startColumn - 1) === startChar;
          const mod = isDecl ? 1 : 0; // declaration modifier

          rawTokens.push({
            line: l,
            startChar,
            length: word.length,
            tokenType: tIndex,
            tokenModifiers: mod
          });
        }
      }
    }

    // Sort tokens by line and column
    rawTokens.sort((a, b) => {
      if (a.line !== b.line) return a.line - b.line;
      return a.startChar - b.startChar;
    });

    // Encode into delta array: [deltaLine, deltaStartChar, length, tokenType, tokenModifiers]
    const data: number[] = [];
    let prevLine = 0;
    let prevChar = 0;

    for (const tok of rawTokens) {
      const deltaLine = tok.line - prevLine;
      const deltaChar = deltaLine === 0 ? tok.startChar - prevChar : tok.startChar;

      data.push(deltaLine, deltaChar, tok.length, tok.tokenType, tok.tokenModifiers);

      prevLine = tok.line;
      prevChar = tok.startChar;
    }

    return {
      data: new Uint32Array(data),
      resultId: undefined
    };
  }

  public releaseDocumentSemanticTokens(_resultId: string | undefined): void {}
}

export const kideSemanticTokensProvider = new KideSemanticTokensProvider();
