import { OutlineItem } from '../types/navigator';
import { parseAst } from '../lib/kide-language/languages';
import {
  DmlPackageNode,
  CapabilityNode,
  OperationNode,
  ActivityDiagramNode,
  MncModelNode
} from '../lib/kide-language/ast';

/**
 * Extracts structured symbols from a file's content for Outline mode.
 * Uses native resilient AST parsers with exact source locations.
 */
export function extractOutlineSymbols(content: string, filename: string): OutlineItem[] {
  if (!content || !content.trim()) return [];

  const lowerName = filename.toLowerCase();

  // Try native AST parsing first
  try {
    const ast = parseAst(filename, content);
    if (ast) {
      if (ast.type === 'DataPackage' || ast.type === 'DmlPackage') {
        const pkg = ast as DmlPackageNode;
        const items: OutlineItem[] = [];
        if (pkg.name) {
          items.push({
            id: `pkg-${pkg.range.startLine}`,
            name: pkg.name,
            kind: 'Package',
            line: pkg.range.startLine,
            column: pkg.range.startColumn,
            detail: 'DML Package'
          });
        }
        const models = pkg.models || pkg.dataModels || [];
        for (const dm of models) {
          const dmName = dm.name || 'DataModel';
          const dmItem: OutlineItem = {
            id: `dm-${dm.range.startLine}-${dmName}`,
            name: dmName,
            kind: 'DataModel',
            line: dm.range.startLine,
            column: dm.range.startColumn,
            detail: 'DataModel',
            children: []
          };
          for (const p of dm.primitives || []) {
            const pName = p.name || 'field';
            dmItem.children?.push({
              id: `field-${p.range.startLine}-${pName}`,
              name: pName,
              kind: 'Field',
              line: p.range.startLine,
              column: p.range.startColumn,
              detail: `${p.paramType}${p.value ? ` = ${p.value}` : ''}`
            });
          }
          for (const c of dm.composites || []) {
            dmItem.children?.push({
              id: `comp-${c.range.startLine}-${c.name}`,
              name: c.name,
              kind: 'Field',
              line: c.range.startLine,
              column: c.range.startColumn,
              detail: 'composite reference'
            });
          }
          items.push(dmItem);
        }
        if (items.length > 0) return items;
      } else if (ast.type === 'Capability') {
        const cap = ast as CapabilityNode;
        const capName = cap.name || 'Capability';
        const ifaces = cap.componentInterfaces?.map(i => i.name).join(', ') || 'unbound';
        const capItem: OutlineItem = {
          id: `cap-${cap.range.startLine}-${capName}`,
          name: capName,
          kind: 'Capability',
          line: cap.range.startLine,
          column: cap.range.startColumn,
          detail: `Interface: ${ifaces}`,
          children: []
        };
        for (const cmd of cap.controlCapabilities?.commands || []) {
          capItem.children?.push({
            id: `cmd-${cmd.range.startLine}-${cmd.name}`,
            name: cmd.name,
            kind: 'Command',
            line: cmd.range.startLine,
            column: cmd.range.startColumn,
            detail: 'fireable command'
          });
        }
        for (const evt of cap.controlCapabilities?.events || []) {
          capItem.children?.push({
            id: `evt-${evt.range.startLine}-${evt.name}`,
            name: evt.name,
            kind: 'Event',
            line: evt.range.startLine,
            column: evt.range.startColumn,
            detail: 'receivable event'
          });
        }
        for (const alm of cap.controlCapabilities?.alarms || []) {
          capItem.children?.push({
            id: `alm-${alm.range.startLine}-${alm.name}`,
            name: alm.name,
            kind: 'Alarm',
            line: alm.range.startLine,
            column: alm.range.startColumn,
            detail: 'raised alarm'
          });
        }
        return [capItem];
      } else if (ast.type === 'Operation') {
        const op = ast as OperationNode;
        const opName = op.name || 'Operation';
        const opItem: OutlineItem = {
          id: `op-${op.range.startLine}-${opName}`,
          name: opName,
          kind: 'Operation',
          line: op.range.startLine,
          column: op.range.startColumn,
          detail: `Operation(${op.inputParameters?.map(p => `${p.paramType} ${p.name || ''}`).join(', ') || ''})`,
          children: []
        };
        if (op.executableScript) {
          opItem.children?.push({
            id: `script-${op.range.startLine}`,
            name: op.executableScript,
            kind: 'Script',
            line: op.range.startLine,
            detail: 'driver script'
          });
        }
        const ret = op.outputParameters || op.outputParameter;
        if (ret) {
          const retName = ret.name || 'result';
          opItem.children?.push({
            id: `ret-${ret.range.startLine}`,
            name: retName,
            kind: 'Field',
            line: ret.range.startLine,
            column: ret.range.startColumn,
            detail: `return ${ret.paramType}`
          });
        }
        return [opItem];
      } else if (ast.type === 'ActivityDiagram') {
        const diag = ast as ActivityDiagramNode;
        const diagName = diag.name || 'ActivityDiagram';
        const contextName = diag.contextDataModel?.name || diag.contextModel?.name || 'none';
        const diagItem: OutlineItem = {
          id: `diag-${diag.range.startLine}-${diagName}`,
          name: diagName,
          kind: 'Activity',
          line: diag.range.startLine,
          column: diag.range.startColumn,
          detail: `Workflow Diagram (context: ${contextName})`,
          children: []
        };
        for (const act of diag.activities || []) {
          const actName = act.name || 'Activity';
          const actItem: OutlineItem = {
            id: `act-${act.range.startLine}-${actName}`,
            name: actName,
            kind: 'Activity',
            line: act.range.startLine,
            column: act.range.startColumn,
            detail: act.description || 'Step',
            children: []
          };
          const reqCap = act.requiredCapability || act.requireCapability;
          if (reqCap) {
            actItem.children?.push({
              id: `req-cap-${reqCap.range.startLine}`,
              name: reqCap.name,
              kind: 'Capability',
              line: reqCap.range.startLine,
              column: reqCap.range.startColumn,
              detail: 'required capability'
            });
          }
          const reqOps = act.requiredOperations || (act.requireOperation ? [act.requireOperation] : []);
          for (const op of reqOps) {
            actItem.children?.push({
              id: `req-op-${op.range.startLine}`,
              name: op.name,
              kind: 'Operation',
              line: op.range.startLine,
              column: op.range.startColumn,
              detail: 'required operation'
            });
          }
          if (act.nextActivity) {
            actItem.children?.push({
              id: `trans-${act.nextActivity.range.startLine}`,
              name: `→ ${act.nextActivity.name}`,
              kind: 'Transition',
              line: act.nextActivity.range.startLine,
              column: act.nextActivity.range.startColumn,
              detail: 'transition'
            });
          }
          diagItem.children?.push(actItem);
        }
        return [diagItem];
      } else if (ast.type === 'Model') {
        const mnc = ast as MncModelNode;
        const items: OutlineItem[] = [];
        if (mnc.interfaceDescription) {
          const iface = mnc.interfaceDescription;
          const ifName = iface.name || 'InterfaceDescription';
          const ifItem: OutlineItem = {
            id: `if-${iface.range.startLine}-${ifName}`,
            name: ifName,
            kind: 'Interface',
            line: iface.range.startLine,
            column: iface.range.startColumn,
            detail: 'Interface Description',
            children: []
          };
          for (const cmd of iface.commands || []) {
            const cName = cmd.name || 'Command';
            ifItem.children?.push({
              id: `cmd-${cmd.range.startLine}-${cName}`,
              name: cName,
              kind: 'Command',
              line: cmd.range.startLine,
              column: cmd.range.startColumn,
              detail: 'Command'
            });
          }
          for (const st of iface.operatingStates || []) {
            const sName = st.name || 'State';
            ifItem.children?.push({
              id: `st-${st.range.startLine}-${sName}`,
              name: sName,
              kind: 'State',
              line: st.range.startLine,
              column: st.range.startColumn,
              detail: 'Operating State'
            });
          }
          items.push(ifItem);
        }
        if (mnc.controlNode) {
          const cn = mnc.controlNode;
          const cnName = cn.name || 'ControlNode';
          const ifaceRef = cn.implementedInterface?.name || cn.implementsInterface?.name || 'none';
          const cnItem: OutlineItem = {
            id: `cn-${cn.range.startLine}-${cnName}`,
            name: cnName,
            kind: 'ControlNode',
            line: cn.range.startLine,
            column: cn.range.startColumn,
            detail: `Control Node (implements ${ifaceRef})`,
            children: []
          };
          for (const tr of cn.transitions || []) {
            const fromState = tr.currentStates?.join(', ') || tr.currentState || 'any';
            cnItem.children?.push({
              id: `tr-${tr.range.startLine}`,
              name: `${fromState} => ${tr.nextState}`,
              kind: 'Transition',
              line: tr.range.startLine,
              column: tr.range.startColumn,
              detail: 'State Transition'
            });
          }
          items.push(cnItem);
        }
        if (items.length > 0) return items;
      }
    }
  } catch {
    // Fall back to line-by-line scanning below if AST fails
  }

  const lines = content.split('\n');
  const items: OutlineItem[] = [];

  // 1. DML
  if (lowerName.endsWith('.dml')) {
    let currentModel: OutlineItem | null = null;

    for (let i = 0; i < lines.length; i++) {
      const lineText = lines[i];
      const lineNum = i + 1;

      // Package
      const pkgMatch = lineText.match(/\bPackage\s+([A-Za-z0-9_]+)/);
      if (pkgMatch) {
        items.push({
          id: `pkg-${lineNum}`,
          name: pkgMatch[1],
          kind: 'Package',
          line: lineNum,
          detail: 'DML Package'
        });
      }

      // DataModel
      const dmMatch = lineText.match(/\bDataModel\s+([A-Za-z0-9_]+)/);
      if (dmMatch) {
        currentModel = {
          id: `dm-${lineNum}`,
          name: dmMatch[1],
          kind: 'DataModel',
          line: lineNum,
          detail: 'Domain Struct',
          children: []
        };
        items.push(currentModel);
        continue;
      }

      // Primitives / Fields inside DataModel
      const fieldMatch = lineText.match(/(?:int|float|boolean|string|date|object)\s+([A-Za-z0-9_]+)(?:\s*=\s*([^,;{}]+))?/);
      if (fieldMatch && currentModel) {
        currentModel.children?.push({
          id: `field-${lineNum}`,
          name: fieldMatch[1],
          kind: 'Field',
          line: lineNum,
          detail: fieldMatch[2] ? `= ${fieldMatch[2].trim()}` : 'parameter'
        });
      }
    }
    return items;
  }

  // 2. Capability
  if (lowerName.endsWith('.cap') || lowerName.endsWith('.capability')) {
    let currentCap: OutlineItem | null = null;

    for (let i = 0; i < lines.length; i++) {
      const lineText = lines[i];
      const lineNum = i + 1;

      const capMatch = lineText.match(/\bCapability\s+([A-Za-z0-9_]+)/);
      if (capMatch) {
        const ifMatch = lineText.match(/interface\s+([A-Za-z0-9_]+)/);
        currentCap = {
          id: `cap-${lineNum}`,
          name: capMatch[1],
          kind: 'Capability',
          line: lineNum,
          detail: ifMatch ? `Interface: ${ifMatch[1]}` : undefined,
          children: []
        };
        items.push(currentCap);
        continue;
      }

      // Commands inside capability
      const cmdMatch = lineText.match(/fireable\s+commands\s*:\s*([A-Za-z0-9_,\s]+)/);
      if (cmdMatch && currentCap) {
        const cmds = cmdMatch[1].split(',').map(c => c.trim()).filter(Boolean);
        for (const c of cmds) {
          currentCap.children?.push({
            id: `cmd-${lineNum}-${c}`,
            name: c,
            kind: 'Command',
            line: lineNum,
            detail: 'fireable command'
          });
        }
      }

      // Events inside capability
      const evtMatch = lineText.match(/receivable\s+events\s*:\s*([A-Za-z0-9_,\s]+)/);
      if (evtMatch && currentCap) {
        const evts = evtMatch[1].split(',').map(e => e.trim()).filter(Boolean);
        for (const e of evts) {
          currentCap.children?.push({
            id: `evt-${lineNum}-${e}`,
            name: e,
            kind: 'Event',
            line: lineNum,
            detail: 'receivable event'
          });
        }
      }

      // Alarms
      const almMatch = lineText.match(/raised\s+alarms\s*:\s*([A-Za-z0-9_,\s]+)/);
      if (almMatch && currentCap) {
        const alms = almMatch[1].split(',').map(a => a.trim()).filter(Boolean);
        for (const a of alms) {
          currentCap.children?.push({
            id: `alm-${lineNum}-${a}`,
            name: a,
            kind: 'Alarm',
            line: lineNum,
            detail: 'raised alarm'
          });
        }
      }
    }
    return items;
  }

  // 3. Operation
  if (lowerName.endsWith('.op') || lowerName.endsWith('.operation')) {
    for (let i = 0; i < lines.length; i++) {
      const lineText = lines[i];
      const lineNum = i + 1;

      const opMatch = lineText.match(/\bOperation\s+([A-Za-z0-9_]+)\s*\(([^)]*)\)/);
      if (opMatch) {
        const children: OutlineItem[] = [];

        // Check next few lines for execute script or return
        for (let j = i + 1; j < Math.min(i + 8, lines.length); j++) {
          const execMatch = lines[j].match(/execute\s+"([^"]+)"/);
          if (execMatch) {
            children.push({
              id: `script-${j + 1}`,
              name: execMatch[1],
              kind: 'Script',
              line: j + 1,
              detail: 'executable driver script'
            });
          }
          const retMatch = lines[j].match(/return\s+([A-Za-z0-9_]+(?:\s+[A-Za-z0-9_]+)?)/);
          if (retMatch) {
            children.push({
              id: `ret-${j + 1}`,
              name: retMatch[1],
              kind: 'Field',
              line: j + 1,
              detail: 'return parameter'
            });
          }
          if (lines[j].includes('}')) break;
        }

        items.push({
          id: `op-${lineNum}`,
          name: opMatch[1],
          kind: 'Operation',
          line: lineNum,
          detail: opMatch[2] ? `(${opMatch[2]})` : '()',
          children
        });
      }
    }
    return items;
  }

  // 4. Supervisory Workflow (.activity)
  if (lowerName.endsWith('.activity')) {
    let currentDiagram: OutlineItem | null = null;
    let currentActivity: OutlineItem | null = null;

    for (let i = 0; i < lines.length; i++) {
      const lineText = lines[i];
      const lineNum = i + 1;

      const diagMatch = lineText.match(/\bActivityDiagram\s+([A-Za-z0-9_]+)/);
      if (diagMatch) {
        currentDiagram = {
          id: `diag-${lineNum}`,
          name: diagMatch[1],
          kind: 'Activity',
          line: lineNum,
          detail: 'Workflow Diagram',
          children: []
        };
        items.push(currentDiagram);
        continue;
      }

      const actMatch = lineText.match(/\bActivity\s+([A-Za-z0-9_]+)\s*\{/);
      if (actMatch) {
        currentActivity = {
          id: `act-${lineNum}`,
          name: actMatch[1],
          kind: 'Activity',
          line: lineNum,
          detail: 'Step',
          children: []
        };
        if (currentDiagram) {
          currentDiagram.children?.push(currentActivity);
        } else {
          items.push(currentActivity);
        }
        continue;
      }

      // Inside activity: requireCapability or requireOperation
      if (currentActivity) {
        const reqCapMatch = lineText.match(/requireCapability\s*:\s*"?([A-Za-z0-9_]+)"?/);
        if (reqCapMatch) {
          currentActivity.children?.push({
            id: `req-cap-${lineNum}`,
            name: reqCapMatch[1],
            kind: 'Capability',
            line: lineNum,
            detail: 'required capability'
          });
        }
        const reqOpMatch = lineText.match(/requireOperation\s*\(\s*([A-Za-z0-9_]+)\s*\)/);
        if (reqOpMatch) {
          currentActivity.children?.push({
            id: `req-op-${lineNum}`,
            name: reqOpMatch[1],
            kind: 'Operation',
            line: lineNum,
            detail: 'required operation'
          });
        }
        const nextActMatch = lineText.match(/nextActivity\s*:\s*([A-Za-z0-9_]+)/);
        if (nextActMatch) {
          currentActivity.children?.push({
            id: `trans-${lineNum}`,
            name: `→ ${nextActMatch[1]}`,
            kind: 'Transition',
            line: lineNum,
            detail: 'transition'
          });
        }
      }
    }
    return items;
  }

  // 5. MNC-ML (.mnc)
  if (lowerName.endsWith('.mnc')) {
    let currentBlock: OutlineItem | null = null;

    for (let i = 0; i < lines.length; i++) {
      const lineText = lines[i];
      const lineNum = i + 1;

      const ifMatch = lineText.match(/\bInterfaceDescription\s+([A-Za-z0-9_]+)/);
      if (ifMatch) {
        currentBlock = {
          id: `if-${lineNum}`,
          name: ifMatch[1],
          kind: 'Interface',
          line: lineNum,
          detail: 'Interface Description',
          children: []
        };
        items.push(currentBlock);
        continue;
      }

      const cnMatch = lineText.match(/\bControlNode\s+([A-Za-z0-9_]+)/);
      if (cnMatch) {
        currentBlock = {
          id: `cn-${lineNum}`,
          name: cnMatch[1],
          kind: 'ControlNode',
          line: lineNum,
          detail: 'Control Node',
          children: []
        };
        items.push(currentBlock);
        continue;
      }

      // States
      const stateMatch = lineText.match(/^\s*([A-Z][A-Za-z0-9_]*)\s*\[\s*\]/);
      if (stateMatch && currentBlock) {
        currentBlock.children?.push({
          id: `st-${lineNum}`,
          name: stateMatch[1],
          kind: 'State',
          line: lineNum,
          detail: 'Operating State'
        });
      }

      // Commands
      const cmdMatch = lineText.match(/(?:async\s+)?([A-Za-z0-9_]+)\s*\[[^\]]*\]/);
      if (cmdMatch && currentBlock && lineText.includes('commands')) {
        currentBlock.children?.push({
          id: `cmd-${lineNum}`,
          name: cmdMatch[1],
          kind: 'Command',
          line: lineNum,
          detail: 'Command'
        });
      }
    }
    return items;
  }

  // 6. JSON workflow
  if (lowerName.endsWith('.json')) {
    try {
      const parsed = JSON.parse(content);
      if (parsed.name) {
        const root: OutlineItem = {
          id: 'json-root',
          name: parsed.name,
          kind: 'Activity',
          line: 1,
          detail: 'Workflow Model',
          children: []
        };

        if (Array.isArray(parsed.default_operating_states)) {
          for (const s of parsed.default_operating_states) {
            root.children?.push({
              id: `json-st-${s}`,
              name: s,
              kind: 'State',
              line: 1,
              detail: 'Operating State'
            });
          }
        }

        if (Array.isArray(parsed.activities)) {
          for (const a of parsed.activities) {
            root.children?.push({
              id: `json-act-${a.name}`,
              name: a.name,
              kind: 'Activity',
              line: 1,
              detail: a.requires_operation ? 'Requires Operation' : 'Autonomous'
            });
          }
        }

        items.push(root);
      }
    } catch {
      // ignore invalid json
    }
    return items;
  }

  return items;
}
