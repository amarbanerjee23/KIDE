"""
Reference Templates Service for KIDE Enterprise
Contains verified reference DSL models, activities, operations,
capabilities, and data models for industrial supervisory control systems.
"""

from typing import Dict, List, Any

REFERENCE_TEMPLATES: Dict[str, Dict[str, Any]] = {
    "industrial_cooling": {
        "name": "Industrial Cooling System",
        "description": "Synthesis reference case study: multi-activity cooling system with state transitions, commands, events, alarms, and data points.",
        "files": [
            {
                "filename": "CoolingSystem.activity",
                "file_type": "activity",
                "content": """ActivityDiagram CoolingSystem
uses Objects [ string status, float temperature ]
produces results ( string finalStatus )
has activities {
    Activity Initialize {
        description : "Initialize pumps and cooling valves"
        requireOperation ( StartPumpOperation )
        nextActivity : Monitor
        time : 2.0 secs
    }
    Activity Monitor {
        description : "Monitor industrial temperature"
        requireCapability : "SensorCap" { ReadTemperature }
        conditions {
            if outcome temperature is ( > 100.0 ) => nextActivity : CoolDown,
            if outcome temperature is ( < 80.0 ) => nextActivity : NormalOperation
        }
        time : 5.0 secs
    }
    Activity CoolDown {
        description : "Engage max cooling fan and raise overheat warning"
        requireOperation ( MaxFanOperation )
        nextActivity : Monitor
        time : 10.0 secs
    }
    Activity NormalOperation {
        description : "Maintain nominal cooling rate"
        nextActivity : Monitor
    }
}
"""
            },
            {
                "filename": "CoolingSystem.json",
                "file_type": "json",
                "content": """{
  "name": "CoolingSystem",
  "default_operating_states": ["STANDBY", "NORMAL", "EMERGENCY"],
  "activities": [
    {
      "name": "Initialize",
      "requires_operation": true,
      "commands": [{"name": "START_PUMP"}, {"name": "OPEN_VALVE"}],
      "events": [{"name": "PUMP_STARTED"}],
      "transitions": [{"from": "Initialize", "to": "Monitor"}]
    },
    {
      "name": "Monitor",
      "requires_operation": false,
      "events": [{"name": "TEMP_HIGH"}],
      "data_points": [{"name": "temperature", "type": "float"}],
      "transitions": [{"from": "Monitor", "to": "CoolDown", "condition": "temp > 100"}]
    },
    {
      "name": "CoolDown",
      "requires_operation": true,
      "commands": [{"name": "MAX_FAN"}],
      "alarms": [{"name": "OVERHEATING", "severity": "CRITICAL"}],
      "transitions": [{"from": "CoolDown", "to": "Monitor", "condition": "temp < 80"}]
    }
  ]
}
"""
            },
            {
                "filename": "CoolingData.dml",
                "file_type": "dml",
                "content": """Package CoolingSystem
DataModel TemperatureData {
    primitives {
        float currentTemp = 25.0,
        float maxThreshold = 100.0,
        boolean isAlert = false,
        string unit = "Celsius"
    }
}
"""
            },
            {
                "filename": "CoolingOps.op",
                "file_type": "operation",
                "content": """Operation StartPumpOperation(int pumpId) {
    execute "scripts/start_pump.py"
    return boolean success
}

Operation MaxFanOperation(float fanSpeed) {
    execute "scripts/max_fan.py"
    return boolean success
}
"""
            },
            {
                "filename": "CoolingCap.cap",
                "file_type": "capability",
                "content": """Capability SensorCap compatible component interface CoolingSystem_Interface {
    Init {
        subscribe alarms [ OVERHEATING() ]
        fire Commands [ START_PUMP() ]
    }
    providesControlCapabilities {
        fireable commands : START_PUMP, OPEN_VALVE, MAX_FAN
        receivable events : PUMP_STARTED, TEMP_HIGH
        raised alarms : OVERHEATING
        subscribable DataPoints : temperature
    }
    providesOutcomes {
        receivable responses : PUMP_ACK
    }
}
"""
            }
        ]
    },
    "pick_and_place": {
        "name": "Pick & Place Robotic Cell",
        "description": "Multi-DSL robotic cell reference: integrates Activity, Capability (ScannerCap), Operations (Pick/Place), and DML.",
        "files": [
            {
                "filename": "PickAndPlace.activity",
                "file_type": "activity",
                "content": """ActivityDiagram PickAndPlace
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
        description : "Pick scanned part with robotic gripper"
        requireOperation ( PickOperation )
        nextActivity : PlacePart
    }
    Activity PlacePart {
        description : "Place part into target sorting bin"
        requireOperation ( PlaceOperation )
        nextActivity : InspectPlacement
    }
    Activity InspectPlacement {
        description : "Vision check of part orientation"
        requireCapability : "VisionCap" { QualityInspected }
    }
}
"""
            },
            {
                "filename": "PickAndPlace.json",
                "file_type": "json",
                "content": """{
  "name": "PickAndPlace",
  "default_operating_states": ["IDLE", "SCANNING", "PICKING", "PLACING", "COMPLETED"],
  "activities": [
    {
      "name": "ScanPart",
      "requires_operation": false,
      "commands": [{"name": "TRIGGER_SCAN"}],
      "events": [{"name": "BARCODE_READ"}],
      "data_points": [{"name": "barcode_id", "type": "string"}],
      "transitions": [{"from": "ScanPart", "to": "PickPart"}]
    },
    {
      "name": "PickPart",
      "requires_operation": true,
      "commands": [{"name": "GRIPPER_CLOSE"}, {"name": "MOVE_Z_UP"}],
      "events": [{"name": "PART_GRIPPED"}],
      "alarms": [{"name": "GRIP_FAILED", "level": 2}],
      "transitions": [{"from": "PickPart", "to": "PlacePart"}]
    },
    {
      "name": "PlacePart",
      "requires_operation": true,
      "commands": [{"name": "MOVE_BIN"}, {"name": "GRIPPER_OPEN"}],
      "events": [{"name": "PART_RELEASED"}],
      "transitions": [{"from": "PlacePart", "to": "ScanPart"}]
    }
  ]
}
"""
            },
            {
                "filename": "RoboticCellData.dml",
                "file_type": "dml",
                "content": """Package RoboticCell
DataModel PartData {
    primitives {
        string partId = "PART-001",
        int targetBin = 3,
        boolean isInspected = false
    }
}
"""
            },
            {
                "filename": "RoboticCellOps.op",
                "file_type": "operation",
                "content": """Operation PickOperation(string partId) {
    execute "scripts/pick_part.py"
    return boolean gripped
}

Operation PlaceOperation(int targetBin) {
    execute "scripts/place_part.py"
    return boolean placed
}
"""
            },
            {
                "filename": "ScannerCap.cap",
                "file_type": "capability",
                "content": """Capability ScannerCap compatible component interface PickAndPlace_Interface {
    Init {
        subscribe events [ BARCODE_READ() ]
        fire Commands [ TRIGGER_SCAN() ]
    }
    providesControlCapabilities {
        fireable commands : TRIGGER_SCAN, GRIPPER_CLOSE, GRIPPER_OPEN
        receivable events : BARCODE_READ, PART_GRIPPED, PART_RELEASED
        raised alarms : GRIP_FAILED
        subscribable DataPoints : barcode_id
    }
    providesOutcomes {
        receivable responses : SCAN_ACK
    }
}
"""
            }
        ]
    },
    "chemical_reactor": {
        "name": "Chemical Reactor Plant",
        "description": "Multi-state chemical plant process: extreme state machine testing (OFF, HEATING, REACTION, COOLING, ERROR) and safety alarms.",
        "files": [
            {
                "filename": "ChemicalReactor.activity",
                "file_type": "activity",
                "content": """ActivityDiagram ChemicalReactor
uses Objects [ float reactorTemp, float pressure ]
produces results ( string batchStatus )
has activities {
    Activity HeatUp {
        description : "Heat reactor to activation temperature"
        requireOperation ( StartHeaterOperation )
        nextActivity : React
        time : 120.0 secs
    }
    Activity React {
        description : "Controlled chemical reaction phase"
        requireCapability : "ReactionCap" { MonitorReaction }
        nextActivity : Quench
        time : 300.0 secs
    }
    Activity Quench {
        description : "Emergency coolant injection"
        requireOperation ( CoolantValveOperation )
    }
}
"""
            },
            {
                "filename": "ChemicalReactor.json",
                "file_type": "json",
                "content": """{
  "name": "ChemicalReactor",
  "default_operating_states": ["OFF", "HEATING", "REACTION", "COOLING", "ERROR"],
  "activities": [
    {
      "name": "HeatUp",
      "requires_operation": true,
      "commands": [{"name": "START_HEATER"}],
      "data_points": [{"name": "reactor_temp", "type": "float"}],
      "transitions": [{"from": "HeatUp", "to": "React", "condition": "reactor_temp > 200"}]
    },
    {
      "name": "React",
      "requires_operation": false,
      "commands": [{"name": "START_STIRRER"}],
      "events": [{"name": "REACTION_COMPLETE"}],
      "alarms": [{"name": "OVERPRESSURE", "severity": "CRITICAL"}],
      "transitions": [{"from": "React", "to": "Quench"}]
    },
    {
      "name": "Quench",
      "requires_operation": true,
      "commands": [{"name": "INJECT_COOLANT"}],
      "events": [{"name": "COOLED_DOWN"}]
    }
  ]
}
"""
            },
            {
                "filename": "ReactorData.dml",
                "file_type": "dml",
                "content": """Package ReactorSystem
DataModel ReactorState {
    primitives {
        float temperature = 180.5,
        float pressure = 3.2,
        boolean emergencyStop = false
    }
}
"""
            },
            {
                "filename": "ReactorOps.op",
                "file_type": "operation",
                "content": """Operation StartHeaterOperation(float targetTemp) {
    execute "scripts/heater.py"
    return boolean heated
}

Operation CoolantValveOperation(int valveCode) {
    execute "scripts/coolant.py"
    return boolean valveOpen
}
"""
            }
        ]
    },
    "assembly_supervisor": {
        "name": "Assembly Cell Supervisor",
        "description": "Automotive assembly cell reference integrating Demo_ECRE.dml data model and AssemblyCell.mnc.",
        "files": [
            {
                "filename": "AssemblySupervisor.activity",
                "file_type": "activity",
                "content": """ActivityDiagram AssemblySupervisor
uses Objects [ boolean isValidTruck, int RFID_TAG_VALUE ]
produces results ( string status )
has activities {
    Activity ReadRFID {
        description : "Read truck RFID tag at entrance bay"
        requireCapability : "RFIDCap" { ReadTagCommand, TagReadEvent }
        nextActivity : ValidateTruck
    }
    Activity ValidateTruck {
        description : "Validate RFID credentials against ECRE manifest"
        requireOperation ( ValidateECREOperation )
        nextActivity : DispatchAssembly
    }
    Activity DispatchAssembly {
        description : "Dispatch guided assembly instructions to station"
        requireOperation ( DispatchOperation )
    }
}
"""
            },
            {
                "filename": "AssemblySupervisor.json",
                "file_type": "json",
                "content": """{
  "name": "AssemblySupervisor",
  "default_operating_states": ["IDLE", "RUNNING", "STOPPED", "FAULT"],
  "activities": [
    {
      "name": "PickPart",
      "requires_operation": true,
      "parameters": ["partType"],
      "commands": [{"name": "pick"}],
      "events": [{"name": "part_detected"}],
      "alarms": [{"name": "gripper_fault"}],
      "dataPoints": [{"name": "part_id"}],
      "transitions": [{"from": "PickPart", "to": "PlacePart"}]
    },
    {
      "name": "PlacePart",
      "requires_operation": true,
      "commands": [{"name": "place"}],
      "events": [{"name": "part_placed"}],
      "transitions": [{"from": "PlacePart", "to": "PickPart"}]
    }
  ]
}
"""
            },
            {
                "filename": "Demo_ECRE.dml",
                "file_type": "dml",
                "content": """DataModel ECRE {
	primitives {
		boolean isValidTruck = false,
		int RFID_TAG_VALUE,
		date ecre_date    
	} 
}
"""
            },
            {
                "filename": "AssemblyCell.mnc",
                "file_type": "mnc",
                "content": """import com.kide.common.*
Model AssemblyCell
InterfaceDescription AssemblyIF {
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
            }
        ]
    }
}

def get_template(key: str) -> Dict[str, Any] | None:
    return REFERENCE_TEMPLATES.get(key)

def list_templates() -> List[Dict[str, Any]]:
    return [
        {
            "key": k,
            "name": v["name"],
            "description": v["description"],
            "file_count": len(v["files"]),
            "files": [f["filename"] for f in v["files"]]
        }
        for k, v in REFERENCE_TEMPLATES.items()
    ]


