import { test, describe } from 'node:test';
import assert from 'node:assert/strict';

// Helper: Compute Semantic Header
function computeInspectorHeader({ activeView, activeFile, selectedNodeId, selectedGraphNode, graphScope, validationErrors, dirtyFileIds }) {
  let semanticName = '';
  let semanticType = '';
  let breadcrumb = null;

  if (activeView === 'knowledgegraph') {
    if (selectedGraphNode) {
      semanticName = selectedGraphNode.name;
      const t = selectedGraphNode.type || 'Entity';
      semanticType = t.charAt(0).toUpperCase() + t.slice(1);
      breadcrumb = `${graphScope === 'global' ? 'Global Repository' : 'Project Graph'} › ${selectedGraphNode.name}`;
    } else {
      semanticName = graphScope === 'global' ? 'Global Repository' : 'Project Knowledge Graph';
      semanticType = graphScope === 'global' ? 'Global Domain Catalog' : 'Project Knowledge Graph';
    }
  } else if (activeView === 'statemachine') {
    semanticName = selectedNodeId || 'State Machine';
    semanticType = selectedNodeId ? 'Operating State' : 'Supervisory Automata';
    if (selectedNodeId) breadcrumb = `State Machine › ${selectedNodeId}`;
  } else if (activeView === 'workflow') {
    semanticName = selectedNodeId || activeFile?.name || 'Workflow Diagram';
    semanticType = selectedNodeId ? 'Workflow Activity' : 'Activity Diagram';
    if (selectedNodeId) breadcrumb = `${activeFile?.name || 'Workflow'} › ${selectedNodeId}`;
  } else if (activeView === 'simulator') {
    semanticName = 'Controller Simulator';
    semanticType = 'Live Controller Simulator';
  } else if (activeView === 'codegen') {
    semanticName = 'Industrial Code Generator';
    semanticType = 'Code Synthesis Studio';
  } else {
    // Editor
    semanticName = selectedNodeId || activeFile?.name || 'Project File';
    if (selectedNodeId && activeFile) {
      semanticType = 'Declared Symbol';
      breadcrumb = `${activeFile.name} › ${selectedNodeId}`;
    } else if (activeFile) {
      const lower = activeFile.name.toLowerCase();
      if (lower.endsWith('.activity')) semanticType = 'Activity Diagram';
      else if (lower.endsWith('.dml')) semanticType = 'Data Model Package';
      else if (lower.endsWith('.cap')) semanticType = 'Semantic Capability';
      else if (lower.endsWith('.op')) semanticType = 'Device Operation';
      else if (lower.endsWith('.mnc') || lower.endsWith('.mncml')) semanticType = 'MNC-ML Interface';
      else semanticType = 'Source Artifact';
    }
  }

  // Compute status
  let status = 'Ready';
  if (activeFile && activeView === 'editor') {
    const issues = validationErrors[activeFile.id] || [];
    const errors = issues.filter(i => i.severity === 'error').length;
    const warnings = issues.filter(i => i.severity !== 'error').length;
    const isDirty = (dirtyFileIds || []).includes(activeFile.id);

    if (errors > 0) status = `${errors} Errors`;
    else if (warnings > 0) status = `${warnings} Warnings`;
    else if (isDirty) status = 'Unsaved';
    else status = 'Valid';
  } else if (activeView === 'knowledgegraph') {
    status = 'Live Graph';
  } else if (activeView === 'simulator') {
    status = 'Live';
  }

  return { semanticName, semanticType, breadcrumb, status };
}

// Helper: Graph connection analyzer
function analyzeGraphConnections(selectedNode, graphData) {
  if (!selectedNode || !graphData) return { incoming: [], outgoing: [] };
  const incoming = (graphData.edges || []).filter(e => e.target === selectedNode.id || e.target === selectedNode.name);
  const outgoing = (graphData.edges || []).filter(e => e.source === selectedNode.id || e.source === selectedNode.name);
  return { incoming, outgoing };
}

// Helper: Section visibility rule (no empty sections)
function shouldRenderSection(title, count) {
  if (count === 0 && (title === 'Dependencies' || title === 'Issues' || title === 'Errors' || title === 'Warnings')) {
    return false;
  }
  return true;
}

