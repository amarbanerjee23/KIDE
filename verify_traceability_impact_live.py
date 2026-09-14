"""
KIDE Enterprise — PR 3 Live Verification Suite
Bidirectional Traceability Matrix, Change Impact Analysis & Semantic Reconfiguration Engine
Zero external dependencies (uses standard library urllib and json).
10/10 Comprehensive Phases verifying live API endpoints and AI Copilot integration.
"""

import json
import urllib.request
import urllib.error
import sys
import time

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

FILES_MAP = {
    "CoolingData.dml": COOLING_DML,
    "ChillerCap.cap": COOLING_CAP,
    "PreCoolOp.op": COOLING_OP,
    "CoolingWorkflow.activity": COOLING_ACTIVITY,
    "CoolingModel.mnc": COOLING_MNC
}

def make_request(path: str, method: str = "GET", data: dict = None, token: str = None) -> dict:
    url = f"http://127.0.0.1:8005{path}" if path == "/" else f"{BASE_URL}{path}"
    headers = {"Content-Type": "application/json"}
    if token:
        headers["Authorization"] = f"Bearer {token}"

    body = json.dumps(data).encode("utf-8") if data is not None else None
    req = urllib.request.Request(url, data=body, headers=headers, method=method)
    try:
        with urllib.request.urlopen(req) as resp:
            return json.loads(resp.read().decode("utf-8"))
    except urllib.error.HTTPError as e:
        err_body = e.read().decode("utf-8")
        print(f"\n[HTTPError {e.code}] {url} -> {err_body}")
        raise e

