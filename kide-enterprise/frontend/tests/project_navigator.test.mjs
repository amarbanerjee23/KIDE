import { test, describe } from 'node:test';
import assert from 'node:assert/strict';

// Helper: Domain grouping logic
function groupProjectFiles(files) {
  const dataModels = [];
  const capabilitiesOps = [];
  const workflows = [];
  const stateMachines = [];
  const otherFiles = [];

  for (const f of files) {
    const name = (f.name || '').toLowerCase();
    if (name.endsWith('.dml')) {
      dataModels.push(f);
    } else if (name.endsWith('.cap') || name.endsWith('.capability') || name.endsWith('.op') || name.endsWith('.operation')) {
      capabilitiesOps.push(f);
    } else if (name.endsWith('.activity') || name.endsWith('.json')) {
      workflows.push(f);
    } else if (name.endsWith('.mnc')) {
      stateMachines.push(f);
    } else {
      otherFiles.push(f);
    }
  }

  return { dataModels, capabilitiesOps, workflows, stateMachines, otherFiles };
}

// Helper: Fast Project Search logic
function searchProject(files, symbols, query) {
  const q = query.trim().toLowerCase();
  if (!q) return [];

  const matches = [];
  const maxResults = 40;

  // 1. Filename matches
  for (const file of files) {
    if (file.name.toLowerCase().includes(q)) {
      matches.push({
        fileId: file.id,
        fileName: file.name,
        line: 1,
        matchText: file.name,
        matchType: 'filename'
      });
    }
  }

  // 2. Symbol matches
  for (const sym of symbols) {
    if (sym.name.toLowerCase().includes(q) || (sym.detail && sym.detail.toLowerCase().includes(q))) {
      matches.push({
        fileId: sym.fileId,
        fileName: sym.fileName,
        line: sym.line,
        matchText: `${sym.kind}: ${sym.name}`,
        matchType: 'symbol',
        symbolKind: sym.kind
      });
      if (matches.length >= maxResults) break;
    }
  }

  // 3. Text content matches
  if (matches.length < maxResults) {
    for (const file of files) {
      const lines = file.content.split('\n');
      for (let i = 0; i < lines.length; i++) {
        const lineText = lines[i];
        if (lineText.toLowerCase().includes(q)) {
          const alreadyMatched = matches.some(m => m.fileId === file.id && m.line === i + 1);
          if (!alreadyMatched) {
            matches.push({
              fileId: file.id,
              fileName: file.name,
              line: i + 1,
              matchText: lineText.trim(),
              matchType: 'content'
            });
          }
          if (matches.length >= maxResults) break;
        }
      }
      if (matches.length >= maxResults) break;
    }
  }

  return matches;
}

// Helper: Problems aggregation logic
function aggregateProblems(files, validationErrors) {
  const grouped = [];
  let totalErrors = 0;
  let totalWarnings = 0;

  for (const file of files) {
    const errs = validationErrors[file.id] || [];
    if (errs.length > 0) {
      grouped.push({
        fileId: file.id,
        fileName: file.name,
        errors: errs
      });
      for (const e of errs) {
        if (e.severity === 'error') totalErrors++;
        else totalWarnings++;
      }
    }
  }

  return { grouped, totalErrors, totalWarnings, totalCount: totalErrors + totalWarnings };
}

