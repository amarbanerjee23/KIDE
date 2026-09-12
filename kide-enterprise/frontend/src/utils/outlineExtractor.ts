import { OutlineItem } from '../types/navigator';

/**
 * Extracts structured symbols from a file's content for Outline mode.
 */
export function extractOutlineSymbols(content: string, filename: string): OutlineItem[] {
  if (!content || !content.trim()) return [];

  const lines = content.split('\n');
  const items: OutlineItem[] = [];

  const lowerName = filename.toLowerCase();

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

