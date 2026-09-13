import * as monaco from 'monaco-editor';
import { useEditorStore } from '../../stores/editorStore';
import { parseText } from '../../api/parse';

export interface SymbolDefinition {
  name: string;
  kind: 'DataModel' | 'Capability' | 'Operation' | 'Activity' | 'InterfaceDescription' | 'ControlNode' | 'State' | 'Command' | 'Event' | 'Alarm' | 'DataPoint';
  fileId: string;
  fileName: string;
  line: number;
  column: number;
  detail: string;
  documentation?: string;
}

/**
 * Builds an index of all symbols declared across all files in the current workspace.
 */
export function buildWorkspaceSymbolIndex(): SymbolDefinition[] {
  const files = useEditorStore.getState().files;
  const symbols: SymbolDefinition[] = [];

  for (const file of files) {
    const lines = file.content.split('\n');

    for (let i = 0; i < lines.length; i++) {
      const lineText = lines[i];
      const lineNum = i + 1;

      // 1. DML DataModels
      const dataModelMatch = lineText.match(/\bDataModel\s+([A-Za-z0-9_]+)/);
      if (dataModelMatch) {
        symbols.push({
          name: dataModelMatch[1],
          kind: 'DataModel',
          fileId: file.id,
          fileName: file.name,
          line: lineNum,
          column: lineText.indexOf(dataModelMatch[1]) + 1,
          detail: `DataModel ${dataModelMatch[1]} (in ${file.name})`,
          documentation: `DML domain entity defining data structures and composite relationships.`
        });
      }

      // 2. Capabilities
      const capMatch = lineText.match(/\bCapability\s+([A-Za-z0-9_]+)/);
      if (capMatch) {
        // Extract interface if present
        const ifMatch = lineText.match(/interface\s+([A-Za-z0-9_]+)/);
        const ifName = ifMatch ? ifMatch[1] : 'unbound';
        symbols.push({
          name: capMatch[1],
          kind: 'Capability',
          fileId: file.id,
          fileName: file.name,
          line: lineNum,
          column: lineText.indexOf(capMatch[1]) + 1,
          detail: `Capability ${capMatch[1]} -> Interface: ${ifName}`,
          documentation: `Device contract providing control capabilities and expected outcomes.`
        });
      }

      // 3. Operations
      const opMatch = lineText.match(/\bOperation\s+([A-Za-z0-9_]+)/);
      if (opMatch) {
        symbols.push({
          name: opMatch[1],
          kind: 'Operation',
          fileId: file.id,
          fileName: file.name,
          line: lineNum,
          column: lineText.indexOf(opMatch[1]) + 1,
          detail: `Operation ${opMatch[1]}() (in ${file.name})`,
          documentation: `Low-level executable device operation / script.`
        });
      }

      // 4. Activities
      const actMatch = lineText.match(/\bActivity\s+([A-Za-z0-9_]+)\s*\{/);
      if (actMatch) {
        symbols.push({
          name: actMatch[1],
          kind: 'Activity',
          fileId: file.id,
          fileName: file.name,
          line: lineNum,
          column: lineText.indexOf(actMatch[1]) + 1,
          detail: `Activity ${actMatch[1]} (in ${file.name})`,
          documentation: `Workflow execution step in the supervisory process.`
        });
      }

      // 5. InterfaceDescriptions in MNC-ML
      const ifDescMatch = lineText.match(/\bInterfaceDescription\s+([A-Za-z0-9_]+)/);
      if (ifDescMatch) {
        symbols.push({
          name: ifDescMatch[1],
          kind: 'InterfaceDescription',
          fileId: file.id,
          fileName: file.name,
          line: lineNum,
          column: lineText.indexOf(ifDescMatch[1]) + 1,
          detail: `InterfaceDescription ${ifDescMatch[1]} (in ${file.name})`,
          documentation: `Formal MNC-ML supervisory interface contract.`
        });
      }

      // 6. ControlNodes in MNC-ML
      const cnMatch = lineText.match(/\bControlNode\s+([A-Za-z0-9_]+)/);
      if (cnMatch) {
        symbols.push({
          name: cnMatch[1],
          kind: 'ControlNode',
          fileId: file.id,
          fileName: file.name,
          line: lineNum,
          column: lineText.indexOf(cnMatch[1]) + 1,
          detail: `ControlNode ${cnMatch[1]} (in ${file.name})`,
          documentation: `MNC-ML hierarchical supervisory control node.`
        });
      }

      // 7. OperatingStates in MNC-ML
      const stateMatch = lineText.match(/^\s*([A-Z][A-Za-z0-9_]*)\s*\[\s*\]/);
      if (stateMatch && file.name.endsWith('.mnc')) {
        symbols.push({
          name: stateMatch[1],
          kind: 'State',
          fileId: file.id,
          fileName: file.name,
          line: lineNum,
          column: lineText.indexOf(stateMatch[1]) + 1,
          detail: `OperatingState ${stateMatch[1]} (in ${file.name})`,
          documentation: `Supervisory lifecycle operating state.`
        });
      }

      // 8. Commands
      const cmdMatch = lineText.match(/\bCommand\s+([A-Za-z0-9_]+)/);
      if (cmdMatch) {
        symbols.push({
          name: cmdMatch[1],
          kind: 'Command',
          fileId: file.id,
          fileName: file.name,
          line: lineNum,
          column: lineText.indexOf(cmdMatch[1]) + 1,
          detail: `Command ${cmdMatch[1]} (in ${file.name})`,
          documentation: `Dispatched control command.`
        });
      }

      // 9. Events
      const evtMatch = lineText.match(/\bEvent\s+([A-Za-z0-9_]+)/);
      if (evtMatch) {
        symbols.push({
          name: evtMatch[1],
          kind: 'Event',
          fileId: file.id,
          fileName: file.name,
          line: lineNum,
          column: lineText.indexOf(evtMatch[1]) + 1,
          detail: `Event ${evtMatch[1]} (in ${file.name})`,
          documentation: `Asynchronous published notification or sensor event.`
        });
      }

      // 10. Alarms
      const almMatch = lineText.match(/\bAlarm\s+([A-Za-z0-9_]+)/);
      if (almMatch) {
        symbols.push({
          name: almMatch[1],
          kind: 'Alarm',
          fileId: file.id,
          fileName: file.name,
          line: lineNum,
          column: lineText.indexOf(almMatch[1]) + 1,
          detail: `Alarm ${almMatch[1]} (in ${file.name})`,
          documentation: `Supervisory alarm condition.`
        });
      }

      // 11. DataPoints
      const dpMatch = lineText.match(/\bDataPoint\s+([A-Za-z0-9_]+)/);
      if (dpMatch) {
        symbols.push({
          name: dpMatch[1],
          kind: 'DataPoint',
          fileId: file.id,
          fileName: file.name,
          line: lineNum,
          column: lineText.indexOf(dpMatch[1]) + 1,
          detail: `DataPoint ${dpMatch[1]} (in ${file.name})`,
          documentation: `Continuous telemetry value.`
        });
      }
    }
  }

  return symbols;
}

export {
  registerNavigationHandler,
  navigateToDefinition
} from '../kide-language/languageService';

/**
 * Xtext Definition Provider (Go to Definition / Ctrl+Click).
 */
export const xtextDefinitionProvider: monaco.languages.DefinitionProvider = {
  provideDefinition(model, position) {
    const wordInfo = model.getWordAtPosition(position);
    if (!wordInfo) return null;

    const word = wordInfo.word;
    const symbols = buildWorkspaceSymbolIndex();

    // Look for exact symbol match
    const matchingSymbols = symbols.filter(s => s.name === word);
    if (matchingSymbols.length === 0) return null;

    const target = matchingSymbols[0];
    const activeFile = useEditorStore.getState().files.find(f => f.id === useEditorStore.getState().activeFileId);

    // If target is in another file, trigger navigation
    if (activeFile && target.fileId !== activeFile.id) {
      setTimeout(() => {
        navigateToDefinition(target.fileId, target.line, target.column);
      }, 50);
    }

    // Return target location
    return {
      uri: model.uri,
      range: {
        startLineNumber: target.line,
        startColumn: target.column,
        endLineNumber: target.line,
        endColumn: target.column + target.name.length,
      }
    };
  }
};

/**
 * Xtext Link Provider (underline clickable references).
 */
export const xtextLinkProvider: monaco.languages.LinkProvider = {
  provideLinks(model) {
    const symbols = buildWorkspaceSymbolIndex();
    const links: monaco.languages.ILink[] = [];
    const lineCount = model.getLineCount();

    for (let i = 1; i <= lineCount; i++) {
      const lineText = model.getLineContent(i);

      for (const sym of symbols) {
        // Find whole-word occurrences of defined symbols
        const regex = new RegExp(`\\b${sym.name}\\b`, 'g');
        let match: RegExpExecArray | null;

        while ((match = regex.exec(lineText)) !== null) {
          const startCol = match.index + 1;
          const endCol = startCol + sym.name.length;

          // Don't link to self on definition line
          const currentFile = useEditorStore.getState().files.find(f => f.id === useEditorStore.getState().activeFileId);
          if (currentFile && currentFile.id === sym.fileId && i === sym.line) {
            continue;
          }

          links.push({
            range: {
              startLineNumber: i,
              startColumn: startCol,
              endLineNumber: i,
              endColumn: endCol,
            },
            tooltip: `Ctrl+Click to jump to ${sym.kind} "${sym.name}" (${sym.fileName}:${sym.line})`,
          });
        }
      }
    }

    return { links };
  },

  resolveLink(link) {
    return link;
  }
};

/**
 * Xtext Documentation and Reference Hover Provider.
 */
const XTEXT_KEYWORD_DOCS: Record<string, string> = {
  // Activity DSL
  ActivityDiagram: `### \`ActivityDiagram\`\n**Thesis Section 4.2: Supervisory Workflow Process**\nDefines an event-driven supervisory coordination process consisting of structured execution activities, context data models, and outcome-based transitions.`,
  Activity: `### \`Activity\`\n**Process Execution Node**\nRepresents a discrete step in the supervisory workflow. Can bind to a resource capability (\`requireCapability\`), invoke an operation (\`requireOperation\`), or embed a nested sub-process (\`childActivityDiagram\`).`,
  requireCapability: `### \`requireCapability\`\n**Equipment Capability Binding**\nBinds this activity to an equipment contract defined in a \`.cap\` specification. The activity invokes commands and monitors alarms/events exposed by the capability.`,
  requireOperation: `### \`requireOperation\`\n**Low-Level Operation Binding**\nDirectly executes an elementary device script or operation defined in an \`.op\` specification.`,
  nextActivity: `### \`nextActivity\`\n**Unconditional Transition**\nSpecifies the target activity to execute immediately following successful completion of the current activity.`,
  conditions: `### \`conditions\`\n**Branching Logic**\nDefines guard conditions and outcome evaluations (\`outcome => nextActivity\`) to dynamically steer execution.`,
  outcome: `### \`outcome\`\nEvaluates return parameters or events received from device capabilities to determine branching paths.`,
  interruptedBy: `### \`interruptedBy\`\nSpecifies activities that preempt or abort the current activity upon activation.`,
  produces: `### \`produces results\`\nSpecifies output parameters generated by the activity diagram upon reaching a terminal state.`,
  on: `### \`on context\`\nBinds the supervisory diagram to a DML domain data model for state and parameter typing.`,

  // Capability DSL
  Capability: `### \`Capability\`\n**Thesis Section 4.1: Resource Capability Contract**\nEncapsulates equipment behavior, defining compatible component interfaces, fireable commands, receivable events, and raised alarms.`,
  compatible: `### \`compatible component interface\`\nReferences the MNC-ML \`InterfaceDescription\` that this capability conforms to.`,
  providesControlCapabilities: `### \`providesControlCapabilities\`\nDeclares the fireable commands, receivable events, and raised alarms offered by this capability.`,
  providesOutcomes: `### \`providesOutcomes\`\nDeclares the response objects, events, and data points that this capability yields upon completion.`,

  // Operation DSL
  Operation: `### \`Operation\`\n**Elementary Device Script**\nDeclares input parameters, an executable shell/Python/C script, and optional return values.`,
  execute: `### \`execute\`\nContains the executable code or command dispatched to the underlying actuator or micro-controller.`,

  // DML DSL
  DataModel: `### \`DataModel\`\n**Thesis Section 3.3: Data Modeling Language (DML)**\nFoundation schema defining structured domain entities with primitive attributes and composite sub-models.`,
  primitives: `### \`primitives\`\nBlock containing primitive variables (\`int\`, \`float\`, \`boolean\`, \`string\`, \`object\`, \`date\`).`,
  composites: `### \`composites\`\nBlock referencing other \`DataModel\` entities to compose hierarchical structures.`,

  // MNC-ML
  InterfaceDescription: `### \`InterfaceDescription\`\n**Thesis Chapter 5: Formal Supervisory Interface**\nSpecifies the complete control contract: commands, events, responses, alarms, data points, and lifecycle operating states.`,
  ControlNode: `### \`ControlNode\`\n**Hierarchical Supervisory Controller**\nImplements an \`InterfaceDescription\` using CommandResponseBlocks, EventBlocks, and AlarmBlocks to manage child devices.`,
  operatingStates: `### \`operatingStates\`\nFinite state machine utility declaring allowed operational states, \`startStates\`, and \`endStates\`.`,
  CommandResponseBlock: `### \`CommandResponseBlock\`\nDefines actions, validations, and response aggregations triggered when receiving a specific supervisory command.`,
};

export const xtextHoverProvider: monaco.languages.HoverProvider = {
  provideHover(model, position) {
    const wordInfo = model.getWordAtPosition(position);
    if (!wordInfo) return null;

    const word = wordInfo.word;

    // 1. Check keyword documentation
    if (XTEXT_KEYWORD_DOCS[word]) {
      return {
        range: {
          startLineNumber: position.lineNumber,
          startColumn: wordInfo.startColumn,
          endLineNumber: position.lineNumber,
          endColumn: wordInfo.endColumn,
        },
        contents: [{ value: XTEXT_KEYWORD_DOCS[word] }]
      };
    }

    // 2. Check workspace symbols
    const symbols = buildWorkspaceSymbolIndex();
    const sym = symbols.find(s => s.name === word);
    if (sym) {
      const contents = [
        { value: `**${sym.kind}**: \`${sym.name}\` *(defined in ${sym.fileName}:${sym.line})*` },
        { value: `${sym.detail}` },
      ];
      if (sym.documentation) {
        contents.push({ value: sym.documentation });
      }
      contents.push({ value: `*Ctrl+Click to jump to definition*` });

      return {
        range: {
          startLineNumber: position.lineNumber,
          startColumn: wordInfo.startColumn,
          endLineNumber: position.lineNumber,
          endColumn: wordInfo.endColumn,
        },
        contents
      };
    }

    return null;
  }
};

/**
 * Contextual Completion Item Provider.
 */
export const xtextCompletionProvider: monaco.languages.CompletionItemProvider = {
  provideCompletionItems(model, position) {
    const wordInfo = model.getWordUntilPosition(position);
    const lineText = model.getLineContent(position.lineNumber);
    const prefix = lineText.substring(0, position.column - 1);

    const range: monaco.IRange = {
      startLineNumber: position.lineNumber,
      endLineNumber: position.lineNumber,
      startColumn: wordInfo.startColumn,
      endColumn: wordInfo.endColumn,
    };

    const suggestions: monaco.languages.CompletionItem[] = [];
    const symbols = buildWorkspaceSymbolIndex();

    // 1. Context: requireCapability
    if (/requireCapability\s*:\s*"?$/.test(prefix)) {
      const caps = symbols.filter(s => s.kind === 'Capability');
      for (const cap of caps) {
        suggestions.push({
          label: `"${cap.name}"`,
          kind: monaco.languages.CompletionItemKind.Class,
          insertText: `"${cap.name}"`,
          documentation: `Bind to Capability contract ${cap.name} (${cap.fileName})`,
          range,
        });
      }
    }

    // 2. Context: requireOperation
    if (/requireOperation\s*\(\s*$/.test(prefix)) {
      const ops = symbols.filter(s => s.kind === 'Operation');
      for (const op of ops) {
        suggestions.push({
          label: op.name,
          kind: monaco.languages.CompletionItemKind.Function,
          insertText: `${op.name} )`,
          documentation: `Execute Operation ${op.name} (${op.fileName})`,
          range,
        });
      }
    }

    // 3. Context: on context
    if (/on\s+context\s+$/.test(prefix)) {
      const dmls = symbols.filter(s => s.kind === 'DataModel');
      for (const dml of dmls) {
        suggestions.push({
          label: dml.name,
          kind: monaco.languages.CompletionItemKind.Interface,
          insertText: dml.name,
          documentation: `Reference DataModel ${dml.name} (${dml.fileName})`,
          range,
        });
      }
    }

    // 4. Context: nextActivity
    if (/nextActivity\s*:\s*$/.test(prefix)) {
      const acts = symbols.filter(s => s.kind === 'Activity');
      for (const act of acts) {
        suggestions.push({
          label: act.name,
          kind: monaco.languages.CompletionItemKind.Reference,
          insertText: act.name,
          documentation: `Transition to Activity ${act.name}`,
          range,
        });
      }
    }

    // 5. Context: compatible component interface
    if (/compatible\s+component\s+interface\s+$/.test(prefix)) {
      const ifaces = symbols.filter(s => s.kind === 'InterfaceDescription');
      for (const iface of ifaces) {
        suggestions.push({
          label: iface.name,
          kind: monaco.languages.CompletionItemKind.Interface,
          insertText: iface.name,
          documentation: `Conform to InterfaceDescription ${iface.name}`,
          range,
        });
      }
    }

    // General symbol completions
    for (const sym of symbols) {
      suggestions.push({
        label: sym.name,
        kind: sym.kind === 'Operation' ? monaco.languages.CompletionItemKind.Function
          : sym.kind === 'Capability' ? monaco.languages.CompletionItemKind.Class
          : sym.kind === 'Activity' ? monaco.languages.CompletionItemKind.Unit
          : sym.kind === 'DataModel' ? monaco.languages.CompletionItemKind.Struct
          : monaco.languages.CompletionItemKind.Variable,
        insertText: sym.name,
        detail: `[${sym.kind}] ${sym.fileName}`,
        documentation: sym.detail,
        range,
      });
    }

    return { suggestions };
  }
};

/**
 * Xtext Document Formatter.
 */
export const xtextDocumentFormattingProvider: monaco.languages.DocumentFormattingEditProvider = {
  provideDocumentFormattingEdits(model) {
    const text = model.getValue();
    const lines = text.split('\n');
    let indentLevel = 0;
    const formattedLines: string[] = [];

    for (let i = 0; i < lines.length; i++) {
      let line = lines[i].trim();
      if (!line) {
        formattedLines.push('');
        continue;
      }

      if (line.startsWith('}') || line.startsWith(']') || line.startsWith(')')) {
        indentLevel = Math.max(0, indentLevel - 1);
      }

      const indent = '  '.repeat(indentLevel);
      formattedLines.push(indent + line);

      if (line.endsWith('{') || line.endsWith('[') || line.endsWith('(')) {
        indentLevel++;
      }
    }

    return [
      {
        range: model.getFullModelRange(),
        text: formattedLines.join('\n'),
      }
    ];
  }
};

/**
 * Real-time Syntax & Cross-File Semantic Validator.
 */
export async function validateActiveModel(
  fileId: string,
  content: string,
  language: string,
  monacoInstance: typeof monaco,
  model: monaco.editor.ITextModel
): Promise<monaco.editor.IMarkerData[]> {
  const markers: monaco.editor.IMarkerData[] = [];
  const symbols = buildWorkspaceSymbolIndex();

  // 1. Syntax Parsing via Backend
  try {
    const parseRes = await parseText(language, content);
    if (parseRes.errors && parseRes.errors.length > 0) {
      for (const err of parseRes.errors) {
        markers.push({
          severity: err.severity === 'warning' ? monacoInstance.MarkerSeverity.Warning : monacoInstance.MarkerSeverity.Error,
          message: err.message,
          startLineNumber: err.line || 1,
          startColumn: err.column || 1,
          endLineNumber: err.line || 1,
          endColumn: (err.column || 1) + 15,
          source: 'Xtext Syntax Validator',
        });
      }
    }
  } catch (err: any) {
    markers.push({
      severity: monacoInstance.MarkerSeverity.Error,
      message: `Syntax Error: ${err.message || String(err)}`,
      startLineNumber: 1,
      startColumn: 1,
      endLineNumber: 1,
      endColumn: 10,
      source: 'Parser',
    });
  }

  // 2. Cross-File Semantic Validation
  const lines = content.split('\n');

  if (language === 'activitydsl' || language === 'activity') {
    const knownDataModels = new Set(symbols.filter(s => s.kind === 'DataModel').map(s => s.name));
    const knownCaps = new Set(symbols.filter(s => s.kind === 'Capability').map(s => s.name));
    const knownOps = new Set(symbols.filter(s => s.kind === 'Operation').map(s => s.name));
    const declaredActivities = new Set<string>();

    // First pass: collect activity names in this diagram
    for (let i = 0; i < lines.length; i++) {
      const match = lines[i].match(/\bActivity\s+([A-Za-z0-9_]+)\s*\{/);
      if (match) {
        if (declaredActivities.has(match[1])) {
          markers.push({
            severity: monacoInstance.MarkerSeverity.Error,
            message: `Duplicate Activity name: "${match[1]}" already defined in this diagram.`,
            startLineNumber: i + 1,
            startColumn: lines[i].indexOf(match[1]) + 1,
            endLineNumber: i + 1,
            endColumn: lines[i].indexOf(match[1]) + 1 + match[1].length,
            source: 'Semantic Validator',
          });
        }
        declaredActivities.add(match[1]);
      }
    }

    // Second pass: validate references
    for (let i = 0; i < lines.length; i++) {
      const line = lines[i];

      // Context DataModel check
      const ctxMatch = line.match(/\bon\s+context\s+([A-Za-z0-9_]+)/);
      if (ctxMatch) {
        const dmlName = ctxMatch[1];
        if (knownDataModels.size > 0 && !knownDataModels.has(dmlName)) {
          markers.push({
            severity: monacoInstance.MarkerSeverity.Warning,
            message: `Unresolved context reference: DataModel "${dmlName}" not found in workspace (.dml files).`,
            startLineNumber: i + 1,
            startColumn: line.indexOf(dmlName) + 1,
            endLineNumber: i + 1,
            endColumn: line.indexOf(dmlName) + 1 + dmlName.length,
            source: 'Semantic Validator',
          });
        }
      }

      // Capability reference check
      const capMatch = line.match(/\brequireCapability\s*:\s*"?([A-Za-z0-9_]+)"?/);
      if (capMatch) {
        const capName = capMatch[1];
        if (knownCaps.size > 0 && !knownCaps.has(capName)) {
          markers.push({
            severity: monacoInstance.MarkerSeverity.Warning,
            message: `Unresolved capability reference: Capability "${capName}" not found in workspace (.cap files).`,
            startLineNumber: i + 1,
            startColumn: line.indexOf(capName) + 1,
            endLineNumber: i + 1,
            endColumn: line.indexOf(capName) + 1 + capName.length,
            source: 'Semantic Validator',
          });
        }
      }

      // Operation reference check
      const opMatch = line.match(/\brequireOperation\s*\(\s*([A-Za-z0-9_]+)\s*\)/);
      if (opMatch) {
        const opName = opMatch[1];
        if (knownOps.size > 0 && !knownOps.has(opName)) {
          markers.push({
            severity: monacoInstance.MarkerSeverity.Warning,
            message: `Unresolved operation reference: Operation "${opName}" not found in workspace (.op files).`,
            startLineNumber: i + 1,
            startColumn: line.indexOf(opName) + 1,
            endLineNumber: i + 1,
            endColumn: line.indexOf(opName) + 1 + opName.length,
            source: 'Semantic Validator',
          });
        }
      }

      // Next Activity target check
      const nextMatch = line.match(/\bnextActivity\s*:\s*([A-Za-z0-9_]+)/);
      if (nextMatch) {
        const nextAct = nextMatch[1];
        if (!declaredActivities.has(nextAct)) {
          markers.push({
            severity: monacoInstance.MarkerSeverity.Error,
            message: `Unresolved transition target: Activity "${nextAct}" is not defined in this diagram.`,
            startLineNumber: i + 1,
            startColumn: line.indexOf(nextAct) + 1,
            endLineNumber: i + 1,
            endColumn: line.indexOf(nextAct) + 1 + nextAct.length,
            source: 'Semantic Validator',
          });
        }
      }
    }
  }

  // Set markers in Monaco
  monacoInstance.editor.setModelMarkers(model, 'xtext-ide', markers);

  // Sync with Zustand store
  const storeMarkers = markers.map(m => ({
    line: m.startLineNumber,
    column: m.startColumn,
    message: m.message,
    severity: m.severity === monacoInstance.MarkerSeverity.Error ? 'error' as const : 'warning' as const,
  }));
  useEditorStore.getState().setValidationErrors(fileId, storeMarkers);

  return markers;
}
