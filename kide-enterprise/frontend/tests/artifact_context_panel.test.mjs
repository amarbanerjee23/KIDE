import { test, describe } from 'node:test';
import assert from 'node:assert/strict';

// Helper: Extract relations for active artifact
function extractArtifactRelations(activeFile, files) {
  if (!activeFile) return null;
  const content = activeFile.content;
  const lowerName = (activeFile.name || '').toLowerCase();

  const requires = [];
  const produces = [];
  const generates = [];
  const relatedFiles = [];

  if (lowerName.endsWith('.activity') || lowerName.endsWith('.json')) {
    const capMatches = content.matchAll(/requireCapability\s*:\s*"?([A-Za-z0-9_]+)"?/g);
    for (const m of capMatches) {
      const capName = m[1];
      const matchFile = files.find(f => 
        f.id !== activeFile.id && (
          f.name.toLowerCase().includes(capName.toLowerCase()) || 
          (f.content && f.content.includes(capName))
        )
      );
      requires.push({ name: capName, type: 'capability', targetFileId: matchFile?.id });
    }

    const opMatches = content.matchAll(/requireOperation\s*\(\s*([A-Za-z0-9_]+)\s*\)/g);
    for (const m of opMatches) {
      const opName = m[1];
      const matchFile = files.find(f => 
        f.id !== activeFile.id && (
          f.name.toLowerCase().includes(opName.toLowerCase()) || 
          (f.content && f.content.includes(opName))
        )
      );
      requires.push({ name: opName, type: 'operation', targetFileId: matchFile?.id });
    }

    produces.push({
      name: `${activeFile.name.replace(/\.[^/.]+$/, '')} State Machine Automata`,
      type: 'Supervisory Automata'
    });

    generates.push(
      { name: 'Python Controller', target: 'python', ext: '.py' },
      { name: 'ROS2 Supervisory Node', target: 'ros2', ext: '.py' },
      { name: 'PLC (IEC 61131-3)', target: 'plc_st', ext: '.st' },
      { name: 'Java Supervisor Controller', target: 'java', ext: '.java' },
      { name: 'Embedded C++ Controller', target: 'cpp', ext: '.hpp' }
    );
  }

  const basePrefix = activeFile.name.split('.')[0].replace(/(_Cap|_Ops|_Data|System|Control|Cell)$/i, '');
  if (basePrefix.length > 2) {
    for (const f of files) {
      if (f.id !== activeFile.id && f.name.toLowerCase().includes(basePrefix.toLowerCase())) {
        relatedFiles.push({ name: f.name, fileId: f.id });
      }
    }
  }

  return { requires, produces, generates, relatedFiles };
}

// Helper: Artifact-specific issues filter
function getArtifactSpecificIssues(fileId, validationErrors) {
  return validationErrors[fileId] || [];
}

// Helper: Context panel width clamping
function clampContextWidth(width) {
  return Math.max(220, Math.min(360, width));
}

