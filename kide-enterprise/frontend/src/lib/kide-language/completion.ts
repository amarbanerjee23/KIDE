/**
 * Semantic Completion Provider for KIDE Enterprise.
 * Implements Monaco's CompletionItemProvider for all KIDE DSLs.
 * Enforces strict semantic scoping (e.g., Capability action proposals strictly filtered
 * to commands/events/alarms/datapoints from compatible component interfaces).
 */

import * as monaco from 'monaco-editor';
import { getLanguageForArtifact } from './languages';
import { projectSymbolIndex, IndexedSymbol } from './symbolIndex';
import { scopeResolver } from './scopeResolver';
import { parseAst } from './languages';
import { CapabilityNode, ActivityDiagramNode, MncModelNode } from './ast';

export class KideCompletionItemProvider implements monaco.languages.CompletionItemProvider {
  public triggerCharacters = [' ', ':', '{', '(', '[', ',', '.'];

  public provideCompletionItems(
    model: monaco.editor.ITextModel,
    position: monaco.Position,
    _context: monaco.languages.CompletionContext,
    _token: monaco.CancellationToken
  ): monaco.languages.ProviderResult<monaco.languages.CompletionList> {
    const text = model.getValue();
    const lang = getLanguageForArtifact(model.uri.path || model.uri.fsPath);
    const linePrefix = model.getLineContent(position.lineNumber).substring(0, position.column - 1);
    const fullTextBeforeCursor = text.substring(0, model.getOffsetAt(position));

    const suggestions: monaco.languages.CompletionItem[] = [];

    // Parse AST of current document for contextual scoping
    const ast = parseAst(lang, text);

    switch (lang) {
      case 'capabilitydsl':
        this.provideCapabilityCompletions(linePrefix, fullTextBeforeCursor, ast as CapabilityNode | null, suggestions);
        break;
      case 'activitydsl':
        this.provideActivityCompletions(linePrefix, fullTextBeforeCursor, ast as ActivityDiagramNode | null, suggestions);
        break;
      case 'operationdsl':
        this.provideOperationCompletions(linePrefix, fullTextBeforeCursor, suggestions);
        break;
      case 'dmldsl':
        this.provideDmlCompletions(linePrefix, fullTextBeforeCursor, suggestions);
        break;
      case 'mncml':
        this.provideMncCompletions(linePrefix, fullTextBeforeCursor, ast as MncModelNode | null, suggestions);
        break;
    }

    // Add language-specific keywords and snippets if not in a strict inner scope
    if (suggestions.length === 0 || !this.isStrictInnerScope(linePrefix)) {
      this.provideSnippetsAndKeywords(lang, linePrefix, suggestions);
    }

    return {
      suggestions
    };
  }

  private isStrictInnerScope(prefix: string): boolean {
    return /((fire\s+Commands|subscribe\s+(alarms|events|data)|execute\s+Operations)\s*\[|(requireCapability|requireOperation|nextActivity)\s*:|implements\s+interface|currentState|nextState)/i.test(prefix);
  }

  // =========================================================================
  // Capability DSL Completions
  // =========================================================================
  private provideCapabilityCompletions(
    prefix: string,
    _fullBefore: string,
    cap: CapabilityNode | null,
    out: monaco.languages.CompletionItem[]
  ) {
    // 1. Compatible component interface
    if (/compatible\s+component\s+interface\s*([A-Za-z0-9_,\s]*)$/i.test(prefix)) {
      const interfaces = projectSymbolIndex.findSymbolsByKind('InterfaceDescription');
      for (const iface of interfaces) {
        out.push(this.createSymbolItem(iface, monaco.languages.CompletionItemKind.Interface, 'Compatible Component Interface'));
      }
      return;
    }

    // 2. Strict Filter: Commands inside fire Commands [ ... ]
    if (/fire\s+Commands\s*\[[^\]]*$/i.test(prefix)) {
      const commands = scopeResolver.getCommandsForCapability(cap);
      for (const cmd of commands) {
        out.push(this.createSymbolItem(cmd, monaco.languages.CompletionItemKind.Function, `Command from ${cmd.containerName}`));
      }
      return;
    }

    // 3. Strict Filter: Alarms inside subscribe alarms [ ... ] or raised alarms : ...
    if (/subscribe\s+alarms\s*\[[^\]]*$/i.test(prefix) || /raised\s+alarms\s*:[^}]*$/i.test(prefix)) {
      const alarms = scopeResolver.getAlarmsForCapability(cap);
      for (const alm of alarms) {
        out.push(this.createSymbolItem(alm, monaco.languages.CompletionItemKind.Issue, `Alarm from ${alm.containerName}`));
      }
      return;
    }

    // 4. Strict Filter: Events inside subscribe events [ ... ] or receivable events : ...
    if (/subscribe\s+events\s*\[[^\]]*$/i.test(prefix) || /receivable\s+events\s*:[^}]*$/i.test(prefix)) {
      const events = scopeResolver.getEventsForCapability(cap);
      for (const evt of events) {
        out.push(this.createSymbolItem(evt, monaco.languages.CompletionItemKind.Event, `Event from ${evt.containerName}`));
      }
      return;
    }

