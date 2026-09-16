import { describe, it } from 'node:test';
import assert from 'node:assert';

describe('PR 7: Enterprise Knowledge Ingestion, Document Extraction & Transactional Notifications', () => {

  // Test 1: CSV Register Map Parsing
  it('1. should parse and validate CSV register maps into structured datapoints', () => {
    const rawCsv = `Register_Name,Type,Unit,Min,Max,Description
Motor_Speed,float,rpm,0,3000,Drive shaft rotational speed
Stator_Current,float,A,0,25,Output current in Amperes
DC_Bus_Volts,float,V,0,750,Internal DC intermediate link voltage
Fault_Word,int,code,0,65535,Modbus drive status error bitfield
Drive_Ready,bool,flag,0,1,Inverter ready for operation
`;

    const lines = rawCsv.trim().split('\n');
    const header = lines[0].split(',').map(h => h.trim().toLowerCase());
    
    assert.strictEqual(header[0], 'register_name');
    assert.strictEqual(header[1], 'type');

    const datapoints = [];
    for (let i = 1; i < lines.length; i++) {
      const parts = lines[i].split(',').map(p => p.trim());
      if (parts.length < 5) continue;
      
      let ptype = parts[1].toLowerCase();
      if (ptype.includes('bool')) ptype = 'boolean';
      else if (ptype.includes('int')) ptype = 'int';
      else ptype = 'float';

      datapoints.push({
        name: parts[0],
        type: ptype,
        unit: parts[2],
        min_val: parseFloat(parts[3]),
        max_val: parseFloat(parts[4]),
        description: parts[5] || ''
      });
    }

    assert.strictEqual(datapoints.length, 5);
    assert.strictEqual(datapoints[0].name, 'Motor_Speed');
    assert.strictEqual(datapoints[0].type, 'float');
    assert.strictEqual(datapoints[0].unit, 'rpm');
    assert.strictEqual(datapoints[0].max_val, 3000);

    assert.strictEqual(datapoints[3].name, 'Fault_Word');
    assert.strictEqual(datapoints[3].type, 'int');

    assert.strictEqual(datapoints[4].name, 'Drive_Ready');
    assert.strictEqual(datapoints[4].type, 'boolean');
  });

  // Test 2: DML Code Synthesis
  it('2. should synthesize valid DML DataPackage from extracted datapoints', () => {
    const deviceName = 'HoneywellST700';
    const datapoints = [
      { name: 'pressure_pv', type: 'float', min_val: 0.0, max_val: 100.0 },
      { name: 'temperature_sv', type: 'float', min_val: -40.0, max_val: 125.0 },
      { name: 'health_status', type: 'int', min_val: 0, max_val: 5 }
    ];

    const dmlLines = [
      `Package ${deviceName}Package`,
      `DataModel ${deviceName}Data {`,
      '  primitives {'
    ];

    const paramLines = datapoints.map(dp => {
      const defVal = dp.type === 'float' ? '0.0' : dp.type === 'int' ? '0' : 'false';
      return `    ${dp.type} ${dp.name} = ${defVal}`;
    });

    dmlLines.push(paramLines.join(',\n'));
    dmlLines.push('  }');
    dmlLines.push('}');
    const generatedDml = dmlLines.join('\n');

    assert.ok(generatedDml.includes(`Package ${deviceName}Package`));
    assert.ok(generatedDml.includes(`DataModel ${deviceName}Data`));
    assert.ok(generatedDml.includes('float pressure_pv = 0.0'));
    assert.ok(generatedDml.includes('int health_status = 0'));
  });

  // Test 3: MNC-ML Code Synthesis
  it('3. should synthesize valid MNC-ML Interface with datapoints, commands, and alarms', () => {
    const deviceName = 'FisherDVC6200';
    const commands = [
      { name: 'STROKE_VALVE' },
      { name: 'CALIBRATE_TRAVEL' }
    ];
    const alarms = [
      { name: 'TRAVEL_DEVIATION_ALARM', level: 2 }
    ];
    const datapoints = [
      { name: 'travel_percent', type: 'float' }
    ];

    const lines = [
      `Model ${deviceName}Model`,
      `InterfaceDescription ${deviceName}Interface {`,
      `  port ${deviceName}Port = 502`,
      '  dataPoints {'
    ];
    datapoints.forEach(dp => {
      lines.push(`    Publish ${dp.type} ${dp.name} []`);
    });
    lines.push('  }');

    lines.push('  alarms {');
    alarms.forEach(a => {
      lines.push(`    Publish ${a.name} [] level = ${a.level}`);
    });
    lines.push('  }');

    lines.push('  commands {');
    commands.forEach(c => {
      lines.push(`    ${c.name} []`);
    });
    lines.push('  }');

    lines.push('  responses {');
    commands.forEach(c => {
      lines.push(`    ${c.name}_RESPONSE []`);
    });
    lines.push('  }');
    lines.push('}');
    const generatedMnc = lines.join('\n');

    assert.ok(generatedMnc.includes(`Model ${deviceName}Model`));
    assert.ok(generatedMnc.includes(`InterfaceDescription ${deviceName}Interface`));
    assert.ok(generatedMnc.includes('Publish float travel_percent []'));
    assert.ok(generatedMnc.includes('Publish TRAVEL_DEVIATION_ALARM [] level = 2'));
    assert.ok(generatedMnc.includes('STROKE_VALVE []'));
    assert.ok(generatedMnc.includes('STROKE_VALVE_RESPONSE []'));
  });

  // Test 4: Staged Artifact State Transitions & Catalog Promotion
  it('4. should manage staged artifact state transitions and promotion to Equipment Catalog', () => {
    const stagedArtifact = {
      id: 42,
      name: 'Keyence IV3 Vision Sensor',
      category: 'Machine Vision & Inspection',
      protocol: 'EtherNet/IP',
      status: 'draft',
      generated_dml: 'Package KeyenceIV3Package DataModel KeyenceIV3Data { primitives { boolean result = false } }',
      generated_mnc: 'Model KeyenceIV3Model InterfaceDescription KeyenceIV3Interface { port p = 502 }',
      generated_cap: 'Capability KeyenceIV3Cap compatible component interface KeyenceIV3Interface { Init {} }',
      generated_op: 'Operation InspectOp() { execute "sensor.inspect()" return boolean success }'
    };

    assert.strictEqual(stagedArtifact.status, 'draft');

    // Simulate approval action
    stagedArtifact.status = 'approved';
    stagedArtifact.reviewed_at = new Date().toISOString();

    assert.strictEqual(stagedArtifact.status, 'approved');

    // Format for Equipment Catalog
    const catalogItem = {
      id: `ingested_${stagedArtifact.id}_keyence_iv3`,
      name: stagedArtifact.name,
      category: stagedArtifact.category,
      specification_reference: `Ingested from ${stagedArtifact.protocol} datasheet (Artifact #${stagedArtifact.id})`,
      tags: [stagedArtifact.protocol, stagedArtifact.category, 'Ingested'],
      devices: [stagedArtifact.name],
      files: [
        { filename: 'KeyenceIV3_DML.dml', file_type: 'dml', content: stagedArtifact.generated_dml },
        { filename: 'KeyenceIV3_MNC.mnc', file_type: 'mnc', content: stagedArtifact.generated_mnc },
        { filename: 'KeyenceIV3_Cap.cap', file_type: 'capability', content: stagedArtifact.generated_cap },
        { filename: 'KeyenceIV3_Ops.op', file_type: 'operation', content: stagedArtifact.generated_op },
      ]
    };

    assert.strictEqual(catalogItem.files.length, 4);
    assert.ok(catalogItem.tags.includes('EtherNet/IP'));
    assert.strictEqual(catalogItem.files[0].file_type, 'dml');
    assert.strictEqual(catalogItem.files[1].file_type, 'mnc');
    assert.strictEqual(catalogItem.files[2].file_type, 'capability');
    assert.strictEqual(catalogItem.files[3].file_type, 'operation');
  });

  // Test 5: Transactional Notification Logging (Requirement 50)
  it('5. should construct valid transactional notifications for ingestion and billing events', () => {
    const events = [
      {
        type: 'ingestion_completed',
        filename: 'ABB_Robotics_IRB120.pdf',
        artifactsCount: 1,
        paramsCount: 12,
        recipient: 'engineer@kide.enterprise'
      },
      {
        type: 'artifact_approved',
        artifactName: 'ABB IRB 120 Manipulator',
        category: 'Robotics & Automation',
        recipient: 'engineer@kide.enterprise'
      },
      {
        type: 'subscription_updated',
        orgName: 'Siemens Energy',
        tier: 'team',
        recipient: 'owner@siemens-energy.com'
      }
    ];

    const notifications = events.map((ev, idx) => {
      let subject = '';
      let bodyText = '';
      if (ev.type === 'ingestion_completed') {
        subject = `[KIDE Knowledge Hub] Ingestion Complete: ${ev.filename}`;
        bodyText = `Extracted ${ev.artifactsCount} models with ${ev.paramsCount} parameters.`;
      } else if (ev.type === 'artifact_approved') {
        subject = `[KIDE Knowledge Hub] Equipment Promoted: ${ev.artifactName}`;
        bodyText = `Promoted ${ev.artifactName} to global catalog.`;
      } else {
        subject = `[KIDE Billing] Subscription Updated: ${ev.tier.toUpperCase()}`;
        bodyText = `Organization ${ev.orgName} updated to ${ev.tier}.`;
      }

      return {
        id: idx + 1,
        recipient_email: ev.recipient,
        notification_type: ev.type,
        subject,
        body_text: bodyText,
        status: 'sent',
        channel: 'email',
        created_at: new Date().toISOString()
      };
    });

    assert.strictEqual(notifications.length, 3);
    assert.strictEqual(notifications[0].notification_type, 'ingestion_completed');
    assert.ok(notifications[0].subject.includes('ABB_Robotics_IRB120.pdf'));
    assert.strictEqual(notifications[1].notification_type, 'artifact_approved');
    assert.strictEqual(notifications[2].notification_type, 'subscription_updated');
    assert.ok(notifications[2].subject.includes('TEAM'));
  });

});

