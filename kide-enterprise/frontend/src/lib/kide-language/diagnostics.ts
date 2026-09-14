/**
 * First-Class Diagnostics and Semantic Validation Engine for KIDE Enterprise DSLs.
 * Computes exact syntax error ranges and deep semantic reference errors.
 */

import * as monaco from 'monaco-editor';
import { AstNode, SourceRange } from './ast';
import { projectSymbolIndex } from './symbolIndex';
import { scopeResolver } from './scopeResolver';
import { ParseSyntaxError } from './languages/dml/parser';

export interface KideDiagnostic {
  code: string;
  severity: 'error' | 'warning' | 'info';
  message: string;
  startLineNumber: number;
  startColumn: number;
  endLineNumber: number;
  endColumn: number;
  source: 'KIDE';
  quickFixSuggestion?: {
    title: string;
    replacement: string;
    range: SourceRange;
  };
}

export class DiagnosticsEngine {
  /**
   * Computes all syntax and semantic diagnostics for a parsed artifact.
   */
  public computeDiagnostics(
    _fileId: string,
    _fileName: string,
    ast: AstNode | null,
    syntaxErrors: ParseSyntaxError[]
  ): KideDiagnostic[] {
    const diagnostics: KideDiagnostic[] = [];

    // 1. Add syntax errors directly from the parser
    for (const synErr of syntaxErrors) {
      diagnostics.push({
        code: synErr.code || 'KIDE-SYNTAX-ERROR',
        severity: synErr.severity || 'error',
        message: synErr.message,
        startLineNumber: synErr.range.startLine,
        startColumn: Math.max(1, synErr.range.startColumn),
        endLineNumber: synErr.range.endLine,
        endColumn: Math.max(synErr.range.startColumn + 1, synErr.range.endColumn),
        source: 'KIDE'
      });
    }

    if (!ast) return diagnostics;

    // 2. Semantic validations
    if (ast.type === 'Capability') {
      this.validateCapability(ast as any, diagnostics);
    } else if (ast.type === 'ActivityDiagram') {
      this.validateActivityDiagram(ast as any, diagnostics);
    } else if (ast.type === 'Model') {
      this.validateMncModel(ast as any, diagnostics);
    } else if (ast.type === 'DataPackage') {
      this.validateDataPackage(ast as any, diagnostics);
    }

    return diagnostics;
  }

  private validateCapability(cap: any, out: KideDiagnostic[]) {
    // Check component interface existence
    if (cap.componentInterfaces) {
      for (const ifRef of cap.componentInterfaces) {
        const found = projectSymbolIndex.findSymbolByName(ifRef.name, 'InterfaceDescription');
        if (!found) {
          out.push({
            code: 'KIDE-CAP-UNRESOLVED-IFACE',
            severity: 'error',
            message: `Unresolved component interface '${ifRef.name}'. Ensure an MNC-ML file declares 'InterfaceDescription ${ifRef.name}'.`,
            startLineNumber: ifRef.range.startLine,
            startColumn: ifRef.range.startColumn,
            endLineNumber: ifRef.range.endLine,
            endColumn: ifRef.range.endColumn,
            source: 'KIDE'
          });
        }
      }
    }

    // Check that fire commands in Init are part of compatible interface
    const validCmds = new Set(scopeResolver.getCommandsForCapability(cap).map(c => c.name));
    if (cap.initAction?.fireCommands) {
      for (const cmdRef of cap.initAction.fireCommands) {
        if (!validCmds.has(cmdRef.name)) {
          out.push({
            code: 'KIDE-CAP-INVALID-COMMAND',
            severity: 'error',
            message: `Command '${cmdRef.name}' is not a part of component interface description.`,
            startLineNumber: cmdRef.range.startLine,
            startColumn: cmdRef.range.startColumn,
            endLineNumber: cmdRef.range.endLine,
            endColumn: cmdRef.range.endColumn,
            source: 'KIDE'
          });
        }
      }
    }

    // Check that alarms in Init are part of compatible interface
    const validAlarms = new Set(scopeResolver.getAlarmsForCapability(cap).map(a => a.name));
    if (cap.initAction?.raiseAlarms) {
      for (const almRef of cap.initAction.raiseAlarms) {
        if (!validAlarms.has(almRef.name)) {
          out.push({
            code: 'KIDE-CAP-INVALID-ALARM',
            severity: 'error',
            message: `Alarm '${almRef.name}' is not a part of component interface description.`,
            startLineNumber: almRef.range.startLine,
            startColumn: almRef.range.startColumn,
            endLineNumber: almRef.range.endLine,
            endColumn: almRef.range.endColumn,
            source: 'KIDE'
          });
        }
      }
    }
  }

