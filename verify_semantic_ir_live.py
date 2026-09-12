"""
KIDE Enterprise — PR 2 Live Verification Suite
Canonical Engineering IR & Multi-File Semantic Validation Engine
Uses Python standard library (urllib.request, json) for 100% zero-dependency live execution.
"""

import json
import urllib.request
import urllib.error
import sys

# Ensure UTF-8 stdout on Windows
if hasattr(sys.stdout, "reconfigure"):
    sys.stdout.reconfigure(encoding="utf-8")

BASE_URL = "http://127.0.0.1:8005/api/v1"

COOLING_DML = """Package Cooling
DataModel TemperatureReading {
    primitives {
        float sensorValue,
        string unit
    }
}
"""

COOLING_CAP = """Capability ChillerCooling compatible component interface CoolingChamberInterface {
    providesControlCapabilities {
        fireable commands: StartCooling, StopCooling
        receivable events: CoolingStarted, CoolingStopped
        raised alarms: OverheatAlarm
        subscribable DataPoints: ChamberTemp
    }
}
"""

COOLING_OP = """Operation RunPreCooling(float targetTemp, string coolMode) {
    execute "chiller.set_temp(targetTemp)"
    return string status
}
"""

COOLING_ACTIVITY = """ActivityDiagram CoolingSystem on context TemperatureReading produces results (CoolingCompleted) has activities {
    Activity InitChamber {
        requireOperation (RunPreCooling)
        nextActivity: ActivateCooling
    },
    Activity ActivateCooling {
        requireCapability: "ChillerCooling"
        nextActivity: MaintainTemp
    },
    Activity MaintainTemp {
        requireCapability: "ChillerCooling"
    }
}
"""

COOLING_MNC = """Model CoolingModel
InterfaceDescription CoolingChamberInterface {
    IPaddress: 192.168.1.50
    port ChillerPort = 8080
    commands {
        async StartCooling[],
        async StopCooling[]
    }
    events {
        Publish CoolingStarted[],
        Publish CoolingStopped[]
    }
    alarms {
        Publish OverheatAlarm[]
    }
    dataPoints {
        Publish float ChamberTemp[]
    }
    operatingStates {
        startStates: INITIALIZED
        endStates: STOPPED
        INITIALIZED[],
        COOLING[],
        STOPPED[]
    }
}
ControlNode CoolingController implements interface CoolingChamberInterface {
}
"""

def request_json(url: str, method: str = "GET", data: dict = None, headers: dict = None):
    req_headers = {"Content-Type": "application/json"}
    if headers:
        req_headers.update(headers)
    body = json.dumps(data).encode("utf-8") if data is not None else None
    req = urllib.request.Request(url, data=body, headers=req_headers, method=method)
    try:
        with urllib.request.urlopen(req) as resp:
            content = resp.read().decode("utf-8")
            return resp.status, json.loads(content) if content else {}
    except urllib.error.HTTPError as e:
        err_content = e.read().decode("utf-8")
        try:
            return e.code, json.loads(err_content)
        except Exception:
            return e.code, {"error": err_content}

def log_phase(num, name):
    print(f"\n{'='*70}")
    print(f"PHASE {num}: {name}")
    print(f"{'='*70}")

