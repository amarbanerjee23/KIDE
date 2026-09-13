import pytest
from app.services.traceability import TraceabilityService
from app.services.reconfiguration import SemanticReconfigurationService
from app.schemas.traceability import (
    ImpactAnalysisRequest,
    ImpactRiskLevel,
    ReconfigurationRequest
)

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

SAMPLE_FILES = [
    {"id": "1", "name": "Cooling.dml", "content": COOLING_DML},
    {"id": "2", "name": "ChillerCooling.cap", "content": COOLING_CAP},
    {"id": "3", "name": "RunPreCooling.op", "content": COOLING_OP},
    {"id": "4", "name": "CoolingSystem.activity", "content": COOLING_ACTIVITY},
    {"id": "5", "name": "CoolingModel.mnc", "content": COOLING_MNC}
]

def test_build_traceability_matrix():
    matrix = TraceabilityService.build_traceability_matrix(SAMPLE_FILES, project_id=1)
    assert matrix.total_symbols >= 10
    assert matrix.total_links >= 5
    assert matrix.coverage_percentage >= 50.0

    # Verify rows across stages
    stages = {r.stage for r in matrix.rows}
    assert 1 in stages # Data Modeling
    assert 2 in stages # Capabilities & Ops
    assert 3 in stages # Activities
    assert 4 in stages # Operating States
    assert 5 in stages # Code Gen

    # Find ActivateCooling
    act_rows = [r for r in matrix.rows if r.symbol == "ActivateCooling"]
    assert len(act_rows) >= 1
    act = act_rows[0]
    assert "ChillerCooling" in act.upstream_symbols

    # Find ChillerCooling
    cap_rows = [r for r in matrix.rows if r.symbol == "ChillerCooling"]
    assert len(cap_rows) >= 1
    cap = cap_rows[0]
    assert "ActivateCooling" in cap.downstream_symbols

def test_change_impact_delete_capability():
    req = ImpactAnalysisRequest(target_symbol="ChillerCooling", action="delete")
    res = TraceabilityService.analyze_change_impact(SAMPLE_FILES, req)

    assert res.target_symbol == "ChillerCooling"
    assert res.risk_level in (ImpactRiskLevel.HIGH, ImpactRiskLevel.CRITICAL)
    assert "ActivateCooling" in res.affected_activities
    assert "MaintainTemp" in res.affected_activities
    assert any("SEM-001" in h for h in res.breaking_hazards)
    assert len(res.recommended_mitigations) >= 1

def test_change_impact_modify_data_model():
    req = ImpactAnalysisRequest(target_symbol="TemperatureReading", action="modify")
    res = TraceabilityService.analyze_change_impact(SAMPLE_FILES, req)

    assert res.target_symbol == "TemperatureReading"
    assert res.impacted_symbols_count >= 1
    assert "CoolingSystem" in res.affected_activities

def test_change_impact_isolated_symbol():
    req = ImpactAnalysisRequest(target_symbol="NonExistentSymbol999", action="modify")
    res = TraceabilityService.analyze_change_impact(SAMPLE_FILES, req)

    assert res.risk_level == ImpactRiskLevel.LOW
    assert res.impacted_symbols_count == 0
    assert len(res.affected_activities) == 0

def test_semantic_reconfiguration_success():
    req = ReconfigurationRequest(
        deprecated_capability="ChillerCooling",
        replacement_capability="SmartChillerV2"
    )
    proposal = SemanticReconfigurationService.compute_reconfiguration_plan(SAMPLE_FILES, req)

    assert proposal.status in ("success", "warning")
    assert proposal.deprecated_capability == "ChillerCooling"
    assert proposal.replacement_capability == "SmartChillerV2"
    assert "CoolingSystem.activity" in proposal.affected_files
    assert len(proposal.patches) == 1

    patch = proposal.patches[0]
    assert patch.filename == "CoolingSystem.activity"
    assert "SmartChillerV2" in patch.new_content
    assert "ChillerCooling" not in patch.new_content
    assert "requireCapability: \"SmartChillerV2\"" in patch.new_content