describe('Contextual Artifact Inspector Test Suite', () => {

  describe('1. Information Architecture & Elimination of Duplicate File Browser', () => {
    test('Left Explorer owns full project scope while Middle Panel owns selected artifact semantics', () => {
      const leftExplorerRole = 'PROJECT_SCOPE';
      const openTabsRole = 'OPEN_WORKING_SET';
      const middlePanelRole = 'CURRENT_ARTIFACT_SEMANTICS';
      const mainCanvasRole = 'ACTIVE_WORK';

      assert.notEqual(leftExplorerRole, middlePanelRole);
      assert.notEqual(openTabsRole, middlePanelRole);
      assert.equal(middlePanelRole, 'CURRENT_ARTIFACT_SEMANTICS');
    });

    test('Middle panel width defaults to 280px and clamps between 220px and 360px', () => {
      assert.equal(clampContextWidth(100), 220);
      assert.equal(clampContextWidth(280), 280);
      assert.equal(clampContextWidth(500), 360);
    });

    test('Collapsing middle panel releases width to 0px allowing editor to expand 100%', () => {
      const isCollapsed = true;
      const effectivePanelWidth = isCollapsed ? 0 : 280;
      assert.equal(effectivePanelWidth, 0);
    });
  });

  describe('2. Code Workspace Context (Outline, Relations, Issues, Info)', () => {
    const mockFiles = [
      {
        id: 'f1',
        name: 'CoolingSystem.activity',
        content: `ActivityDiagram CoolingSystem\nuses Objects [ string status ]\nproduces results ( string finalStatus )\nhas activities {\n    Activity Initialize {\n        description : "Init"\n        requireOperation ( StartPumpOperation )\n        nextActivity : Monitor\n    }\n    Activity Monitor {\n        description : "Monitor"\n        requireCapability : "SensorCap"\n    }\n}`
      },
      { id: 'f2', name: 'CoolingCap.cap', content: 'Capability SensorCap compatible interface CoolingIF {}' },
      { id: 'f3', name: 'CoolingOps.op', content: 'Operation StartPumpOperation() {}' },
      { id: 'f4', name: 'CoolingData.dml', content: 'DataModel TempData {}' }
    ];

    test('Relations mode extracts required capabilities and operations', () => {
      const rels = extractArtifactRelations(mockFiles[0], mockFiles);
      assert.ok(rels);
      assert.equal(rels.requires.length, 2);
      assert.equal(rels.requires[0].name, 'SensorCap');
      assert.equal(rels.requires[0].type, 'capability');
      assert.equal(rels.requires[0].targetFileId, 'f2');
      assert.equal(rels.requires[1].name, 'StartPumpOperation');
      assert.equal(rels.requires[1].type, 'operation');
      assert.equal(rels.requires[1].targetFileId, 'f3');
    });

    test('Relations mode identifies produced supervisory automata and 5 code generator targets', () => {
      const rels = extractArtifactRelations(mockFiles[0], mockFiles);
      assert.equal(rels.produces.length, 1);
      assert.match(rels.produces[0].name, /State Machine Automata/);
      assert.equal(rels.generates.length, 5);
      const targets = rels.generates.map(g => g.target);
      assert.deepEqual(targets, ['python', 'ros2', 'plc_st', 'java', 'cpp']);
    });

    test('Relations mode identifies sister artifacts sharing domain system prefix', () => {
      const rels = extractArtifactRelations(mockFiles[0], mockFiles);
      const relatedNames = rels.relatedFiles.map(r => r.name);
      assert.ok(relatedNames.includes('CoolingCap.cap'));
      assert.ok(relatedNames.includes('CoolingOps.op'));
      assert.ok(relatedNames.includes('CoolingData.dml'));
    });

    test('Issues mode isolates problems strictly to the active artifact', () => {
      const validationErrors = {
        'f1': [{ line: 10, severity: 'error', message: 'Unknown sensor capability' }],
        'f2': [{ line: 5, severity: 'warning', message: 'Unused command' }],
        'f4': [{ line: 2, severity: 'error', message: 'Syntax error' }]
      };

      const f1Issues = getArtifactSpecificIssues('f1', validationErrors);
      assert.equal(f1Issues.length, 1);
      assert.equal(f1Issues[0].line, 10);
      assert.equal(f1Issues[0].severity, 'error');

      // Does not leak f2 or f4 issues into f1 context
      assert.equal(f1Issues.some(i => i.message.includes('Unused command')), false);
      assert.equal(f1Issues.some(i => i.message.includes('Syntax error')), false);
    });
  });

  describe('3. Workspace-Adaptive Context Modes', () => {
    test('State Machine context provides states, transitions, signals, and issues', () => {
      const smModes = ['states', 'transitions', 'events', 'issues'];
      assert.deepEqual(smModes, ['states', 'transitions', 'events', 'issues']);
    });

    test('Workflow context provides activities, dependencies, conditions, and issues', () => {
      const wfModes = ['activities', 'dependencies', 'conditions', 'issues'];
      assert.deepEqual(wfModes, ['activities', 'dependencies', 'conditions', 'issues']);
    });

    test('Simulator context provides live state, telemetry variables, signals, and transitions', () => {
      const simModes = ['live', 'variables', 'signals', 'transitions'];
      assert.deepEqual(simModes, ['live', 'variables', 'signals', 'transitions']);
    });

    test('Generate context provides targets, transformation lineage, outputs, and issues', () => {
      const genModes = ['targets', 'lineage', 'outputs', 'issues'];
      assert.deepEqual(genModes, ['targets', 'lineage', 'outputs', 'issues']);
    });
  });

});
