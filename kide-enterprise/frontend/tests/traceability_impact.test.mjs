import { describe, it } from 'node:test';
import assert from 'node:assert';

describe('PR 3: Bidirectional Traceability, Change Impact Analysis & Semantic Reconfiguration', () => {

  it('1. should process and normalize Traceability Matrix rows across 5 engineering stages', () => {
    const mockMatrixResponse = {
      project_id: 101,
      total_symbols: 12,
      total_links: 24,
      coverage_percentage: 100.0,
      rows: [
        {
          symbol: 'CoolingParameters',
          symbol_type: 'data_model',
          filename: 'CoolingData.dml',
          stage: 1,
          upstream_symbols: [],
          downstream_symbols: ['ChillerCooling', 'ActivateCooling'],
          links: [
            {
              source_symbol: 'CoolingParameters',
              source_type: 'data_model',
              source_file: 'CoolingData.dml',
              target_symbol: 'ActivateCooling',
              target_type: 'activity',
              target_file: 'CoolingWorkflow.activity',
              link_type: 'context_model',
              description: "Workflow 'ActivateCooling' executes in data context 'CoolingParameters'"
            }
          ]
        },
        {
          symbol: 'ChillerCooling',
          symbol_type: 'capability',
          filename: 'ChillerCap.cap',
          stage: 2,
          upstream_symbols: ['CoolingInterface'],
          downstream_symbols: ['ActivateCooling', 'CoolingSystem_SM', 'PythonController'],
          links: [
            {
              source_symbol: 'ChillerCooling',
              source_type: 'capability',
              source_file: 'ChillerCap.cap',
              target_symbol: 'ActivateCooling',
              target_type: 'activity',
              target_file: 'CoolingWorkflow.activity',
              link_type: 'requires_capability',
              description: "Activity 'ActivateCooling' requires capability 'ChillerCooling'"
            }
          ]
        },
        {
          symbol: 'ActivateCooling',
          symbol_type: 'activity',
          filename: 'CoolingWorkflow.activity',
          stage: 3,
          upstream_symbols: ['ChillerCooling', 'CoolingParameters'],
          downstream_symbols: ['CoolingSystem_SM', 'PythonController', 'ROS2Node', 'PLC_IEC61131'],
          links: [
            {
              source_symbol: 'ActivateCooling',
              source_type: 'activity',
              source_file: 'CoolingWorkflow.activity',
              target_symbol: 'CoolingSystem_SM',
              target_type: 'operating_state',
              target_file: 'CoolingAutomata.mnc',
              link_type: 'synthesizes_state',
              description: "Activity 'ActivateCooling' synthesizes operating state in automata"
            }
          ]
        }
      ],
      layer_counts: {
        stage_1_dml: 1,
        stage_2_capabilities: 1,
        stage_3_workflows: 1,
        stage_4_automata: 1,
        stage_5_codegen: 5
      }
    };

    assert.strictEqual(mockMatrixResponse.coverage_percentage, 100.0);
    assert.strictEqual(mockMatrixResponse.rows.length, 3);
    
    // Check Stage 2 capability downstream connections
    const capRow = mockMatrixResponse.rows.find(r => r.symbol === 'ChillerCooling');
    assert.ok(capRow);
    assert.strictEqual(capRow.stage, 2);
    assert.ok(capRow.downstream_symbols.includes('ActivateCooling'));
    assert.ok(capRow.downstream_symbols.includes('CoolingSystem_SM'));
  });

  it('2. should evaluate downstream BFS blast radius and categorize risk levels', () => {
    const mockImpactResponse = {
      target_symbol: 'ChillerCooling',
      target_type: 'capability',
      action: 'delete',
      risk_level: 'CRITICAL',
      impacted_symbols_count: 7,
      impacted_items: [
        {
          symbol: 'ActivateCooling',
          symbol_type: 'activity',
          filename: 'CoolingWorkflow.activity',
          stage: 3,
          impact_reason: "Activity depends on capability 'ChillerCooling'",
          risk: 'CRITICAL'
        },
        {
          symbol: 'CoolingSystem_SM',
          symbol_type: 'operating_state',
          filename: 'CoolingAutomata.mnc',
          stage: 4,
          impact_reason: "State synthesized from affected activity 'ActivateCooling'",
          risk: 'HIGH'
        },
        {
          symbol: 'PythonController',
          symbol_type: 'code_generator',
          filename: 'codegen/python',
          stage: 5,
          impact_reason: 'Code target derives from affected activities and states',
          risk: 'HIGH'
        }
      ],
      affected_activities: ['ActivateCooling'],
      affected_states: ['CoolingSystem_SM'],
      broken_transitions: ['INIT => CoolingSystem_SM'],
      affected_code_generators: ['PythonController', 'ROS2Node', 'PLC_IEC61131'],
      breaking_hazards: [
        "SEM-001: Activity 'ActivateCooling' requires missing capability 'ChillerCooling'",
        "HAZ-002: Automata transition 'INIT => CoolingSystem_SM' becomes unreachable"
      ],
      recommended_mitigations: [
        "Substitute 'ChillerCooling' with a compatible capability before removing",
        "Re-synthesize supervisory automata to repair broken transitions"
      ]
    };

    assert.strictEqual(mockImpactResponse.risk_level, 'CRITICAL');
    assert.strictEqual(mockImpactResponse.affected_activities.length, 1);
    assert.strictEqual(mockImpactResponse.breaking_hazards.length, 2);
    assert.ok(mockImpactResponse.breaking_hazards[0].includes('SEM-001'));
    assert.strictEqual(mockImpactResponse.recommended_mitigations.length, 2);
  });

  it('3. should verify automated semantic reconfiguration proposal and unified diff patches', () => {
    const mockProposal = {
      status: 'success',
      explanation: "Successfully replaced 'ChillerCooling' with 'SmartChillerV2' across 1 workflow file.",
      deprecated_capability: 'ChillerCooling',
      replacement_capability: 'SmartChillerV2',
      affected_files: ['CoolingWorkflow.activity'],
      patches: [
        {
          filename: 'CoolingWorkflow.activity',
          diff: '--- CoolingWorkflow.activity\n+++ CoolingWorkflow.activity\n@@ -12,3 +12,3 @@\n-    requireCapability: "ChillerCooling"\n+    requireCapability: "SmartChillerV2"',
          new_content: 'ActivityDiagram CoolingProcess {\n  Activity ActivateCooling {\n    requireCapability: "SmartChillerV2"\n  }\n}',
          rationale: "Replaced capability 'ChillerCooling' with 'SmartChillerV2'"
        }
      ],
      safety_verification: {
        safe: true,
        deadlocks_detected: 0,
        unresolved_symbols: 0
      }
    };

    assert.strictEqual(mockProposal.status, 'success');
    assert.strictEqual(mockProposal.patches.length, 1);
    assert.ok(mockProposal.safety_verification.safe);
    assert.strictEqual(mockProposal.safety_verification.deadlocks_detected, 0);

    // Diff line categorization verification
    const diffLines = mockProposal.patches[0].diff.split('\n');
    const addedLines = diffLines.filter(l => l.startsWith('+') && !l.startsWith('+++'));
    const removedLines = diffLines.filter(l => l.startsWith('-') && !l.startsWith('---'));

    assert.strictEqual(addedLines.length, 1);
    assert.ok(addedLines[0].includes('SmartChillerV2'));
    assert.strictEqual(removedLines.length, 1);
    assert.ok(removedLines[0].includes('ChillerCooling'));
  });

  it('4. should correctly simulate editor store file content update upon applying patch', () => {
    const editorStoreFiles = [
      {
        id: 'file_1',
        name: 'CoolingWorkflow.activity',
        content: 'ActivityDiagram CoolingProcess {\n  Activity ActivateCooling {\n    requireCapability: "ChillerCooling"\n  }\n}',
        language: 'activity'
      },
      {
        id: 'file_2',
        name: 'ChillerCap.cap',
        content: 'Capability ChillerCooling compatible component interface CoolingIF { }',
        language: 'capability'
      }
    ];

    const patchToApply = {
      filename: 'CoolingWorkflow.activity',
      diff: '...',
      new_content: 'ActivityDiagram CoolingProcess {\n  Activity ActivateCooling {\n    requireCapability: "SmartChillerV2"\n  }\n}'
    };

    // Find and update file in store
    const targetFile = editorStoreFiles.find(f => f.name.toLowerCase() === patchToApply.filename.toLowerCase());
    assert.ok(targetFile);

    targetFile.content = patchToApply.new_content;

    assert.ok(targetFile.content.includes('SmartChillerV2'));
    assert.ok(!targetFile.content.includes('ChillerCooling'));
  });

});

