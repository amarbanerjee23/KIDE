/**
 * Document & Workspace Symbol Provider for KIDE Enterprise.
 * Implements Monaco's DocumentSymbolProvider (Outline) and WorkspaceSymbolProvider (Ctrl+T fuzzy search).
 * Builds clean hierarchical symbol trees from ASTs.
 */

import * as monaco from 'monaco-editor';
import { getLanguageForArtifact, parseAst } from './languages';
import { projectSymbolIndex } from './symbolIndex';
import { documentManager } from './documentManager';
import {
  AstNode,
  DmlPackageNode,
  CapabilityNode,
  OperationNode,
  ActivityDiagramNode,
  MncModelNode
} from './ast';

export class KideDocumentSymbolProvider implements monaco.languages.DocumentSymbolProvider {
  public provideDocumentSymbols(
    model: monaco.editor.ITextModel,
    _token: monaco.CancellationToken
  ): monaco.languages.ProviderResult<monaco.languages.DocumentSymbol[]> {
    const text = model.getValue();
    const lang = getLanguageForArtifact(model.uri.path || model.uri.fsPath);
    const ast = parseAst(lang, text);
    if (!ast) return [];

    const symbols: monaco.languages.DocumentSymbol[] = [];

    switch (lang) {
      case 'dmldsl':
        this.extractDmlSymbols(ast as DmlPackageNode, symbols);
        break;
      case 'capabilitydsl':
        this.extractCapabilitySymbols(ast as CapabilityNode, symbols);
        break;
      case 'activitydsl':
        this.extractActivitySymbols(ast as ActivityDiagramNode, symbols);
        break;
      case 'operationdsl':
        this.extractOperationSymbols(ast as OperationNode, symbols);
        break;
      case 'mncml':
        this.extractMncSymbols(ast as MncModelNode, symbols);
        break;
    }

    return symbols;
  }

  private extractDmlSymbols(pkg: DmlPackageNode, out: monaco.languages.DocumentSymbol[]) {
    const models = pkg.models || pkg.dataModels || [];

    for (const dm of models) {
      const dmChildren: monaco.languages.DocumentSymbol[] = [];

      // Primitives
      if (dm.primitives) {
        for (const p of dm.primitives) {
          dmChildren.push(this.createDocSymbol(
            `${p.name || 'unnamed'}: ${p.paramType}`,
            monaco.languages.SymbolKind.Field,
            p.range,
            `Primitive ${p.paramType}`
          ));
        }
      }

      // Composites
      if (dm.composites) {
        for (const c of dm.composites) {
          dmChildren.push(this.createDocSymbol(
            c.name,
            monaco.languages.SymbolKind.Class,
            c.range,
            'Composite reference'
          ));
        }
      }

      out.push(this.createDocSymbol(
        dm.name || 'DataModel',
        monaco.languages.SymbolKind.Class,
        dm.range,
        `DataModel with ${dmChildren.length} members`,
        dmChildren
      ));
    }
  }

  private extractCapabilitySymbols(cap: CapabilityNode, out: monaco.languages.DocumentSymbol[]) {
    const children: monaco.languages.DocumentSymbol[] = [];

    // Interfaces
    if (cap.componentInterfaces) {
      for (const iface of cap.componentInterfaces) {
        children.push(this.createDocSymbol(
          `Interface: ${iface.name}`,
          monaco.languages.SymbolKind.Interface,
          iface.range,
          'Compatible Component Interface'
        ));
      }
    }

    // Init actions
    if (cap.initAction) {
      const initChildren: monaco.languages.DocumentSymbol[] = [];
      const fireCmds = cap.initAction.fireCommands || [];
      for (const cmd of fireCmds) {
        initChildren.push(this.createDocSymbol(
          `fire: ${cmd.name}`,
          monaco.languages.SymbolKind.Function,
          cmd.range,
          'Fire Command'
        ));
      }

      const alarms = cap.initAction.raiseAlarms || cap.initAction.subscribeAlarms || [];
      for (const alm of alarms) {
        initChildren.push(this.createDocSymbol(
          `subscribe: ${alm.name}`,
          monaco.languages.SymbolKind.Event,
          alm.range,
          'Subscribe Alarm'
        ));
      }

      const events = cap.initAction.publishEvents || cap.initAction.subscribeEvents || [];
      for (const evt of events) {
        initChildren.push(this.createDocSymbol(
          `subscribe: ${evt.name}`,
          monaco.languages.SymbolKind.Event,
          evt.range,
          'Subscribe Event'
        ));
      }

      children.push(this.createDocSymbol(
        'Init Action',
        monaco.languages.SymbolKind.Constructor,
        cap.initAction.range,
        'Initialization sequence',
        initChildren
      ));
    }

    // Control capabilities
    if (cap.controlCapabilities) {
      const ctrlChildren: monaco.languages.DocumentSymbol[] = [];
      for (const cmd of cap.controlCapabilities.commands || []) {
        ctrlChildren.push(this.createDocSymbol(cmd.name, monaco.languages.SymbolKind.Function, cmd.range, 'Fireable Command'));
      }
      for (const evt of cap.controlCapabilities.events || []) {
        ctrlChildren.push(this.createDocSymbol(evt.name, monaco.languages.SymbolKind.Event, evt.range, 'Receivable Event'));
      }
      for (const alm of cap.controlCapabilities.alarms || []) {
        ctrlChildren.push(this.createDocSymbol(alm.name, monaco.languages.SymbolKind.Event, alm.range, 'Raised Alarm'));
      }

      children.push(this.createDocSymbol(
        'Control Capabilities',
        monaco.languages.SymbolKind.Module,
        cap.controlCapabilities.range,
        'Exposed capabilities',
        ctrlChildren
      ));
    }

    out.push(this.createDocSymbol(
      cap.name || 'Capability',
      monaco.languages.SymbolKind.Interface,
      cap.range,
      'Capability Contract',
      children
    ));
  }

