/**
 * Definition Provider for KIDE Enterprise.
 * Implements Monaco's DefinitionProvider for cross-file navigation (F12 / Ctrl+Click).
 * Returns exact Location objects with stable kide:// URIs and AST-precise SourceRanges.
 */

import * as monaco from 'monaco-editor';
import { projectSymbolIndex } from './symbolIndex';
import { documentManager } from './documentManager';

export class KideDefinitionProvider implements monaco.languages.DefinitionProvider {
  public provideDefinition(
    model: monaco.editor.ITextModel,
    position: monaco.Position,
    _token: monaco.CancellationToken
  ): monaco.languages.ProviderResult<monaco.languages.Definition> {
    const wordInfo = model.getWordAtPosition(position);
    if (!wordInfo) return null;

    const word = wordInfo.word;

    // Search symbol index for all declarations with this name
    const allSymbols = projectSymbolIndex.getAllSymbols();
    const matches = allSymbols.filter(s => s.name === word);

    if (matches.length === 0) return null;

    // Prioritize symbol from same file or project match
    const locations: monaco.languages.Location[] = [];

    for (const sym of matches) {
      let targetUri = model.uri;

      // If symbol is in another file, resolve URI
      if (sym.fileId) {
        const doc = documentManager.getDocument(sym.fileId);
        if (doc) {
          targetUri = doc.uri;
        } else {
          // Check open models by path matching
          const openModel = monaco.editor.getModels().find(m => 
            m.uri.path.includes(sym.fileName) || (m.uri.fsPath && m.uri.fsPath.includes(sym.fileName))
          );
          if (openModel) {
            targetUri = openModel.uri;
          } else {
            targetUri = documentManager.getUri(1, sym.fileId, sym.fileName);
          }
        }
      }

      locations.push({
        uri: targetUri,
        range: new monaco.Range(
          sym.range.startLine,
          sym.range.startColumn,
          sym.range.endLine,
          sym.range.endColumn
        )
      });
    }

    // Return current file matches first
    locations.sort((a, b) => {
      const aCurrent = a.uri.toString() === model.uri.toString();
      const bCurrent = b.uri.toString() === model.uri.toString();
      if (aCurrent && !bCurrent) return -1;
      if (!aCurrent && bCurrent) return 1;
      return 0;
    });

    return locations;
  }
}

export const kideDefinitionProvider = new KideDefinitionProvider();
