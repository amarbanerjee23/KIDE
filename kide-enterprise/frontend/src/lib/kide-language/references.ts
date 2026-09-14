/**
 * Reference Provider for KIDE Enterprise.
 * Implements Monaco's ReferenceProvider for finding all usages across the workspace (Shift+F12).
 */

import * as monaco from 'monaco-editor';
import { projectSymbolIndex } from './symbolIndex';
import { documentManager } from './documentManager';

export class KideReferenceProvider implements monaco.languages.ReferenceProvider {
  public provideReferences(
    model: monaco.editor.ITextModel,
    position: monaco.Position,
    context: monaco.languages.ReferenceContext,
    _token: monaco.CancellationToken
  ): monaco.languages.ProviderResult<monaco.languages.Location[]> {
    const wordInfo = model.getWordAtPosition(position);
    if (!wordInfo) return null;

    const word = wordInfo.word;
    const locations: monaco.languages.Location[] = [];

    // If declaration should be included, add declaration locations
    if (context.includeDeclaration) {
      const allSymbols = projectSymbolIndex.getAllSymbols();
      const declarations = allSymbols.filter(s => s.name === word);

      for (const sym of declarations) {
        let uri = model.uri;
        if (sym.fileId) {
          const doc = documentManager.getDocument(sym.fileId);
          if (doc) {
            uri = doc.uri;
          } else {
            const openModel = monaco.editor.getModels().find(m => 
              m.uri.path.includes(sym.fileName) || (m.uri.fsPath && m.uri.fsPath.includes(sym.fileName))
            );
            if (openModel) {
              uri = openModel.uri;
            } else {
              uri = documentManager.getUri(1, sym.fileId, sym.fileName);
            }
          }
        }

        locations.push({
          uri,
          range: new monaco.Range(
            sym.range.startLine,
            sym.range.startColumn,
            sym.range.endLine,
            sym.range.endColumn
          )
        });
      }
    }

    // Add all AST references across all files
    const refs = projectSymbolIndex.findReferences(word);
    for (const ref of refs) {
      let uri = model.uri;
      if (ref.fileId) {
        const doc = documentManager.getDocument(ref.fileId);
        if (doc) {
          uri = doc.uri;
        } else {
          const openModel = monaco.editor.getModels().find(m => 
            m.uri.path.includes(ref.fileName) || (m.uri.fsPath && m.uri.fsPath.includes(ref.fileName))
          );
          if (openModel) {
            uri = openModel.uri;
          } else {
            uri = documentManager.getUri(1, ref.fileId, ref.fileName);
          }
        }
      }

      locations.push({
        uri,
        range: new monaco.Range(
          ref.range.startLine,
          ref.range.startColumn,
          ref.range.endLine,
          ref.range.endColumn
        )
      });
    }

    return locations;
  }
}

export const kideReferenceProvider = new KideReferenceProvider();