  private extractActivitySymbols(diag: ActivityDiagramNode, out: monaco.languages.DocumentSymbol[]) {
    const actChildren: monaco.languages.DocumentSymbol[] = [];

    if (diag.activities) {
      for (const act of diag.activities) {
        const itemChildren: monaco.languages.DocumentSymbol[] = [];

        const reqCap = act.requiredCapability || act.requireCapability;
        if (reqCap) {
          itemChildren.push(this.createDocSymbol(
            `requireCapability: ${reqCap.name}`,
            monaco.languages.SymbolKind.Interface,
            reqCap.range,
            'Device Capability Binding'
          ));
        }

        const reqOps = act.requiredOperations || (act.requireOperation ? [act.requireOperation] : []);
        for (const op of reqOps) {
          itemChildren.push(this.createDocSymbol(
            `requireOperation: ${op.name}`,
            monaco.languages.SymbolKind.Function,
            op.range,
            'Operation Binding'
          ));
        }

        if (act.nextActivity) {
          itemChildren.push(this.createDocSymbol(
            `nextActivity: ${act.nextActivity.name}`,
            monaco.languages.SymbolKind.Variable,
            act.nextActivity.range,
            'Unconditional Transition'
          ));
        }

        actChildren.push(this.createDocSymbol(
          act.name || 'Activity',
          monaco.languages.SymbolKind.Method,
          act.range,
          act.description || 'Activity Step',
          itemChildren
        ));
      }
    }

    const contextName = diag.contextDataModel?.name || diag.contextModel?.name || 'none';
    out.push(this.createDocSymbol(
      diag.name || 'ActivityDiagram',
      monaco.languages.SymbolKind.Package,
      diag.range,
      `ActivityDiagram on context ${contextName}`,
      actChildren
    ));
  }

  private extractOperationSymbols(op: OperationNode, out: monaco.languages.DocumentSymbol[]) {
    const children: monaco.languages.DocumentSymbol[] = [];

    if (op.inputParameters) {
      for (const p of op.inputParameters) {
        children.push(this.createDocSymbol(
          `in: ${p.name || 'param'} (${p.paramType})`,
          monaco.languages.SymbolKind.Variable,
          p.range,
          'Input parameter'
        ));
      }
    }

    const ret = op.outputParameters || op.outputParameter;
    if (ret) {
      children.push(this.createDocSymbol(
        `return: ${ret.name || 'ret'} (${ret.paramType})`,
        monaco.languages.SymbolKind.Variable,
        ret.range,
        'Return parameter'
      ));
    }

    out.push(this.createDocSymbol(
      op.name || 'Operation',
      monaco.languages.SymbolKind.Function,
      op.range,
      `Operation with ${children.length} params`,
      children
    ));
  }

