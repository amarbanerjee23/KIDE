/**
 * Document Model Manager for KIDE Enterprise.
 * Implements persistent Monaco models with stable URIs (kide://project/{projectId}/artifact/{fileId}/{filename}).
 * Preserves per-file undo/redo history, view state, and cursor selections across tab switches.
 */

import * as monaco from 'monaco-editor';
import { getLanguageForArtifact } from './languages';

export interface DocumentEntry {
  fileId: string;
  projectId: number;
  fileName: string;
  languageId: string;
  uri: monaco.Uri;
  model: monaco.editor.ITextModel;
  viewState: monaco.editor.ICodeEditorViewState | null;
  version: number;
}

class DocumentManager {
  private documents: Map<string, DocumentEntry> = new Map();
  private monacoInstance: typeof monaco | null = null;

  public setMonaco(m: typeof monaco) {
    this.monacoInstance = m;
  }

  private getMonaco(): typeof monaco {
    return this.monacoInstance || monaco;
  }

  public getUri(projectId: number, fileId: string, fileName: string): monaco.Uri {
    const m = this.getMonaco();
    const cleanName = encodeURIComponent(fileName || 'untitled');
    return m.Uri.parse(`kide://project/${projectId}/artifact/${fileId}/${cleanName}`);
  }

  /**
   * Retrieves an existing persistent Monaco model or creates a new one.
   */
  public getOrCreateDocument(
    projectId: number,
    fileId: string,
    fileName: string,
    initialContent: string
  ): DocumentEntry {
    const m = this.getMonaco();
    const existing = this.documents.get(fileId);

    if (existing && !existing.model.isDisposed()) {
      // If content was updated externally (e.g. from backend/patch) and model is unmodified
      return existing;
    }

    const uri = this.getUri(projectId, fileId, fileName);
    const languageId = getLanguageForArtifact(fileName);

    // Check if Monaco already has a model with this URI
    let model = m.editor.getModel(uri);
    if (!model || model.isDisposed()) {
      model = m.editor.createModel(initialContent, languageId, uri);
    } else {
      // Update language if needed
      if (model.getLanguageId() !== languageId) {
        m.editor.setModelLanguage(model, languageId);
      }
    }

    const entry: DocumentEntry = {
      fileId,
      projectId,
      fileName,
      languageId,
      uri,
      model,
      viewState: null,
      version: 1
    };

    this.documents.set(fileId, entry);
    return entry;
  }

  /**
   * Switches the active editor to the given file, preserving and restoring view states.
   */
  public attachToEditor(
    editor: monaco.editor.IStandaloneCodeEditor,
    projectId: number,
    fileId: string,
    fileName: string,
    initialContent: string
  ): DocumentEntry {
    // Save previous model viewState if editor had an active model
    const currentModel = editor.getModel();
    if (currentModel) {
      for (const doc of this.documents.values()) {
        if (doc.model === currentModel) {
          doc.viewState = editor.saveViewState();
          break;
        }
      }
    }

    const doc = this.getOrCreateDocument(projectId, fileId, fileName, initialContent);

    if (editor.getModel() !== doc.model) {
      editor.setModel(doc.model);
      if (doc.viewState) {
        editor.restoreViewState(doc.viewState);
      }
    }

    return doc;
  }

  public getDocument(fileId: string): DocumentEntry | undefined {
    return this.documents.get(fileId);
  }

  public getAllDocuments(): DocumentEntry[] {
    return Array.from(this.documents.values());
  }

  public updateContent(fileId: string, newContent: string) {
    const doc = this.documents.get(fileId);
    if (doc && !doc.model.isDisposed()) {
      if (doc.model.getValue() !== newContent) {
        doc.model.setValue(newContent);
        doc.version++;
      }
    }
  }

  public disposeDocument(fileId: string) {
    const doc = this.documents.get(fileId);
    if (doc) {
      if (!doc.model.isDisposed()) {
        doc.model.dispose();
      }
      this.documents.delete(fileId);
    }
  }

  public disposeAll() {
    for (const doc of this.documents.values()) {
      if (!doc.model.isDisposed()) {
        doc.model.dispose();
      }
    }
    this.documents.clear();
  }
}

export const documentManager = new DocumentManager();

