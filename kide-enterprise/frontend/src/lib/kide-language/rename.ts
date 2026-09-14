/**
 * Rename Provider for KIDE Enterprise.
 * Implements Monaco's RenameProvider (F2) for atomic multi-file semantic rename.
 * Gathers AST declaration and reference source ranges to generate a safe WorkspaceEdit.
 */

import * as monaco from 'monaco-editor';
import { projectSymbolIndex } from './symbolIndex';
import { documentManager } from './documentManager';

export class KideRenameProvider implements monaco.languages.RenameProvider {
  public resolveRenameLocation(
    model: monaco.editor.ITextModel,
    position: monaco.Position,
    _token: monaco.CancellationToken
  ): monaco.languages.ProviderResult<monaco.languages.RenameLocation & { rejectReason?: string }> {
    const wordInfo = model.getWordAtPosition(position);
    if (!wordInfo) {
      return {
        range: new monaco.Range(position.lineNumber, position.column, position.lineNumber, position.column),
        text: '',
        rejectReason: 'Cursor must be placed on a valid KIDE symbol or identifier to rename.'
      };
    }

    const word = wordInfo.word;
    const allSymbols = projectSymbolIndex.getAllSymbols();
    const isDeclared = allSymbols.some(s => s.name === word);
    const hasReferences = projectSymbolIndex.findReferences(word).length > 0;

    if (!isDeclared && !hasReferences) {
      return {
        range: new monaco.Range(position.lineNumber, wordInfo.startColumn, position.lineNumber, wordInfo.endColumn),
        text: word,
        rejectReason: `'${word}' is not an indexed KIDE symbol.`
      };
    }

    return {
      range: new monaco.Range(position.lineNumber, wordInfo.startColumn, position.lineNumber, wordInfo.endColumn),
      text: word
    };
  }

  public provideRenameEdits(
    model: monaco.editor.ITextModel,
    position: monaco.Position,
    newName: string,
    _token: monaco.CancellationToken
  ): monaco.languages.ProviderResult<monaco.languages.WorkspaceEdit & { rejectReason?: string }> {
    const wordInfo = model.getWordAtPosition(position);
    if (!wordInfo) return null;

    const oldName = wordInfo.word;
    const edits: monaco.languages.IWorkspaceTextEdit[] = [];

    // 1. Rename declaration(s)
    const allSymbols = projectSymbolIndex.getAllSymbols();
    const declarations = allSymbols.filter(s => s.name === oldName);

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

      // Rename identifier in declaration
      const selRange = sym.selectionRange || sym.range;
      edits.push({
        resource: uri,
        textEdit: {
          range: new monaco.Range(
            selRange.startLine,
            selRange.startColumn,
            selRange.endLine,
            selRange.endColumn
          ),
          text: newName
        },
        versionId: undefined
      });
    }

    // 2. Rename all AST references
    const refs = projectSymbolIndex.findReferences(oldName);
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

      edits.push({
        resource: uri,
        textEdit: {
          range: new monaco.Range(
            ref.range.startLine,
            ref.range.startColumn,
            ref.range.endLine,
            ref.range.endColumn
          ),
          text: newName
        },
        versionId: undefined
      });
    }

    return {
      edits
    };
  }
}

export const kideRenameProvider = new KideRenameProvider();

