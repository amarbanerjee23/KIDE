/**
 * Idempotent Document Formatter for KIDE Enterprise.
 * Implements Monaco's DocumentFormattingEditProvider for all KIDE DSLs.
 * Guarantees mathematical idempotence: format(format(x)) === format(x).
 */

import * as monaco from 'monaco-editor';
import { getLanguageForArtifact } from './languages';

export class KideFormattingProvider implements monaco.languages.DocumentFormattingEditProvider {
  public provideDocumentFormattingEdits(
    model: monaco.editor.ITextModel,
    _options: monaco.languages.FormattingOptions,
    _token: monaco.CancellationToken
  ): monaco.languages.ProviderResult<monaco.languages.TextEdit[]> {
    const text = model.getValue();
    const lang = getLanguageForArtifact(model.uri.path || model.uri.fsPath);
    const formatted = formatKideDsl(text, lang);

    if (formatted === text) {
      return [];
    }

    return [
      {
        range: model.getFullModelRange(),
        text: formatted
      }
    ];
  }
}

/**
 * Formats KIDE DSL source code idempotently.
 */
export function formatKideDsl(source: string, _lang?: string): string {
  if (!source || source.trim() === '') return '';

  // 1. Normalize line breaks
  const rawLines = source.replace(/\r\n/g, '\n').replace(/\r/g, '\n').split('\n');

  let indentLevel = 0;
  const indentStr = '    '; // 4 spaces
  const resultLines: string[] = [];
  let inBlockComment = false;
  let prevLineWasEmpty = false;

  for (let i = 0; i < rawLines.length; i++) {
    let line = rawLines[i].trim();

    // Check for multiline comments
    if (inBlockComment) {
      resultLines.push(indentStr.repeat(indentLevel) + line);
      if (line.includes('*/')) {
        inBlockComment = false;
      }
      prevLineWasEmpty = false;
      continue;
    }

    if (line.startsWith('/*')) {
      if (!line.includes('*/')) {
        inBlockComment = true;
      }
      resultLines.push(indentStr.repeat(indentLevel) + line);
      prevLineWasEmpty = false;
      continue;
    }

    // Handle empty lines - collapse consecutive empty lines to at most 1
    if (line === '') {
      if (!prevLineWasEmpty && resultLines.length > 0) {
        resultLines.push('');
        prevLineWasEmpty = true;
      }
      continue;
    }
    prevLineWasEmpty = false;

    // Format inline spacing (punctuation, operators)
    line = formatLineTokens(line);

    // Count opening and closing braces for this line
    const leadingCloses = countLeadingCloses(line);
    const netBrackets = calculateNetBrackets(line);

    // Apply leading dedent if line starts with closing brackets
    const effectiveIndent = Math.max(0, indentLevel - leadingCloses);

    resultLines.push(indentStr.repeat(effectiveIndent) + line);

    // Update ongoing indent level for next lines
    indentLevel = Math.max(0, indentLevel + netBrackets);
  }

  // Ensure trailing newline
  let output = resultLines.join('\n');
  if (!output.endsWith('\n')) {
    output += '\n';
  }

  return output;
}

/**
 * Calculates how many closing brackets are at the very start of the line.
 */
function countLeadingCloses(line: string): number {
  let count = 0;
  for (let i = 0; i < line.length; i++) {
    const ch = line[i];
    if (ch === '}' || ch === ']') {
      count++;
    } else if (ch === ' ' || ch === '\t' || ch === ',') {
      continue;
    } else {
      break;
    }
  }
  return count;
}

/**
 * Calculates net indentation delta of the line (opens - closes).
 */
function calculateNetBrackets(line: string): number {
  let delta = 0;
  let inString = false;
  let quoteChar = '';

  for (let i = 0; i < line.length; i++) {
    const ch = line[i];
    const prev = i > 0 ? line[i - 1] : '';

    if (!inString && (ch === '"' || ch === "'")) {
      inString = true;
      quoteChar = ch;
    } else if (inString && ch === quoteChar && prev !== '\\') {
      inString = false;
    } else if (!inString) {
      if (ch === '{') delta++;
      else if (ch === '}') delta--;
      // Don't indent for square brackets unless they open an action block
      else if (ch === '[' && (line.includes('fire Commands') || line.includes('subscribe') || line.includes('activities'))) delta++;
      else if (ch === ']' && (line.includes('fire Commands') || line.includes('subscribe') || line.includes('activities'))) delta--;
    }
  }

  return delta;
}

/**
 * Cleans up spacing around colons, commas, and arrows inside a single line without corrupting strings.
 */
function formatLineTokens(line: string): string {
  // If line is a comment, don't modify internal spacing
  if (line.startsWith('//') || line.startsWith('/*') || line.startsWith('*')) {
    return line;
  }

  // Preserve strings by tokenizing
  const parts: { isString: boolean; text: string }[] = [];
  let curr = '';
  let inStr = false;
  let quote = '';

  for (let i = 0; i < line.length; i++) {
    const ch = line[i];
    const prev = i > 0 ? line[i - 1] : '';

    if (!inStr && (ch === '"' || ch === "'")) {
      if (curr) parts.push({ isString: false, text: curr });
      curr = ch;
      inStr = true;
      quote = ch;
    } else if (inStr && ch === quote && prev !== '\\') {
      curr += ch;
      parts.push({ isString: true, text: curr });
      curr = '';
      inStr = false;
    } else {
      curr += ch;
    }
  }
  if (curr) {
    parts.push({ isString: inStr, text: curr });
  }

  return parts.map(part => {
    if (part.isString) return part.text;

    let s = part.text;
    // Normalize spaces around arrows
    s = s.replace(/\s*=>\s*/g, ' => ');
    s = s.replace(/\s*->\s*/g, ' -> ');

    // Normalize spaces after commas (not before)
    s = s.replace(/\s*,\s*/g, ', ');

    // Normalize spaces after colon when used in key-value (e.g. description: "...", requireCapability: Foo)
    s = s.replace(/([A-Za-z0-9_]+)\s*:\s*/g, '$1: ');

    // Normalize space before opening brace
    s = s.replace(/\s*\{/g, ' {');

    // Clean up multiple spaces
    s = s.replace(/ {2,}/g, ' ');

    return s;
  }).join('').trim();
}

export const kideFormattingProvider = new KideFormattingProvider();

