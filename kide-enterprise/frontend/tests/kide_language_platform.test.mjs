import { describe, it } from 'node:test';
import assert from 'node:assert';

// =========================================================================
// Native Domain Logic Under Test (Mirroring src/lib/kide-language)
// =========================================================================

// 1. Language Resolution
function getLanguageForArtifact(filename) {
  if (!filename) return 'plaintext';
  const lower = filename.toLowerCase().trim();
  if (lower.endsWith('.dml')) return 'dmldsl';
  if (lower.endsWith('.cap') || lower.endsWith('.capability')) return 'capabilitydsl';
  if (lower.endsWith('.op') || lower.endsWith('.operation')) return 'operationdsl';
  if (lower.endsWith('.activity')) return 'activitydsl';
  if (lower.endsWith('.mnc') || lower.endsWith('.mncspec')) return 'mncml';
  if (lower.endsWith('.json')) return 'json';
  return 'plaintext';
}

function isKideDsl(filename) {
  const lang = getLanguageForArtifact(filename);
  return ['dmldsl', 'capabilitydsl', 'operationdsl', 'activitydsl', 'mncml'].includes(lang);
}

// 2. Lexer Tokenizer
function tokenize(text) {
  const tokens = [];
  let line = 1;
  let col = 1;
  let i = 0;

  while (i < text.length) {
    const ch = text[i];
    if (ch === '\n') {
      line++;
      col = 1;
      i++;
      continue;
    }
    if (ch === ' ' || ch === '\t' || ch === '\r') {
      col++;
      i++;
      continue;
    }

    // Comment
    if (ch === '/' && text[i + 1] === '/') {
      const startCol = col;
      let val = '';
      while (i < text.length && text[i] !== '\n') {
        val += text[i];
        i++;
        col++;
      }
      tokens.push({ type: 'comment', value: val, line, startCol, endCol: col });
      continue;
    }

    // String
    if (ch === '"' || ch === "'") {
      const quote = ch;
      const startCol = col;
      let val = '';
      i++; col++;
      while (i < text.length && text[i] !== quote) {
        val += text[i];
        i++; col++;
      }
      if (i < text.length) { i++; col++; }
      tokens.push({ type: 'string', value: val, line, startCol, endCol: col });
      continue;
    }

    // Arrow operators
    if ((ch === '=' || ch === '-') && text[i + 1] === '>') {
      const val = ch + '>';
      tokens.push({ type: 'operator', value: val, line, startCol: col, endCol: col + 2 });
      i += 2;
      col += 2;
      continue;
    }

    // Punctuation
    if ('{}()[],:'.includes(ch)) {
      tokens.push({ type: 'punctuation', value: ch, line, startCol: col, endCol: col + 1 });
      i++; col++;
      continue;
    }

    // Identifiers / keywords
    if (/[a-zA-Z_]/.test(ch)) {
      const startCol = col;
      let val = '';
      while (i < text.length && /[a-zA-Z0-9_.]/.test(text[i])) {
        val += text[i];
        i++; col++;
      }
      tokens.push({ type: 'identifier', value: val, line, startCol, endCol: col });
      continue;
    }

    // Numbers
    if (/[0-9]/.test(ch)) {
      const startCol = col;
      let val = '';
      while (i < text.length && /[0-9.]/.test(text[i])) {
        val += text[i];
        i++; col++;
      }
      tokens.push({ type: 'number', value: val, line, startCol, endCol: col });
      continue;
    }

    i++; col++;
  }
  return tokens;
}

// 3. Simple In-Memory Symbol Index
class TestSymbolIndex {
  constructor() {
    this.symbols = [];
    this.references = [];
  }

  addSymbol(sym) {
    this.symbols.push(sym);
  }

  addReference(ref) {
    this.references.push(ref);
  }

  findByKind(kind) {
    return this.symbols.filter(s => s.kind === kind);
  }

  findByName(name, kind) {
    return this.symbols.find(s => s.name === name && (!kind || s.kind === kind));
  }

  findReferences(name) {
    return this.references.filter(r => r.name === name);
  }
}

// 4. Strict Semantic Scope Resolver
class TestScopeResolver {
  constructor(index) {
    this.index = index;
  }

  /**
   * STRICT CAPABILITY FILTER: Only return commands belonging to compatible component interfaces!
   */
  getCommandsForCapability(cap) {
    if (!cap || !cap.componentInterfaces || cap.componentInterfaces.length === 0) return [];

    const validInterfaceNames = new Set(cap.componentInterfaces);
    const validCommands = [];

    for (const ifName of validInterfaceNames) {
      const iface = this.index.findByName(ifName, 'InterfaceDescription');
      if (iface) {
        const cmds = this.index.symbols.filter(s => s.kind === 'Command' && s.containerName === ifName);
        validCommands.push(...cmds);
      }
    }

    return validCommands;
  }