def test_semantic_reconfiguration_no_change_needed():
    req = ReconfigurationRequest(
        deprecated_capability="NonExistentCap",
        replacement_capability="AnyCap"
    )
    proposal = SemanticReconfigurationService.compute_reconfiguration_plan(SAMPLE_FILES, req)

    assert proposal.status == "no_change_needed"
    assert len(proposal.patches) == 0
    assert len(proposal.affected_files) == 0

@pytest.mark.asyncio
async def test_api_matrix_and_impact_endpoints(client):
    # Test POST /api/v1/traceability/matrix
    resp = await client.post("/api/v1/traceability/matrix", json={"files": SAMPLE_FILES})
    assert resp.status_code == 200
    data = resp.json()
    assert "rows" in data
    assert data["total_symbols"] >= 10
    assert data["coverage_percentage"] > 0

    # Test POST /api/v1/traceability/impact
    resp2 = await client.post("/api/v1/traceability/impact", json={
        "files": SAMPLE_FILES,
        "target_symbol": "ChillerCooling",
        "action": "delete"
    })
    assert resp2.status_code == 200
    data2 = resp2.json()
    assert data2["target_symbol"] == "ChillerCooling"
    assert data2["risk_level"] in ("HIGH", "CRITICAL")
    assert len(data2["affected_activities"]) >= 2

    # Test POST /api/v1/traceability/reconfigure
    resp3 = await client.post("/api/v1/traceability/reconfigure", json={
        "files": SAMPLE_FILES,
        "deprecated_capability": "ChillerCooling",
        "replacement_capability": "SmartChillerV2"
    })
    assert resp3.status_code == 200
    data3 = resp3.json()
    assert data3["status"] in ("success", "warning")
    assert len(data3["patches"]) == 1

@pytest.mark.asyncio
async def test_ai_copilot_traceability_and_persistent_project(client, test_user):
    headers = {"Authorization": f"Bearer {test_user['access_token']}"}

    # 1. Create a project from template
    p_resp = await client.post("/api/v1/projects/from-template", json={
        "name": "Traceability Test Project",
        "template": "industrial_cooling"
    }, headers=headers)
    assert p_resp.status_code == 200
    proj_id = p_resp.json()["id"]

    # 2. Query persistent project traceability matrix
    m_resp = await client.get(f"/api/v1/traceability/{proj_id}/matrix", headers=headers)
    assert m_resp.status_code == 200
    m_data = m_resp.json()
    assert m_data["project_id"] == proj_id
    assert m_data["total_symbols"] >= 8
    assert len(m_data["rows"]) >= 8

    # 3. AI Copilot: Test analyze_impact tool
    ai_resp1 = await client.post("/api/v1/ai/chat", json={
        "project_id": proj_id,
        "messages": [
            {"role": "user", "content": "Analyze the change impact of deleting ChillerCooling"}
        ]
    }, headers=headers)
    assert ai_resp1.status_code == 200
    ai_data1 = ai_resp1.json()
    tool_names1 = [t["tool"] for t in ai_data1.get("tool_calls", [])]
    assert "analyze_impact" in tool_names1
    assert "Risk Level" in ai_data1["message"]

    # 4. AI Copilot: Test reconfigure_capability tool
    ai_resp2 = await client.post("/api/v1/ai/chat", json={
        "project_id": proj_id,
        "messages": [
            {"role": "user", "content": "Reconfigure workflow to substitute ChillerCooling with SmartChillerV2"}
        ]
    }, headers=headers)
    assert ai_resp2.status_code == 200
    ai_data2 = ai_resp2.json()
    tool_names2 = [t["tool"] for t in ai_data2.get("tool_calls", [])]
    assert "reconfigure_capability" in tool_names2
    assert "Reconfiguration Proposal" in ai_data2["message"]
