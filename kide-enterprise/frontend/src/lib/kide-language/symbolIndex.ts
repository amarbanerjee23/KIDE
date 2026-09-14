/**
 * Incremental Project-Wide Symbol Index for KIDE Enterprise.
 * Indexes typed symbols and references across all project artifacts for instant
 * Go-to-Definition (F12), Find References (Shift+F12), Workspace Symbols (Ctrl+T), and Semantic Completion.
 */

import * as monaco from 'monaco-editor';
import { AstNode, SourceRange } from './ast';

export interface IndexedSymbol {
  id: string;
  name: string;
  kind: 'DataModel' | 'Capability' | 'Operation' | 'Activity' | 'ActivityDiagram' | 'InterfaceDescription' | 'ControlNode' | 'State' | 'Command' | 'Event' | 'Alarm' | 'DataPoint';
  fileId: string;
  fileName: string;
  uri: monaco.Uri;
  range: SourceRange;
  selectionRange: SourceRange;
  detail: string;
  documentation?: string;
  containerName?: string;
  metadata?: Record<string, any>;
}

export interface IndexedReference {
  symbolName: string;
  kind: string;
  sourceFileId: string;
  sourceFileName: string;
  sourceUri: monaco.Uri;
  range: SourceRange;
  containerName?: string;
  fileId: string;
  fileName: string;
  uri: monaco.Uri;
}

class ProjectSymbolIndex {
  // Map fileId -> Array of symbols declared in that file
  private fileSymbols: Map<string, IndexedSymbol[]> = new Map();
  // Map fileId -> Array of references made in that file
  private fileReferences: Map<string, IndexedReference[]> = new Map();

  // Fast lookup caches
  private nameIndex: Map<string, IndexedSymbol[]> = new Map();

  /**
   * Rebuilds fast lookup cache from fileSymbols.
   */
  private rebuildCaches() {
    this.nameIndex.clear();
    for (const symbols of this.fileSymbols.values()) {
      for (const sym of symbols) {
        const list = this.nameIndex.get(sym.name) || [];
        list.push(sym);
        this.nameIndex.set(sym.name, list);
      }
    }
  }

  /**
   * Incrementally indexes an artifact AST.
   */
  public indexArtifact(
    fileId: string,
    fileName: string,
    uri: monaco.Uri,
    ast: AstNode | null
  ) {
    if (!ast) {
      this.fileSymbols.delete(fileId);
      this.fileReferences.delete(fileId);
      this.rebuildCaches();
      return;
    }

    const discoveredSymbols: IndexedSymbol[] = [];
    const discoveredReferences: IndexedReference[] = [];

    const traverse = (node: AstNode, currentContainer?: string) => {
      if (!node) return;

      // Map AST node to IndexedSymbol if it represents a declared entity
      let symKind: IndexedSymbol['kind'] | null = null;
      let symDetail = '';
      let symDoc = node.docComment || '';

      if (node.type === 'DataModel') {
        symKind = 'DataModel';
        symDetail = `DataModel ${node.name}`;
        symDoc = symDoc || 'DML domain entity defining data structures and composite relationships.';
      } else if (node.type === 'Capability') {
        symKind = 'Capability';
        const interfaces = (node as any).componentInterfaces?.map((i: any) => i.name).join(', ') || 'unbound';
        symDetail = `Capability ${node.name} -> Interface: ${interfaces}`;
        symDoc = symDoc || 'Industrial device capability contract providing control capabilities and outcomes.';
      } else if (node.type === 'Operation') {
        symKind = 'Operation';
        symDetail = `Operation ${node.name}()`;
        symDoc = symDoc || 'Low-level executable device operation with typed parameters.';
      } else if (node.type === 'Activity') {
        symKind = 'Activity';
        symDetail = `Activity ${node.name}`;
        symDoc = symDoc || (node as any).description || 'Supervisory workflow execution step.';
      } else if (node.type === 'ActivityDiagram') {
        symKind = 'ActivityDiagram';
        symDetail = `ActivityDiagram ${node.name}`;
      } else if (node.type === 'InterfaceDescription') {
        symKind = 'InterfaceDescription';
        symDetail = `InterfaceDescription ${node.name}`;
        symDoc = symDoc || 'MNC-ML supervisory component interface contract.';
      } else if (node.type === 'ControlNode') {
        symKind = 'ControlNode';
        symDetail = `ControlNode ${node.name}`;
      } else if (node.type === 'OperatingState') {
        symKind = 'State';
        symDetail = `OperatingState ${node.name}`;
        symDoc = symDoc || 'Supervisory controller operating lifecycle state.';
      } else if (node.type === 'Command') {
        symKind = 'Command';
        symDetail = `Command ${node.name}`;
      } else if (node.type === 'Event') {
        symKind = 'Event';
        symDetail = `Event ${node.name}`;
      } else if (node.type === 'Alarm') {
        symKind = 'Alarm';
        symDetail = `Alarm ${node.name}`;
      } else if (node.type === 'DataPoint') {
        symKind = 'DataPoint';
        symDetail = `DataPoint ${node.name}`;
      }

      if (symKind && node.name) {
        discoveredSymbols.push({
          id: node.id,
          name: node.name,
          kind: symKind,
          fileId,
          fileName,
          uri,
          range: node.range,
          selectionRange: {
            startLine: node.range.startLine,
            startColumn: node.range.startColumn,
            endLine: node.range.startLine,
            endColumn: node.range.startColumn + node.name.length
          },
          detail: symDetail,
          documentation: symDoc,
          containerName: currentContainer,
          metadata: node.metadata
        });
      }

      // Collect references made by this node
      if (node.references && node.references.length > 0) {
        for (const ref of node.references) {
          discoveredReferences.push({
            symbolName: ref.name,
            kind: ref.kind,
            sourceFileId: fileId,
            sourceFileName: fileName,
            sourceUri: uri,
            range: ref.range,
            containerName: node.name || currentContainer,
            fileId,
            fileName,
            uri
          });
        }
      }

      const nextContainer = (symKind ? node.name : currentContainer);
      if (node.children) {
        for (const child of node.children) {
          traverse(child, nextContainer);
        }
      }
    };

    traverse(ast);

    this.fileSymbols.set(fileId, discoveredSymbols);
    this.fileReferences.set(fileId, discoveredReferences);
    this.rebuildCaches();
  }

