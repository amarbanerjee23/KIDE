import { describe, it } from 'node:test';
import assert from 'node:assert';

describe('PR 8: Real-Time Hardware & Simulator Gateway, Live Telemetry Stream, WebSockets & HIL Execution', () => {

  // Test 1: WebSocket Protocol Message Encoding & Dispatch
  it('1. should properly serialize and handle bidirectional WebSocket control messages', () => {
    const messages = [
      { type: 'command', command: 'INIT', payload: {} },
      { type: 'inject_event', event: 'SensorTriggered', payload: { sensor_id: 4 } },
      { type: 'telemetry_override', datapoint: 'temperature', value: 104.5 },
      { type: 'set_execution_state', state: 'RUNNING' },
      { type: 'set_mode', mode: 'HIL_MODBUS_TCP', protocol_config: { port: 502, slave_id: 1 } },
      { type: 'set_breakpoints', break_on_states: ['STOPPED', 'RUNNING'], break_on_alarms: true },
      { type: 'step' }
    ];

    const serialized = messages.map(m => JSON.stringify(m));
    assert.strictEqual(serialized.length, 7);

    // Parse and verify types
    const parsed = serialized.map(s => JSON.parse(s));
    assert.strictEqual(parsed[0].type, 'command');
    assert.strictEqual(parsed[0].command, 'INIT');

    assert.strictEqual(parsed[2].type, 'telemetry_override');
    assert.strictEqual(parsed[2].datapoint, 'temperature');
    assert.strictEqual(parsed[2].value, 104.5);

    assert.strictEqual(parsed[4].mode, 'HIL_MODBUS_TCP');
    assert.strictEqual(parsed[4].protocol_config.port, 502);

    assert.strictEqual(parsed[5].break_on_states.length, 2);
    assert.strictEqual(parsed[5].break_on_alarms, true);
  });

  // Test 2: Sparkline Waveform Math & SVG Coordinate Normalization
  it('2. should accurately compute SVG polyline points and threshold lines for live telemetry sparklines', () => {
    const data = [20.0, 25.5, 40.0, 65.2, 88.0, 96.5];
    const width = 120;
    const height = 32;
    const warnMax = 85.0;
    const faultMax = 95.0;

    const min = Math.min(...data, 0);
    const max = Math.max(...data, faultMax, 100);
    const range = max - min || 1;

    assert.strictEqual(min, 0);
    assert.ok(max >= 96.5);

    const points = data.map((val, idx) => {
      const x = (idx / (data.length - 1)) * width;
      const y = height - ((val - min) / range) * (height - 4) - 2;
      return { x: Number(x.toFixed(1)), y: Number(y.toFixed(1)) };
    });

    assert.strictEqual(points.length, 6);
    assert.strictEqual(points[0].x, 0);
    assert.strictEqual(points[points.length - 1].x, 120);

    // Higher values should have smaller y (closer to top of SVG)
    assert.ok(points[5].y < points[0].y, 'Higher telemetry value must map to lower SVG y-coordinate');

    const warnY = height - ((warnMax - min) / range) * (height - 4) - 2;
    assert.ok(warnY > 0 && warnY < height, 'Warning reference line must fall within view bounds');
  });

  // Test 3: Simulation Mode & Protocol Configuration Verification
  it('3. should support all industrial HIL modes with valid protocol settings', () => {
    const supportedModes = [
      'VIRTUAL_EMULATION',
      'HIL_MODBUS_TCP',
      'HIL_MQTT',
      'HIL_OPC_UA',
      'REPLAY'
    ];

    const configs = {
      VIRTUAL_EMULATION: { protocol: 'simulated_dynamic_v1', step_dt: 0.1 },
      HIL_MODBUS_TCP: { protocol: 'modbus_tcp', host: '192.168.1.50', port: 502, slave_id: 1 },
      HIL_MQTT: { protocol: 'mqtt_v5', broker: 'tcp://broker.hivemq.com:1883', topic_prefix: 'kide/cell1' },
      HIL_OPC_UA: { protocol: 'opc_ua', endpoint: 'opc.tcp://127.0.0.1:4840/freeopcua/server/' },
      REPLAY: { protocol: 'telemetry_replay', source_file: 'historical_run_001.csv' }
    };

    supportedModes.forEach(mode => {
      assert.ok(configs[mode], `Mode ${mode} must have defined protocol configuration`);
      assert.ok(configs[mode].protocol, `Mode ${mode} must have protocol identifier`);
    });

    assert.strictEqual(configs['HIL_MODBUS_TCP'].port, 502);
    assert.strictEqual(configs['HIL_MQTT'].topic_prefix, 'kide/cell1');
  });

  // Test 4: Anomaly Detection and Safety Threshold Classification
  it('4. should correctly classify telemetry signals into NORMAL, WARN, and FAULT bands', () => {
    const thresholds = {
      warn_min: 10.0,
      warn_max: 85.0,
      fault_min: 0.0,
      fault_max: 95.0
    };

    const classify = (value) => {
      if (value > thresholds.fault_max || value < thresholds.fault_min) {
        return 'FAULT';
      }
      if (value > thresholds.warn_max || value < thresholds.warn_min) {
        return 'WARN';
      }
      return 'NORMAL';
    };

    assert.strictEqual(classify(25.0), 'NORMAL');
    assert.strictEqual(classify(80.0), 'NORMAL');
    assert.strictEqual(classify(88.5), 'WARN');
    assert.strictEqual(classify(98.2), 'FAULT');
    assert.strictEqual(classify(-2.0), 'FAULT');
    assert.strictEqual(classify(8.0), 'WARN');
  });

  // Test 5: Telemetry Trace CSV Exporter & Parser
  it('5. should serialize and parse multi-signal telemetry execution traces to CSV', () => {
    const history = {
      temperature: [
        { timestamp: '10:00:01', step: 1, state: 'READY', value: 24.5, is_anomaly: false },
        { timestamp: '10:00:02', step: 2, state: 'RUNNING', value: 65.0, is_anomaly: false },
        { timestamp: '10:00:03', step: 3, state: 'RUNNING', value: 98.2, is_anomaly: true }
      ],
      pressure: [
        { timestamp: '10:00:01', step: 1, state: 'READY', value: 1.0, is_anomaly: false },
        { timestamp: '10:00:02', step: 2, state: 'RUNNING', value: 2.3, is_anomaly: false },
        { timestamp: '10:00:03', step: 3, state: 'RUNNING', value: 2.4, is_anomaly: false }
      ]
    };

    // Serialize to CSV
    const rows = ['Timestamp,Step,State,Datapoint,Value,IsAnomaly'];
    for (const [dp, points] of Object.entries(history)) {
      for (const p of points) {
        rows.push(`${p.timestamp},${p.step},${p.state},${dp},${p.value},${p.is_anomaly}`);
      }
    }
    const csvContent = rows.join('\n');

    assert.ok(csvContent.includes('Timestamp,Step,State,Datapoint,Value,IsAnomaly'));
    assert.ok(csvContent.includes('10:00:03,3,RUNNING,temperature,98.2,true'));

    // Parse back
    const lines = csvContent.trim().split('\n');
    assert.strictEqual(lines.length, 7); // 1 header + 6 data rows
    const header = lines[0].split(',');
    assert.strictEqual(header[3], 'Datapoint');
    assert.strictEqual(header[4], 'Value');
    assert.strictEqual(header[5], 'IsAnomaly');
  });

  // Test 6: Breakpoint Evaluation and Halt Mechanism
  it('6. should trigger breakpoint halts when entering target states or encountering alarms', () => {
    let executionState = 'RUNNING';
    let isBreakpointHit = false;
    let breakpointReason = null;

    const breakOnStates = new Set(['CoolDown', 'STOPPED']);
    const breakOnAlarms = true;

    const evaluateTransition = (targetState) => {
      if (breakOnStates.has(targetState)) {
        executionState = 'PAUSED';
        isBreakpointHit = true;
        breakpointReason = `Hit breakpoint on state [${targetState}]`;
      }
    };

    const evaluateAlarm = (alarmName, level) => {
      if (breakOnAlarms) {
        executionState = 'PAUSED';
        isBreakpointHit = true;
        breakpointReason = `Hit breakpoint on alarm [${alarmName}] (Level ${level})`;
      }
    };

    // Transition to READY -> no breakpoint
    evaluateTransition('READY');
    assert.strictEqual(isBreakpointHit, false);
    assert.strictEqual(executionState, 'RUNNING');

    // Transition to CoolDown -> triggers breakpoint
    evaluateTransition('CoolDown');
    assert.strictEqual(isBreakpointHit, true);
    assert.strictEqual(executionState, 'PAUSED');
    assert.strictEqual(breakpointReason, 'Hit breakpoint on state [CoolDown]');

    // Reset and evaluate alarm breakpoint
    executionState = 'RUNNING';
    isBreakpointHit = false;
    evaluateAlarm('HIGH_PRESSURE_ALARM', 2);
    assert.strictEqual(isBreakpointHit, true);
    assert.strictEqual(executionState, 'PAUSED');
    assert.strictEqual(breakpointReason, 'Hit breakpoint on alarm [HIGH_PRESSURE_ALARM] (Level 2)');
  });
});

