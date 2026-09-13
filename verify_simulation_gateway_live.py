"""
Live 10-Phase Verification Script for PR 8:
Real-Time Hardware & Simulator Gateway, Live Telemetry Stream, WebSockets & HIL Execution.
Tests the live REST and streaming endpoints against a running uvicorn instance.
"""

import sys
import time
import uuid
import urllib.request
import urllib.error
import json

sys.stdout.reconfigure(encoding="utf-8")

BASE_URL = "http://127.0.0.1:8005/api/v1"

def api_call(method: str, path: str, payload=None, token=None, return_raw=False):
    url = f"{BASE_URL}{path}"
    data = json.dumps(payload).encode("utf-8") if payload is not None else None
    headers = {"Content-Type": "application/json"}
    if token:
        headers["Authorization"] = f"Bearer {token}"
    req = urllib.request.Request(url, data=data, headers=headers, method=method)
    try:
        with urllib.request.urlopen(req) as resp:
            raw_body = resp.read().decode("utf-8")
            if return_raw:
                return resp.status, raw_body
            return resp.status, json.loads(raw_body) if raw_body else {}
    except urllib.error.HTTPError as e:
        body = e.read().decode("utf-8")
        if return_raw:
            return e.code, body
        try:
            parsed = json.loads(body)
        except Exception:
            parsed = {"error": body}
        return e.code, parsed