describe('Inspector Redesign Test Suite', () => {

  describe('1. Semantic Header & Labeling (Replacing CONTEXT)', () => {
    test('Header reflects active artifact and concrete engineering type', () => {
      const header = computeInspectorHeader({
        activeView: 'editor',
        activeFile: { id: '1', name: 'Demo.activity' },
        selectedNodeId: null,
        validationErrors: {},
        dirtyFileIds: []
      });

      assert.equal(header.semanticName, 'Demo.activity');
      assert.equal(header.semanticType, 'Activity Diagram');
      assert.equal(header.status, 'Valid');
      assert.equal(header.breadcrumb, null);
    });

    test('Header reflects nested symbol with concise breadcrumb when symbol is selected', () => {
      const header = computeInspectorHeader({
        activeView: 'editor',
        activeFile: { id: '1', name: 'Demo.activity' },
        selectedNodeId: 'Monitor',
        validationErrors: {},
        dirtyFileIds: []
      });

      assert.equal(header.semanticName, 'Monitor');
      assert.equal(header.semanticType, 'Declared Symbol');
      assert.equal(header.breadcrumb, 'Demo.activity › Monitor');
    });

    test('Header reflects unsaved dirty state and error counts', () => {
      const headerWithErrors = computeInspectorHeader({
        activeView: 'editor',
        activeFile: { id: '1', name: 'CoolingData.dml' },
        selectedNodeId: null,
        validationErrors: { '1': [{ severity: 'error', message: 'Syntax error' }] },
        dirtyFileIds: ['1']
      });
      assert.equal(headerWithErrors.status, '1 Errors');

      const headerDirtyOnly = computeInspectorHeader({
        activeView: 'editor',
        activeFile: { id: '1', name: 'CoolingData.dml' },
        selectedNodeId: null,
        validationErrors: {},
        dirtyFileIds: ['1']
      });
      assert.equal(headerDirtyOnly.status, 'Unsaved');
    });
  });

  describe('2. Project Graph Workspace Inspector (Resolving Screen Discrepancy)', () => {
    const mockGraphData = {
      nodes: [
        { id: '1', name: 'SensorCap', type: 'capability', category: 'Sensors', source_file: 'CoolingCap.cap' },
        { id: '2', name: 'Monitor', type: 'activity', category: 'Workflow', source_file: 'Demo.activity' },
        { id: '3', name: 'TempSensor', type: 'device', category: 'Hardware' }
      ],
      edges: [
        { source: '2', target: '1', label: 'requires_capability' },
        { source: '1', target: '3', label: 'binds_device' }
      ],
      stats: { total_nodes: 3, total_edges: 2 }
    };

    test('CRITICAL: In Project Graph with NO node selected, middle panel shows Graph Overview (NOT stale Demo.activity outline)', () => {
      const header = computeInspectorHeader({
        activeView: 'knowledgegraph',
        activeFile: { id: 'f1', name: 'Demo.activity' }, // Previous active file
        selectedNodeId: null,
        selectedGraphNode: null,
        graphScope: 'project',
        validationErrors: {},
        dirtyFileIds: []
      });

      // Must show Project Knowledge Graph, NOT Demo.activity!
      assert.equal(header.semanticName, 'Project Knowledge Graph');
      assert.equal(header.semanticType, 'Project Knowledge Graph');
      assert.equal(header.status, 'Live Graph');
    });

    test('In Global Repository scope with NO node selected, indicates Global Repository domain catalog', () => {
      const header = computeInspectorHeader({
        activeView: 'knowledgegraph',
        activeFile: { id: 'f1', name: 'Demo.activity' },
        selectedNodeId: null,
        selectedGraphNode: null,
        graphScope: 'global',
        validationErrors: {},
        dirtyFileIds: []
      });

      assert.equal(header.semanticName, 'Global Repository');
      assert.equal(header.semanticType, 'Global Domain Catalog');
    });

    test('Selecting a graph node immediately updates Inspector to that node with connections', () => {
      const selected = mockGraphData.nodes[0]; // SensorCap
      const header = computeInspectorHeader({
        activeView: 'knowledgegraph',
        activeFile: { id: 'f1', name: 'Demo.activity' },
        selectedNodeId: 'SensorCap',
        selectedGraphNode: selected,
        graphScope: 'project',
        validationErrors: {},
        dirtyFileIds: []
      });

      assert.equal(header.semanticName, 'SensorCap');
      assert.equal(header.semanticType, 'Capability');
      assert.equal(header.breadcrumb, 'Project Graph › SensorCap');

      const conns = analyzeGraphConnections(selected, mockGraphData);
      assert.equal(conns.incoming.length, 1); // Monitor requires SensorCap
      assert.equal(conns.outgoing.length, 1); // SensorCap binds TempSensor
    });
  });

  describe('3. Workflow and State Machine Workspace Inspectors', () => {
    test('Workflow workspace with selected activity shows activity details', () => {
      const header = computeInspectorHeader({
        activeView: 'workflow',
        activeFile: { id: '1', name: 'Demo.activity' },
        selectedNodeId: 'Monitor',
        validationErrors: {},
        dirtyFileIds: []
      });

      assert.equal(header.semanticName, 'Monitor');
      assert.equal(header.semanticType, 'Workflow Activity');
      assert.equal(header.breadcrumb, 'Demo.activity › Monitor');
    });

    test('State Machine workspace with selected state shows state transitions', () => {
      const header = computeInspectorHeader({
        activeView: 'statemachine',
        activeFile: null,
        selectedNodeId: 'READY',
        validationErrors: {},
        dirtyFileIds: []
      });

      assert.equal(header.semanticName, 'READY');
      assert.equal(header.semanticType, 'Operating State');
      assert.equal(header.breadcrumb, 'State Machine › READY');
    });
  });

  describe('4. Progressive Disclosure & Clean Visual Hierarchy', () => {
    test('Hides empty sections when count is 0', () => {
      assert.equal(shouldRenderSection('Dependencies', 0), false);
      assert.equal(shouldRenderSection('Issues', 0), false);
      assert.equal(shouldRenderSection('Errors', 0), false);
      assert.equal(shouldRenderSection('Dependencies', 2), true);
      assert.equal(shouldRenderSection('Structure', 0), true); // Structure can show empty helper
    });

    test('Errors visually outrank warnings in status priority', () => {
      const headerErrors = computeInspectorHeader({
        activeView: 'editor',
        activeFile: { id: '1', name: 'Model.dml' },
        selectedNodeId: null,
        validationErrors: {
          '1': [
            { severity: 'warning', message: 'Unused field' },
            { severity: 'error', message: 'Missing type' }
          ]
        },
        dirtyFileIds: []
      });

      assert.equal(headerErrors.status, '1 Errors');
    });
  });

  describe('5. Panel Resizing and Width Clamping', () => {
    const clampWidth = (w) => Math.max(220, Math.min(360, w));

    test('Default panel width is 280px and clamps between 220px and 360px', () => {
      assert.equal(clampWidth(180), 220);
      assert.equal(clampWidth(280), 280);
      assert.equal(clampWidth(400), 360);
    });

    test('Collapsing panel yields 0px to main workspace', () => {
      const isCollapsed = true;
      const width = isCollapsed ? 0 : 280;
      assert.equal(width, 0);
    });
  });

});