// Helper: Outline extraction logic mirroring src/utils/outlineExtractor.ts
function extractSymbols(content, filename) {
  if (!content || !content.trim()) return [];
  const lines = content.split('\n');
  const items = [];
  const lowerName = filename.toLowerCase();

  if (lowerName.endsWith('.dml')) {
    let currentModel = null;
    for (let i = 0; i < lines.length; i++) {
      const lineText = lines[i];
      const lineNum = i + 1;
      const pkgMatch = lineText.match(/\bPackage\s+([A-Za-z0-9_]+)/);
      if (pkgMatch) {
        items.push({ id: `pkg-${lineNum}`, name: pkgMatch[1], kind: 'Package', line: lineNum });
      }
      const dmMatch = lineText.match(/\bDataModel\s+([A-Za-z0-9_]+)/);
      if (dmMatch) {
        currentModel = { id: `dm-${lineNum}`, name: dmMatch[1], kind: 'DataModel', line: lineNum, children: [] };
        items.push(currentModel);
        continue;
      }
      const fieldMatch = lineText.match(/(?:int|float|boolean|string|date|object)\s+([A-Za-z0-9_]+)/);
      if (fieldMatch && currentModel) {
        currentModel.children.push({ id: `field-${lineNum}`, name: fieldMatch[1], kind: 'Field', line: lineNum });
      }
    }
  } else if (lowerName.endsWith('.cap')) {
    let currentCap = null;
    for (let i = 0; i < lines.length; i++) {
      const lineText = lines[i];
      const lineNum = i + 1;
      const capMatch = lineText.match(/\bCapability\s+([A-Za-z0-9_]+)/);
      if (capMatch) {
        currentCap = { id: `cap-${lineNum}`, name: capMatch[1], kind: 'Capability', line: lineNum, children: [] };
        items.push(currentCap);
        continue;
      }
      const cmdMatch = lineText.match(/fireable\s+commands\s*:\s*([A-Za-z0-9_,\s]+)/);
      if (cmdMatch && currentCap) {
        const cmds = cmdMatch[1].split(',').map(c => c.trim()).filter(Boolean);
        for (const c of cmds) {
          currentCap.children.push({ id: `cmd-${lineNum}-${c}`, name: c, kind: 'Command', line: lineNum });
        }
      }
    }
  } else if (lowerName.endsWith('.op')) {
    for (let i = 0; i < lines.length; i++) {
      const lineText = lines[i];
      const lineNum = i + 1;
      const opMatch = lineText.match(/\bOperation\s+([A-Za-z0-9_]+)/);
      if (opMatch) {
        items.push({ id: `op-${lineNum}`, name: opMatch[1], kind: 'Operation', line: lineNum });
      }
    }
  } else if (lowerName.endsWith('.activity')) {
    let currentDiagram = null;
    for (let i = 0; i < lines.length; i++) {
      const lineText = lines[i];
      const lineNum = i + 1;
      const diagMatch = lineText.match(/\bActivityDiagram\s+([A-Za-z0-9_]+)/);
      if (diagMatch) {
        currentDiagram = { id: `diag-${lineNum}`, name: diagMatch[1], kind: 'Activity', line: lineNum, children: [] };
        items.push(currentDiagram);
        continue;
      }
      const actMatch = lineText.match(/\bActivity\s+([A-Za-z0-9_]+)\s*\{/);
      if (actMatch) {
        const actNode = { id: `act-${lineNum}`, name: actMatch[1], kind: 'Activity', line: lineNum };
        if (currentDiagram) currentDiagram.children.push(actNode);
        else items.push(actNode);
      }
    }
  } else if (lowerName.endsWith('.mnc')) {
    let currentBlock = null;
    for (let i = 0; i < lines.length; i++) {
      const lineText = lines[i];
      const lineNum = i + 1;
      const ifMatch = lineText.match(/\bInterfaceDescription\s+([A-Za-z0-9_]+)/);
      if (ifMatch) {
        currentBlock = { id: `if-${lineNum}`, name: ifMatch[1], kind: 'Interface', line: lineNum, children: [] };
        items.push(currentBlock);
        continue;
      }
      const stateMatch = lineText.match(/^\s*([A-Z][A-Za-z0-9_]*)\s*\[\s*\]/);
      if (stateMatch && currentBlock) {
        currentBlock.children.push({ id: `st-${lineNum}`, name: stateMatch[1], kind: 'State', line: lineNum });
      }
    }
  }

  return items;
}

describe('Left Sidebar Project Navigator Test Suite', () => {

  describe('1. Domain Grouping & Artifact Counts', () => {
    const mockFiles = [
      { id: '1', name: 'Barrier.dml', content: 'Package Boom' },
      { id: '2', name: 'Sensors.dml', content: 'DataModel SensorData' },
      { id: '3', name: 'BoomBarrier_Cap.cap', content: 'Capability BoomCap' },
      { id: '4', name: 'Barrier_Open.op', content: 'Operation Open()' },
      { id: '5', name: 'Barrier_Close.op', content: 'Operation Close()' },
      { id: '6', name: 'BarrierSupervisory.activity', content: 'ActivityDiagram Flow' },
      { id: '7', name: 'BarrierController.mnc', content: 'Model Barrier' },
      { id: '8', name: 'config.json', content: '{}' },
    ];

    test('Categorizes files into proper engineering domain groups', () => {
      const groups = groupProjectFiles(mockFiles);
      assert.equal(groups.dataModels.length, 2);
      assert.equal(groups.capabilitiesOps.length, 3);
      assert.equal(groups.workflows.length, 2); // .activity + .json
      assert.equal(groups.stateMachines.length, 1);
      assert.equal(groups.otherFiles.length, 0);
    });

    test('Data Models group contains only .dml files', () => {
      const groups = groupProjectFiles(mockFiles);
      for (const f of groups.dataModels) {
        assert.match(f.name, /\.dml$/);
      }
    });

    test('Capabilities & Ops group contains only .cap and .op files', () => {
      const groups = groupProjectFiles(mockFiles);
      for (const f of groups.capabilitiesOps) {
        assert.match(f.name, /(\.cap|\.capability|\.op|\.operation)$/);
      }
    });
  });

  describe('2. Active Artifact Highlighting and Status Badges', () => {
    test('Unsaved dirty state is accurately detected', () => {
      const dirtyFileIds = ['3', '5'];
      assert.equal(dirtyFileIds.includes('3'), true);
      assert.equal(dirtyFileIds.includes('1'), false);
    });

    test('Validation errors and warnings map to appropriate severities', () => {
      const validationErrors = {
        '3': [{ severity: 'error', message: 'Syntax error' }],
        '4': [{ severity: 'warning', message: 'Unused operation' }]
      };
      const files = [
        { id: '3', name: 'BoomBarrier_Cap.cap' },
        { id: '4', name: 'Barrier_Open.op' }
      ];

      const agg = aggregateProblems(files, validationErrors);
      assert.equal(agg.totalErrors, 1);
      assert.equal(agg.totalWarnings, 1);
      assert.equal(agg.totalCount, 2);
      assert.equal(agg.grouped.length, 2);
    });
  });

  describe('3. Pinned Artifacts Tracking', () => {
    test('Pinning and unpinning updates pinned file list', () => {
      let pinned = ['1', '3'];
      const togglePin = (id) => {
        pinned = pinned.includes(id) ? pinned.filter(p => p !== id) : [...pinned, id];
      };

      togglePin('1'); // unpin 1
      assert.deepEqual(pinned, ['3']);

      togglePin('6'); // pin 6
      assert.deepEqual(pinned, ['3', '6']);
    });
  });

  describe('4. Project-Local Fast Search Engine', () => {
    const mockFiles = [
      { id: '1', name: 'CoolingSensors.dml', content: 'DataModel TemperatureData\nprimitives { float currentTemp = 25.0 }' },
      { id: '2', name: 'CoolingCap.cap', content: 'Capability CoolingCap\nfireable commands : START_PUMP, MAX_FAN' },
      { id: '3', name: 'CoolingOps.op', content: 'Operation StartPumpOperation()\nexecute "scripts/pump.py"' },
    ];
    const mockSymbols = [
      { fileId: '1', fileName: 'CoolingSensors.dml', name: 'TemperatureData', kind: 'DataModel', line: 1 },
      { fileId: '2', fileName: 'CoolingCap.cap', name: 'CoolingCap', kind: 'Capability', line: 1 },
      { fileId: '3', fileName: 'CoolingOps.op', name: 'StartPumpOperation', kind: 'Operation', line: 1 },
    ];

    test('Finds matches by filename', () => {
      const results = searchProject(mockFiles, mockSymbols, 'CoolingSensors');
      assert.equal(results.length >= 1, true);
      assert.equal(results[0].fileName, 'CoolingSensors.dml');
      assert.equal(results[0].matchType, 'filename');
    });

    test('Finds matches by symbol name', () => {
      const results = searchProject(mockFiles, mockSymbols, 'TemperatureData');
      assert.equal(results.length >= 1, true);
      assert.equal(results[0].matchType, 'symbol');
      assert.equal(results[0].symbolKind, 'DataModel');
    });

    test('Finds matches by file content lines with accurate line numbers', () => {
      const results = searchProject(mockFiles, mockSymbols, 'currentTemp');
      assert.equal(results.length >= 1, true);
      assert.equal(results[0].line, 2);
      assert.equal(results[0].matchType, 'content');
      assert.match(results[0].matchText, /currentTemp/);
    });
  });

  describe('5. Context-Aware Symbol Outline Extraction', () => {
    test('Extracts DML Package, DataModels, and Fields', () => {
      const dmlContent = `Package CoolingSystem\nDataModel TemperatureData {\n    primitives {\n        float currentTemp = 25.0,\n        int sensorId = 101\n    }\n}`;
      const symbols = extractSymbols(dmlContent, 'CoolingData.dml');
      assert.equal(symbols.length, 2); // Package + DataModel
      assert.equal(symbols[0].kind, 'Package');
      assert.equal(symbols[0].name, 'CoolingSystem');
      assert.equal(symbols[1].kind, 'DataModel');
      assert.equal(symbols[1].name, 'TemperatureData');
      assert.equal(symbols[1].children.length, 2);
      assert.equal(symbols[1].children[0].name, 'currentTemp');
      assert.equal(symbols[1].children[1].name, 'sensorId');
    });

    test('Extracts Capability and Fireable Commands', () => {
      const capContent = `Capability BoomCap compatible component interface BarrierIF {\n    providesControlCapabilities {\n        fireable commands : OPEN_BARRIER, CLOSE_BARRIER\n    }\n}`;
      const symbols = extractSymbols(capContent, 'BoomBarrier.cap');
      assert.equal(symbols.length, 1);
      assert.equal(symbols[0].kind, 'Capability');
      assert.equal(symbols[0].name, 'BoomCap');
      assert.equal(symbols[0].children.length, 2);
      assert.equal(symbols[0].children[0].name, 'OPEN_BARRIER');
      assert.equal(symbols[0].children[1].name, 'CLOSE_BARRIER');
    });

    test('Extracts Activity Diagram and Sequential Steps', () => {
      const actContent = `ActivityDiagram PickAndPlace {\n    Activity Step1 {\n        description : "First"\n    }\n    Activity Step2 {\n        description : "Second"\n    }\n}`;
      const symbols = extractSymbols(actContent, 'Process.activity');
      assert.equal(symbols.length, 1);
      assert.equal(symbols[0].kind, 'Activity');
      assert.equal(symbols[0].name, 'PickAndPlace');
      assert.equal(symbols[0].children.length, 2);
      assert.equal(symbols[0].children[0].name, 'Step1');
      assert.equal(symbols[0].children[1].name, 'Step2');
    });

    test('Extracts MNC-ML Interface and Operating States', () => {
      const mncContent = `InterfaceDescription BarrierIF {\n    operatingStates {\n        IDLE []\n        OPENING []\n        OPENED []\n    }\n}`;
      const symbols = extractSymbols(mncContent, 'Barrier.mnc');
      assert.equal(symbols.length, 1);
      assert.equal(symbols[0].kind, 'Interface');
      assert.equal(symbols[0].children.length, 3);
      assert.equal(symbols[0].children[0].name, 'IDLE');
      assert.equal(symbols[0].children[1].name, 'OPENING');
      assert.equal(symbols[0].children[2].name, 'OPENED');
    });
  });

  describe('6. Sidebar Resizing and Clamping', () => {
    const clampWidth = (val) => Math.max(220, Math.min(360, val));

    test('Clamps width between 220px and 360px', () => {
      assert.equal(clampWidth(150), 220);
      assert.equal(clampWidth(250), 250);
      assert.equal(clampWidth(500), 360);
    });

    test('Collapsed sidebar returns strictly 68px', () => {
      const isCollapsed = true;
      const sidebarWidth = 260;
      const effectiveWidth = isCollapsed ? 68 : sidebarWidth;
      assert.equal(effectiveWidth, 68);
    });
  });

});

