import pytest
from httpx import AsyncClient

pytestmark = pytest.mark.asyncio

async def test_e2e_cooling_system_synthesis(client: AsyncClient, test_user):
    """
    Test scenario: Industrial Cooling System
    This tests the full synthesis pipeline including:
    - Multiple activities with operations
    - State transitions
    - Auto-generated lifecycle blocks (INIT, Started, Stopped)
    - Alarms, events, and data points
    """
    token = test_user["access_token"]
    
    payload = {
        "activity_diagram": {
            "name": "CoolingSystem",
            "default_operating_states": ["STANDBY", "NORMAL", "EMERGENCY"],
            "activities": [
                {
                    "name": "Initialize",
                    "requires_operation": True,
                    "commands": [{"name": "START_PUMP"}, {"name": "OPEN_VALVE"}],
                    "events": [{"name": "PUMP_STARTED"}],
                    "transitions": [{"from": "Initialize", "to": "Monitor"}]
                },
                {
                    "name": "Monitor",
                    "requires_operation": False,
                    "events": [{"name": "TEMP_HIGH"}],
                    "data_points": [{"name": "temperature", "type": "float"}],
                    "transitions": [{"from": "Monitor", "to": "CoolDown", "condition": "temp > 100"}]
                },
                {
                    "name": "CoolDown",
                    "requires_operation": True,
                    "commands": [{"name": "MAX_FAN"}],
                    "alarms": [{"name": "OVERHEATING", "severity": "CRITICAL"}],
                    "transitions": [{"from": "CoolDown", "to": "Monitor", "condition": "temp < 80"}]
                }
            ]
        }
    }
    
    # 1. Test Transform Endpoint
    res = await client.post("/api/v1/transform/", json=payload, headers={"Authorization": f"Bearer {token}"})
    assert res.status_code == 200, res.text
    data = res.json()
    
    model = data["model"]
    assert model["name"] == "CoolingSystem"
    
    # 2. Verify synthesized interface properties
    iface = model["interface_description"]
    assert iface["name"] == "CoolingSystem_Interface"
    
    commands = [c["name"] for c in iface["commands"]]
    assert "INIT" in commands  # Lifecycle
    assert "START_PUMP" in commands
    assert "OPEN_VALVE" in commands
    assert "MAX_FAN" in commands
    
    events = [e["name"] for e in iface["events"]]
    assert "Started" in events # Lifecycle
    assert "PUMP_STARTED" in events
    assert "TEMP_HIGH" in events
    
    alarms = [a["name"] for a in iface["alarms"]]
    assert "Aborted" in alarms # Lifecycle
    assert "OVERHEATING" in alarms
    
    dps = [d["name"] for d in iface["data_points"]]
    assert "temperature" in dps
    
    # 3. Verify composition structure
    cn = model["control_node"]
    assert "STANDBY" in cn["operating_states"]
    assert "NORMAL" in cn["operating_states"]
    assert "EMERGENCY" in cn["operating_states"]
    
    actions = {a["name"]: a for a in cn["actions"]}
    assert "Initialize" in actions
    assert "Monitor" in actions
    assert "CoolDown" in actions
    
    assert actions["Initialize"]["requires_operation"] is True
    assert actions["Monitor"]["requires_operation"] is False
    
    # 4. Test Export Endpoints
    # JSON Export
    res_json = await client.post("/api/v1/export/json", json=model, headers={"Authorization": f"Bearer {token}"})
    assert res_json.status_code == 200
    assert "CoolingSystem" in res_json.text
    
    # DSL Export
    res_dsl = await client.post("/api/v1/export/dsl", json=model, headers={"Authorization": f"Bearer {token}"})
    assert res_dsl.status_code == 200
    assert "Model CoolingSystem" in res_dsl.text
    assert "ControlNode CoolingSystem" in res_dsl.text
    assert "Command START_PUMP" in res_dsl.text
    
    # Python Export
    res_py = await client.post("/api/v1/export/python", json=model, headers={"Authorization": f"Bearer {token}"})
    assert res_py.status_code == 200
    assert "class CoolingSystemHandler:" in res_py.text
    assert "def handle_start_pump(self, payload):" in res_py.text

