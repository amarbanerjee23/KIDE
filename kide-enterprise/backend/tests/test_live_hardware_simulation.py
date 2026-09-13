import pytest
import uuid
from app.services.simulation_runner import SimulationSession, SimulationService, SimulationMode, ExecutionState

@pytest.mark.asyncio
async def test_simulation_session_unit_logic():
    """Unit test for SimulationSession state machine, telemetry dynamics, and thresholds."""
    model = {
        "name": "Bioreactor_Controller",
        "states": ["INITIALIZED", "READY", "RUNNING", "CoolDown", "STOPPED"],
        "commands": ["INIT", "START", "STOP", "ABORT"],
        "events": ["Started", "Stopped", "Overheat"],
        "alarms": ["HIGH_TEMP", "Aborted"],
        "data_points": [{"name": "temperature"}, {"name": "pressure"}],
        "transitions": [
            {"currentState": "INITIALIZED", "nextState": "READY"},
            {"currentState": "READY", "nextState": "RUNNING"},
            {"currentState": "RUNNING", "nextState": "CoolDown"},
            {"currentState": "CoolDown", "nextState": "STOPPED"}
        ]
    }
    session = SimulationSession(project_id=999, model=model)
    assert session.current_state == "INITIALIZED"
    assert session.execution_state == ExecutionState.PAUSED
    assert session.mode == SimulationMode.VIRTUAL_EMULATION

    # Execute INIT command
    res = session.execute_command("INIT")
    assert res["status"] == "SUCCESS"
    assert session.current_state == "READY"

    # Step tick physics
    status1 = session.step_simulation()
    assert status1["step_counter"] == 1
    assert "temperature" in session.telemetry
    assert len(session.telemetry_history["temperature"]) >= 1

    # Configure breakpoint on RUNNING
    session.set_breakpoints(break_states=["RUNNING"], break_on_alarms=True)
    assert "RUNNING" in session.break_on_states
    assert session.break_on_alarms is True

    # Execute START -> transitions to RUNNING -> breakpoint hits
    res2 = session.execute_command("START")
    assert session.current_state == "RUNNING"
    assert session.is_breakpoint_hit is True
    assert "Hit breakpoint on state [RUNNING]" in session.breakpoint_reason
    assert session.execution_state == ExecutionState.PAUSED

    # Resume execution
    session.set_execution_state(ExecutionState.RUNNING)
    assert session.is_breakpoint_hit is False
    assert session.execution_state == ExecutionState.RUNNING

    # Fault Injection: override temperature to 110.0 (above fault_max 95.0)
    override_res = session.set_telemetry_override("temperature", 110.0)
    assert override_res["datapoint"] == "temperature"
    assert override_res["value"] == 110.0
    assert session.telemetry["temperature"] == 110.0

    # Verify anomaly alarm triggered
    alarm_names = [a["alarm"] for a in session.alarm_history]
    assert any("HIGH_TEMPERATURE_FAULT" in name for name in alarm_names)
    # Breakpoint on alarm should also trigger
    assert session.is_breakpoint_hit is True
    assert session.execution_state == ExecutionState.PAUSED

    # Test trace export
    csv_trace = session.export_telemetry(format="csv")
    assert "Timestamp,Step,State,Datapoint,Value,IsAnomaly" in csv_trace
    assert "temperature" in csv_trace

    json_trace = session.export_telemetry(format="json")
    assert '"project_id": 999' in json_trace
    assert '"telemetry_history"' in json_trace