  private validateActivityDiagram(diag: any, out: KideDiagnostic[]) {
    // Check context DataModel
    if (diag.contextDataModel) {
      const dm = projectSymbolIndex.findSymbolByName(diag.contextDataModel.name, 'DataModel');
      if (!dm) {
        out.push({
          code: 'KIDE-ACT-UNRESOLVED-DATAMODEL',
          severity: 'error',
          message: `Unresolved context DataModel '${diag.contextDataModel.name}'.`,
          startLineNumber: diag.contextDataModel.range.startLine,
          startColumn: diag.contextDataModel.range.startColumn,
          endLineNumber: diag.contextDataModel.range.endLine,
          endColumn: diag.contextDataModel.range.endColumn,
          source: 'KIDE'
        });
      }
    }

    const activityNames = new Set(diag.activities.map((a: any) => a.name));

    for (const act of diag.activities) {
      // Check required capability
      if (act.requiredCapability) {
        const capSym = projectSymbolIndex.findSymbolByName(act.requiredCapability.name, 'Capability');
        if (!capSym) {
          out.push({
            code: 'KIDE-ACT-UNRESOLVED-CAPABILITY',
            severity: 'error',
            message: `Unknown capability '${act.requiredCapability.name}'.`,
            startLineNumber: act.requiredCapability.range.startLine,
            startColumn: act.requiredCapability.range.startColumn,
            endLineNumber: act.requiredCapability.range.endLine,
            endColumn: act.requiredCapability.range.endColumn,
            source: 'KIDE'
          });
        }
      }

      // Check required operations
      if (act.requiredOperations) {
        for (const opRef of act.requiredOperations) {
          const opSym = projectSymbolIndex.findSymbolByName(opRef.name, 'Operation');
          if (!opSym) {
            out.push({
              code: 'KIDE-ACT-UNRESOLVED-OPERATION',
              severity: 'error',
              message: `Unknown operation '${opRef.name}'.`,
              startLineNumber: opRef.range.startLine,
              startColumn: opRef.range.startColumn,
              endLineNumber: opRef.range.endLine,
              endColumn: opRef.range.endColumn,
              source: 'KIDE'
            });
          }
        }
      }

      // Check nextActivity target
      if (act.nextActivity) {
        if (!activityNames.has(act.nextActivity.name)) {
          out.push({
            code: 'KIDE-ACT-UNRESOLVED-TARGET',
            severity: 'error',
            message: `Target activity '${act.nextActivity.name}' does not exist in this ActivityDiagram.`,
            startLineNumber: act.nextActivity.range.startLine,
            startColumn: act.nextActivity.range.startColumn,
            endLineNumber: act.nextActivity.range.endLine,
            endColumn: act.nextActivity.range.endColumn,
            source: 'KIDE'
          });
        }
      }
    }
  }

  private validateMncModel(model: any, out: KideDiagnostic[]) {
    if (model.controlNode?.implementedInterface) {
      const ifRef = model.controlNode.implementedInterface;
      const ifSym = projectSymbolIndex.findSymbolByName(ifRef.name, 'InterfaceDescription');
      if (!ifSym) {
        out.push({
          code: 'KIDE-MNC-UNRESOLVED-IFACE',
          severity: 'error',
          message: `ControlNode implements unknown interface '${ifRef.name}'.`,
          startLineNumber: ifRef.range.startLine,
          startColumn: ifRef.range.startColumn,
          endLineNumber: ifRef.range.endLine,
          endColumn: ifRef.range.endColumn,
          source: 'KIDE'
        });
      }
    }
  }

  private validateDataPackage(pkg: any, out: KideDiagnostic[]) {
    const declaredNames = new Set<string>();
    for (const model of pkg.models) {
      if (declaredNames.has(model.name)) {
        out.push({
          code: 'KIDE-DML-DUPLICATE-MODEL',
          severity: 'error',
          message: `Duplicate DataModel '${model.name}' declared in the same package.`,
          startLineNumber: model.range.startLine,
          startColumn: model.range.startColumn,
          endLineNumber: model.range.endLine,
          endColumn: model.range.endColumn,
          source: 'KIDE'
        });
      }
      declaredNames.add(model.name);
    }
  }

  /**
   * Applies diagnostics as Monaco Markers to an active model.
   */
  public applyToModel(model: monaco.editor.ITextModel, diagnostics: KideDiagnostic[]) {
    const markers: monaco.editor.IMarkerData[] = diagnostics.map(d => ({
      severity: d.severity === 'error'
        ? monaco.MarkerSeverity.Error
        : d.severity === 'warning'
        ? monaco.MarkerSeverity.Warning
        : monaco.MarkerSeverity.Info,
      message: d.message,
      startLineNumber: d.startLineNumber,
      startColumn: d.startColumn,
      endLineNumber: d.endLineNumber,
      endColumn: d.endColumn,
      source: d.source,
      code: d.code
    }));

    monaco.editor.setModelMarkers(model, 'KIDE', markers);
  }
}

export const diagnosticsEngine = new DiagnosticsEngine();