  public removeArtifact(fileId: string) {
    this.fileSymbols.delete(fileId);
    this.fileReferences.delete(fileId);
    this.rebuildCaches();
  }

  public clear() {
    this.fileSymbols.clear();
    this.fileReferences.clear();
    this.nameIndex.clear();
  }

  /**
   * Finds a symbol definition by exact name and optional kind.
   */
  public findSymbolByName(name: string, kind?: IndexedSymbol['kind']): IndexedSymbol | undefined {
    const list = this.nameIndex.get(name);
    if (!list || list.length === 0) return undefined;
    if (!kind) return list[0];
    return list.find(s => s.kind === kind) || list[0];
  }

  /**
   * Returns all symbols matching a given kind across the entire project.
   */
  public findSymbolsByKind(kind: IndexedSymbol['kind']): IndexedSymbol[] {
    const results: IndexedSymbol[] = [];
    for (const symbols of this.fileSymbols.values()) {
      for (const s of symbols) {
        if (s.kind === kind) results.push(s);
      }
    }
    return results;
  }

  /**
   * Returns all symbols defined in a specific file (for Document Outline).
   */
  public getSymbolsForFile(fileId: string): IndexedSymbol[] {
    return this.fileSymbols.get(fileId) || [];
  }

  /**
   * Returns all symbols across all files.
   */
  public getAllSymbols(): IndexedSymbol[] {
    const all: IndexedSymbol[] = [];
    for (const symbols of this.fileSymbols.values()) {
      all.push(...symbols);
    }
    return all;
  }

  /**
   * Finds all references to a given symbol name across all files (Shift+F12).
   */
  public findReferences(symbolName: string): IndexedReference[] {
    const matches: IndexedReference[] = [];
    for (const refs of this.fileReferences.values()) {
      for (const r of refs) {
        if (r.symbolName === symbolName) {
          matches.push(r);
        }
      }
    }
    return matches;
  }

  /**
   * Fuzzy search for Workspace Symbol Search (Ctrl+T).
   */
  public fuzzySearch(query: string, maxResults = 30): IndexedSymbol[] {
    const cleanQuery = query.toLowerCase().trim();
    if (!cleanQuery) return this.getAllSymbols().slice(0, maxResults);

    const matches: { symbol: IndexedSymbol; score: number }[] = [];

    for (const s of this.getAllSymbols()) {
      const lowerName = s.name.toLowerCase();
      if (lowerName === cleanQuery) {
        matches.push({ symbol: s, score: 100 });
      } else if (lowerName.startsWith(cleanQuery)) {
        matches.push({ symbol: s, score: 80 });
      } else if (lowerName.includes(cleanQuery)) {
        matches.push({ symbol: s, score: 50 });
      }
    }

    matches.sort((a, b) => b.score - a.score);
    return matches.slice(0, maxResults).map(m => m.symbol);
  }

  /**
   * Convenience alias for workspace symbol searches.
   */
  public findSymbols(query: string): IndexedSymbol[] {
    return this.fuzzySearch(query);
  }

  /**
   * Convenience method to index a file by ID and name.
   */
  public indexFile(fileId: string, fileName: string, _content: string, ast: AstNode | null) {
    const uri = monaco.Uri.parse(`kide://workspace/${fileId}/${encodeURIComponent(fileName)}`);
    this.indexArtifact(fileId, fileName, uri, ast);
  }
}

export const projectSymbolIndex = new ProjectSymbolIndex();