  private extractMncSymbols(mnc: MncModelNode, out: monaco.languages.DocumentSymbol[]) {
    if (mnc.interfaceDescription) {
      const iface = mnc.interfaceDescription;
      const ifaceChildren: monaco.languages.DocumentSymbol[] = [];

      for (const cmd of iface.commands || []) {
        ifaceChildren.push(this.createDocSymbol(cmd.name || 'Command', monaco.languages.SymbolKind.Function, cmd.range, 'Command'));
      }
      for (const evt of iface.events || []) {
        ifaceChildren.push(this.createDocSymbol(evt.name || 'Event', monaco.languages.SymbolKind.Event, evt.range, 'Event'));
      }
      for (const alm of iface.alarms || []) {
        ifaceChildren.push(this.createDocSymbol(alm.name || 'Alarm', monaco.languages.SymbolKind.Event, alm.range, 'Alarm'));
      }
      for (const dp of iface.dataPoints || []) {
        ifaceChildren.push(this.createDocSymbol(dp.name || 'DataPoint', monaco.languages.SymbolKind.Field, dp.range, 'DataPoint'));
      }
      for (const st of iface.operatingStates || []) {
        ifaceChildren.push(this.createDocSymbol(st.name || 'State', monaco.languages.SymbolKind.EnumMember, st.range, 'Operating State'));
      }

      out.push(this.createDocSymbol(
        iface.name || 'InterfaceDescription',
        monaco.languages.SymbolKind.Interface,
        iface.range,
        'InterfaceDescription',
        ifaceChildren
      ));
    }

    if (mnc.controlNode) {
      const cn = mnc.controlNode;
      const cnChildren: monaco.languages.DocumentSymbol[] = [];

      for (const tr of cn.transitions || []) {
        const fromState = tr.currentStates?.join(', ') || tr.currentState || 'any';
        cnChildren.push(this.createDocSymbol(
          `${fromState} => ${tr.nextState}`,
          monaco.languages.SymbolKind.Event,
          tr.range,
          'State Transition'
        ));
      }

      const ifaceRef = cn.implementedInterface?.name || cn.implementsInterface?.name || 'none';
      out.push(this.createDocSymbol(
        cn.name || 'ControlNode',
        monaco.languages.SymbolKind.Class,
        cn.range,
        `ControlNode implements ${ifaceRef}`,
        cnChildren
      ));
    }
  }

  private createDocSymbol(
    name: string,
    kind: monaco.languages.SymbolKind,
    range: AstNode['range'],
    detail: string,
    children?: monaco.languages.DocumentSymbol[]
  ): monaco.languages.DocumentSymbol {
    const monacoRange = new monaco.Range(
      range.startLine,
      range.startColumn,
      range.endLine,
      range.endColumn
    );

    return {
      name,
      detail,
      kind,
      range: monacoRange,
      selectionRange: monacoRange,
      tags: [],
      children: children || []
    };
  }
}

export interface KideWorkspaceSymbol {
  name: string;
  kind: monaco.languages.SymbolKind;
  containerName?: string;
  location: {
    uri: monaco.Uri;
    range: monaco.Range;
  };
}

export class KideWorkspaceSymbolProvider {
  public provideWorkspaceSymbols(
    query: string
  ): KideWorkspaceSymbol[] {
    const symbols = projectSymbolIndex.findSymbols(query);
    const results: KideWorkspaceSymbol[] = [];

    const KIND_MAP: Record<string, monaco.languages.SymbolKind> = {
      DataModel: monaco.languages.SymbolKind.Class,
      Capability: monaco.languages.SymbolKind.Interface,
      Operation: monaco.languages.SymbolKind.Function,
      Activity: monaco.languages.SymbolKind.Method,
      ActivityDiagram: monaco.languages.SymbolKind.Package,
      InterfaceDescription: monaco.languages.SymbolKind.Interface,
      ControlNode: monaco.languages.SymbolKind.Class,
      State: monaco.languages.SymbolKind.EnumMember,
      Command: monaco.languages.SymbolKind.Function,
      Event: monaco.languages.SymbolKind.Event,
      Alarm: monaco.languages.SymbolKind.Event,
      DataPoint: monaco.languages.SymbolKind.Field
    };

    for (const sym of symbols) {
      let uri = monaco.Uri.parse(`kide://workspace/${sym.fileId}/${sym.fileName}`);
      const doc = documentManager.getDocument(sym.fileId);
      if (doc) {
        uri = doc.uri;
      }

      results.push({
        name: sym.name,
        kind: KIND_MAP[sym.kind] || monaco.languages.SymbolKind.Variable,
        containerName: sym.containerName || sym.fileName,
        location: {
          uri,
          range: new monaco.Range(
            sym.range.startLine,
            sym.range.startColumn,
            sym.range.endLine,
            sym.range.endColumn
          )
        }
      });
    }

    return results;
  }
}

export const kideDocumentSymbolProvider = new KideDocumentSymbolProvider();
export const kideWorkspaceSymbolProvider = new KideWorkspaceSymbolProvider();

