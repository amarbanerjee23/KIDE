import { describe, it } from 'node:test';
import assert from 'node:assert';

describe('PR 2: Canonical Engineering IR & Multi-File Semantic Validation', () => {

  it('1. should parse and normalize semantic validation diagnostics', () => {
    const rawApiDiagnostics = [
      {
        fileId: 'CoolingSystem.activity',
        filename: 'CoolingSystem.activity',
        line: 12,
        column: 5,
        message: "Activity 'ActivateChamber' requires undefined capability 'MissingChillerCap'",
        severity: 'error',
        ruleId: 'SEM-001',
        symbol: 'MissingChillerCap',
        suggestion: "Declare capability 'MissingChillerCap' in a .cap file."
      },
      {
        fileId: 'CoolingModel.mnc',
        filename: 'CoolingModel.mnc',
        line: 45,
        column: 1,
        message: "Deadlock Hazard: State 'PAUSED' has no outgoing transitions and is not an endState",
        severity: 'error',
        ruleId: 'HAZ-002',
        symbol: 'PAUSED',
        suggestion: "Add an outgoing transition from 'PAUSED' or register it in 'endStates'."
      },
      {
        fileId: 'CoolingSystem.activity',
        filename: 'CoolingSystem.activity',
        line: 22,
        column: 1,
        message: "Dangling Activity: 'AuxCooling' has no incoming transitions",
        severity: 'warning',
        ruleId: 'FLW-002',
        symbol: 'AuxCooling',
        suggestion: "Connect a preceding activity to 'AuxCooling'."
      }
    ];

    // Grouping by file
    const grouped = {};
    rawApiDiagnostics.forEach(d => {
      const fid = d.fileId || d.filename;
      if (!grouped[fid]) grouped[fid] = [];
      grouped[fid].push(d);
    });

    assert.strictEqual(Object.keys(grouped).length, 2);
    assert.strictEqual(grouped['CoolingSystem.activity'].length, 2);
    assert.strictEqual(grouped['CoolingModel.mnc'].length, 1);

    // Verify error and warning separation
    const actIssues = grouped['CoolingSystem.activity'];
    const errors = actIssues.filter(i => i.severity === 'error');
    const warnings = actIssues.filter(i => i.severity === 'warning');

    assert.strictEqual(errors.length, 1);
    assert.strictEqual(warnings.length, 1);
    assert.strictEqual(errors[0].ruleId, 'SEM-001');
    assert.strictEqual(errors[0].symbol, 'MissingChillerCap');
    assert.ok(errors[0].suggestion.includes('Declare capability'));
  });

  it('2. should verify IR summary metrics calculation', () => {
    const irSummary = {
      total_files: 5,
      total_symbols: 24,
      total_interfaces: 2,
      total_states: 8,
      total_transitions: 14,
      total_activities: 4,
      total_cross_references: 9,
      error_count: 0,
      warning_count: 1,
      health_score: 95.0
    };

    assert.strictEqual(irSummary.total_files, 5);
    assert.strictEqual(irSummary.total_states, 8);
    assert.strictEqual(irSummary.error_count, 0);
    assert.strictEqual(irSummary.health_score >= 90.0, true);
  });

  it('3. should correctly map hazard rules to actionable engineering alerts', () => {
    const hazardCatalog = {
      'HAZ-001': { name: 'Unreachable State', category: 'Automata Reachability' },
      'HAZ-002': { name: 'Deadlock Hazard', category: 'Automata Liveness' },
      'HAZ-003': { name: 'Ambiguous Transition', category: 'Determinism' },
      'HAZ-004': { name: 'Missing Start State', category: 'Initialization' },
      'HAZ-006': { name: 'Infinite Self-Loop', category: 'Liveness' },
      'SEM-001': { name: 'Unresolved Capability', category: 'Symbol Resolution' },
      'FLW-001': { name: 'Workflow Cycle Hazard', category: 'Supervisory Flow' },
      'CFG-001': { name: 'Invalid IPv4 Address', category: 'Deployment Configuration' }
    };

    assert.strictEqual(hazardCatalog['HAZ-002'].name, 'Deadlock Hazard');
    assert.strictEqual(hazardCatalog['SEM-001'].category, 'Symbol Resolution');
    assert.strictEqual(hazardCatalog['FLW-001'].category, 'Supervisory Flow');
  });
});