def main():
    print(">>> Starting KIDE Enterprise PR 2 Live Verification Suite...")
    
    # Wait for backend server readiness
    print("  Waiting for backend server at http://127.0.0.1:8005 ...")
    import time
    server_ready = False
    for i in range(15):
        try:
            with urllib.request.urlopen("http://127.0.0.1:8005/api/v1/projects/templates", timeout=2) as r:
                if r.status in (200, 404):
                    server_ready = True
                    break
        except Exception:
            time.sleep(1)
    if not server_ready:
        print("  [ERROR] Backend server not reachable on port 8005 after 15s.")
        sys.exit(1)
    print("  [READY] Backend server reachable.")

    # Phase 1: Authentication
    log_phase(1, "User Authentication")
    status, reg_data = request_json(f"{BASE_URL}/auth/register", method="POST", data={
        "email": "semantic_tester@kide.enterprise",
        "password": "Password123!",
        "full_name": "Semantic Verification Engineer",
        "org_name": "Verification Labs"
    })
    token = None
    if status == 200:
        token = reg_data["access_token"]
        print("  [SUCCESS] Registered new test user.")
    else:
        status, login_data = request_json(f"{BASE_URL}/auth/login", method="POST", data={
            "email": "semantic_tester@kide.enterprise",
            "password": "Password123!"
        })
        assert status == 200, f"Login failed: {login_data}"
        token = login_data["access_token"]
        print("  [SUCCESS] Logged in existing test user.")

    headers = {"Authorization": f"Bearer {token}"}

    # Phase 2: Clean Model Validation
    log_phase(2, "Multi-File Semantic Validation (Clean Thesis Project)")
    clean_files = [
        {"id": "1", "name": "Cooling.dml", "content": COOLING_DML},
        {"id": "2", "name": "ChillerCooling.cap", "content": COOLING_CAP},
        {"id": "3", "name": "RunPreCooling.op", "content": COOLING_OP},
        {"id": "4", "name": "CoolingSystem.activity", "content": COOLING_ACTIVITY},
        {"id": "5", "name": "CoolingModel.mnc", "content": COOLING_MNC}
    ]
    status, diagnostics = request_json(f"{BASE_URL}/validate/semantic", method="POST", data={"files": clean_files}, headers=headers)
    assert status == 200, f"Validation failed ({status}): {diagnostics}"
    errors = [d for d in diagnostics if d["severity"] == "error"]
    print(f"  Diagnostics count: {len(diagnostics)}, Errors: {len(errors)}")
    assert len(errors) == 0, f"Expected 0 errors on clean project, got: {errors}"
    print("  [SUCCESS] Clean project validated with 0 errors.")

    # Phase 3: Unresolved Capability Check (SEM-001)
    log_phase(3, "Hazard Detection: Unresolved Capability Reference (SEM-001)")
    broken_cap_act = """ActivityDiagram BrokenCapDiag has activities {
        Activity Step1 {
            requireCapability: "NonExistentCapability999"
        }
    }
    """
    status, d3 = request_json(f"{BASE_URL}/validate/semantic", method="POST", data={
        "files": [{"id": "b1", "name": "Broken.activity", "content": broken_cap_act}]
    }, headers=headers)
    assert status == 200
    sem001 = [d for d in d3 if d.get("ruleId") == "SEM-001"]
    assert len(sem001) >= 1, f"Expected SEM-001 diagnostic, got: {d3}"
    print(f"  [SUCCESS] Correctly detected SEM-001: '{sem001[0]['message']}'")
    assert "NonExistentCapability999" in sem001[0]["symbol"]

    # Phase 4: Unresolved Operation Check (SEM-002)
    log_phase(4, "Hazard Detection: Unresolved Operation Reference (SEM-002)")
    broken_op_act = """ActivityDiagram BrokenOpDiag has activities {
        Activity Step1 {
            requireOperation (NonExistentOp999)
        }
    }
    """
    status, d4 = request_json(f"{BASE_URL}/validate/semantic", method="POST", data={
        "files": [{"id": "b2", "name": "BrokenOp.activity", "content": broken_op_act}]
    }, headers=headers)
    assert status == 200
    sem002 = [d for d in d4 if d.get("ruleId") == "SEM-002"]
    assert len(sem002) >= 1, f"Expected SEM-002 diagnostic, got: {d4}"
    print(f"  [SUCCESS] Correctly detected SEM-002: '{sem002[0]['message']}'")

    # Phase 5: Workflow Cycle Hazard (FLW-001)
    log_phase(5, "Hazard Detection: Workflow Self-Loop Cycle (FLW-001)")
    cycle_act = """ActivityDiagram CycleDiag has activities {
        Activity EndlessLoop {
            nextActivity: EndlessLoop
        }
    }
    """
    status, d5 = request_json(f"{BASE_URL}/validate/semantic", method="POST", data={
        "files": [{"id": "b3", "name": "Cycle.activity", "content": cycle_act}]
    }, headers=headers)
    assert status == 200
    flw001 = [d for d in d5 if d.get("ruleId") == "FLW-001"]
    assert len(flw001) >= 1, f"Expected FLW-001 diagnostic, got: {d5}"
    print(f"  [SUCCESS] Correctly detected FLW-001: '{flw001[0]['message']}'")

    # Phase 6: Dangling Activity Warning (FLW-002)
    log_phase(6, "Hazard Detection: Dangling Unconnected Activity (FLW-002)")
    dangling_act = """ActivityDiagram DanglingDiag has activities {
        Activity FirstStep {
            nextActivity: FinalStep
        },
        Activity OrphanStep {
            nextActivity: FinalStep
        },
        Activity FinalStep {
        }
    }
    """
    status, d6 = request_json(f"{BASE_URL}/validate/semantic", method="POST", data={
        "files": [{"id": "b4", "name": "Dangling.activity", "content": dangling_act}]
    }, headers=headers)
    assert status == 200
    flw002 = [d for d in d6 if d.get("ruleId") == "FLW-002"]
    assert len(flw002) >= 1, f"Expected FLW-002 diagnostic, got: {d6}"
    print(f"  [SUCCESS] Correctly detected FLW-002: '{flw002[0]['message']}'")

    # Phase 7: Configuration Invariants: Invalid IP & Port (CFG-001, CFG-002)
    log_phase(7, "Configuration Sanity: Invalid IPv4 & Port Invariants (CFG-001, CFG-002)")
    bad_cfg_mnc = """Model NetModel
InterfaceDescription BadNetIface {
    IPaddress: 999.888.777.666
    port OutOfRangePort = 99999
}
"""
    status, d7 = request_json(f"{BASE_URL}/validate/semantic", method="POST", data={
        "files": [{"id": "b5", "name": "BadNet.mnc", "content": bad_cfg_mnc}]
    }, headers=headers)
    assert status == 200
    cfg001 = [d for d in d7 if d.get("ruleId") == "CFG-001"]
    cfg002 = [d for d in d7 if d.get("ruleId") == "CFG-002"]
    assert len(cfg001) >= 1, f"Expected CFG-001 diagnostic, got: {d7}"
    assert len(cfg002) >= 1, f"Expected CFG-002 diagnostic, got: {d7}"
    print(f"  [SUCCESS] Correctly detected CFG-001: '{cfg001[0]['message']}'")
    print(f"  [SUCCESS] Correctly detected CFG-002: '{cfg002[0]['message']}'")

    # Phase 8: Canonical Engineering IR Compilation (POST /validate/ir)
    log_phase(8, "Canonical Engineering IR Compilation & Symbol Table Inspection")
    status, ir_data = request_json(f"{BASE_URL}/validate/ir", method="POST", data={"files": clean_files}, headers=headers)
    assert status == 200, f"IR compilation failed ({status}): {ir_data}"
    summary = ir_data["summary"]
    symbols = ir_data["ir"]["symbols"]
    cross_refs = ir_data["ir"]["cross_references"]

    print(f"  Total Symbols: {summary['total_symbols']}")
    print(f"  Total Activities: {summary['total_activities']}")
    print(f"  Total States: {summary['total_states']}")
    print(f"  Total Cross-References: {len(cross_refs)}")
    print(f"  Health Score: {summary['health_score']}%")

    assert summary["total_symbols"] >= 10
    assert summary["total_activities"] == 3
    assert summary["total_states"] == 3
    assert len(cross_refs) >= 3
    assert summary["health_score"] >= 95.0
    print("  [SUCCESS] Canonical Engineering IR compiled with rich symbol table and cross-references.")

    # Phase 9: Persistent Project IR Inspection (GET /projects/{id}/ir)
    log_phase(9, "Persistent Project IR Endpoint (GET /projects/{id}/ir)")
    status, proj_data = request_json(f"{BASE_URL}/projects/from-template", method="POST", data={
        "name": "Cooling System IR Project",
        "template": "industrial_cooling",
        "description": "Project for IR inspection"
    }, headers=headers)
    assert status == 200, f"Project creation failed: {proj_data}"
    proj_id = proj_data["id"]

    status, p_ir_data = request_json(f"{BASE_URL}/projects/{proj_id}/ir", method="GET", headers=headers)
    assert status == 200, f"Failed to get project IR ({status}): {p_ir_data}"
    assert p_ir_data["project_id"] == proj_id
    assert p_ir_data["summary"]["total_files"] >= 4
    assert p_ir_data["summary"]["total_symbols"] >= 8
    print(f"  [SUCCESS] Project {proj_id} IR successfully queried: {p_ir_data['summary']['total_symbols']} symbols, health score {p_ir_data['summary']['health_score']}%.")

    # Phase 10: AI Copilot Tool Integration with Semantic Validator
    log_phase(10, "AI Copilot Integration with Semantic Validator")
    status, ai_data = request_json(f"{BASE_URL}/ai/chat", method="POST", data={
        "project_id": proj_id,
        "messages": [
            {"role": "user", "content": "Run semantic validation on this project and tell me the health score and diagnostics."}
        ]
    }, headers=headers)
    assert status == 200, f"AI chat failed ({status}): {ai_data}"
    tool_calls = [tc["tool"] for tc in ai_data.get("tool_calls", [])]
    print(f"  AI Tools Executed: {tool_calls}")
    assert "validate_project" in tool_calls, f"Expected validate_project tool call, got: {tool_calls}"
    print(f"  AI Copilot Response: {ai_data['message'][:140]}...")
    print("  [SUCCESS] AI Copilot invoked deep semantic validator and reported real project health.")

    print("\n" + "="*70)
    print("[PASS] ALL 10 PHASES OF PR 2 LIVE VERIFICATION PASSED SUCCESSFULLY!")
    print("="*70)

if __name__ == "__main__":
    main()
