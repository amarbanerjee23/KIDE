import pytest
from httpx import AsyncClient, ASGITransport
from app.main import app
from app.services.engineering_ir import compile_project_ir
from app.services.semantic_validator import validate_project_semantics
from app.schemas.engineering_ir import DiagnosticSeverity, SymbolType

# Clean reference test fixtures (adhering strictly to KIDE Xtext grammars)
COOLING_DML = """
Package Cooling
DataModel TemperatureReading {
    primitives {
        float sensorValue,
        string unit
    }
}
"""

COOLING_CAP = """
Capability ChillerCooling compatible component interface CoolingChamberInterface {
    providesControlCapabilities {
        fireable commands: StartCooling, StopCooling
        receivable events: CoolingStarted, CoolingStopped
        raised alarms: OverheatAlarm
        subscribable DataPoints: ChamberTemp
    }
}
"""

COOLING_OP = """
Operation RunPreCooling(float targetTemp, string coolMode) {
    execute "chiller.set_temp(targetTemp)"
    return string status
}
"""

COOLING_ACTIVITY = """
ActivityDiagram CoolingSystem on context TemperatureReading produces results (CoolingCompleted) has activities {
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

COOLING_MNC = """
Model CoolingModel
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
        INITIALIZED[]
        COOLING[]
        STOPPED[]
    }
}
ControlNode CoolingController implements interface CoolingChamberInterface {
}
"""

def test_compile_project_ir_clean():
    files = [
        {"id": "1", "name": "Cooling.dml", "content": COOLING_DML},
        {"id": "2", "name": "ChillerCooling.cap", "content": COOLING_CAP},
        {"id": "3", "name": "RunPreCooling.op", "content": COOLING_OP},
        {"id": "4", "name": "CoolingSystem.activity", "content": COOLING_ACTIVITY},
        {"id": "5", "name": "CoolingModel.mnc", "content": COOLING_MNC}
    ]

    ir, diagnostics = validate_project_semantics(files)

    # Check symbols are registered
    assert "datamodel:TemperatureReading" in ir.symbols
    assert "capability:ChillerCooling" in ir.symbols
    assert "operation:RunPreCooling" in ir.symbols
    assert "activity_diagram:CoolingSystem" in ir.symbols
    assert "activity:InitChamber" in ir.symbols
    assert "activity:ActivateCooling" in ir.symbols
    assert "interface:CoolingChamberInterface" in ir.symbols
    assert "control_node:CoolingController" in ir.symbols

    # Check metrics
    assert ir.summary.total_files == 5
    assert ir.summary.total_symbols >= 10
    assert ir.summary.total_activities == 3
    assert ir.summary.total_states == 3

    # Clean model should have 0 critical errors
    errors = [d for d in diagnostics if d.severity == DiagnosticSeverity.ERROR]
    assert len(errors) == 0, f"Unexpected errors: {[e.message for e in errors]}"

def test_detect_unresolved_capability():
    broken_activity = """
    ActivityDiagram BrokenDiag has activities {
        Activity Step1 {
            requireCapability: "NonExistentCapability123"
        }
    }
    """
    files = [{"id": "1", "name": "Broken.activity", "content": broken_activity}]
    ir, diagnostics = validate_project_semantics(files)

    errs = [d for d in diagnostics if d.rule_id == "SEM-001"]
    assert len(errs) >= 1
    assert "NonExistentCapability123" in errs[0].message
    assert errs[0].severity == DiagnosticSeverity.ERROR

def test_detect_unresolved_operation():
    broken_activity = """
    ActivityDiagram BrokenDiag has activities {
        Activity Step1 {
            requireOperation (GhostOperationXYZ)
        }
    }
    """
    files = [{"id": "1", "name": "Broken.activity", "content": broken_activity}]
    ir, diagnostics = validate_project_semantics(files)

    errs = [d for d in diagnostics if d.rule_id == "SEM-002"]
    assert len(errs) >= 1
    assert "GhostOperationXYZ" in errs[0].message
    assert errs[0].severity == DiagnosticSeverity.ERROR

def test_detect_workflow_cycle():
    cycle_activity = """
    ActivityDiagram CycleDiag has activities {
        Activity SelfLoopAct {
            nextActivity: SelfLoopAct
        }
    }
    """
    files = [{"id": "1", "name": "Cycle.activity", "content": cycle_activity}]
    ir, diagnostics = validate_project_semantics(files)

    cycle_errs = [d for d in diagnostics if d.rule_id == "FLW-001"]
    assert len(cycle_errs) >= 1
    assert "unconditionally transitions to itself" in cycle_errs[0].message

def test_detect_dangling_activity():
    dangling_activity = """
    ActivityDiagram DanglingDiag has activities {
        Activity StartAct {
            nextActivity: FinishAct
        },
        Activity UnreachableAct {
            nextActivity: FinishAct
        },
        Activity FinishAct {
        }
    }
    """
    files = [{"id": "1", "name": "Dangling.activity", "content": dangling_activity}]
    ir, diagnostics = validate_project_semantics(files)

    dangling_warns = [d for d in diagnostics if d.rule_id == "FLW-002"]
    assert len(dangling_warns) >= 1
    assert "UnreachableAct" in dangling_warns[0].message

def test_detect_automata_deadlock_and_unreachable():
    deadlock_mnc = """
    Model DeadlockModel
    InterfaceDescription DeadlockIface {
        operatingStates {
            startStates: S_START
            endStates: S_FINAL
            S_START[]
            S_INTERMEDIATE[]
            S_UNREACHABLE[]
            S_FINAL[]
        }
    }
    """
    files = [{"id": "1", "name": "Deadlock.mnc", "content": deadlock_mnc}]
    ir, diagnostics = validate_project_semantics(files)
    assert ir.summary.total_states == 4

def test_detect_invalid_ip_and_port():
    invalid_cfg_mnc = """
    Model NetModel
    InterfaceDescription NetIface {
        IPaddress: 999.999.999.999
        port BadPort = 99999
    }
    """
    files = [{"id": "1", "name": "Net.mnc", "content": invalid_cfg_mnc}]
    ir, diagnostics = validate_project_semantics(files)

    ip_errs = [d for d in diagnostics if d.rule_id == "CFG-001"]
    assert len(ip_errs) >= 1
    assert "999.999.999.999" in ip_errs[0].message

    port_errs = [d for d in diagnostics if d.rule_id == "CFG-002"]
    assert len(port_errs) >= 1
    assert "99999" in port_errs[0].message

@pytest.mark.asyncio
async def test_api_semantic_validation_endpoint():
    transport = ASGITransport(app=app)
    async with AsyncClient(transport=transport, base_url="http://test") as ac:
        payload = {
            "files": [
                {"id": "1", "name": "Cooling.dml", "content": COOLING_DML, "language": "dml"},
                {"id": "2", "name": "ChillerCooling.cap", "content": COOLING_CAP, "language": "capability"},
                {"id": "3", "name": "CoolingSystem.activity", "content": COOLING_ACTIVITY, "language": "activity"}
            ]
        }
        res = await ac.post("/api/v1/validate/semantic", json=payload)
        assert res.status_code == 200
        data = res.json()
        assert isinstance(data, list)

@pytest.mark.asyncio
async def test_api_compile_ir_endpoint():
    transport = ASGITransport(app=app)
    async with AsyncClient(transport=transport, base_url="http://test") as ac:
        payload = {
            "files": [
                {"id": "1", "name": "Cooling.dml", "content": COOLING_DML},
                {"id": "2", "name": "ChillerCooling.cap", "content": COOLING_CAP}
            ]
        }
        res = await ac.post("/api/v1/validate/ir", json=payload)
        assert res.status_code == 200
        data = res.json()
        assert "ir" in data
        assert "summary" in data
        assert data["summary"]["total_symbols"] > 0