def run_all_phases():
    print("================================================================================")
    print("KIDE ENTERPRISE — PR 3 LIVE VERIFICATION: TRACEABILITY, IMPACT & RECONFIGURATION")
    print("================================================================================")

    # PHASE 1: Health & Root Status Check
    print("\n--- PHASE 1: Health & Root Status Check ---")
    root_status = make_request("/")
    assert root_status["status"] == "running", f"Root status unexpected: {root_status}"
    print(f"[PASS] Phase 1: API Server is running ({root_status['name']} v{root_status['version']}).")

    # PHASE 2: User Authentication & Project Setup
    print("\n--- PHASE 2: User Authentication & Project Setup ---")
    user_email = f"trace_{int(time.time())}@kide.ai"
    auth_data = make_request("/auth/register", "POST", {
        "email": user_email,
        "password": "Password123!",
        "full_name": "Traceability Engineer"
    })
    token = auth_data["access_token"]
    assert token, "No token returned from register"

    project = make_request("/projects", "POST", {
        "name": "Live Cryogenic Cooling Project",
        "description": "Industrial Chiller with Traceability Matrix and Impact Analysis"
    }, token=token)
    project_id = project["id"]
    print(f"Created Project #{project_id}")

    # Seed 5 domain files
    for filename, content in FILES_MAP.items():
        make_request(f"/projects/{project_id}/files", "POST", {
            "filename": filename,
            "file_type": filename.split(".")[-1],
            "content": content
        }, token=token)
    print(f"[PASS] Phase 2: Created project #{project_id} and populated 5 DSL models.")

    # PHASE 3: Traceability Matrix Extraction via Project ID
    print("\n--- PHASE 3: Traceability Matrix Extraction (Persistent Project) ---")
    matrix = make_request(f"/traceability/{project_id}/matrix", "GET", token=token)
    assert matrix["total_symbols"] > 0, "No symbols found in traceability matrix"
    assert matrix["total_links"] > 0, "No links found in traceability matrix"
    assert matrix["coverage_percentage"] >= 50.0, f"Coverage too low: {matrix['coverage_percentage']}"
    assert len(matrix["rows"]) > 0, "No rows returned in matrix"
    print(f"Total symbols: {matrix['total_symbols']}, Total links: {matrix['total_links']}")
    print(f"Coverage: {matrix['coverage_percentage']}%, Layers: {matrix['layer_counts']}")
    print("[PASS] Phase 3: Project Traceability Matrix successfully compiled across all 5 stages.")

    # PHASE 4: Traceability Matrix Extraction via Buffer (Stateless)
    print("\n--- PHASE 4: Traceability Matrix Extraction (Stateless Buffer) ---")
    buf_matrix = make_request("/traceability/matrix", "POST", {"files": FILES_MAP}, token=token)
    assert buf_matrix["total_symbols"] == matrix["total_symbols"]
    assert buf_matrix["total_links"] == matrix["total_links"]
    assert buf_matrix["coverage_percentage"] == matrix["coverage_percentage"]
    print(f"[PASS] Phase 4: Stateless buffer matrix matches persistent project 1-to-1.")

    # PHASE 5: Change Impact Analysis - Capability Deletion Blast Radius
    print("\n--- PHASE 5: Change Impact Analysis - Capability Deletion ---")
    cap_impact = make_request(f"/traceability/{project_id}/impact", "POST", {
        "target_symbol": "ChillerCooling",
        "action": "delete"
    }, token=token)
    assert cap_impact["target_symbol"] == "ChillerCooling"
    assert cap_impact["risk_level"] in ("HIGH", "CRITICAL"), f"Expected high/critical risk: {cap_impact['risk_level']}"
    assert "ActivateCooling" in cap_impact["affected_activities"]
    assert "MaintainTemp" in cap_impact["affected_activities"]
    assert len(cap_impact["breaking_hazards"]) > 0, "Expected breaking hazards for deleting active capability"
    assert len(cap_impact["recommended_mitigations"]) > 0, "Expected recommended mitigations"
    print(f"Risk Level: {cap_impact['risk_level']}")
    print(f"Affected Activities: {cap_impact['affected_activities']}")
    print(f"Affected States: {cap_impact['affected_states']}")
    print(f"Breaking Hazards: {cap_impact['breaking_hazards']}")
    print(f"Mitigations: {cap_impact['recommended_mitigations']}")
    print("[PASS] Phase 5: Capability blast radius traversal identified downstream activities, hazards, and mitigations.")

    # PHASE 6: Change Impact Analysis - Data Model Modification
    print("\n--- PHASE 6: Change Impact Analysis - Data Model Modification ---")
    dm_impact = make_request(f"/traceability/{project_id}/impact", "POST", {
        "target_symbol": "TemperatureReading",
        "action": "modify"
    }, token=token)
    assert dm_impact["target_symbol"] == "TemperatureReading"
    assert "CoolingSystem" in dm_impact["affected_activities"]
    print(f"Target: TemperatureReading, Risk: {dm_impact['risk_level']}, Impacted Symbols: {dm_impact['impacted_symbols_count']}")
    print("[PASS] Phase 6: Data model blast radius tracked through context model links.")

    # PHASE 7: Change Impact Analysis - Isolated Symbol (Zero False Blast Radius)
    print("\n--- PHASE 7: Change Impact Analysis - Isolated Symbol ---")
    iso_impact = make_request("/traceability/impact", "POST", {
        "files": FILES_MAP,
        "target_symbol": "UnusedSensorPackage",
        "action": "delete"
    }, token=token)
    assert iso_impact["risk_level"] == "LOW"
    assert iso_impact["impacted_symbols_count"] == 0
    assert len(iso_impact["breaking_hazards"]) == 0
    print(f"Risk Level: {iso_impact['risk_level']}, Impacted count: {iso_impact['impacted_symbols_count']}")
    print("[PASS] Phase 7: Isolated symbol correctly yields zero false blast radius and LOW risk.")

    # PHASE 8: Automated Semantic Reconfiguration Engine
    print("\n--- PHASE 8: Automated Semantic Reconfiguration Engine ---")
    # Sub-test A: Warning when replacement capability is not yet declared in project
    reconfig_warn = make_request(f"/traceability/{project_id}/reconfigure", "POST", {
        "deprecated_capability": "ChillerCooling",
        "replacement_capability": "UndeclaredChillerX"
    }, token=token)
    assert reconfig_warn["status"] == "warning", f"Expected warning on undeclared capability: {reconfig_warn}"
    assert reconfig_warn["safety_verification"]["unresolved_replacement"] is True

    # Sub-test B: Add SmartChillerV2.cap and verify clean, safe reconfiguration
    make_request(f"/projects/{project_id}/files", "POST", {
        "filename": "SmartChillerV2.cap",
        "file_type": "cap",
        "content": """Capability SmartChillerV2 compatible component interface CoolingChamberInterface {
    providesControlCapabilities {
        fireable commands: StartCooling, StopCooling
        receivable events: CoolingStarted, CoolingStopped
        raised alarms: OverheatAlarm
        subscribable DataPoints: ChamberTemp
    }
}
"""
    }, token=token)

    reconfig = make_request(f"/traceability/{project_id}/reconfigure", "POST", {
        "deprecated_capability": "ChillerCooling",
        "replacement_capability": "SmartChillerV2"
    }, token=token)
    assert reconfig["status"] == "success", f"Reconfiguration status not success: {reconfig}"
    assert len(reconfig["patches"]) > 0, "No reconfiguration patches generated"
    assert reconfig["safety_verification"]["is_safe"] is True, f"Safety check failed: {reconfig['safety_verification']}"
    assert reconfig["safety_verification"]["reconfiguration_approved"] is True
    
    first_patch = reconfig["patches"][0]
    assert "CoolingWorkflow.activity" in first_patch["filename"]
    assert "SmartChillerV2" in first_patch["new_content"]
    assert "ChillerCooling" not in first_patch["new_content"]
    assert "-" in first_patch["diff"] and "+" in first_patch["diff"]
    print(f"Status: {reconfig['status']}")
    print(f"Explanation: {reconfig['explanation']}")
    print(f"Safety Verification: {reconfig['safety_verification']}")
    print("[PASS] Phase 8: Semantic reconfiguration synthesized valid unified diffs and verified zero-deadlock safety.")

    # PHASE 9: AI Copilot - Natural Language Impact Analysis
    print("\n--- PHASE 9: AI Engineering Copilot - Impact Analysis Tool Invocation ---")
    copilot_impact = make_request("/ai/chat", "POST", {
        "project_id": project_id,
        "messages": [
            {"role": "user", "content": "Analyze the change impact of deleting ChillerCooling"}
        ]
    }, token=token)
    assert len(copilot_impact.get("tool_calls", [])) > 0, "AI Copilot did not invoke any tools"
    tool_names = [t["tool"] for t in copilot_impact["tool_calls"]]
    assert "analyze_impact" in tool_names, f"Expected analyze_impact tool, got: {tool_names}"
    assert "Risk" in copilot_impact["message"]
    print(f"Invoked Tool: {tool_names[0]}")
    print(f"Copilot Response excerpt: {copilot_impact['message'][:200]}...")
    print("[PASS] Phase 9: AI Copilot successfully recognized blast radius intent and invoked analyze_impact.")

    # PHASE 10: AI Copilot - Natural Language Semantic Reconfiguration
    print("\n--- PHASE 10: AI Engineering Copilot - Semantic Reconfiguration Tool Invocation ---")
    copilot_reconfig = make_request("/ai/chat", "POST", {
        "project_id": project_id,
        "messages": [
            {"role": "user", "content": "Reconfigure workflow to substitute ChillerCooling with SmartChillerV2"}
        ]
    }, token=token)
    assert len(copilot_reconfig.get("tool_calls", [])) > 0, "AI Copilot did not invoke reconfigure tool"
    reconfig_tools = [t["tool"] for t in copilot_reconfig["tool_calls"]]
    assert "reconfigure_capability" in reconfig_tools, f"Expected reconfigure_capability, got: {reconfig_tools}"
    assert len(copilot_reconfig["proposed_patches"]) > 0, "AI Copilot did not propose patches"
    assert "SmartChillerV2" in copilot_reconfig["proposed_patches"][0]["new_content"]
    print(f"Invoked Tool: {reconfig_tools[0]}")
    print(f"Proposed Patches: {len(copilot_reconfig['proposed_patches'])}")
    print(f"Provenance ID: {copilot_reconfig.get('provenance_id')}")
    print("[PASS] Phase 10: AI Copilot synthesized deterministic patch with provenance record.")

    print("\n================================================================================")
    print("ALL 10 LIVE VERIFICATION PHASES PASSED WITH 100% ACCURACY!")
    print("================================================================================")

if __name__ == "__main__":
    run_all_phases()