    // 5. Strict Filter: DataPoints inside subscribe data [ ... ] or subscribable DataPoints : ...
    if (/subscribe\s+data\s*\[[^\]]*$/i.test(prefix) || /subscribable\s+DataPoints\s*:[^}]*$/i.test(prefix)) {
      const dps = scopeResolver.getDataPointsForCapability(cap);
      for (const dp of dps) {
        out.push(this.createSymbolItem(dp, monaco.languages.CompletionItemKind.Field, `DataPoint from ${dp.containerName}`));
      }
      return;
    }

    // 6. Operations in execute Operations [ ... ]
    if (/execute\s+Operations\s*\[[^\]]*$/i.test(prefix)) {
      const ops = scopeResolver.getOperationsForActivity();
      for (const op of ops) {
        out.push(this.createSymbolItem(op, monaco.languages.CompletionItemKind.Function, 'Executable Operation'));
      }
      return;
    }

    // Top-level / block suggestions
    if (/providesControlCapabilities\s*\{[^}]*$/i.test(prefix)) {
      const subClauses = [
        { label: 'fireable commands:', insert: 'fireable commands: ' },
        { label: 'receivable events:', insert: 'receivable events: ' },
        { label: 'raised alarms:', insert: 'raised alarms: ' },
        { label: 'subscribable DataPoints:', insert: 'subscribable DataPoints: ' }
      ];
      for (const sc of subClauses) {
        out.push({
          label: sc.label,
          kind: monaco.languages.CompletionItemKind.Property,
          insertText: sc.insert,
          detail: 'Control Capability Category'
        } as monaco.languages.CompletionItem);
      }
    }
  }

  // =========================================================================
  // Activity DSL Completions
  // =========================================================================
  private provideActivityCompletions(
    prefix: string,
    _fullBefore: string,
    diagram: ActivityDiagramNode | null,
    out: monaco.languages.CompletionItem[]
  ) {
    // 1. on context <DataModel>
    if (/on\s+context\s*([A-Za-z0-9_,\s]*)$/i.test(prefix)) {
      const models = scopeResolver.getDataModelsForContext();
      for (const m of models) {
        out.push(this.createSymbolItem(m, monaco.languages.CompletionItemKind.Class, 'Context DataModel'));
      }
      return;
    }

    // 2. requireCapability : <Capability>
    if (/requireCapability\s*:\s*([A-Za-z0-9_]*)$/i.test(prefix)) {
      const caps = scopeResolver.getCapabilitiesForActivity();
      for (const c of caps) {
        out.push(this.createSymbolItem(c, monaco.languages.CompletionItemKind.Interface, 'Device Capability'));
      }
      return;
    }

    // 3. requireOperation( <Operation> )
    if (/requireOperation\s*\(\s*([A-Za-z0-9_,\s]*)$/i.test(prefix)) {
      const ops = scopeResolver.getOperationsForActivity();
      for (const o of ops) {
        out.push(this.createSymbolItem(o, monaco.languages.CompletionItemKind.Function, 'Required Operation'));
      }
      return;
    }

    // 4. nextActivity : <Activity> or interrupts/interruptedBy
    if (/(nextActivity\s*:|interrupts\s*\(|interruptedBy\s*\()\s*([A-Za-z0-9_]*)$/i.test(prefix)) {
      const acts = scopeResolver.getActivitiesForDiagram(diagram);
      for (const a of acts) {
        out.push(this.createSymbolItem(a, monaco.languages.CompletionItemKind.Event, 'Next Activity Step'));
      }
      return;
    }

    // 5. Unit of time suggestions
    if (/time\s*:\s*[0-9.]+\s*([a-z]*)$/i.test(prefix)) {
      const units = ['secs', 'mins', 'hrs', 'days'];
      for (const u of units) {
        out.push({
          label: u,
          kind: monaco.languages.CompletionItemKind.Unit,
          insertText: u,
          detail: `Time unit: ${u}`
        } as monaco.languages.CompletionItem);
      }
      return;
    }
  }

  // =========================================================================
  // Operation DSL Completions
  // =========================================================================
  private provideOperationCompletions(
    prefix: string,
    _fullBefore: string,
    out: monaco.languages.CompletionItem[]
  ) {
    // Types in parameters or return
    if (/(\(|,\s*|return\s+)\s*([A-Za-z0-9_]*)$/i.test(prefix)) {
      const primitives = ['int', 'boolean', 'float', 'string', 'object', 'date'];
      for (const p of primitives) {
        out.push({
          label: p,
          kind: monaco.languages.CompletionItemKind.TypeParameter,
          insertText: p,
          detail: 'Primitive parameter type'
        } as monaco.languages.CompletionItem);
      }
      const dataModels = projectSymbolIndex.findSymbolsByKind('DataModel');
      for (const dm of dataModels) {
        out.push(this.createSymbolItem(dm, monaco.languages.CompletionItemKind.Class, 'Composite DataModel Type'));
      }
    }
  }

  // =========================================================================
  // DML DSL Completions
  // =========================================================================
  private provideDmlCompletions(
    prefix: string,
    _fullBefore: string,
    out: monaco.languages.CompletionItem[]
  ) {
    // In composites { ... }
    if (/composites\s*\{[^}]*$/i.test(prefix)) {
      const models = projectSymbolIndex.findSymbolsByKind('DataModel');
      for (const m of models) {
        out.push(this.createSymbolItem(m, monaco.languages.CompletionItemKind.Class, 'Composite DataModel'));
      }
      return;
    }

    // In primitives { ... }
    if (/primitives\s*\{[^}]*$/i.test(prefix)) {
      const primitives = ['int', 'boolean', 'float', 'string', 'object', 'date'];
      for (const p of primitives) {
        out.push({
          label: `${p} paramName`,
          kind: monaco.languages.CompletionItemKind.Snippet,
          insertText: `${p} \${1:name} = \${2:value}`,
          insertTextRules: monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
          detail: `New ${p} parameter`
        } as monaco.languages.CompletionItem);
      }
    }
  }

  // =========================================================================
  // MNC-ML Completions
  // =========================================================================
  private provideMncCompletions(
    prefix: string,
    _fullBefore: string,
    mnc: MncModelNode | null,
    out: monaco.languages.CompletionItem[]
  ) {
    // 1. implements interface <InterfaceDescription>
    if (/implements\s+interface\s*([A-Za-z0-9_]*)$/i.test(prefix)) {
      const ifaces = projectSymbolIndex.findSymbolsByKind('InterfaceDescription');
      for (const iface of ifaces) {
        out.push(this.createSymbolItem(iface, monaco.languages.CompletionItemKind.Interface, 'Interface to Implement'));
      }
      return;
    }

    // 2. currentState ... => nextState ...
    if (/(currentState|nextState)\s*([A-Za-z0-9_]*)$/i.test(prefix)) {
      const states = scopeResolver.getOperatingStatesForMnc(mnc);
      for (const st of states) {
        out.push(this.createSymbolItem(st, monaco.languages.CompletionItemKind.Value, `OperatingState of ${st.containerName || 'Model'}`));
      }
      return;
    }

    // 3. Command in Command block
    if (/CommandResponseBlock\s*\{[^}]*Command\s*([A-Za-z0-9_]*)$/i.test(prefix)) {
      const commands = projectSymbolIndex.findSymbolsByKind('Command');
      for (const cmd of commands) {
        out.push(this.createSymbolItem(cmd, monaco.languages.CompletionItemKind.Function, `Interface Command ${cmd.name}`));
      }
      return;
    }
  }

  // =========================================================================
  // Helper: Create Monaco Completion Item from IndexedSymbol
  // =========================================================================
  private createSymbolItem(
    sym: IndexedSymbol,
    kind: monaco.languages.CompletionItemKind,
    detailDesc?: string
  ): monaco.languages.CompletionItem {
    return {
      label: sym.name,
      kind,
      insertText: sym.name,
      detail: detailDesc || `${sym.kind} ${sym.name} (${sym.fileName})`,
      documentation: {
        value: `**${sym.kind}** \`${sym.name}\`\n\n*Declared in:* \`${sym.fileName}\` (line ${sym.range.startLine})\n\n${sym.documentation || ''}`
      },
      sortText: `0_${sym.name}`
    } as monaco.languages.CompletionItem;
  }

  // =========================================================================
  // Language Snippets & Keywords
  // =========================================================================
  private provideSnippetsAndKeywords(
    lang: string,
    _prefix: string,
    out: monaco.languages.CompletionItem[]
  ) {
    if (lang === 'capabilitydsl') {
      out.push({
        label: 'Capability template',
        kind: monaco.languages.CompletionItemKind.Snippet,
        insertText: [
          'Capability ${1:CapabilityName} compatible component interface ${2:InterfaceName} {',
          '    Init {',
          '        fire Commands [ ${3:commandName}() ]',
          '        subscribe alarms [ ${4:alarmName}() ]',
          '    }',
          '    providesControlCapabilities {',
          '        fireable commands: ${3:commandName}',
          '    }',
          '}'
        ].join('\n'),
        insertTextRules: monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
        detail: 'Full Capability declaration snippet'
      } as monaco.languages.CompletionItem);
    } else if (lang === 'activitydsl') {
      out.push({
        label: 'ActivityDiagram template',
        kind: monaco.languages.CompletionItemKind.Snippet,
        insertText: [
          'ActivityDiagram ${1:DiagramName} on context ${2:ContextModel} has activities {',
          '    Activity ${3:InitActivity} {',
          '        description: "${4:Initial Step}"',
          '        requireCapability: ${5:CapabilityName}',
          '        nextActivity: ${6:NextActivity}',
          '    },',
          '    Activity ${6:NextActivity} {',
          '        description: "${7:Final Step}"',
          '    }',
          '}'
        ].join('\n'),
        insertTextRules: monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
        detail: 'Full ActivityDiagram declaration snippet'
      } as monaco.languages.CompletionItem);

      out.push({
        label: 'Activity step',
        kind: monaco.languages.CompletionItemKind.Snippet,
        insertText: [
          'Activity ${1:ActivityName} {',
          '    description: "${2:Activity description}"',
          '    requireCapability: ${3:CapabilityName}',
          '    nextActivity: ${4:NextActivity}',
          '}'
        ].join('\n'),
        insertTextRules: monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
        detail: 'New Activity step'
      } as monaco.languages.CompletionItem);
    } else if (lang === 'dmldsl') {
      out.push({
        label: 'DataModel template',
        kind: monaco.languages.CompletionItemKind.Snippet,
        insertText: [
          'DataModel ${1:ModelName} {',
          '    primitives {',
          '        int ${2:id} = 0,',
          '        string ${3:name} = "default"',
          '    }',
          '}'
        ].join('\n'),
        insertTextRules: monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
        detail: 'DataModel with primitives'
      } as monaco.languages.CompletionItem);
    } else if (lang === 'operationdsl') {
      out.push({
        label: 'Operation template',
        kind: monaco.languages.CompletionItemKind.Snippet,
        insertText: [
          'Operation ${1:OperationName}(${2:int inputParam}) {',
          '    execute "${3:echo executing}"',
          '    return ${4:int status = 0}',
          '}'
        ].join('\n'),
        insertTextRules: monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
        detail: 'Operation definition with script and return'
      } as monaco.languages.CompletionItem);
    } else if (lang === 'mncml') {
      out.push({
        label: 'InterfaceDescription template',
        kind: monaco.languages.CompletionItemKind.Snippet,
        insertText: [
          'InterfaceDescription ${1:InterfaceName} {',
          '    commands {',
          '        ${2:startCommand}[]',
          '    }',
          '    operatingStates {',
          '        IDLE[]',
          '        RUNNING[]',
          '        startStates: IDLE',
          '        endStates: IDLE',
          '    }',
          '}'
        ].join('\n'),
        insertTextRules: monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
        detail: 'InterfaceDescription with commands and states'
      } as monaco.languages.CompletionItem);
    }
  }
}

export const kideCompletionProvider = new KideCompletionItemProvider();