def run_verification():
    print("================================================================================")
    print("      KIDE ENTERPRISE — PR 8 LIVE 10-PHASE VERIFICATION SUITE                   ")
    print("  Real-Time Hardware Gateway, Live Telemetry Stream & HIL Execution             ")
    print("================================================================================\n")

    # Phase 1: Verify Server Online
    print("Phase 1: Verifying Backend Server Online...")
    code, res = api_call("GET", "/auth/me")
    if code not in (200, 401):
        print(f"FAILED: Backend not responding at {BASE_URL} (Status {code}: {res})")
        sys.exit(1)
    print(f"  ✓ Server is online and responding at {BASE_URL}")

    # Phase 2: Register Lead Control Engineer & Organization
    print("\nPhase 2: Registering Lead Control Systems Engineer & Organization...")
    unique_suffix = uuid.uuid4().hex[:6]
    engineer_email = f"hil_lead_{unique_suffix}@siemens-energy.com"
    code, reg_res = api_call("POST", "/auth/register", {
        "email": engineer_email,
        "password": "Password123!",
        "full_name": "Dr. Marcus Vance (Principal HIL Architect)",
        "org_name": "Siemens Energy Automation"
    })
    assert code == 200, f"Registration failed: {reg_res}"
    token = reg_res["access_token"]
    print(f"  ✓ Registered {engineer_email} with active tenant session")

    # Phase 3: Create Industrial Packaging Automation Project
    print("\nPhase 3: Provisioning Industrial Robotic Packaging Cell Project...")
    code, proj_res = api_call("POST", "/projects/", {
        "name": "High-Speed Palletizer Cell",
        "description": "Dual-axis robotic cell with Modbus-TCP and MQTT HIL instrumentation"
    }, token=token)
    assert code == 200, f"Project creation failed: {proj_res}"
    project_id = proj_res["id"]
    print(f"  ✓ Project '{proj_res['name']}' initialized (ID: {project_id})")

    # Phase 4: Start / Initialize Controller Simulation Session
    print("\nPhase 4: Starting Controller Simulation Environment...")
    code, sim_status = api_call("POST", f"/projects/{project_id}/simulate", {"force_reset": True}, token=token)
    assert code == 200, f"Simulate init failed: {sim_status}"
    assert sim_status["project_id"] == project_id
    assert sim_status["current_state"] in ("INITIALIZED", "READY")
    print(f"  ✓ Simulation session created for model '{sim_status['model_name']}'")
    print(f"    - Current State: [{sim_status['current_state']}]")
    print(f"    - Available States: {sim_status['states']}")
    print(f"    - Commands: {sim_status['commands']}")
    print(f"    - Active Telemetry: {list(sim_status['telemetry'].keys())}")

    # Phase 5: Configure Hardware-in-the-Loop (HIL) Protocols
    print("\nPhase 5: Configuring HIL Modes (Modbus-TCP & MQTT)...")
    code, modbus_status = api_call("POST", f"/projects/{project_id}/simulate/mode", {
        "mode": "HIL_MODBUS_TCP",
        "protocol_config": {"host": "10.0.1.20", "port": 502, "slave_id": 1}
    }, token=token)
    assert code == 200, f"Modbus mode config failed: {modbus_status}"
    assert modbus_status["mode"] == "HIL_MODBUS_TCP"
    assert modbus_status["protocol_config"]["port"] == 502
    print(f"  ✓ Mode transitioned to HIL_MODBUS_TCP (Port {modbus_status['protocol_config']['port']})")

    code, mqtt_status = api_call("POST", f"/projects/{project_id}/simulate/mode", {
        "mode": "HIL_MQTT",
        "protocol_config": {"broker": "mqtt.siemens-energy.lan", "topic_prefix": "factory/cell_4"}
    }, token=token)
    assert code == 200, f"MQTT mode config failed: {mqtt_status}"
    assert mqtt_status["mode"] == "HIL_MQTT"
    print(f"  ✓ Mode transitioned to HIL_MQTT (Broker: {mqtt_status['protocol_config']['broker']})")

    # Phase 6: Execution State Transition (RUNNING <-> PAUSED)
    print("\nPhase 6: Testing Execution State Management (RUNNING / PAUSED)...")
    code, run_status = api_call("POST", f"/projects/{project_id}/simulate/execution-state", {
        "state": "RUNNING"
    }, token=token)
    assert code == 200, f"Run state failed: {run_status}"
    assert run_status["execution_state"] == "RUNNING"
    print("  ✓ Execution state set to RUNNING")

    code, pause_status = api_call("POST", f"/projects/{project_id}/simulate/execution-state", {
        "state": "PAUSED"
    }, token=token)
    assert code == 200, f"Pause state failed: {pause_status}"
    assert pause_status["execution_state"] == "PAUSED"
    print("  ✓ Execution state toggled to PAUSED")

    # Phase 7: Breakpoint Arming & Interception
    print("\nPhase 7: Configuring State Machine Breakpoints...")
    target_state = "READY" if sim_status["current_state"] != "READY" else "STOPPED"
    code, bp_status = api_call("POST", f"/projects/{project_id}/simulate/breakpoints", {
        "break_on_states": [target_state],
        "break_on_alarms": True
    }, token=token)
    assert code == 200, f"Set breakpoints failed: {bp_status}"
    assert target_state in bp_status["break_on_states"]
    assert bp_status["break_on_alarms"] is True
    print(f"  ✓ Breakpoints armed on State: [{target_state}] and any critical alarms")

    # Execute command that triggers the target state
    if target_state == "READY":
        cmd_to_fire = "INIT"
    else:
        cmd_to_fire = "STOP"
    code, exec_res = api_call("POST", f"/projects/{project_id}/simulate/command", {
        "command": cmd_to_fire
    }, token=token)
    assert code == 200, f"Command {cmd_to_fire} failed: {exec_res}"
    sess = exec_res["session"]
    assert sess["current_state"] == target_state
    assert sess["is_breakpoint_hit"] is True
    assert sess["execution_state"] == "PAUSED"
    print(f"  ✓ Breakpoint successfully intercepted! Reason: '{sess['breakpoint_reason']}'")

    # Phase 8: Fault Injection & Anomaly Alarm Generation
    print("\nPhase 8: Fault Injection & Signal Override...")
    # Find any active datapoint to override
    first_dp = list(sim_status["telemetry"].keys())[0]
    fault_val = 112.5  # Exceeds max threshold of 95.0
    code, override_res = api_call("POST", f"/projects/{project_id}/simulate/telemetry/override", {
        "datapoint": first_dp,
        "value": fault_val
    }, token=token)
    assert code == 200, f"Override failed: {override_res}"
    assert override_res["result"]["value"] == fault_val
    alarm_list = override_res["session"]["alarm_history"]
    assert len(alarm_list) > 0, "Expected anomaly alarm to be triggered"
    latest_alarm = alarm_list[-1]
    print(f"  ✓ Fault injected on '{first_dp}' = {fault_val}")
    print(f"  ✓ Anomaly Alarm Triggered: [{latest_alarm['alarm']}] - '{latest_alarm['message']}'")

    # Phase 9: Rolling Telemetry History Validation
    print("\nPhase 9: Querying Rolling Time-Series Telemetry History...")
    code, history_data = api_call("GET", f"/projects/{project_id}/simulate/telemetry/history", token=token)
    assert code == 200, f"Telemetry history query failed: {history_data}"
    assert first_dp in history_data
    points = history_data[first_dp]
    assert len(points) >= 2
    latest_point = points[-1]
    assert latest_point["value"] == fault_val
    assert latest_point["is_anomaly"] is True
    print(f"  ✓ Retrieved {len(points)} telemetry snapshots for '{first_dp}'")
    print(f"    - Latest Snapshot: Step #{latest_point['step']}, Val: {latest_point['value']}, Anomaly: {latest_point['is_anomaly']}")

    # Phase 10: Trace Export Verification (CSV & JSON)
    print("\nPhase 10: Verifying Telemetry Trace Exports (CSV & JSON)...")
    code, csv_trace = api_call("GET", f"/projects/{project_id}/simulate/telemetry/export?format=csv", token=token, return_raw=True)
    assert code == 200, f"CSV export failed: {csv_trace}"
    assert "Timestamp,Step,State,Datapoint,Value,IsAnomaly" in csv_trace
    assert first_dp in csv_trace
    print(f"  ✓ CSV trace generated ({len(csv_trace.splitlines())} rows including headers)")

    code, json_trace = api_call("GET", f"/projects/{project_id}/simulate/telemetry/export?format=json", token=token)
    assert code == 200, f"JSON export failed: {json_trace}"
    assert json_trace["project_id"] == project_id
    assert "telemetry_history" in json_trace
    assert "alarm_history" in json_trace
    print(f"  ✓ JSON trace exported with {len(json_trace['telemetry_history'])} signals and {len(json_trace['alarm_history'])} alarms")

    print("\n================================================================================")
    print("  ✓ ALL 10 PHASES PASSED WITH 100% SUCCESS — PR 8 LIVE VERIFIED")
    print("================================================================================\n")

if __name__ == "__main__":
    run_verification()