async def test_e2e_history_tracking(client: AsyncClient, test_user):
    token = test_user["access_token"]
    
    # Send a valid diagram to ensure history is updated
    payload = {
        "activity_diagram": {
            "name": "HistoryTest",
            "activities": []
        }
    }
    
    await client.post("/api/v1/transform/", json=payload, headers={"Authorization": f"Bearer {token}"})
    
    res = await client.get("/api/v1/transform/history", headers={"Authorization": f"Bearer {token}"})
    assert res.status_code == 200
    history = res.json()
    assert len(history) > 0
    assert history[0]["status"] == "success"

async def test_e2e_chemical_reactor_synthesis(client: AsyncClient, test_user):
    """
    Test scenario: Chemical Reactor Plant
    Testing extreme state machine and alarm behaviors.
    """
    token = test_user["access_token"]
    
    payload = {
        "activity_diagram": {
            "name": "ChemicalReactor",
            "default_operating_states": ["OFF", "HEATING", "REACTION", "COOLING", "ERROR"],
            "activities": [
                {
                    "name": "HeatUp",
                    "requires_operation": True,
                    "commands": [{"name": "START_HEATER"}],
                    "data_points": [{"name": "reactor_temp", "type": "float"}],
                    "transitions": [{"from": "HeatUp", "to": "React", "condition": "reactor_temp > 200"}]
                },
                {
                    "name": "React",
                    "requires_operation": False,
                    "events": [{"name": "REACTION_STARTED"}, {"name": "REACTION_COMPLETE"}],
                    "transitions": [{"from": "React", "to": "CoolDown", "condition": "reaction_time > 60"}]
                },
                {
                    "name": "CoolDown",
                    "requires_operation": True,
                    "commands": [{"name": "START_COOLER"}],
                    "alarms": [{"name": "PRESSURE_HIGH", "severity": "CRITICAL"}],
                    "transitions": [{"from": "CoolDown", "to": "HeatUp", "condition": "reactor_temp < 50"}]
                }
            ]
        }
    }
    
    res = await client.post("/api/v1/transform/", json=payload, headers={"Authorization": f"Bearer {token}"})
    assert res.status_code == 200, res.text
    data = res.json()
    
    model = data["model"]
    assert model["name"] == "ChemicalReactor"
    
    # Check alarms
    alarms = [a["name"] for a in model["interface_description"]["alarms"]]
    assert "PRESSURE_HIGH" in alarms

async def test_e2e_assembly_line_synthesis(client: AsyncClient, test_user):
    """
    Test scenario: Assembly Line
    Testing parallel states or missing state transitions handling.
    """
    token = test_user["access_token"]
    
    payload = {
        "activity_diagram": {
            "name": "AssemblyLine",
            "activities": [
                {
                    "name": "BeltStart",
                    "requires_operation": True,
                    "commands": [{"name": "MOVE_BELT"}],
                    "transitions": [{"from": "BeltStart", "to": "ScanItem"}]
                },
                {
                    "name": "ScanItem",
                    "requires_operation": False,
                    "commands": [{"name": "SCAN_BARCODE"}],
                    "transitions": [{"from": "ScanItem", "to": "BeltStart"}]
                }
            ]
        }
    }
    
    res = await client.post("/api/v1/transform/", json=payload, headers={"Authorization": f"Bearer {token}"})
    assert res.status_code == 200, res.text
    model = res.json()["model"]
    
    assert model["name"] == "AssemblyLine"
    commands = [c["name"] for c in model["interface_description"]["commands"]]
    assert "MOVE_BELT" in commands
    assert "SCAN_BARCODE" in commands