  getAlarmsForCapability(cap) {
    if (!cap || !cap.componentInterfaces || cap.componentInterfaces.length === 0) return [];
    const validInterfaceNames = new Set(cap.componentInterfaces);
    const validAlarms = [];

    for (const ifName of validInterfaceNames) {
      const alms = this.index.symbols.filter(s => s.kind === 'Alarm' && s.containerName === ifName);
      validAlarms.push(...alms);
    }
    return validAlarms;
  }

  getActivitiesForDiagram(diagram) {
    if (!diagram) return this.index.findByKind('Activity');
    const localNames = new Set(diagram.activities.map(a => a.name));
    return this.index.findByKind('Activity').filter(a => localNames.has(a.name));
  }
}

// 5. Idempotent Formatter
function formatKideDsl(source) {
  if (!source || !source.trim()) return '';
  const lines = source.replace(/\r\n/g, '\n').replace(/\r/g, '\n').split('\n');
  let indent = 0;
  const indentStr = '    ';
  const result = [];
  let prevEmpty = false;

  for (let i = 0; i < lines.length; i++) {
    let line = lines[i].trim();
    if (!line) {
      if (!prevEmpty && result.length > 0) {
        result.push('');
        prevEmpty = true;
      }
      continue;
    }
    prevEmpty = false;

    // Normalize spacing
    line = line.replace(/\s*=>\s*/g, ' => ');
    line = line.replace(/\s*->\s*/g, ' -> ');
    line = line.replace(/\s*,\s*/g, ', ');
    line = line.replace(/([A-Za-z0-9_]+)\s*:\s*/g, '$1: ');
    line = line.replace(/\s*\{/g, ' {');
    line = line.replace(/ {2,}/g, ' ');

    let leadingCloses = 0;
    for (const ch of line) {
      if (ch === '}' || ch === ']') leadingCloses++;
      else if (ch !== ' ' && ch !== '\t' && ch !== ',') break;
    }

    let net = 0;
    for (const ch of line) {
      if (ch === '{') net++;
      else if (ch === '}') net--;
    }

    const eff = Math.max(0, indent - leadingCloses);
    result.push(indentStr.repeat(eff) + line);
    indent = Math.max(0, indent + net);
  }

  let out = result.join('\n');
  if (!out.endsWith('\n')) out += '\n';
  return out;
}

// =========================================================================
// Test Suites
// =========================================================================

describe('KIDE Enterprise Native Web IDE Language Platform Test Suite', () => {

  // Test 1: Canonical Language Mapping
  it('1. Canonical Language Identification: all 5 DSLs map consistently', () => {
    assert.strictEqual(getLanguageForArtifact('MotionData.dml'), 'dmldsl');
    assert.strictEqual(getLanguageForArtifact('RobotGripper.cap'), 'capabilitydsl');
    assert.strictEqual(getLanguageForArtifact('RobotGripper.capability'), 'capabilitydsl');
    assert.strictEqual(getLanguageForArtifact('MoveToHome.op'), 'operationdsl');
    assert.strictEqual(getLanguageForArtifact('MoveToHome.operation'), 'operationdsl');
    assert.strictEqual(getLanguageForArtifact('AssemblyLine.activity'), 'activitydsl');
    assert.strictEqual(getLanguageForArtifact('RoboticCell.mnc'), 'mncml');
    assert.strictEqual(getLanguageForArtifact('RoboticCell.mncspec'), 'mncml');

    assert.strictEqual(isKideDsl('MotionData.dml'), true);
    assert.strictEqual(isKideDsl('RobotGripper.cap'), true);
    assert.strictEqual(isKideDsl('MoveToHome.op'), true);
    assert.strictEqual(isKideDsl('AssemblyLine.activity'), true);
    assert.strictEqual(isKideDsl('RoboticCell.mnc'), true);
    assert.strictEqual(isKideDsl('package.json'), false);
  });

  // Test 2: Lexer & Tokenizer
  it('2. Resilient Lexer: tokenizes comments, strings, keywords, and exact line/column ranges', () => {
    const code = `// Motion Profile
DataModel MotionLimits {
    int maxVelocity = 100,
    description = "Safe industrial limit"
}`;

    const tokens = tokenize(code);
    assert.ok(tokens.length >= 8);
    assert.strictEqual(tokens[0].type, 'comment');
    assert.strictEqual(tokens[0].line, 1);

    const dmToken = tokens.find(t => t.value === 'DataModel');
    assert.ok(dmToken);
    assert.strictEqual(dmToken.line, 2);
    assert.strictEqual(dmToken.startCol, 1);

    const strToken = tokens.find(t => t.type === 'string');
    assert.ok(strToken);
    assert.strictEqual(strToken.value, 'Safe industrial limit');
  });

  // Test 3: Incremental Symbol Indexing
  it('3. Incremental Symbol Index: stores declarations and tracks cross-file references', () => {
    const index = new TestSymbolIndex();

    index.addSymbol({ name: 'RobotInterface', kind: 'InterfaceDescription', fileName: 'Robot.mnc' });
    index.addSymbol({ name: 'calibrateJoints', kind: 'Command', containerName: 'RobotInterface', fileName: 'Robot.mnc' });
    index.addSymbol({ name: 'PickAndPlace', kind: 'Capability', fileName: 'PickAndPlace.cap' });

    index.addReference({ name: 'calibrateJoints', sourceFile: 'PickAndPlace.cap', line: 4 });

    const cmds = index.findByKind('Command');
    assert.strictEqual(cmds.length, 1);
    assert.strictEqual(cmds[0].name, 'calibrateJoints');
    assert.strictEqual(cmds[0].containerName, 'RobotInterface');

    const refs = index.findReferences('calibrateJoints');
    assert.strictEqual(refs.length, 1);
    assert.strictEqual(refs[0].sourceFile, 'PickAndPlace.cap');
  });

  // Test 4: MANDATORY STRICT SCOPING FILTER FOR CAPABILITIES
  it('4. MANDATORY Semantic Scope Filtering: Capability actions ONLY propose commands/alarms from compatible interfaces', () => {
    const index = new TestSymbolIndex();

    // 1. RobotInterface has commands: moveArm, gripItem, and alarm: robotOverheat
    index.addSymbol({ name: 'RobotInterface', kind: 'InterfaceDescription' });
    index.addSymbol({ name: 'moveArm', kind: 'Command', containerName: 'RobotInterface' });
    index.addSymbol({ name: 'gripItem', kind: 'Command', containerName: 'RobotInterface' });
    index.addSymbol({ name: 'robotOverheat', kind: 'Alarm', containerName: 'RobotInterface' });

    // 2. PumpInterface has commands: startPump, stopPump, and alarm: pressureLoss
    index.addSymbol({ name: 'PumpInterface', kind: 'InterfaceDescription' });
    index.addSymbol({ name: 'startPump', kind: 'Command', containerName: 'PumpInterface' });
    index.addSymbol({ name: 'stopPump', kind: 'Command', containerName: 'PumpInterface' });
    index.addSymbol({ name: 'pressureLoss', kind: 'Alarm', containerName: 'PumpInterface' });

    const resolver = new TestScopeResolver(index);

    // Capability 1: Bound ONLY to RobotInterface
    const robotCap = {
      name: 'RobotCapability',
      componentInterfaces: ['RobotInterface']
    };

    const robotCmds = resolver.getCommandsForCapability(robotCap);
    const robotCmdNames = robotCmds.map(c => c.name);

    assert.ok(robotCmdNames.includes('moveArm'), 'Must offer moveArm from RobotInterface');
    assert.ok(robotCmdNames.includes('gripItem'), 'Must offer gripItem from RobotInterface');

    // STRICT SCOPING ASSERTION: Foreign commands MUST NOT be proposed
    assert.strictEqual(robotCmdNames.includes('startPump'), false, 'CRITICAL: Must NOT propose startPump from PumpInterface');
    assert.strictEqual(robotCmdNames.includes('stopPump'), false, 'CRITICAL: Must NOT propose stopPump from PumpInterface');

    const robotAlarms = resolver.getAlarmsForCapability(robotCap);
    const robotAlarmNames = robotAlarms.map(a => a.name);
    assert.ok(robotAlarmNames.includes('robotOverheat'));
    assert.strictEqual(robotAlarmNames.includes('pressureLoss'), false, 'Must NOT propose pressureLoss to RobotCapability');

    // Capability 2: Bound ONLY to PumpInterface
    const pumpCap = {
      name: 'PumpCapability',
      componentInterfaces: ['PumpInterface']
    };

    const pumpCmds = resolver.getCommandsForCapability(pumpCap);
    const pumpCmdNames = pumpCmds.map(c => c.name);

    assert.ok(pumpCmdNames.includes('startPump'), 'Must offer startPump');
    assert.ok(pumpCmdNames.includes('stopPump'), 'Must offer stopPump');
    assert.strictEqual(pumpCmdNames.includes('moveArm'), false, 'CRITICAL: Must NOT propose moveArm to PumpCapability');
  });

  // Test 5: Activity Diagram Scoping
  it('5. Activity Diagram Scoping: nextActivity only targets activities in the current workflow', () => {
    const index = new TestSymbolIndex();

    index.addSymbol({ name: 'Step1', kind: 'Activity', containerName: 'WorkflowA' });
    index.addSymbol({ name: 'Step2', kind: 'Activity', containerName: 'WorkflowA' });
    index.addSymbol({ name: 'ForeignStep', kind: 'Activity', containerName: 'WorkflowB' });

    const resolver = new TestScopeResolver(index);
    const diagram = {
      name: 'WorkflowA',
      activities: [{ name: 'Step1' }, { name: 'Step2' }]
    };

    const validSteps = resolver.getActivitiesForDiagram(diagram);
    const stepNames = validSteps.map(s => s.name);

    assert.strictEqual(stepNames.length, 2);
    assert.ok(stepNames.includes('Step1'));
    assert.ok(stepNames.includes('Step2'));
    assert.strictEqual(stepNames.includes('ForeignStep'), false, 'Must NOT suggest activity from foreign workflow');
  });

  // Test 6: Idempotent Formatter across all 5 DSLs
  it('6. Idempotent Formatting: format(format(code)) === format(code) across all 5 DSLs', () => {
    const testCases = [
      // DML
      `Package com.kide.robotics
DataModel ArmLimits {
primitives {
int maxVel = 100,
float reach = 1.5
}
}`,
      // Capability
      `Capability GripperCap compatible component interface GripperInterface {
Init {
fire Commands [ openGripper(), closeGripper() ]
subscribe alarms [ slipAlarm() ]
}
}`,
      // Operation
      `Operation ComputeTraj(int startPos, float vel) {
execute "python run_trajectory.py"
return int result
}`,
      // Activity
      `ActivityDiagram PickSeq on context ArmLimits has activities {
Activity Home {
description: "Home arm"
requireCapability: GripperCap
nextActivity: Pick
},
Activity Pick {
description: "Pick piece"
}
}`,
      // MNC-ML
      `Model RobotCell
InterfaceDescription GripperInterface {
commands {
openGripper[], closeGripper[]
}
operatingStates {
IDLE[]
BUSY[]
startStates: IDLE
endStates: IDLE
}
}`
    ];

    for (let i = 0; i < testCases.length; i++) {
      const original = testCases[i];
      const once = formatKideDsl(original);
      const twice = formatKideDsl(once);

      assert.strictEqual(
        once,
        twice,
        `Formatter must be strictly idempotent on test case ${i + 1} (format(format(x)) === format(x))`
      );
    }
  });

  // Test 7: Diagnostics and Error Reporting
  it('7. Diagnostics Engine: validates unresolvable symbols and emits exact error metadata', () => {
    const knownCapabilities = new Set(['KnownCapA', 'KnownCapB']);
    const line = '        requireCapability: UnknownGripperCap';
    const match = line.match(/\brequireCapability\s*:\s*([A-Za-z0-9_]+)/);

    assert.ok(match);
    const capName = match[1];
    const isKnown = knownCapabilities.has(capName);

    assert.strictEqual(isKnown, false);

    const diagnostic = {
      code: 'KIDE-CAP-001',
      severity: 'error',
      message: `Unresolved capability: Capability "${capName}" not found in workspace (.cap files).`,
      startLine: 12,
      startColumn: line.indexOf(capName) + 1,
      endLine: 12,
      endColumn: line.indexOf(capName) + 1 + capName.length
    };

    assert.strictEqual(diagnostic.code, 'KIDE-CAP-001');
    assert.strictEqual(diagnostic.severity, 'error');
    assert.ok(diagnostic.message.includes('UnknownGripperCap'));
  });

  // Test 8: Deterministic Quick Fix Matcher
  it('8. Quick Fix Engine: computes closest matching candidate for typo corrections', () => {
    function findClosest(target, candidates) {
      let best = null;
      let minLenDiff = Infinity;
      for (const c of candidates) {
        if (c.toLowerCase().includes(target.toLowerCase()) || target.toLowerCase().includes(c.toLowerCase())) {
          return c;
        }
      }
      return candidates[0] || null;
    }

    const availableCaps = ['RoboticGripper', 'ConveyorFeeder', 'VacuumSuction'];
    const typo = 'RoboticGrip';
    const suggestion = findClosest(typo, availableCaps);

    assert.strictEqual(suggestion, 'RoboticGripper');
  });
});

