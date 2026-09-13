/**
 * Central Language Service Coordinator for KIDE Enterprise.
 * Manages incremental parsing, workspace indexing, diagnostics calculation,
 * exact AST node lookup, and editor/UI synchronization.
 */

import * as monaco from 'monaco-editor';
import { parseArtifact, parseAst } from './languages';
import { projectSymbolIndex } from './symbolIndex';
import { diagnosticsEngine, KideDiagnostic } from './diagnostics';
import { documentManager } from './documentManager';
import { formatKideDsl } from './formatting';
import { AstNode, SourceRange } from './ast';
import { useEditorStore } from '../../stores/editorStore';

export interface BreadcrumbItem {
  name: string;
  kind: string;
  range: SourceRange;
}

export class KideLanguageService {
  private monacoInstance: typeof monaco | null = null;
  private validationDebounceTimers: Map<string, any> = new Map();

  public setMonaco(m: typeof monaco) {
    this.monacoInstance = m;
  }

  private getMonaco(): typeof monaco {
    return this.monacoInstance || monaco;
  }

  /**
   * Initializes or refreshes the entire workspace symbol index from project files.
   */
  public indexWorkspaceFiles(files: Array<{ id: string; name: string; content: string }>) {
    for (const file of files) {
      try {
        const parsed = parseArtifact(file.name, file.content);
        projectSymbolIndex.indexFile(file.id, file.name, file.content, parsed.ast);
      } catch (err) {
        console.warn(`[KIDE Language Service] Error indexing ${file.name}:`, err);
      }
    }
  }

  /**
   * Called when a document's content changes in Monaco.
   * Debounces incremental parsing, index update, diagnostics calculation, and store sync.
   */
  public onDocumentChanged(
    fileId: string,
    fileName: string,
    content: string,
    debounceMs: number = 250
  ) {
    // Clear pending debounce timer
    const existingTimer = this.validationDebounceTimers.get(fileId);
    if (existingTimer) {
      clearTimeout(existingTimer);
    }

    const timer = setTimeout(() => {
      this.revalidateAndIndexFile(fileId, fileName, content);
      this.validationDebounceTimers.delete(fileId);
    }, debounceMs);

    this.validationDebounceTimers.set(fileId, timer);
  }

  /**
   * Immediately validates, parses, indexes, and publishes diagnostics for a single file.
   */
  public revalidateAndIndexFile(fileId: string, fileName: string, content: string): KideDiagnostic[] {
    const parseResult = parseArtifact(fileName, content);

    // 1. Incrementally update project symbol index
    projectSymbolIndex.indexFile(fileId, fileName, content, parseResult.ast);

    // 2. Compute syntax + semantic diagnostics
    const diagnostics = diagnosticsEngine.computeDiagnostics(
      fileId,
      fileName,
      parseResult.ast,
      parseResult.errors
    );

    // 3. Apply markers to active Monaco model
    const m = this.getMonaco();
    const doc = documentManager.getDocument(fileId);
    if (doc && !doc.model.isDisposed()) {
      diagnosticsEngine.applyToModel(doc.model, diagnostics);
    } else {
      // Find open model with matching URI
      const openModel = m.editor.getModels().find(mod => 
        mod.uri.path.includes(fileName) || (mod.uri.fsPath && mod.uri.fsPath.includes(fileName))
      );
      if (openModel && !openModel.isDisposed()) {
        diagnosticsEngine.applyToModel(openModel, diagnostics);
      }
    }

    // 4. Sync validation errors with Zustand store
    try {
      const storeErrors = diagnostics.map(d => ({
        line: d.startLineNumber,
        column: d.startColumn,
        message: d.message,
        severity: d.severity === 'error' ? ('error' as const) : ('warning' as const)
      }));
      useEditorStore.getState().setValidationErrors(fileId, storeErrors);
    } catch {
      // Zustand store not yet mounted
    }

    return diagnostics;
  }

  /**
   * Finds the exact AST node covering the given line and column position.
   * Completely replaces regex line-guessing!
   */
  public findNodeAtPosition(
    langOrFilename: string,
    content: string,
    line: number,
    column: number
  ): AstNode | null {
    const ast = parseAst(langOrFilename, content);
    if (!ast) return null;

    return this.searchNodeInTree(ast, line, column);
  }

  private searchNodeInTree(node: AstNode, line: number, col: number): AstNode | null {
    if (!node || !node.range) return null;

    const inRange = this.isPositionInRange(line, col, node.range);
    if (!inRange) return null;

    // Check all properties for child AST nodes
    let deepestChild: AstNode | null = null;

    for (const key of Object.keys(node)) {
      if (key === 'range') continue;
      const val = (node as any)[key];

      if (val && typeof val === 'object') {
        if (Array.isArray(val)) {
          for (const item of val) {
            if (item && item.range) {
              const child = this.searchNodeInTree(item, line, col);
              if (child) {
                deepestChild = child;
              }
            }
          }
        } else if (val.range) {
          const child = this.searchNodeInTree(val, line, col);
          if (child) {
            deepestChild = child;
          }
        }
      }
    }

    return deepestChild || node;
  }

  private isPositionInRange(line: number, col: number, range: SourceRange): boolean {
    if (line < range.startLine || line > range.endLine) return false;
    if (line === range.startLine && col < range.startColumn) return false;
    if (line === range.endLine && col > range.endColumn) return false;
    return true;
  }

  /**
   * Computes hierarchical breadcrumb trail for status bar or breadcrumb bar.
   */
  public getBreadcrumbs(
    langOrFilename: string,
    content: string,
    line: number,
    column: number
  ): BreadcrumbItem[] {
    const ast = parseAst(langOrFilename, content);
    if (!ast) return [];

    const breadcrumbs: BreadcrumbItem[] = [];
    this.collectBreadcrumbTrail(ast, line, column, breadcrumbs);
    return breadcrumbs;
  }

  private collectBreadcrumbTrail(
    node: AstNode,
    line: number,
    col: number,
    trail: BreadcrumbItem[]
  ) {
    if (!node || !node.range || !this.isPositionInRange(line, col, node.range)) return;

    if (node.name || node.type) {
      trail.push({
        name: node.name || node.type,
        kind: node.type,
        range: node.range
      });
    }

    for (const key of Object.keys(node)) {
      if (key === 'range') continue;
      const val = (node as any)[key];
      if (val && typeof val === 'object') {
        if (Array.isArray(val)) {
          for (const item of val) {
            if (item && item.range && this.isPositionInRange(line, col, item.range)) {
              this.collectBreadcrumbTrail(item, line, col, trail);
              return;
            }
          }
        } else if (val.range && this.isPositionInRange(line, col, val.range)) {
          this.collectBreadcrumbTrail(val, line, col, trail);
          return;
        }
      }
    }
  }

  /**
   * Formats source text with the idempotent language formatter.
   */
  public format(content: string, langOrFilename?: string): string {
    return formatKideDsl(content, langOrFilename);
  }
}

export const kideLanguageService = new KideLanguageService();

// Cross-file navigation callback handlers
export type NavigationHandler = (targetFileId: string, line: number, column: number) => void;
let globalNavHandler: NavigationHandler | null = null;

export function registerNavigationHandler(handler: NavigationHandler) {
  globalNavHandler = handler;
}

export function navigateToDefinition(fileId: string, line: number, column: number) {
  if (globalNavHandler) {
    globalNavHandler(fileId, line, column);
  } else {
    try {
      useEditorStore.getState().setActiveFileId(fileId);
    } catch {
      // Store not mounted
    }
  }
}
