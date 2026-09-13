/**
 * Folding Range Provider for KIDE Enterprise.
 * Implements Monaco's FoldingRangeProvider based on native AST blocks and comment blocks.
 */

import * as monaco from 'monaco-editor';
import { getLanguageForArtifact } from './languages';
import { parseAst } from './languages';
import {
  AstNode,
  DmlPackageNode,
  CapabilityNode,
  ActivityDiagramNode,
  MncModelNode
} from './ast';

export class KideFoldingRangeProvider implements monaco.languages.FoldingRangeProvider {
  public provideFoldingRanges(
    model: monaco.editor.ITextModel,
    _context: monaco.languages.FoldingContext,
    _token: monaco.CancellationToken
  ): monaco.languages.ProviderResult<monaco.languages.FoldingRange[]> {
    const text = model.getValue();
    const lang = getLanguageForArtifact(model.uri.path || model.uri.fsPath);
    const ranges: monaco.languages.FoldingRange[] = [];

    // 1. Comment folding (multiline comments)
    this.extractCommentFolding(text, ranges);

    // 2. AST block folding
    const ast = parseAst(lang, text);
    if (ast) {
      this.extractAstFolding(ast, ranges);
    }

    return ranges;
  }

  private extractCommentFolding(text: string, out: monaco.languages.FoldingRange[]) {
    const lines = text.split('\n');
    let inBlockComment = false;
    let commentStartLine = 0;

    for (let i = 0; i < lines.length; i++) {
      const line = lines[i];
      const lineNum = i + 1;

      if (!inBlockComment && line.includes('/*')) {
        inBlockComment = true;
        commentStartLine = lineNum;
      }

      if (inBlockComment && line.includes('*/')) {
        inBlockComment = false;
        if (lineNum > commentStartLine) {
          out.push({
            start: commentStartLine,
            end: lineNum,
            kind: monaco.languages.FoldingRangeKind.Comment
          });
        }
      }
    }
  }

  private extractAstFolding(node: AstNode, out: monaco.languages.FoldingRange[]) {
    if (!node) return;

    if (node.range && node.range.endLine > node.range.startLine) {
      out.push({
        start: node.range.startLine,
        end: node.range.endLine,
        kind: monaco.languages.FoldingRangeKind.Region
      });
    }

    // Recurse children based on node type
    switch (node.type) {
      case 'DmlPackage': {
        const pkg = node as DmlPackageNode;
        const models = pkg.models || pkg.dataModels || [];
        for (const dm of models) {
          this.extractAstFolding(dm, out);
        }
        break;
      }
      case 'DataModel': {
        // Fold primitives/composites if they span multiple lines
        break;
      }
      case 'Capability': {
        const cap = node as CapabilityNode;
        if (cap.initAction) this.extractAstFolding(cap.initAction, out);
        if (cap.controlCapabilities) this.extractAstFolding(cap.controlCapabilities, out);
        break;
      }
      case 'ActivityDiagram': {
        const diag = node as ActivityDiagramNode;
        for (const act of diag.activities || []) {
          this.extractAstFolding(act, out);
        }
        break;
      }
      case 'Activity': {
        // Individual activity is already folded by node.range
        break;
      }
      case 'Operation': {
        // Operation node already folded by node.range
        break;
      }
      case 'MncModel': {
        const mnc = node as MncModelNode;
        if (mnc.interfaceDescription) this.extractAstFolding(mnc.interfaceDescription, out);
        if (mnc.controlNode) this.extractAstFolding(mnc.controlNode, out);
        break;
      }
      case 'InterfaceDescription':
      case 'ControlNode': {
        // Handled by range
        break;
      }
    }
  }
}

export const kideFoldingRangeProvider = new KideFoldingRangeProvider();
