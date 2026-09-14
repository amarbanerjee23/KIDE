/**
 * Signature Help Provider for KIDE Enterprise.
 * Implements Monaco's SignatureHelpProvider for parameter hints in operations,
 * commands, and actions.
 */

import * as monaco from 'monaco-editor';
import { projectSymbolIndex } from './symbolIndex';

export class KideSignatureHelpProvider implements monaco.languages.SignatureHelpProvider {
  public signatureHelpTriggerCharacters = ['(', ','];
  public signatureHelpRetriggerCharacters = [','];

  public provideSignatureHelp(
    model: monaco.editor.ITextModel,
    position: monaco.Position,
    _token: monaco.CancellationToken,
    _context: monaco.languages.SignatureHelpContext
  ): monaco.languages.ProviderResult<monaco.languages.SignatureHelpResult> {
    const lineUntilCursor = model.getLineContent(position.lineNumber).substring(0, position.column - 1);

    // Find the opening parenthesis before cursor
    const lastOpenParen = lineUntilCursor.lastIndexOf('(');
    if (lastOpenParen === -1) return null;

    // Find the identifier preceding '('
    const beforeParen = lineUntilCursor.substring(0, lastOpenParen).trim();
    const match = beforeParen.match(/([A-Za-z0-9_]+)$/);
    if (!match) return null;

    const funcName = match[1];

    // Count commas between lastOpenParen and cursor to determine active parameter index
    const argsSection = lineUntilCursor.substring(lastOpenParen + 1);
    const activeParameter = (argsSection.match(/,/g) || []).length;

    // Search symbol index for Operation or Command matching funcName
    const sym = projectSymbolIndex.findSymbolByName(funcName, 'Operation') ||
                projectSymbolIndex.findSymbolByName(funcName, 'Command');

    if (!sym) return null;

    const sigLabel = sym.detail || `${sym.name}()`;

    const signatures: monaco.languages.SignatureInformation[] = [{
      label: sigLabel,
      documentation: sym.documentation || `${sym.kind} ${sym.name}`,
      parameters: [
        {
          label: 'parameter',
          documentation: 'Argument to ' + sym.name
        }
      ]
    }];

    return {
      value: {
        signatures,
        activeSignature: 0,
        activeParameter
      },
      dispose: () => {}
    };
  }
}

export const kideSignatureHelpProvider = new KideSignatureHelpProvider();

