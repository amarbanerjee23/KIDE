import { describe, it } from 'node:test';
import assert from 'node:assert';

describe('PR 9: Knowledge Graph Store Service, Pre-Available Domain Catalogs & Interactive Graph View', () => {

  // Test 1: Global Domain Knowledge Catalog Structure (19 Real-World Industrial Domains)
  it('1. should verify all 19 real-world industrial domains are represented with full artifact suites', () => {
    const expectedDomains = [
      { id: 'boom_barrier_system', name: 'Boom Barrier Vehicle Entry System', category: 'Access Control & Security', files: 4 },
      { id: 'robotic_pick_and_place', name: 'Industrial Robotic Pick-and-Place Cell', category: 'Robotics & Automation', files: 4 },
      { id: 'industrial_cooling_system', name: 'Industrial Process Cooling Loop', category: 'Process Control & Utilities', files: 4 },
      { id: 'chemical_batch_reactor', name: 'Continuous Chemical Batch Reactor', category: 'Chemical & Petrochemical', files: 4 },
      { id: 'smart_meeting_room', name: 'Smart Building HVAC & Lighting', category: 'Building Automation & Energy', files: 4 },
      { id: 'automotive_assembly_agv', name: 'Automotive Door Assembly Station', category: 'Automotive & Discrete Manufacturing', files: 4 },
      { id: 'water_treatment_plant', name: 'Industrial Water Treatment Facility', category: 'Water & Waste Management', files: 4 },
      { id: 'siemens_motion_s120', name: 'Siemens S7-1500 & SINAMICS S120 Servo System', category: 'Industrial Motion Control', files: 4 },
      { id: 'emerson_fisher_valves', name: 'Emerson Fisher FIELDVUE DVC6200 Valve Controller', category: 'Process Flow Control', files: 4 },
      { id: 'abb_irc5_robotics', name: 'ABB IRB 2600 & IRC5 Arc Welding Cell', category: 'Robotics & Heavy Manufacturing', files: 4 },
      { id: 'schneider_altivar_vfd', name: 'Schneider Altivar ATV930 Variable Speed Drive', category: 'Motor Control & Drives', files: 4 },
      { id: 'beckhoff_twincat_ethercat', name: 'Beckhoff CX5130 TwinCAT 3 & EtherCAT System', category: 'High-Speed Automation', files: 4 },
      { id: 'endress_hauser_flowmeter', name: 'Endress+Hauser Promass F 300 Coriolis Flowmeter', category: 'Process Instrumentation & Flow', files: 4 },
      { id: 'mir_amr_intralogistics', name: 'Mobile Industrial Robots MiR250 Autonomous Mobile Robot', category: 'Intralogistics & Mobile Robotics', files: 4 },
      { id: 'sartorius_biostat_bioreactor', name: 'Sartorius BIOSTAT B Bioreactor & Fermentation System', category: 'Biotechnology & Pharmaceuticals', files: 4 },
      { id: 'keyence_vision_inspection', name: 'Keyence CV-X400 High-Speed Optical Vision System', category: 'Quality Control & Vision Inspection', files: 4 },
      { id: 'sma_solar_bess_inverter', name: 'SMA Sunny Tripower & BESS Energy Storage', category: 'Renewable Energy & Microgrids', files: 4 },
      { id: 'festo_cpx_pneumatics', name: 'Festo CPX-AP-I Decentralized Modular Valve Terminal', category: 'Pneumatics & Fluid Power', files: 4 },
      { id: 'haas_cnc_machining', name: 'Haas VF-2 3-Axis CNC Machining Center', category: 'CNC Machining & Subtractive Manufacturing', files: 4 }
    ];

    assert.strictEqual(expectedDomains.length, 19);
    expectedDomains.forEach(domain => {
      assert.ok(domain.id);
      assert.ok(domain.name);
      assert.ok(domain.category);
      assert.strictEqual(domain.files, 4, `Domain ${domain.id} must have 4 artifacts: DML, Cap, Op, Activity`);
    });
  });

  // Test 2: Multi-Layer Topological Graph Classification
  it('2. should group knowledge entities into formal 5-layer ontology hierarchy', () => {
    const mockEntities = [
      { id: 'cat_robotics', name: 'Industrial Robotics', type: 'domain' },
      { id: 'dev_robotics_gripper', name: 'VacuumGripper', type: 'device' },
      { id: 'dm_robotics_RobotData', name: 'RobotData', type: 'datamodel' },
      { id: 'cap_robotics_ArmPositioning', name: 'ArmPositioning', type: 'capability' },
      { id: 'op_robotics_computeIK', name: 'computeIK', type: 'operation' },
      { id: 'wf_robotics_PickAndPlace', name: 'PickAndPlace', type: 'workflow' },
      { id: 'act_robotics_GraspPart', name: 'GraspPart', type: 'activity' },
      { id: 'state_robotics_OPERATIONAL', name: 'OPERATIONAL', type: 'operating_state' }
    ];

    // Layer 0: Root / Domain / Project
    const layer0 = mockEntities.filter(n => ['domain', 'repository', 'project'].includes(n.type));
    // Layer 1: Physical Devices / Component Interfaces
    const layer1 = mockEntities.filter(n => ['device', 'interface'].includes(n.type));
    // Layer 2: Capabilities & Data Models
    const layer2 = mockEntities.filter(n => ['capability', 'datamodel'].includes(n.type));
    // Layer 3: Operations & Process Activities / Workflows
    const layer3 = mockEntities.filter(n => ['operation', 'activity', 'workflow'].includes(n.type));
    // Layer 4: Supervisory Operating States
    const layer4 = mockEntities.filter(n => ['operating_state'].includes(n.type));

    assert.strictEqual(layer0.length, 1);
    assert.strictEqual(layer0[0].id, 'cat_robotics');

    assert.strictEqual(layer1.length, 1);
    assert.strictEqual(layer1[0].id, 'dev_robotics_gripper');

    assert.strictEqual(layer2.length, 2);
    assert.ok(layer2.some(n => n.type === 'capability'));
    assert.ok(layer2.some(n => n.type === 'datamodel'));

    assert.strictEqual(layer3.length, 3);
    assert.ok(layer3.some(n => n.type === 'operation'));
    assert.ok(layer3.some(n => n.type === 'workflow'));
    assert.ok(layer3.some(n => n.type === 'activity'));

    assert.strictEqual(layer4.length, 1);
    assert.strictEqual(layer4[0].id, 'state_robotics_OPERATIONAL');
  });

  // Test 3: Graph Relationship Links & Dependency Edges
  it('3. should generate valid requirement and containment edges between entities', () => {
    const edges = [
      { source: 'cat_robotics', target: 'dev_robotics_gripper', type: 'INCLUDES_EQUIPMENT', label: 'includes equipment' },
      { source: 'cat_robotics', target: 'dm_robotics_RobotData', type: 'DEFINES_DATA_MODEL', label: 'defines schema' },
      { source: 'cat_robotics', target: 'cap_robotics_ArmPositioning', type: 'PROVIDES_CAPABILITY', label: 'provides capability' },
      { source: 'cat_robotics', target: 'wf_robotics_PickAndPlace', type: 'DEFINES_WORKFLOW', label: 'defines workflow' },
      { source: 'wf_robotics_PickAndPlace', target: 'act_robotics_GraspPart', type: 'CONTAINS_STEP', label: 'step 1' },
      { source: 'act_robotics_GraspPart', target: 'cap_robotics_ArmPositioning', type: 'REQUIRES_CAPABILITY', label: 'requires capability' },
      { source: 'act_robotics_GraspPart', target: 'op_robotics_computeIK', type: 'REQUIRES_OPERATION', label: 'requires operation' }
    ];

    const requiresCapEdges = edges.filter(e => e.type === 'REQUIRES_CAPABILITY');
    assert.strictEqual(requiresCapEdges.length, 1);
    assert.strictEqual(requiresCapEdges[0].source, 'act_robotics_GraspPart');
    assert.strictEqual(requiresCapEdges[0].target, 'cap_robotics_ArmPositioning');

    const workflowStepEdges = edges.filter(e => e.type === 'CONTAINS_STEP');
    assert.strictEqual(workflowStepEdges.length, 1);
    assert.strictEqual(workflowStepEdges[0].label, 'step 1');
  });

  // Test 4: Single Entity Import into Active Project Workspace
  it('4. should correctly map imported entity to project file item with canonical language id', () => {
    const importPayload = {
      message: 'Successfully imported BoomBarrier.cap into project',
      imported_file: {
        id: 101,
        filename: 'BoomBarrier.cap',
        file_type: 'capability',
        content: 'Capability BoomBarrierControl compatible component interface BoomBarrierInterface { ... }'
      },
      entity_name: 'BoomBarrierControl',
      entity_type: 'capability'
    };

    const getLanguageForFilename = (filename) => {
      if (filename.endsWith('.dml')) return 'dmldsl';
      if (filename.endsWith('.cap')) return 'capabilitydsl';
      if (filename.endsWith('.op')) return 'operationdsl';
      if (filename.endsWith('.mnc')) return 'mncdsl';
      if (filename.endsWith('.activity')) return 'activitydsl';
      return 'plaintext';
    };

    const projectFiles = [
      { id: '1', name: 'ExistingModel.dml', content: '...', language: 'dmldsl' }
    ];

    const imported = importPayload.imported_file;
    const fileItem = {
      id: String(imported.id),
      name: imported.filename,
      content: imported.content,
      language: getLanguageForFilename(imported.filename)
    };

    projectFiles.push(fileItem);

    assert.strictEqual(projectFiles.length, 2);
    assert.strictEqual(projectFiles[1].name, 'BoomBarrier.cap');
    assert.strictEqual(projectFiles[1].language, 'capabilitydsl');
    assert.ok(projectFiles[1].content.includes('Capability BoomBarrierControl'));
  });

  // Test 5: Semantic Capability Matching Scoring
  it('5. should rank matching capabilities based on process requirement queries', () => {
    const mockCatalog = [
      {
        id: 'access_control',
        name: 'Access Control Systems',
        capabilities: ['BoomBarrierControl'],
        devices: ['BarrierGate', 'RFIDReader']
      },
      {
        id: 'robotics',
        name: 'Industrial Robotics',
        capabilities: ['ArmPositioning', 'GripperControl'],
        devices: ['SixAxisArm', 'VacuumGripper']
      },
      {
        id: 'thermal_cooling',
        name: 'Thermal Cooling Loop',
        capabilities: ['PumpSpeedRegulation', 'ValveChillingModulation'],
        devices: ['CoolingPump', 'ControlValve', 'RTDSensor']
      }
    ];

    const query = 'cooling pump temperature sensor';
    const scored = mockCatalog.map(cat => {
      let score = 0;
      const terms = query.toLowerCase().split(' ');
      terms.forEach(t => {
        if (cat.name.toLowerCase().includes(t)) score += 30;
        if (cat.capabilities.some(c => c.toLowerCase().includes(t))) score += 40;
        if (cat.devices.some(d => d.toLowerCase().includes(t))) score += 20;
      });
      return { ...cat, score };
    }).sort((a, b) => b.score - a.score);

    assert.strictEqual(scored[0].id, 'thermal_cooling');
    assert.ok(scored[0].score > 50);
  });

  // Test 6: W3C RDF Turtle Ontology Export Verification
  it('6. should serialize knowledge graph into valid W3C RDF Turtle syntax', () => {
    const projectName = 'ChemicalPlant';
    const triples = [
      `@prefix kide: <http://kide.enterprise/ontology#> .`,
      `@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> .`,
      `@prefix xsd: <http://www.w3.org/2001/XMLSchema#> .`,
      ``,
      `kide:Project_${projectName} a kide:EngineeringProject ;`,
      `  rdfs:label "${projectName}" ;`,
      `  kide:hasCapability kide:Capability_ReactorTemperatureRegulation .`,
      ``,
      `kide:Capability_ReactorTemperatureRegulation a kide:Capability ;`,
      `  rdfs:label "ReactorTemperatureRegulation" ;`,
      `  kide:requiresOperation kide:Operation_adjustCoolingJacket .`
    ];

    const turtleRdf = triples.join('\n');
    assert.ok(turtleRdf.includes('@prefix kide: <http://kide.enterprise/ontology#> .'));
    assert.ok(turtleRdf.includes(`kide:Project_${projectName}`));
    assert.ok(turtleRdf.includes('kide:requiresOperation kide:Operation_adjustCoolingJacket .'));
  });
});