@pytest.mark.asyncio
async def test_simulation_api_endpoints(client):
    """End-to-end test of simulation REST endpoints and HIL gateway configuration."""
    owner_email = f"sim_owner_{uuid.uuid4()}@robotics.corp"
    reg = await client.post("/api/v1/auth/register", json={
        "email": owner_email,
        "password": "Password123!",
        "full_name": "Simulation Engineer",
        "org_name": "Autonomous Automation Inc"
    })
    assert reg.status_code == 200
    token = reg.json()["access_token"]
    headers = {"Authorization": f"Bearer {token}"}

    # Create project
    proj_res = await client.post("/api/v1/projects/", json={
        "name": "Robotic Arm Cell",
        "description": "Industrial robotic welding cell with HIL gateway"
    }, headers=headers)
    assert proj_res.status_code == 200
    project_id = proj_res.json()["id"]

    # 1. Initialize simulation
    init_res = await client.post(f"/api/v1/projects/{project_id}/simulate", json={"force_reset": True}, headers=headers)
    assert init_res.status_code == 200
    status = init_res.json()
    assert status["project_id"] == project_id
    assert status["current_state"] in ("INITIALIZED", "READY")

    # 2. Change simulation mode to HIL_MODBUS_TCP
    mode_res = await client.post(f"/api/v1/projects/{project_id}/simulate/mode", json={
        "mode": "HIL_MODBUS_TCP",
        "protocol_config": {"host": "192.168.1.100", "port": 502, "slave_id": 1}
    }, headers=headers)
    assert mode_res.status_code == 200
    assert mode_res.json()["mode"] == "HIL_MODBUS_TCP"
    assert mode_res.json()["protocol_config"]["port"] == 502

    # 3. Change mode to HIL_MQTT
    mqtt_res = await client.post(f"/api/v1/projects/{project_id}/simulate/mode", json={
        "mode": "HIL_MQTT",
        "protocol_config": {"broker": "mqtt.industrial.lan", "topic_prefix": "plant/cell1"}
    }, headers=headers)
    assert mqtt_res.status_code == 200
    assert mqtt_res.json()["mode"] == "HIL_MQTT"

    # 4. Set execution state to RUNNING then PAUSED
    exec_res = await client.post(f"/api/v1/projects/{project_id}/simulate/execution-state", json={
        "state": "RUNNING"
    }, headers=headers)
    assert exec_res.status_code == 200
    assert exec_res.json()["execution_state"] == "RUNNING"

    pause_res = await client.post(f"/api/v1/projects/{project_id}/simulate/execution-state", json={
        "state": "PAUSED"
    }, headers=headers)
    assert pause_res.status_code == 200
    assert pause_res.json()["execution_state"] == "PAUSED"

    # 5. Set breakpoints
    bp_res = await client.post(f"/api/v1/projects/{project_id}/simulate/breakpoints", json={
        "break_on_states": ["STOPPED", "CoolDown"],
        "break_on_alarms": True
    }, headers=headers)
    assert bp_res.status_code == 200
    assert "STOPPED" in bp_res.json()["break_on_states"]
    assert bp_res.json()["break_on_alarms"] is True

    # 6. Execute step ticks
    step_res = await client.post(f"/api/v1/projects/{project_id}/simulate/step", headers=headers)
    assert step_res.status_code == 200
    assert step_res.json()["step_counter"] >= 1

    # 7. Telemetry override / fault injection
    fault_res = await client.post(f"/api/v1/projects/{project_id}/simulate/telemetry/override", json={
        "datapoint": "temperature",
        "value": 102.5
    }, headers=headers)
    assert fault_res.status_code == 200
    assert fault_res.json()["result"]["value"] == 102.5
    assert fault_res.json()["session"]["telemetry"]["temperature"] == 102.5

    # 8. Query telemetry history
    hist_res = await client.get(f"/api/v1/projects/{project_id}/simulate/telemetry/history", headers=headers)
    assert hist_res.status_code == 200
    hist_data = hist_res.json()
    assert "temperature" in hist_data
    assert len(hist_data["temperature"]) >= 1

    # 9. Export telemetry CSV trace
    csv_res = await client.get(f"/api/v1/projects/{project_id}/simulate/telemetry/export?format=csv", headers=headers)
    assert csv_res.status_code == 200
    assert "text/csv" in csv_res.headers["content-type"]
    assert "Timestamp,Step,State,Datapoint,Value,IsAnomaly" in csv_res.text

    # 10. Export telemetry JSON trace
    json_res = await client.get(f"/api/v1/projects/{project_id}/simulate/telemetry/export?format=json", headers=headers)
    assert json_res.status_code == 200
    assert "application/json" in json_res.headers["content-type"]
    export_json = json_res.json()
    assert export_json["project_id"] == project_id
    assert "telemetry_history" in export_json
