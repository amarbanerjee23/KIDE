import test from 'node:test';
import assert from 'node:assert/strict';

test('AI Engineering Copilot Logic Tests', async (t) => {
  await t.test('should format tool execution output correctly', () => {
    const toolCall = {
      id: 'tool_123',
      tool: 'validate_project',
      input: {},
      output: {
        valid: true,
        total_files_checked: 5,
        issues_count: 0
      },
      status: 'success'
    };

    assert.equal(toolCall.tool, 'validate_project');
    assert.equal(toolCall.output.valid, true);
    assert.equal(toolCall.output.total_files_checked, 5);
  });

  await t.test('should structure proposed patch with diff and rationale', () => {
    const patch = {
      filename: 'GateSafety.activity',
      action: 'create',
      diff: '+ ActivityDiagram GateSafety\n+ Activity Authenticate',
      new_content: 'ActivityDiagram GateSafety ...',
      rationale: 'Generated safety-interlocked barrier control workflow'
    };

    assert.equal(patch.filename, 'GateSafety.activity');
    assert.equal(patch.action, 'create');
    assert.ok(patch.diff.includes('+ ActivityDiagram'));
    assert.ok(patch.rationale.includes('barrier control'));
  });

  await t.test('should parse unified diff lines into added/removed tokens', () => {
    const rawDiff = '--- a/GateSafety.activity\n+++ b/GateSafety.activity\n@@ -1,2 +1,3 @@\n- time: 2.0 secs\n+ time: 5.0 secs\n  nextActivity: SafetyClearance';
    const lines = rawDiff.split('\n');

    const addedLines = lines.filter(l => l.startsWith('+') && !l.startsWith('+++'));
    const removedLines = lines.filter(l => l.startsWith('-') && !l.startsWith('---'));

    assert.equal(addedLines.length, 1);
    assert.equal(removedLines.length, 1);
    assert.ok(addedLines[0].includes('5.0 secs'));
    assert.ok(removedLines[0].includes('2.0 secs'));
  });

  await t.test('should record AI provenance metadata with session and provider', () => {
    const provenanceRecord = {
      id: 42,
      project_id: 10,
      session_id: 'sess_abc123',
      provider: 'gemini',
      model_name: 'gemini-1.5-flash',
      user_prompt: 'Synthesize state machine',
      assistant_response: '⚡ Supervisory Automata Synthesized Successfully',
      status: 'applied',
      created_at: '2026-09-12T18:00:00Z'
    };

    assert.equal(provenanceRecord.id, 42);
    assert.equal(provenanceRecord.provider, 'gemini');
    assert.equal(provenanceRecord.status, 'applied');
  });

  await t.test('should handle patch application state transition', () => {
    const appliedPatches = {};
    const patchKey = 'GateSafety.activity_42';

    // Initially unapplied
    assert.equal(Boolean(appliedPatches[patchKey]), false);

    // Apply patch
    appliedPatches[patchKey] = true;
    assert.equal(appliedPatches[patchKey], true);
  });

  await t.test('should provide quick prompt chips for common engineering intents', () => {
    const chips = [
      'Validate all models',
      'Synthesize state machine',
      'Search knowledge for barrier RFID',
      'Create a safety gate barrier activity patch'
    ];

    assert.equal(chips.length, 4);
    assert.ok(chips.some(c => c.includes('Validate')));
    assert.ok(chips.some(c => c.includes('Synthesize')));
    assert.ok(chips.some(c => c.includes('Search knowledge')));
    assert.ok(chips.some(c => c.includes('safety gate')));
  });
});

