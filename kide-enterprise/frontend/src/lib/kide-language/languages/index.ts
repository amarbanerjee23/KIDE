/**
 * Unified Parser Dispatcher for all 5 KIDE Enterprise DSLs.
 */

import { parseDml } from './dml/parser';
import { parseCapability } from './capability/parser';
import { parseOperation } from './operation/parser';
import { parseActivity } from './activity/parser';
import { parseMnc } from './mnc/parser';
import { getLanguageForArtifact } from '../languages';
import { AstNode } from '../ast';
import { ParseSyntaxError } from './dml/parser';
import { Token } from '../lexer';

export { parseDml, parseCapability, parseOperation, parseActivity, parseMnc };

export interface UnifiedParseResult {
  ast: AstNode | null;
  errors: ParseSyntaxError[];
  tokens: Token[];
  languageId: string;
}

export function parseArtifact(langOrFilename: string, text: string): UnifiedParseResult {
  const langId = getLanguageForArtifact(langOrFilename);

  switch (langId) {
    case 'dmldsl': {
      const res = parseDml(text);
      return { ast: res.ast, errors: res.errors, tokens: res.tokens, languageId: langId };
    }
    case 'capabilitydsl': {
      const res = parseCapability(text);
      return { ast: res.ast, errors: res.errors, tokens: res.tokens, languageId: langId };
    }
    case 'operationdsl': {
      const res = parseOperation(text);
      const opAst = res.ast && res.ast.operations && res.ast.operations.length === 1 ? res.ast.operations[0] : res.ast;
      return { ast: opAst, errors: res.errors, tokens: res.tokens, languageId: langId };
    }
    case 'activitydsl': {
      const res = parseActivity(text);
      return { ast: res.ast, errors: res.errors, tokens: res.tokens, languageId: langId };
    }
    case 'mncml': {
      const res = parseMnc(text);
      return { ast: res.ast, errors: res.errors, tokens: res.tokens, languageId: langId };
    }
    default:
      return { ast: null, errors: [], tokens: [], languageId: langId };
  }
}

export function parseAst(langOrFilename: string, text: string): AstNode | null {
  return parseArtifact(langOrFilename, text).ast;
}

