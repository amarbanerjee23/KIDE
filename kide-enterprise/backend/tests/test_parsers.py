import pytest
from app.parsers.dml_parser import parse as parse_dml
from app.parsers.operation_parser import parse as parse_operation
from app.parsers.mnc_parser import parse as parse_mnc
from app.parsers.capability_parser import parse as parse_capability
from app.parsers.activity_parser import parse as parse_activity

def test_parse_dml():
    dml_text = """
    Package IndustrialData
    DataModel TemperatureData {
        primitives {
            int currentTemp = 25,
            float maxThreshold = 100.5,
            boolean isAlert = false,
            string unit = "Celsius"
        }
    }
    """
    res = parse_dml(dml_text)
    assert res["name"] == "IndustrialData"
    assert len(res["data_models"]) == 1
    dm = res["data_models"][0]
    assert dm["name"] == "TemperatureData"
    assert len(dm["primitives"]) == 4
    names = [p["name"] for p in dm["primitives"]]
    assert "currentTemp" in names
    assert "maxThreshold" in names

def test_parse_operation():
    op_text = """
    Operation CalibrateSensor(int sensorId, float targetVal) {
        execute "python_script_calibrate.py"
        return boolean status
    }
    """
    res = parse_operation(op_text)
    assert len(res["operations"]) == 1
    op = res["operations"][0]
    assert op["name"] == "CalibrateSensor"
    assert len(op["input_parameters"]) == 2
    assert op["executable_script"] == "python_script_calibrate.py"
    assert op["output_parameters"]["name"] == "status"

def test_parse_mnc():
    mnc_text = """
    import com.kide.common.*
    Model AssemblyCell
    InterfaceDescription AssemblyIF uses SensorIF {
        IPaddress : 192.168.1.100
        port mainPort = 8080
        commands {
            async StartAssembly [int partId]
            StopAssembly []
        }
        events {
            Publish AssemblyCompleted [string partId]
        }
        alarms {
            Publish EmergencyStop [] level = 1
        }
        dataPoints {
            Publish int PartCount = 0 []
        }
        operatingStates {
            IDLE []
            RUNNING []
            FAULT []
            startStates : IDLE
            endStates : FAULT
        }
    }
    ControlNode AssemblyCN implements interface AssemblyIF {
        childNodes (SubCell1, SubCell2)
        CommandResponseBlock {
            Command StartAssembly {
                Action {
                    fire commands [ StartAssembly() ]
                    transition states [ currentState IDLE => nextState RUNNING ]
                }
            }
        }
    }
    """
    res = parse_mnc(mnc_text)
    assert res["name"] == "AssemblyCell"
    assert res["interface_description"]["name"] == "AssemblyIF"
    assert res["interface_description"]["ip_address"] == "192.168.1.100"
    assert len(res["interface_description"]["commands"]) == 2
    assert res["control_node"]["name"] == "AssemblyCN"
    assert res["control_node"]["child_nodes"] == ["SubCell1", "SubCell2"]

def test_parse_capability():
    cap_text = """
    Capability RobotArmCap compatible component interface RobotArmIF {
        Init {
            subscribe alarms [ OverheatAlarm() ]
            fire Commands [ HomeAxis() ]
        }
        providesControlCapabilities {
            fireable commands : HomeAxis, MoveToPosition
            receivable events : MotionComplete
            raised alarms : CollisionAlarm
        }
        providesOutcomes {
            receivable responses : MoveACK
        }
    }
    """
    res = parse_capability(cap_text)
    assert res["name"] == "RobotArmCap"
    assert "RobotArmIF" in res["compatible_component_interfaces"]
    assert res["provides_control_capabilities"] is not None
    assert len(res["provides_control_capabilities"]["commands"]) == 2

def test_parse_activity():
    act_text = """
    ActivityDiagram PickAndPlace
    uses Objects [ string partId, int targetBin ]
    produces results ( string status )
    has activities {
        Activity ScanPart {
            description : "Scan barcode on incoming part"
            requireCapability : "ScannerCap" { ScanCommand, BarcodeScanned }
            conditions {
                if outcome barCode is ( = ("VALID") ) => nextActivity : PickPart
            }
            time : 1.5 secs
        }
        Activity PickPart {
            description : "Pick scanned part"
            requireOperation ( PickOperation )
            nextActivity : PlacePart
        }
        Activity PlacePart {
            nextActivityDiagram : FinalizeDiagram
        }
    }
    """
    res = parse_activity(act_text)
    assert res["name"] == "PickAndPlace"
    assert len(res["activities"]) == 3
    assert res["activities"][0]["name"] == "ScanPart"
    assert res["activities"][0]["require_capability"] == "ScannerCap"
    assert res["activities"][0]["time"] == 1.5
    assert res["activities"][0]["unit"] == "secs"
