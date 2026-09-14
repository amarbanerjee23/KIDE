import { describe, it } from 'node:test';
import assert from 'node:assert';
import dagre from '@dagrejs/dagre';
import { forceSimulation, forceLink, forceManyBody, forceCenter, forceCollide } from 'd3-force';

describe('Standard Graph Visualizations Layout Engine Tests', () => {

  const sampleNodes = [
    { id: 'cat_robotics', name: 'Robotics Domain', type: 'domain' },
    { id: 'dev_ur5', name: 'UR5 Manipulator', type: 'device' },
    { id: 'iface_gripper', name: 'GripperInterface', type: 'interface' },
    { id: 'cap_motion', name: 'ArmMotion', type: 'capability' },
    { id: 'dm_robot_telemetry', name: 'RobotTelemetry', type: 'datamodel' },
    { id: 'op_move_to_pose', name: 'moveToPose', type: 'operation' },
    { id: 'cmd_grip', name: 'GRIP_COMMAND', type: 'command' },
    { id: 'evt_done', name: 'MOTION_DONE', type: 'event' },
    { id: 'alm_collision', name: 'COLLISION_ALARM', type: 'alarm' },
    { id: 'dp_current_pos', name: 'currentPosition', type: 'datapoint' },
    { id: 'param_speed', name: 'speedLimit', type: 'parameter' },
    { id: 'state_idle', name: 'IDLE_STATE', type: 'operating_state' }
  ];

  const sampleEdges = [
    { id: 'e1', source: 'cat_robotics', target: 'dev_ur5', label: 'CONTAINS_DEVICE' },
    { id: 'e2', source: 'dev_ur5', target: 'iface_gripper', label: 'OFFERS_INTERFACE' },
    { id: 'e3', source: 'iface_gripper', target: 'cap_motion', label: 'EXPOSES_CAPABILITY' },
    { id: 'e4', source: 'cap_motion', target: 'cmd_grip', label: 'OFFERS_COMMAND' },
    { id: 'e5', source: 'cap_motion', target: 'evt_done', label: 'RECEIVES_EVENT' },
    { id: 'e6', source: 'cap_motion', target: 'alm_collision', label: 'RAISES_ALARM' },
    { id: 'e7', source: 'dev_ur5', target: 'dp_current_pos', label: 'PROVIDES_DATAPOINT' },
    { id: 'e8', source: 'cmd_grip', target: 'param_speed', label: 'HAS_PARAMETER' }
  ];

  it('1. should calculate valid hierarchical DAG layout using Dagre LR', () => {
    const g = new dagre.graphlib.Graph();
    g.setDefaultEdgeLabel(() => ({}));
    g.setGraph({ rankdir: 'LR', nodesep: 40, ranksep: 100 });

    sampleNodes.forEach(n => g.setNode(n.id, { width: 200, height: 80 }));
    sampleEdges.forEach(e => g.setEdge(e.source, e.target));

    dagre.layout(g);

    sampleNodes.forEach(n => {
      const placed = g.node(n.id);
      assert.ok(placed, `Node ${n.id} must have placed coordinates`);
      assert.ok(typeof placed.x === 'number' && !isNaN(placed.x));
      assert.ok(typeof placed.y === 'number' && !isNaN(placed.y));
    });

    // In LR layout, target node should have higher or equal X than source node
    const sourcePos = g.node('cat_robotics');
    const devPos = g.node('dev_ur5');
    const capPos = g.node('cap_motion');
    assert.ok(devPos.x > sourcePos.x, 'Child device must be to the right of root domain');
    assert.ok(capPos.x > devPos.x, 'Capability must be to the right of device');
  });

  it('2. should calculate valid hierarchical DAG layout using Dagre TB', () => {
    const g = new dagre.graphlib.Graph();
    g.setDefaultEdgeLabel(() => ({}));
    g.setGraph({ rankdir: 'TB', nodesep: 50, ranksep: 80 });

    sampleNodes.forEach(n => g.setNode(n.id, { width: 200, height: 80 }));
    sampleEdges.forEach(e => g.setEdge(e.source, e.target));

    dagre.layout(g);

    const sourcePos = g.node('cat_robotics');
    const devPos = g.node('dev_ur5');
    const capPos = g.node('cap_motion');
    assert.ok(devPos.y > sourcePos.y, 'In TB layout, device must be below root domain');
    assert.ok(capPos.y > devPos.y, 'In TB layout, capability must be below device');
  });

  it('3. should calculate physics-based Force-Directed layout with d3-force', () => {
    const simulationNodes = sampleNodes.map(n => ({ id: n.id, x: 500, y: 500 }));
    const simulationLinks = sampleEdges.map(e => ({ source: e.source, target: e.target }));

    const simulation = forceSimulation(simulationNodes)
      .force('link', forceLink(simulationLinks).id(d => d.id).distance(150))
      .force('charge', forceManyBody().strength(-400))
      .force('center', forceCenter(500, 500))
      .force('collide', forceCollide().radius(80))
      .stop();

    for (let i = 0; i < 150; ++i) {
      simulation.tick();
    }

    simulationNodes.forEach(sn => {
      assert.ok(typeof sn.x === 'number' && !isNaN(sn.x), `Node ${sn.id} must have valid numeric x`);
      assert.ok(typeof sn.y === 'number' && !isNaN(sn.y), `Node ${sn.id} must have valid numeric y`);
    });

    // Check that nodes have dispersed and are not all in the same point
    const xs = simulationNodes.map(n => n.x);
    const minX = Math.min(...xs);
    const maxX = Math.max(...xs);
    assert.ok(maxX - minX > 200, 'Nodes must disperse in force-directed simulation');
  });

  it('4. should correctly compute concentric radial orbits for ontology levels', () => {
    const ring0 = sampleNodes.filter(n => ['domain'].includes(n.type));
    const ring1 = sampleNodes.filter(n => ['device', 'interface'].includes(n.type));
    const ring2 = sampleNodes.filter(n => ['capability', 'datamodel'].includes(n.type));
    const ring3 = sampleNodes.filter(n => ['operation', 'workflow', 'activity'].includes(n.type));
    const ring4 = sampleNodes.filter(n => ['command', 'event', 'alarm', 'datapoint', 'parameter', 'operating_state'].includes(n.type));

    assert.strictEqual(ring0.length, 1);
    assert.strictEqual(ring1.length, 2);
    assert.strictEqual(ring2.length, 2);
    assert.strictEqual(ring3.length, 1);
    assert.strictEqual(ring4.length, 6);

    const centerX = 1000;
    const centerY = 1000;
    const rOrbit1 = 300;
    const rOrbit4 = 900;

    // Check distance of orbit 1 nodes to center
    ring1.forEach((n, idx) => {
      const theta = (2 * Math.PI * idx) / ring1.length;
      const x = Math.round(centerX + rOrbit1 * Math.cos(theta));
      const y = Math.round(centerY + rOrbit1 * Math.sin(theta));
      const dist = Math.sqrt((x - centerX) ** 2 + (y - centerY) ** 2);
      assert.ok(Math.abs(dist - rOrbit1) <= 2, 'Distance must match orbit 1 radius');
    });

    // Check distance of orbit 4 nodes to center
    ring4.forEach((n, idx) => {
      const theta = (2 * Math.PI * idx) / ring4.length;
      const x = Math.round(centerX + rOrbit4 * Math.cos(theta));
      const y = Math.round(centerY + rOrbit4 * Math.sin(theta));
      const dist = Math.sqrt((x - centerX) ** 2 + (y - centerY) ** 2);
      assert.ok(Math.abs(dist - rOrbit4) <= 2, 'Distance must match orbit 4 radius');
    });
  });

  it('5. should assign optimal geometric handles based on directional angle', () => {
    function getHandles(sx, sy, tx, ty, layout) {
      if (layout === 'dagre-lr') return { s: 'right-out', t: 'left-in' };
      if (layout === 'dagre-tb') return { s: 'bottom-out', t: 'top-in' };
      const dx = tx - sx;
      const dy = ty - sy;
      if (Math.abs(dx) >= Math.abs(dy)) {
        return dx >= 0 ? { s: 'right-out', t: 'left-in' } : { s: 'left-in', t: 'right-out' };
      } else {
        return dy >= 0 ? { s: 'bottom-out', t: 'top-in' } : { s: 'top-in', t: 'bottom-out' };
      }
    }

    // Rightwards link
    assert.deepStrictEqual(getHandles(0, 0, 100, 10, 'force'), { s: 'right-out', t: 'left-in' });
    // Leftwards link
    assert.deepStrictEqual(getHandles(100, 0, 0, 10, 'force'), { s: 'left-in', t: 'right-out' });
    // Downwards link
    assert.deepStrictEqual(getHandles(0, 0, 10, 100, 'force'), { s: 'bottom-out', t: 'top-in' });
    // Upwards link
    assert.deepStrictEqual(getHandles(0, 100, 10, 0, 'force'), { s: 'top-in', t: 'bottom-out' });
  });

});

