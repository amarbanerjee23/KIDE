"""
External Knowledge Hub & Equipment Catalog Service.
Provides comprehensive pre-available real-world device ontologies and DSL specifications:
 1. Boom Barrier Vehicle Entry (RFID Reader, Tag Validator, Gate Motor Actuator)
 2. Industrial Robotic Pick-and-Place (KUKA / UR5 Arm, Vacuum Gripper, Vision Sensor)
 3. Industrial Cooling & Thermal Management (Chiller, Modulating Valve, PT100 RTD)
 4. Chemical Batch Reactor (Dosing Pump, pH Transmitter, Agitator Motor, Jacket Valve)
 5. Smart Building HVAC & Lighting (DALI Dimmer, BACnet Thermostat, PIR Presence Sensor)
 6. Automotive Assembly & Fastening (Nutrunner, Vision Locator, Hydraulic Clamp)
 7. Water Treatment & RO Filtration (High-Pressure Feed Pump, RO Membrane, Turbidity Sensor)
 8. Siemens S7-1500 & SINAMICS S120 Servo (Multi-Axis Motion, STO Safety, Absolute Encoder)
 9. Emerson Fisher FIELDVUE DVC6200 Valve (HART Digital Positioner, Globe Valve, Pressure Sensor)
10. ABB IRB 2600 & IRC5 Robotic Welding (TrueMove Manipulator, Fronius CMT Welder, Seam Tracker)
11. Schneider Altivar ATV930 VFD (Variable Speed Pump Drive, Modbus TCP, Power Meter)
12. Beckhoff CX5130 TwinCAT 3 & EtherCAT (Embedded IPC, Ultra-Fast Slice I/O, Servo Terminal)
13. Endress+Hauser Promass F 300 Flowmeter (Coriolis Mass Flow, Density, Heartbeat Diagnostics)
14. Mobile Industrial Robots MiR250 AMR (Autonomous Mobile Robot, 2D LiDAR SLAM, Fleet Server)
15. Sartorius BIOSTAT B Bioreactor (Cell Fermentation, Dissolved Oxygen Cascade, Peristaltic Pumps)
16. Keyence CV-X400 Optical Quality Vision (High-Speed CMOS Camera, Ring Strobe, OCR Verifier)
17. SMA Sunny Tripower & BESS Energy Storage (Solar PV Inverter, LiFePO4 Rack, SunSpec Modbus)
18. Festo CPX-AP-I Modular Pneumatics (Decentralized Valve Manifold, Proportional Regulator, IO-Link)
19. Haas VF-2 CNC Vertical Machining Center (3-Axis Spindle Vector Drive, Renishaw Touch Probe)
"""

from typing import Any, Dict, List, Optional
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy import select
from ..models.project import Project, ProjectFile

EQUIPMENT_CATALOG: List[Dict[str, Any]] = [
    # -------------------------------------------------------------------------
    # 1. ACCESS CONTROL & SECURITY: BOOM BARRIER
    # -------------------------------------------------------------------------
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
            },
            {
                "filename": "BoomBarrier.activity",
                "file_type": "activity",
                "content": """ActivityDiagram BoomBarrierWorkflow
uses Objects [ string tagId, boolean isAuthorized ]
on context VehicleAuthData
produces results ( boolean gateCompleted )
has activities {
    Activity DetectVehicle {
        description : "Presence sensor detects incoming vehicle"
        requireCapability : "BoomBarrierCap" { VehicleTagDetected }
        nextActivity : PollCredentials
        time : 0.5 secs
    }
    Activity PollCredentials {
        description : "Query RFID authorization database"
        requireOperation ( ValidateTagOperation )
        conditions {
            if outcome isAuthorized is ( = (true) ) => nextActivity : OpenBarrier,
            if outcome isAuthorized is ( = (false) ) => nextActivity : DenyAccess
        }
        time : 1.0 secs
    }
    Activity OpenBarrier {
        description : "Actuate motor driver to lift barrier"
        requireOperation ( ActuateGateOperation )
        nextActivity : AwaitVehicleClear
        time : 3.0 secs
    }
    Activity AwaitVehicleClear {
        description : "Wait until vehicle has cleared the optical safety curtain"
        requireCapability : "BoomBarrierCap" { GateOpened }
        nextActivity : CloseBarrier
        time : 4.0 secs
    }
    Activity CloseBarrier {
        description : "Command gate motor to close barrier"
        requireOperation ( ActuateGateOperation )
        time : 3.0 secs
    }
    Activity DenyAccess {
        description : "Raise access violation alarm and keep barrier closed"
        requireCapability : "BoomBarrierCap" { AccessDeniedAlarm }
    }
}
"""
            }
        ]
    },

    # -------------------------------------------------------------------------
    # 2. INDUSTRIAL ROBOTICS: PICK & PLACE
    # -------------------------------------------------------------------------
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
    float vacuumPressureKPa = -80.0
  }
}
"""
            },
            {
                "filename": "RoboticCell_Cap.cap",
                "file_type": "capability",
                "content": """Capability RoboticArmCap compatible component interface RoboticManipulatorInterface {
  Init {
    fire Commands [ HOME_ARM() ]
    subscribe events [ TargetApproached ]
  }
  providesControlCapabilities {
    fireable commands : MOVE_TO_POSE, ACTIVATE_VACUUM, RELEASE_VACUUM, HOME_ARM
    receivable events : MotionComplete, TargetApproached, PartGrasped
    raised alarms : CollisionDetectedAlarm, VacuumLossAlarm
    subscribable DataPoints : currentPoseX, currentPoseY, currentPoseZ
  }
}
"""
            },
            {
                "filename": "RoboticCell_Ops.op",
                "file_type": "operation",
                "content": """Operation ComputeKinematicsOperation(float x, float y, float z) {
  execute "return kinematics_solver.inverse(x, y, z)"
  return boolean validSolution
}
Operation GripControlOperation(boolean activate) {
  execute "pneumatic_valve.set_vacuum(activate)"
  return boolean gripConfirmed
}
"""
            },
            {
                "filename": "PickAndPlace.activity",
                "file_type": "activity",
                "content": """ActivityDiagram PickAndPlaceWorkflow
uses Objects [ float targetX, boolean gripConfirmed ]
on context CartesianPose
produces results ( boolean cycleFinished )
has activities {
    Activity ScanIncomingPart {
        description : "2D Vision camera captures part location on conveyor"
        requireCapability : "RoboticArmCap" { TargetApproached }
        nextActivity : CalculateApproachTrajectory
        time : 0.8 secs
    }
    Activity CalculateApproachTrajectory {
        description : "Run inverse kinematics algorithm for target coordinates"
        requireOperation ( ComputeKinematicsOperation )
        nextActivity : MoveArmToPart
        time : 0.2 secs
    }
    Activity MoveArmToPart {
        description : "Move 6-DOF arm to grasp position above conveyor"
        requireCapability : "RoboticArmCap" { MotionComplete }
        nextActivity : EngageVacuumGrasp
        time : 1.5 secs
    }
    Activity EngageVacuumGrasp {
        description : "Activate vacuum solenoid valve to grasp part"
        requireOperation ( GripControlOperation )
        conditions {
            if outcome gripConfirmed is ( = (true) ) => nextActivity : TransferToBin,
            if outcome gripConfirmed is ( = (false) ) => nextActivity : AbortAndAlarm
        }
        time : 0.4 secs
    }
    Activity TransferToBin {
        description : "Transfer part along smooth spline trajectory to pallet bin"
        requireCapability : "RoboticArmCap" { MotionComplete }
        nextActivity : ReleasePartInBin
        time : 2.0 secs
    }
    Activity ReleasePartInBin {
        description : "De-energize vacuum valve and verify part detachment"
        requireOperation ( GripControlOperation )
        time : 0.3 secs
    }
    Activity AbortAndAlarm {
        description : "Halt manipulator motion and notify operator of vacuum loss"
        requireCapability : "RoboticArmCap" { VacuumLossAlarm }
    }
}
"""
            }
        ]
    },

    # -------------------------------------------------------------------------
    # 3. THERMAL & PROCESS COOLING: CHILLER LOOP
    # -------------------------------------------------------------------------
    {
        "id": "industrial_cooling_system",
        "name": "Industrial Process Cooling Loop",
        "category": "Process Control & Utilities",
        "thesis_reference": "Chapter 8.3, Table 8.1",
        "description": "Continuous process temperature regulation using circulating chilled water pump, modulating bypass valve, and PT100 temperature sensors.",
        "tags": ["Process Control", "HVAC", "Temperature", "PID", "Valve"],
        "devices": ["Circulating Chiller Pump", "3-Way Modulating Valve", "PT100 Temperature Sensor"],
        "files": [
            {
                "filename": "CoolingLoop_DML.dml",
                "file_type": "dml",
                "content": """Package CoolingLoopPackage
DataModel TemperatureTelemetry {
  primitives {
    float supplyTempC = 7.2,
    float returnTempC = 12.5,
    float deltaTempC = 5.3,
    float pumpSpeedRpm = 1450.0,
    float valvePositionPct = 45.0
  }
}
"""
            },
            {
                "filename": "CoolingLoop_Cap.cap",
                "file_type": "capability",
                "content": """Capability CoolingSystemCap compatible component interface CoolingSystemInterface {
  Init {
    fire Commands [ START_CIRCULATION_PUMP() ]
    subscribe data [ supplyTempC, returnTempC ]
  }
  providesControlCapabilities {
    fireable commands : START_CIRCULATION_PUMP, STOP_CIRCULATION_PUMP, SET_VALVE_POSITION, SET_PUMP_SPEED
    receivable events : TemperatureSetpointReached, PumpRunningEvent
    raised alarms : HighTemperatureAlarm, PumpCavitationAlarm
    subscribable DataPoints : supplyTempC, returnTempC, valvePositionPct
  }
}
"""
            },
            {
                "filename": "CoolingLoop_Ops.op",
                "file_type": "operation",
                "content": """Operation ModulateValveOperation(float valvePositionPct) {
  execute "valve_actuator.write_analog_out(valvePositionPct)"
  return boolean success
}
Operation CalculatePIDOperation(float currentTemp, float setpoint) {
  execute "return pid_controller.compute(currentTemp, setpoint)"
  return float valveOutput
}
"""
            },
            {
                "filename": "CoolingSystem.activity",
                "file_type": "activity",
                "content": """ActivityDiagram CoolingSystemWorkflow
uses Objects [ float currentTemp, float valveOutput ]
on context TemperatureTelemetry
produces results ( boolean coolingSteadyState )
has activities {
    Activity ReadLoopTemperatures {
        description : "Sample supply and return PT100 RTD sensor channels"
        requireCapability : "CoolingSystemCap" { TemperatureSetpointReached }
        nextActivity : ComputeValveTrim
        time : 1.0 secs
    }
    Activity ComputeValveTrim {
        description : "Run PID loop calculation to determine chilled water flow"
        requireOperation ( CalculatePIDOperation )
        nextActivity : AdjustValvePosition
        time : 0.1 secs
    }
    Activity AdjustValvePosition {
        description : "Send 4-20mA positioning signal to 3-way modulating bypass valve"
        requireOperation ( ModulateValveOperation )
        nextActivity : VerifyHeatDissipation
        time : 2.5 secs
    }
    Activity VerifyHeatDissipation {
        description : "Verify process delta T stabilizes within acceptable bounds"
        requireCapability : "CoolingSystemCap" { TemperatureSetpointReached }
        time : 5.0 secs
    }
}
"""
            }
        ]
    },

    # -------------------------------------------------------------------------
    # 4. CHEMICAL PROCESS: BATCH REACTOR
    # -------------------------------------------------------------------------
    {
        "id": "chemical_batch_reactor",
        "name": "Continuous Chemical Batch Reactor",
        "category": "Chemical & Petrochemical",
        "thesis_reference": "Chapter 8.4, Table 8.1",
        "description": "Exothermic chemical reaction control system managing precision acid/base reagent dosing, vessel agitation, and thermal runaway prevention.",
        "tags": ["Chemical", "Batch Process", "Dosing", "Safety Interlock"],
        "devices": ["Peristaltic Dosing Pump", "Magnetic Drive Agitator Motor", "Optical pH Sensor", "Pressure Relief Valve"],
        "files": [
            {
                "filename": "Reactor_DML.dml",
                "file_type": "dml",
                "content": """Package ReactorPackage
DataModel ReactorStateData {
  primitives {
    float coreTempC = 65.0,
    float vesselPressureBar = 1.2,
    float reagentPh = 7.0,
    float agitatorRpm = 350.0,
    boolean coolingJacketEngaged = false
  }
}
"""
            },
            {
                "filename": "Reactor_Cap.cap",
                "file_type": "capability",
                "content": """Capability ReactorControlCap compatible component interface ReactorControlInterface {
  Init {
    fire Commands [ START_AGITATOR(350.0) ]
    subscribe alarms [ ThermalRunawayAlarm ]
  }
  providesControlCapabilities {
    fireable commands : START_AGITATOR, STOP_AGITATOR, DOSE_REAGENT, OPEN_COOLING_JACKET, VENT_VESSEL
    receivable events : PhNeutralized, BatchComplete
    raised alarms : ThermalRunawayAlarm, OverpressureAlarm
    subscribable DataPoints : coreTempC, reagentPh, vesselPressureBar
  }
}
"""
            },
            {
                "filename": "Reactor_Ops.op",
                "file_type": "operation",
                "content": """Operation DoseReagentOperation(float volumeMl, float rateMlPerSec) {
  execute "dosing_pump.pulse(volumeMl, rateMlPerSec)"
  return boolean doseCompleted
}
Operation EngageEmergencyCooling(boolean openJacket) {
  execute "cooling_jacket_valve.set_solenoid(openJacket)"
  return boolean jacketActive
}
"""
            },
            {
                "filename": "ChemicalReactor.activity",
                "file_type": "activity",
                "content": """ActivityDiagram ChemicalReactorWorkflow
uses Objects [ float coreTempC, boolean doseCompleted ]
on context ReactorStateData
produces results ( boolean batchSuccess )
has activities {
    Activity PreHeatJacket {
        description : "Bring reactor vessel up to activation temperature"
        requireCapability : "ReactorControlCap" { PhNeutralized }
        nextActivity : DoseChemicalReagents
        time : 10.0 mins
    }
    Activity DoseChemicalReagents {
        description : "Inject catalyst and chemical reagents at calibrated flow rate"
        requireOperation ( DoseReagentOperation )
        conditions {
            if outcome doseCompleted is ( = (true) ) => nextActivity : MaintainSteadyState,
            if outcome doseCompleted is ( = (false) ) => nextActivity : AbortAndEmergencyVent
        }
        time : 5.0 mins
    }
    Activity MaintainSteadyState {
        description : "Regulate exothermic reaction heat with jacket cooling"
        requireCapability : "ReactorControlCap" { BatchComplete }
        nextActivity : CoolDownVessel
        time : 30.0 mins
    }
    Activity CoolDownVessel {
        description : "Circulate cold glycol to quench reaction and cool vessel"
        requireOperation ( EngageEmergencyCooling )
        time : 15.0 mins
    }
    Activity AbortAndEmergencyVent {
        description : "Open emergency relief valve and flood cooling jacket"
        requireCapability : "ReactorControlCap" { ThermalRunawayAlarm }
    }
}
"""
            }
        ]
    },

    # -------------------------------------------------------------------------
    # 5. SMART BUILDING: HVAC & LIGHTING
    # -------------------------------------------------------------------------
    {
        "id": "smart_meeting_room",
        "name": "Smart Building HVAC & Lighting",
        "category": "Building Automation & Energy",
        "thesis_reference": "Chapter 8.5, Table 8.1",
        "description": "Commercial building automation managing occupancy-triggered DALI dimmable lighting, VAV damper ventilation, and presentation scene automation.",
        "tags": ["Building Automation", "BACnet", "DALI", "Energy Management", "HVAC"],
        "devices": ["PIR Presence Motion Sensor", "DALI Dimmable Luminaire", "BACnet VAV Damper Actuator"],
        "files": [
            {
                "filename": "SmartBuilding_DML.dml",
                "file_type": "dml",
                "content": """Package SmartBuildingPackage
DataModel RoomEnvironmentData {
  primitives {
    boolean occupancyDetected = false,
    float ambientLux = 350.0,
    float roomTempC = 22.5,
    float co2Ppm = 520.0,
    int lightingLevelPct = 80
  }
}
"""
            },
            {
                "filename": "SmartBuilding_Cap.cap",
                "file_type": "capability",
                "content": """Capability SmartBuildingCap compatible component interface SmartBuildingInterface {
  Init {
    subscribe events [ OccupancyDetectedEvent ]
    fire Commands [ SET_ECO_MODE() ]
  }
  providesControlCapabilities {
    fireable commands : SET_LIGHT_LEVEL, SET_VAV_DAMPER, SET_ECO_MODE, ENGAGE_PRESENTATION_SCENE
    receivable events : OccupancyDetectedEvent, SpaceVacatedEvent
    raised alarms : AirQualityWarningAlarm
    subscribable DataPoints : ambientLux, co2Ppm, roomTempC
  }
}
"""
            },
            {
                "filename": "SmartBuilding_Ops.op",
                "file_type": "operation",
                "content": """Operation SetDaliLightingOperation(int brightnessPct) {
  execute "dali_gateway.broadcast_level(brightnessPct)"
  return boolean success
}
Operation ModulateVavDamperOperation(float positionPct) {
  execute "bacnet_controller.write_vav_flow(positionPct)"
  return boolean success
}
"""
            },
            {
                "filename": "SmartRoom.activity",
                "file_type": "activity",
                "content": """ActivityDiagram SmartRoomWorkflow
uses Objects [ boolean occupancyDetected, int lightingLevelPct ]
on context RoomEnvironmentData
produces results ( boolean roomOptimized )
has activities {
    Activity AwaitOccupantArrival {
        description : "PIR motion sensor detects personnel entering the room"
        requireCapability : "SmartBuildingCap" { OccupancyDetectedEvent }
        nextActivity : ActivateComfortLighting
        time : 0.2 secs
    }
    Activity ActivateComfortLighting {
        description : "Ramp DALI fixtures smoothly to 80% lux setpoint"
        requireOperation ( SetDaliLightingOperation )
        nextActivity : AdjustFreshAirVentilation
        time : 1.5 secs
    }
    Activity AdjustFreshAirVentilation {
        description : "Modulate BACnet VAV air damper based on occupancy demand"
        requireOperation ( ModulateVavDamperOperation )
        nextActivity : MaintainIndoorClimate
        time : 2.0 secs
    }
    Activity MaintainIndoorClimate {
        description : "Continuously monitor CO2 and temperature levels"
        requireCapability : "SmartBuildingCap" { SpaceVacatedEvent }
        nextActivity : PowerDownEcoMode
        time : 60.0 mins
    }
    Activity PowerDownEcoMode {
        description : "Fade lights off and set VAV dampers to minimum standby ventilation"
        requireCapability : "SmartBuildingCap" { SET_ECO_MODE }
        time : 10.0 secs
    }
}
"""
            }
        ]
    },

    # -------------------------------------------------------------------------
    # 6. AUTOMOTIVE ASSEMBLY: ROBOTIC FASTENING
    # -------------------------------------------------------------------------
    {
        "id": "automotive_assembly_agv",
        "name": "Automotive Door Assembly Station",
        "category": "Automotive & Discrete Manufacturing",
        "thesis_reference": "Chapter 8.6, Table 8.1",
        "description": "Automotive door framing cell coordinating AGV pallet docking, optical hole localization, hydraulic part clamping, and automated torque nutrunning.",
        "tags": ["Automotive", "Assembly", "Nutrunner", "Torque", "AGV"],
        "devices": ["Electric Torque Nutrunner", "Hydraulic Workpiece Clamp", "Optical Hole Finder Camera", "RFID Pallet Reader"],
        "files": [
            {
                "filename": "AssemblyStation_DML.dml",
                "file_type": "dml",
                "content": """Package AssemblyStationPackage
DataModel FastenerTorqueData {
  primitives {
    string partVin = "WAUZZZ8V1KA000001",
    float targetTorqueNm = 45.0,
    float actualTorqueNm = 0.0,
    float angleDegrees = 0.0,
    boolean torquePass = false
  }
}
"""
            },
            {
                "filename": "AssemblyStation_Cap.cap",
                "file_type": "capability",
                "content": """Capability AssemblyStationCap compatible component interface AssemblyStationInterface {
  Init {
    subscribe events [ PalletArrivedEvent ]
    fire Commands [ CLAMP_WORKPIECE() ]
  }
  providesControlCapabilities {
    fireable commands : CLAMP_WORKPIECE, UNCLAMP_WORKPIECE, FASTEN_BOLT, LOCATE_HOLES
    receivable events : PalletArrivedEvent, FasteningCompleteEvent
    raised alarms : FastenerCrossThreadAlarm, ClampFailureAlarm
    subscribable DataPoints : actualTorqueNm, angleDegrees
  }
}
"""
            },
            {
                "filename": "AssemblyStation_Ops.op",
                "file_type": "operation",
                "content": """Operation ExecuteTorqueTighteningOperation(float targetTorqueNm) {
  execute "atlas_copco_tool.run_torque_angle_program(targetTorqueNm)"
  return boolean torquePass
}
Operation LocateHoleCoordinatesOperation() {
  execute "cognex_vision.find_fastener_datum()"
  return boolean coordinatesValid
}
"""
            },
            {
                "filename": "AssemblySupervisor.activity",
                "file_type": "activity",
                "content": """ActivityDiagram AssemblySupervisorWorkflow
uses Objects [ string partVin, boolean torquePass ]
on context FastenerTorqueData
produces results ( boolean assemblyApproved )
has activities {
    Activity AwaitAGVPalletArrival {
        description : "RFID reader identifies arrival of chassis carrier pallet"
        requireCapability : "AssemblyStationCap" { PalletArrivedEvent }
        nextActivity : ClampWorkpieceSecurely
        time : 2.0 secs
    }
    Activity ClampWorkpieceSecurely {
        description : "Actuate hydraulic clamps to lock door panel against datums"
        requireCapability : "AssemblyStationCap" { CLAMP_WORKPIECE }
        nextActivity : LocateFastenerHoles
        time : 1.2 secs
    }
    Activity LocateFastenerHoles {
        description : "Vision system detects coordinate offsets for bolt holes"
        requireOperation ( LocateHoleCoordinatesOperation )
        nextActivity : FastenStructuralBolts
        time : 0.6 secs
    }
    Activity FastenStructuralBolts {
        description : "Electric nutrunner tightens bolts to calibrated 45.0 Nm torque"
        requireOperation ( ExecuteTorqueTighteningOperation )
        conditions {
            if outcome torquePass is ( = (true) ) => nextActivity : ReleasePalletAndDispatch,
            if outcome torquePass is ( = (false) ) => nextActivity : FlagQualityDefect
        }
        time : 3.8 secs
    }
    Activity ReleasePalletAndDispatch {
        description : "Retract hydraulic clamps and release pallet to next AGV station"
        requireCapability : "AssemblyStationCap" { UNCLAMP_WORKPIECE }
        time : 1.5 secs
    }
    Activity FlagQualityDefect {
        description : "Raise cross-thread alarm and route carrier to rework spur"
        requireCapability : "AssemblyStationCap" { FastenerCrossThreadAlarm }
    }
}
"""
            }
        ]
    },

    # -------------------------------------------------------------------------
    # 7. WATER TREATMENT: RO FILTRATION
    # -------------------------------------------------------------------------
    {
        "id": "water_treatment_plant",
        "name": "Industrial Water Treatment Facility",
        "category": "Water & Waste Management",
        "thesis_reference": "Chapter 8.7, Table 8.1",
        "description": "Multi-stage industrial reverse osmosis (RO) purification plant managing intake pumping, cartridge filtration, high-pressure desalination, and automated membrane backwash.",
        "tags": ["Water Treatment", "Desalination", "Reverse Osmosis", "Turbidity", "Filtration"],
        "devices": ["High-Pressure RO Feed Pump", "Semi-Permeable Membrane Vessel", "Optical Turbidity Sensor", "Automated Backwash Flush Valve"],
        "files": [
            {
                "filename": "WaterTreatment_DML.dml",
                "file_type": "dml",
                "content": """Package WaterTreatmentPackage
DataModel WaterQualityData {
  primitives {
    float rawTurbidityNtu = 12.4,
    float permeatePh = 7.1,
    float conductivityMicroS = 180.0,
    float membraneDeltaPressureBar = 2.8,
    float permeateFlowRateM3h = 45.0
  }
}
"""
            },
            {
                "filename": "WaterTreatment_Cap.cap",
                "file_type": "capability",
                "content": """Capability WaterPurificationControlCap compatible component interface WaterPurificationInterface {
  Init {
    fire Commands [ START_INTAKE_PUMP() ]
    subscribe data [ rawTurbidityNtu, permeatePh ]
  }
  providesControlCapabilities {
    fireable commands : START_INTAKE_PUMP, START_HIGH_PRESSURE_RO_PUMP, TRIGGER_BACKWASH_CYCLE, STOP_PURIFICATION
    receivable events : WaterQualityNominalEvent, PermeateTankFullEvent
    raised alarms : MembraneFoulingAlarm, HighTurbidityAlarm
    subscribable DataPoints : permeatePh, conductivityMicroS, membraneDeltaPressureBar
  }
}
"""
            },
            {
                "filename": "WaterTreatment_Ops.op",
                "file_type": "operation",
                "content": """Operation ExecuteBackwashFlushOperation(float flushDurationSecs) {
  execute "backwash_valve.pulse_reverse_flow(flushDurationSecs)"
  return boolean flushComplete
}
Operation CalculateMembraneFoulingOperation(float deltaPressure, float flowRate) {
  execute "return fouling_estimator.compute_silt_density(deltaPressure, flowRate)"
  return boolean requiresCleaning
}
"""
            },
            {
                "filename": "WaterPurification.activity",
                "file_type": "activity",
                "content": """ActivityDiagram WaterPurificationWorkflow
uses Objects [ float membraneDeltaPressureBar, boolean requiresCleaning ]
on context WaterQualityData
produces results ( boolean potableBatchComplete )
has activities {
    Activity IntakeRawWater {
        description : "Pump source water into primary sand filter chamber"
        requireCapability : "WaterPurificationControlCap" { START_INTAKE_PUMP }
        nextActivity : InspectIntakeTurbidity
        time : 2.0 mins
    }
    Activity InspectIntakeTurbidity {
        description : "Measure turbidity NTU and pH before feeding RO membranes"
        requireCapability : "WaterPurificationControlCap" { WaterQualityNominalEvent }
        nextActivity : HighPressureDesalination
        time : 30.0 secs
    }
    Activity HighPressureDesalination {
        description : "Drive water through spiral-wound RO membranes at 65 bar"
        requireCapability : "WaterPurificationControlCap" { START_HIGH_PRESSURE_RO_PUMP }
        nextActivity : CheckMembraneDifferentialPressure
        time : 45.0 mins
    }
    Activity CheckMembraneDifferentialPressure {
        description : "Calculate transmembrane pressure differential and detect fouling"
        requireOperation ( CalculateMembraneFoulingOperation )
        conditions {
            if outcome requiresCleaning is ( = (false) ) => nextActivity : RoutePermeateToStorage,
            if outcome requiresCleaning is ( = (true) ) => nextActivity : TriggerMembraneBackwash
        }
        time : 1.0 mins
    }
    Activity RoutePermeateToStorage {
        description : "Direct purified drinking water permeate to storage reservoir"
        requireCapability : "WaterPurificationControlCap" { PermeateTankFullEvent }
        time : 60.0 mins
    }
    Activity TriggerMembraneBackwash {
        description : "Execute automated reverse-flow clean-in-place backwash sequence"
        requireOperation ( ExecuteBackwashFlushOperation )
        time : 8.0 mins
    }
}
"""
            }
        ]
    },

    # -------------------------------------------------------------------------
    # 8. SIEMENS S7-1500 & SINAMICS S120 SERVO DRIVE
    # -------------------------------------------------------------------------
    {
        "id": "siemens_motion_s120",
        "name": "Siemens S7-1500 & SINAMICS S120 Servo System",
        "category": "Industrial Motion Control",
        "thesis_reference": "IEC 61131-3, PLCopen Motion Part 1/2, Safe Torque Off (STO)",
        "description": "High-performance multi-axis servo motion control system using Siemens S7-1516F Safety CPU, SINAMICS S120 Double Motor Module, PROFINET IRT, and DRIVE-CLiQ optical encoder feedback.",
        "tags": ["Siemens", "S7-1500", "SINAMICS", "Servo", "PROFINET IRT", "Motion Control", "Safe Torque Off"],
        "devices": ["Siemens S7-1516F-3 PN/DP Safety PLC", "SINAMICS S120 Double Motor Module", "Siemens 1FK7 Synchronous Servo Motor", "Siemens ET 200SP Slice I/O"],
        "files": [
            {
                "filename": "SiemensMotion_DML.dml",
                "file_type": "dml",
                "content": """Package SiemensMotionPackage
DataModel AxisTelemetryData {
  primitives {
    float actualPositionMm = 0.0,
    float actualVelocityMmPerSec = 0.0,
    float torqueActualNm = 0.0,
    boolean isHomed = false,
    boolean isSafeTorqueOffActive = false,
    int driveFaultCode = 0
  }
}
"""
            },
            {
                "filename": "SiemensMotion_Cap.cap",
                "file_type": "capability",
                "content": """Capability SiemensMotionCap compatible component interface SiemensAxisInterface {
  Init {
    fire Commands [ ENABLE_AXIS() ]
    subscribe events [ AxisHomedEvent ]
  }
  providesControlCapabilities {
    fireable commands : ENABLE_AXIS, DISABLE_AXIS, HOME_AXIS, MOVE_ABSOLUTE, ENGAGE_SAFE_TORQUE_OFF, RESET_DRIVE_FAULT
    receivable events : AxisHomedEvent, TargetPositionReachedEvent, SafeTorqueOffEngagedEvent
    raised alarms : FollowingErrorAlarm, DriveThermalOverloadAlarm
    subscribable DataPoints : actualPositionMm, actualVelocityMmPerSec, torqueActualNm
  }
}
"""
            },
            {
                "filename": "SiemensMotion_Ops.op",
                "file_type": "operation",
                "content": """Operation ExecuteSplineMoveOperation(float targetPosMm, float maxVelMmPerSec) {
  execute "sinamics_driver.mc_move_absolute(targetPosMm, maxVelMmPerSec)"
  return boolean moveCompleted
}
Operation EngageSafeTorqueOffOperation() {
  execute "profisafe.set_sto_active(true)"
  return boolean stoConfirmed
}
"""
            },
            {
                "filename": "SiemensMotion.activity",
                "file_type": "activity",
                "content": """ActivityDiagram SiemensMotionWorkflow
uses Objects [ float targetPosMm, boolean moveCompleted ]
on context AxisTelemetryData
produces results ( boolean axisCycleFinished )
has activities {
    Activity VerifySafetyCircuit {
        description : "Check emergency stop buttons and light curtain status via PROFIsafe"
        requireCapability : "SiemensMotionCap" { ENABLE_AXIS }
        nextActivity : HomeServoAxis
        time : 0.5 secs
    }
    Activity HomeServoAxis {
        description : "Execute active reference homing sequence using DRIVE-CLiQ zero marker"
        requireCapability : "SiemensMotionCap" { AxisHomedEvent }
        nextActivity : TraverseToWorkstation
        time : 2.0 secs
    }
    Activity TraverseToWorkstation {
        description : "Command S-curve jerk-limited move to target tooling position"
        requireOperation ( ExecuteSplineMoveOperation )
        conditions {
            if outcome moveCompleted is ( = (true) ) => nextActivity : HoldPositionTorque,
            if outcome moveCompleted is ( = (false) ) => nextActivity : TriggerSafetyStop
        }
        time : 1.8 secs
    }
    Activity HoldPositionTorque {
        description : "Maintain micro-positioning holding torque during manufacturing process"
        requireCapability : "SiemensMotionCap" { TargetPositionReachedEvent }
        time : 3.0 secs
    }
    Activity TriggerSafetyStop {
        description : "De-energize IGBT motor pulse width modulation via Safe Torque Off"
        requireOperation ( EngageSafeTorqueOffOperation )
        time : 0.1 secs
    }
}
"""
            }
        ]
    },

    # -------------------------------------------------------------------------
    # 9. EMERSON FISHER FIELDVUE DVC6200 & HART PROCESS VALVES
    # -------------------------------------------------------------------------
    {
        "id": "emerson_fisher_valves",
        "name": "Emerson Fisher FIELDVUE DVC6200 Valve Controller",
        "category": "Process Flow Control",
        "thesis_reference": "HART 7 Protocol Specification, ANSI/ISA-75 Control Valve Standards",
        "description": "Microprocessor-based digital valve positioner with non-contact Hall effect stem travel feedback, online partial stroke testing (PST), and pneumatically actuated globe control valve.",
        "tags": ["Emerson", "Fisher", "FIELDVUE", "HART", "Pneumatic Valve", "Process Automation"],
        "devices": ["Fisher DVC6200 Digital Valve Controller", "Fisher easy-e ET Control Valve", "Rosemount 3051S Coplanar Pressure Transmitter"],
        "files": [
            {
                "filename": "FisherValve_DML.dml",
                "file_type": "dml",
                "content": """Package FisherValvePackage
DataModel ValvePositionerTelemetry {
  primitives {
    float stemTravelPct = 0.0,
    float targetTravelPct = 0.0,
    float actuatorPressurePsi = 35.0,
    float supplyPressurePsi = 60.0,
    boolean partialStrokeTestPassed = true,
    int cycleCountTotal = 1420
  }
}
"""
            },
            {
                "filename": "FisherValve_Cap.cap",
                "file_type": "capability",
                "content": """Capability FisherValveCap compatible component interface FisherValveInterface {
  Init {
    fire Commands [ RUN_TRAVEL_CALIBRATION() ]
    subscribe data [ stemTravelPct, actuatorPressurePsi ]
  }
  providesControlCapabilities {
    fireable commands : SET_VALVE_TRAVEL, RUN_PARTIAL_STROKE_TEST, RUN_TRAVEL_CALIBRATION, VENT_ACTUATOR
    receivable events : TravelSetpointReachedEvent, PartialStrokeTestCompletedEvent
    raised alarms : TravelDeviationAlarm, SupplyPressureLowAlarm
    subscribable DataPoints : stemTravelPct, actuatorPressurePsi, supplyPressurePsi
  }
}
"""
            },
            {
                "filename": "FisherValve_Ops.op",
                "file_type": "operation",
                "content": """Operation ModulateStemPositionOperation(float targetTravelPct) {
  execute "dvc6200_hart.write_pv_percent(targetTravelPct)"
  return boolean travelConfirmed
}
Operation RunPartialStrokeDiagnosticOperation() {
  execute "dvc6200_hart.trigger_pst_profile()"
  return boolean pstPassed
}
"""
            },
            {
                "filename": "FisherValve.activity",
                "file_type": "activity",
                "content": """ActivityDiagram FisherValveWorkflow
uses Objects [ float targetTravelPct, boolean pstPassed ]
on context ValvePositionerTelemetry
produces results ( boolean valveOnline )
has activities {
    Activity VerifySupplyAirPressure {
        description : "Check instrument air supply pressure exceeds 50 PSI requirement"
        requireCapability : "FisherValveCap" { TravelSetpointReachedEvent }
        nextActivity : ExecutePartialStrokeSafetyTest
        time : 1.0 secs
    }
    Activity ExecutePartialStrokeSafetyTest {
        description : "Command 10% valve stroke perturbation to verify stem freedom without disrupting flow"
        requireOperation ( RunPartialStrokeDiagnosticOperation )
        conditions {
            if outcome pstPassed is ( = (true) ) => nextActivity : ModulateFlowPosition,
            if outcome pstPassed is ( = (false) ) => nextActivity : RaiseValveStictionAlert
        }
        time : 8.0 secs
    }
    Activity ModulateFlowPosition {
        description : "Accurately position globe plug to regulate main pipeline throughput"
        requireOperation ( ModulateStemPositionOperation )
        nextActivity : LogDiagnostics
        time : 3.0 secs
    }
    Activity LogDiagnostics {
        description : "Record stem packing friction and cycle count to asset health database"
        requireCapability : "FisherValveCap" { TravelSetpointReachedEvent }
        time : 0.5 secs
    }
    Activity RaiseValveStictionAlert {
        description : "Alert control room of pneumatic actuator sluggishness or packing galling"
        requireCapability : "FisherValveCap" { TravelDeviationAlarm }
    }
}
"""
            }
        ]
    },

    # -------------------------------------------------------------------------
    # 10. ABB IRB 2600 & IRC5 ROBOTIC WELDING CELL
    # -------------------------------------------------------------------------
    {
        "id": "abb_irc5_robotics",
        "name": "ABB IRB 2600 & IRC5 Arc Welding Cell",
        "category": "Robotics & Heavy Manufacturing",
        "thesis_reference": "RAPID Programming Standard, ISO 10218-1/2 Industrial Robot Safety",
        "description": "High-precision 6-axis arc welding system comprising an ABB IRB 2600 robot manipulator, IRC5 controller running TrueMove motion technology, Fronius Cold Metal Transfer (CMT) power source, and Scansonic optical seam tracking.",
        "tags": ["ABB", "IRC5", "IRB2600", "Arc Welding", "Robotics", "RAPID", "Seam Tracking"],
        "devices": ["ABB IRB 2600 Manipulator Arm", "ABB IRC5 Controller Cabinet", "Fronius TPS 500i CMT Welding Source", "Scansonic Optical Seam Tracker"],
        "files": [
            {
                "filename": "ABBRobotics_DML.dml",
                "file_type": "dml",
                "content": """Package ABBRoboticsPackage
DataModel WeldingProcessTelemetry {
  primitives {
    float arcVoltageVolts = 22.4,
    float weldCurrentAmps = 240.0,
    float wireFeedSpeedMpm = 6.8,
    float torchTraverseSpeedMms = 12.0,
    boolean arcEstablished = false,
    boolean seamTrackingLocked = true
  }
}
"""
            },
            {
                "filename": "ABBRobotics_Cap.cap",
                "file_type": "capability",
                "content": """Capability ABBWeldRoboticsCap compatible component interface ABBRobotInterface {
  Init {
    fire Commands [ POWER_ON_MOTORS() ]
    subscribe events [ ArcIgnitedEvent ]
  }
  providesControlCapabilities {
    fireable commands : POWER_ON_MOTORS, START_WELD_SEAM, EXTINGUISH_ARC, RETRACT_TORCH, CLEAN_GAS_NOZZLE
    receivable events : ArcIgnitedEvent, SeamCompletedEvent, WireStuckEvent
    raised alarms : ArcFailureAlarm, RobotCollisionAlarm, GasShieldFlowLowAlarm
    subscribable DataPoints : arcVoltageVolts, weldCurrentAmps, wireFeedSpeedMpm
  }
}
"""
            },
            {
                "filename": "ABBRobotics_Ops.op",
                "file_type": "operation",
                "content": """Operation ExecuteWeldSeamOperation(float startX, float endX, float wireSpeed) {
  execute "irc5_rapid_client.execute_weld_linear(startX, endX, wireSpeed)"
  return boolean seamSuccessful
}
Operation CleanWeldingTorchNozzle() {
  execute "torch_cleaner.ream_and_spray_antispatter()"
  return boolean torchCleaned
}
"""
            },
            {
                "filename": "ABBRobotics.activity",
                "file_type": "activity",
                "content": """ActivityDiagram ABBWeldWorkflow
uses Objects [ float wireSpeed, boolean seamSuccessful ]
on context WeldingProcessTelemetry
produces results ( boolean weldedJointApproved )
has activities {
    Activity EnergizeRobotDrives {
        description : "Engage IRC5 servo drive contactors and run motor brake release test"
        requireCapability : "ABBWeldRoboticsCap" { POWER_ON_MOTORS }
        nextActivity : ScanJointCoordinates
        time : 1.5 secs
    }
    Activity ScanJointCoordinates {
        description : "Use Scansonic laser seam tracker to triangulate V-groove seam geometry"
        requireCapability : "ABBWeldRoboticsCap" { ArcIgnitedEvent }
        nextActivity : StrikeWeldingArc
        time : 0.8 secs
    }
    Activity StrikeWeldingArc {
        description : "Trigger Fronius CMT power source high-frequency arc ignition"
        requireCapability : "ABBWeldRoboticsCap" { ArcIgnitedEvent }
        nextActivity : ExecuteContinuousWeld
        time : 0.4 secs
    }
    Activity ExecuteContinuousWeld {
        description : "Coordinate 6-axis TCP trajectory with automated wire feed modulation"
        requireOperation ( ExecuteWeldSeamOperation )
        conditions {
            if outcome seamSuccessful is ( = (true) ) => nextActivity : RetractAndServiceTorch,
            if outcome seamSuccessful is ( = (false) ) => nextActivity : HaltArcFailureAlarm
        }
        time : 14.0 secs
    }
    Activity RetractAndServiceTorch {
        description : "Retract arm to maintenance station and execute nozzle reamer clean"
        requireOperation ( CleanWeldingTorchNozzle )
        time : 4.0 secs
    }
    Activity HaltArcFailureAlarm {
        description : "Instantaneously extinguish arc power and trigger welding supervisor halt"
        requireCapability : "ABBWeldRoboticsCap" { ArcFailureAlarm }
    }
}
"""
            }
        ]
    },

    # -------------------------------------------------------------------------
    # 11. SCHNEIDER ELECTRIC ALTIVAR ATV930 VARIABLE SPEED DRIVE
    # -------------------------------------------------------------------------
    {
        "id": "schneider_altivar_vfd",
        "name": "Schneider Altivar ATV930 Variable Speed Drive",
        "category": "Motor Control & Drives",
        "thesis_reference": "Modbus TCP Drive Profile, IEEE 519 Harmonic Control",
        "description": "Industrial heavy-duty variable frequency drive (VFD) for pumping, centrifugal fans, and mechanical conveyors featuring regenerative braking and active thermal overload management.",
        "tags": ["Schneider", "Altivar", "ATV930", "VFD", "Motor Drive", "Modbus TCP", "Energy Efficiency"],
        "devices": ["Altivar Process ATV930 IP21 Drive", "Schneider TeSys Island Motor Starter", "Schneider PowerLogic PM8000 Power Meter"],
        "files": [
            {
                "filename": "SchneiderVFD_DML.dml",
                "file_type": "dml",
                "content": """Package SchneiderVFDPackage
DataModel DriveOperatingTelemetry {
  primitives {
    float outputFrequencyHz = 50.0,
    float motorCurrentAmps = 42.5,
    float motorSpeedRpm = 1480.0,
    float activePowerKw = 28.6,
    float motorThermalPct = 68.0,
    int driveStatusRegister = 16
  }
}
"""
            },
            {
                "filename": "SchneiderVFD_Cap.cap",
                "file_type": "capability",
                "content": """Capability SchneiderDriveCap compatible component interface SchneiderDriveInterface {
  Init {
    fire Commands [ CLEAR_DRIVE_FAULTS() ]
    subscribe data [ outputFrequencyHz, motorCurrentAmps ]
  }
  providesControlCapabilities {
    fireable commands : START_DRIVE_FORWARD, STOP_DRIVE_RAMP, SET_SPEED_REFERENCE, CLEAR_DRIVE_FAULTS
    receivable events : MotorNominalSpeedReachedEvent, DriveStoppedEvent
    raised alarms : MotorOvertemperatureAlarm, PhaseImbalanceAlarm
    subscribable DataPoints : outputFrequencyHz, motorCurrentAmps, activePowerKw
  }
}
"""
            },
            {
                "filename": "SchneiderVFD_Ops.op",
                "file_type": "operation",
                "content": """Operation SetSpeedReferenceOperation(float targetSpeedRpm) {
  execute "atv930_modbus.write_word(0x2135, targetSpeedRpm)"
  return boolean speedAccepted
}
Operation ExecuteDecelerationRampOperation(float rampTimeSecs) {
  execute "atv930_modbus.execute_ramp_down(rampTimeSecs)"
  return boolean stoppedSuccessfully
}
"""
            },
            {
                "filename": "SchneiderVFD.activity",
                "file_type": "activity",
                "content": """ActivityDiagram SchneiderDriveWorkflow
uses Objects [ float targetSpeedRpm, boolean speedAccepted ]
on context DriveOperatingTelemetry
produces results ( boolean driveRunComplete )
has activities {
    Activity CheckPreStartInterlocks {
        description : "Verify motor insulation resistance, phase voltage, and thermal reserve"
        requireCapability : "SchneiderDriveCap" { CLEAR_DRIVE_FAULTS }
        nextActivity : SoftRampAcceleration
        time : 0.8 secs
    }
    Activity SoftRampAcceleration {
        description : "Command VFD inverter bridge to smoothly ramp frequency from 0 to 50 Hz"
        requireOperation ( SetSpeedReferenceOperation )
        conditions {
            if outcome speedAccepted is ( = (true) ) => nextActivity : MaintainPumpingDutyPoint,
            if outcome speedAccepted is ( = (false) ) => nextActivity : RaiseDriveTripAlarm
        }
        time : 6.0 secs
    }
    Activity MaintainPumpingDutyPoint {
        description : "Hold constant speed and monitor active power draw via PowerLogic PM8000"
        requireCapability : "SchneiderDriveCap" { MotorNominalSpeedReachedEvent }
        nextActivity : ControlledRampDown
        time : 120.0 mins
    }
    Activity ControlledRampDown {
        description : "Decelerate motor along S-curve ramp to prevent water hammer pressure surge"
        requireOperation ( ExecuteDecelerationRampOperation )
        time : 10.0 secs
    }
    Activity RaiseDriveTripAlarm {
        description : "Halt VFD run signal and report motor overtemperature event to SCADA"
        requireCapability : "SchneiderDriveCap" { MotorOvertemperatureAlarm }
    }
}
"""
            }
        ]
    },

    # -------------------------------------------------------------------------
    # 12. BECKHOFF TWINCAT 3 & ETHERCAT HIGH-SPEED SLICE I/O
    # -------------------------------------------------------------------------
    {
        "id": "beckhoff_twincat_ethercat",
        "name": "Beckhoff CX5130 TwinCAT 3 & EtherCAT System",
        "category": "High-Speed Automation",
        "thesis_reference": "EtherCAT Technology Group (ETG), IEC 61158 Fieldbus Standard",
        "description": "Deterministic microsecond-level motion and discrete control platform utilizing Beckhoff CX5130 Embedded PC, TwinCAT 3 soft-PLC, and EtherCAT modular slice terminals with Distributed Clocks (DC).",
        "tags": ["Beckhoff", "TwinCAT", "EtherCAT", "Deterministic", "Embedded PC", "Packaging"],
        "devices": ["Beckhoff CX5130 Embedded PC", "Beckhoff EK1100 EtherCAT Coupler", "Beckhoff EL7211 Servo Slice Terminal", "Beckhoff EL3064 Analog Input Slice"],
        "files": [
            {
                "filename": "BeckhoffControl_DML.dml",
                "file_type": "dml",
                "content": """Package BeckhoffControlPackage
DataModel EtherCATBusTelemetry {
  primitives {
    int busCycleTimeUs = 250,
    int distributedClockJitterNs = 45,
    float currentPositionMm = 120.5,
    float feederAnalogVoltage = 7.42,
    boolean isBusSynchronized = true
  }
}
"""
            },
            {
                "filename": "BeckhoffControl_Cap.cap",
                "file_type": "capability",
                "content": """Capability BeckhoffSliceCap compatible component interface BeckhoffEtherCATInterface {
  Init {
    fire Commands [ INITIALIZE_ETHERCAT_BUS() ]
    subscribe data [ currentPositionMm, feederAnalogVoltage ]
  }
  providesControlCapabilities {
    fireable commands : INITIALIZE_ETHERCAT_BUS, SYNC_DISTRIBUTED_CLOCKS, DISPATCH_CAM_PROFILE, TRIGGER_HIGH_SPEED_LATCH
    receivable events : DistributedClocksLockedEvent, HighSpeedLatchCapturedEvent
    raised alarms : WorkingCounterInvalidAlarm, SyncJitterExceededAlarm
    subscribable DataPoints : busCycleTimeUs, distributedClockJitterNs, currentPositionMm
  }
}
"""
            },
            {
                "filename": "BeckhoffControl_Ops.op",
                "file_type": "operation",
                "content": """Operation SynchronizeBusClocksOperation() {
  execute "twincat_ads.send_dc_sync_command(PORT_MOTION)"
  return boolean syncConfirmed
}
Operation ExecuteElectronicCammingOperation(float masterRatio) {
  execute "nc_motion.engage_electronic_gearing(masterRatio)"
  return boolean cammingActive
}
"""
            },
            {
                "filename": "BeckhoffControl.activity",
                "file_type": "activity",
                "content": """ActivityDiagram BeckhoffPackagingWorkflow
uses Objects [ float masterRatio, boolean syncConfirmed ]
on context EtherCATBusTelemetry
produces results ( boolean packagingBatchFinished )
has activities {
    Activity ScanEtherCATSlaves {
        description : "Enumerate EK1100 bus couplers and verify Working Counter integrity"
        requireCapability : "BeckhoffSliceCap" { INITIALIZE_ETHERCAT_BUS }
        nextActivity : LockDistributedClocks
        time : 0.2 secs
    }
    Activity LockDistributedClocks {
        description : "Phase-lock EtherCAT hardware clocks to sub-100 nanosecond jitter tolerance"
        requireOperation ( SynchronizeBusClocksOperation )
        conditions {
            if outcome syncConfirmed is ( = (true) ) => nextActivity : EngageHighSpeedElectronicCam,
            if outcome syncConfirmed is ( = (false) ) => nextActivity : RaiseJitterFaultAlarm
        }
        time : 0.5 secs
    }
    Activity EngageHighSpeedElectronicCam {
        description : "Couple rotary knife servomotor to continuous packaging film feed"
        requireOperation ( ExecuteElectronicCammingOperation )
        nextActivity : ContinuousFormFillSeal
        time : 1.0 secs
    }
    Activity ContinuousFormFillSeal {
        description : "Execute 250 microsecond synchronized packaging feed and rotary seal"
        requireCapability : "BeckhoffSliceCap" { HighSpeedLatchCapturedEvent }
        time : 60.0 mins
    }
    Activity RaiseJitterFaultAlarm {
        description : "Abort deterministic bus transmission on lost sync pulse"
        requireCapability : "BeckhoffSliceCap" { SyncJitterExceededAlarm }
    }
}
"""
            }
        ]
    },

    # -------------------------------------------------------------------------
    # 13. ENDRESS+HAUSER PROMASS F 300 CORIOLIS FLOWMETER
    # -------------------------------------------------------------------------
    {
        "id": "endress_hauser_flowmeter",
        "name": "Endress+Hauser Promass F 300 Coriolis Flowmeter",
        "category": "Process Instrumentation & Flow",
        "thesis_reference": "OIML R 117 Custody Transfer, NAMUR NE 107 Diagnostics",
        "description": "Multivariable Coriolis mass flowmeter for custody transfer and high-precision fluid billing providing direct real-time mass flow, density, temperature, and Heartbeat Technology self-diagnostics.",
        "tags": ["Endress+Hauser", "Coriolis", "Mass Flow", "Density", "Heartbeat Diagnostics", "Food & Beverage", "Chemical"],
        "devices": ["E+H Promass F 300 Coriolis Sensor", "E+H Liquiline CM44P Multi-Parameter Transmitter", "E+H Cerabar M PMC51 Pressure Transmitter"],
        "files": [
            {
                "filename": "EndressFlow_DML.dml",
                "file_type": "dml",
                "content": """Package EndressFlowPackage
DataModel CoriolisFlowTelemetry {
  primitives {
    float massFlowRateKgH = 12500.0,
    float fluidDensityKgM3 = 998.2,
    float fluidTemperatureC = 24.6,
    float batchAccumulatedMassKg = 0.0,
    int sensorIntegrityIndex = 98
  }
}
"""
            },
            {
                "filename": "EndressFlow_Cap.cap",
                "file_type": "capability",
                "content": """Capability EndressCoriolisCap compatible component interface EndressFlowInterface {
  Init {
    fire Commands [ EXECUTE_ZERO_POINT_ADJUST() ]
    subscribe data [ massFlowRateKgH, fluidDensityKgM3 ]
  }
  providesControlCapabilities {
    fireable commands : START_BATCH_TOTALIZER, STOP_BATCH_TOTALIZER, EXECUTE_ZERO_POINT_ADJUST, RUN_HEARTBEAT_VERIFICATION
    receivable events : BatchTargetReachedEvent, HeartbeatPassedEvent
    raised alarms : SensorTubeCorrosionAlarm, EmptyPipeDetectionAlarm
    subscribable DataPoints : massFlowRateKgH, fluidDensityKgM3, fluidTemperatureC
  }
}
"""
            },
            {
                "filename": "EndressFlow_Ops.op",
                "file_type": "operation",
                "content": """Operation ExecuteHeartbeatVerificationOperation() {
  execute "promass300_modbus.trigger_heartbeat_test()"
  return boolean verificationPassed
}
Operation ResetTotalizerAndStartBatch(float targetKg) {
  execute "promass300_modbus.configure_batch_preset(targetKg)"
  return boolean totalizerStarted
}
"""
            },
            {
                "filename": "EndressFlow.activity",
                "file_type": "activity",
                "content": """ActivityDiagram SanitaryDispensingWorkflow
uses Objects [ float targetKg, boolean verificationPassed ]
on context CoriolisFlowTelemetry
produces results ( boolean transferCertified )
has activities {
    Activity CheckZeroPointStability {
        description : "Verify measuring tube oscillation resonance with static fluid"
        requireCapability : "EndressCoriolisCap" { EXECUTE_ZERO_POINT_ADJUST }
        nextActivity : RunHeartbeatDiagnostics
        time : 2.0 secs
    }
    Activity RunHeartbeatDiagnostics {
        description : "Perform non-intrusive internal sensor validation according to NAMUR NE 107"
        requireOperation ( ExecuteHeartbeatVerificationOperation )
        conditions {
            if outcome verificationPassed is ( = (true) ) => nextActivity : DispenseSanitaryLiquidBatch,
            if outcome verificationPassed is ( = (false) ) => nextActivity : TriggerTubeFoulingAlarm
        }
        time : 5.0 secs
    }
    Activity DispenseSanitaryLiquidBatch {
        description : "Open fill valve and measure mass flow until target batch kilogram is reached"
        requireOperation ( ResetTotalizerAndStartBatch )
        nextActivity : FinalizeBatchReceipt
        time : 30.0 mins
    }
    Activity FinalizeBatchReceipt {
        description : "Log totalized mass, density, and temperature to pharmaceutical batch log"
        requireCapability : "EndressCoriolisCap" { BatchTargetReachedEvent }
        time : 1.0 secs
    }
    Activity TriggerTubeFoulingAlarm {
        description : "Halt batch transfer due to tube coating or erosion outside calibration limits"
        requireCapability : "EndressCoriolisCap" { SensorTubeCorrosionAlarm }
    }
}
"""
            }
        ]
    },

    # -------------------------------------------------------------------------
    # 14. MOBILE INDUSTRIAL ROBOTS MIR250 AMR INTRALOGISTICS
    # -------------------------------------------------------------------------
    {
        "id": "mir_amr_intralogistics",
        "name": "Mobile Industrial Robots MiR250 Autonomous Mobile Robot",
        "category": "Intralogistics & Mobile Robotics",
        "thesis_reference": "ISO 3691-4 Industrial AGV/AMR Safety, VDA 5050 Fleet Interface",
        "description": "Autonomous mobile robot (AMR) designed for agile intralogistics material transfer featuring 250 kg payload capacity, 360-degree safety LiDAR coverage, SLAM navigation, and VDA 5050 fleet dispatch coordination.",
        "tags": ["MiR", "AMR", "AGV", "Mobile Robotics", "SLAM", "LiDAR", "Intralogistics", "VDA 5050"],
        "devices": ["MiR250 Autonomous Mobile Robot Base", "SICK S300 Safety Laser Scanner", "Intel RealSense D435 3D Camera", "MiR Fleet Dispatch Server"],
        "files": [
            {
                "filename": "MiRLogistics_DML.dml",
                "file_type": "dml",
                "content": """Package MiRLogisticsPackage
DataModel AMRNavigationTelemetry {
  primitives {
    float coordinateX = 24.5,
    float coordinateY = 12.8,
    float headingTheta = 1.57,
    float batterySocPct = 84.0,
    float payloadWeightKg = 180.0,
    boolean isDockedToStation = false
  }
}
"""
            },
            {
                "filename": "MiRLogistics_Cap.cap",
                "file_type": "capability",
                "content": """Capability MiRLogisticsCap compatible component interface MiRAMRInterface {
  Init {
    subscribe events [ DestinationWaypointArrivedEvent ]
    fire Commands [ DOCK_TO_CHARGER() ]
  }
  providesControlCapabilities {
    fireable commands : NAVIGATE_TO_STATION, PICKUP_PALLET_RACK, DROP_PALLET_RACK, DOCK_TO_CHARGER, EMERGENCY_E_STOP
    receivable events : DestinationWaypointArrivedEvent, PalletTransferCompleteEvent, ObstacleAvoidanceEngagedEvent
    raised alarms : SafetyLaserMutingViolationAlarm, BatteryDepletedAlarm
    subscribable DataPoints : coordinateX, coordinateY, batterySocPct
  }
}
"""
            },
            {
                "filename": "MiRLogistics_Ops.op",
                "file_type": "operation",
                "content": """Operation DispatchToWaypointOperation(float x, float y, float theta) {
  execute "mir_vda5050.post_order_node(x, y, theta)"
  return boolean orderAccepted
}
Operation ActuateTopRollerConveyor(boolean load) {
  execute "top_roller_module.run_conveyor_direction(load)"
  return boolean transferComplete
}
"""
            },
            {
                "filename": "MiRLogistics.activity",
                "file_type": "activity",
                "content": """ActivityDiagram IntralogisticsMissionWorkflow
uses Objects [ float x, boolean orderAccepted ]
on context AMRNavigationTelemetry
produces results ( boolean deliveryMissionDone )
has activities {
    Activity CheckBatteryStateOfCharge {
        description : "Query onboard battery BMS to confirm minimum 30% charge for mission"
        requireCapability : "MiRLogisticsCap" { DestinationWaypointArrivedEvent }
        nextActivity : DispatchToWarehousePickPoint
        time : 0.5 secs
    }
    Activity DispatchToWarehousePickPoint {
        description : "Navigate autonomously through facility avoiding dynamic pedestrians"
        requireOperation ( DispatchToWaypointOperation )
        conditions {
            if outcome orderAccepted is ( = (true) ) => nextActivity : AlignWithPalletSpur,
            if outcome orderAccepted is ( = (false) ) => nextActivity : HaltSafetyLaserMuting
        }
        time : 45.0 secs
    }
    Activity AlignWithPalletSpur {
        description : "Use 3D camera to precisely dock against stationary conveyor transfer roller"
        requireCapability : "MiRLogisticsCap" { DestinationWaypointArrivedEvent }
        nextActivity : TransferPalletCargo
        time : 3.0 secs
    }
    Activity TransferPalletCargo {
        description : "Synchronize AMR top roller module with warehouse discharge track"
        requireOperation ( ActuateTopRollerConveyor )
        nextActivity : TransportToProductionLine
        time : 4.5 secs
    }
    Activity TransportToProductionLine {
        description : "Deliver pallet load to CNC machining cell assembly line"
        requireCapability : "MiRLogisticsCap" { PalletTransferCompleteEvent }
        time : 60.0 secs
    }
    Activity HaltSafetyLaserMuting {
        description : "Trigger yellow strobe and wait for safety field clearance"
        requireCapability : "MiRLogisticsCap" { SafetyLaserMutingViolationAlarm }
    }
}
"""
            }
        ]
    },

    # -------------------------------------------------------------------------
    # 15. SARTORIUS BIOSTAT B BIOREACTOR / FERMENTATION
    # -------------------------------------------------------------------------
    {
        "id": "sartorius_biostat_bioreactor",
        "name": "Sartorius BIOSTAT B Bioreactor & Fermentation System",
        "category": "Biotechnology & Pharmaceuticals",
        "thesis_reference": "FDA 21 CFR Part 11, ISA-88 Batch Control in Life Sciences",
        "description": "Precision laboratory and pilot-scale single-use bioreactor system controlling mammalian and bacterial cell cultures with optical dissolved oxygen cascade, peristaltic acid/base pumps, and thermal jacket regulation.",
        "tags": ["Sartorius", "Bioreactor", "Fermentation", "Cell Culture", "Dissolved Oxygen", "Pharma"],
        "devices": ["Sartorius BIOSTAT B Tower", "Mettler Toledo InPro 6860i Optical DO Sensor", "Hamilton EasyFerm Plus pH Sensor", "Watson-Marlow Peristaltic Dosing Pump"],
        "files": [
            {
                "filename": "Bioreactor_DML.dml",
                "file_type": "dml",
                "content": """Package BioreactorPackage
DataModel BioreactorProcessTelemetry {
  primitives {
    float dissolvedOxygenPct = 40.0,
    float culturePh = 7.15,
    float vesselTemperatureC = 37.0,
    float agitatorSpeedRpm = 250.0,
    float o2GasFlowLpm = 1.2,
    boolean antifoamDosed = false
  }
}
"""
            },
            {
                "filename": "Bioreactor_Cap.cap",
                "file_type": "capability",
                "content": """Capability BioreactorControlCap compatible component interface BioreactorInterface {
  Init {
    fire Commands [ START_STIRRING(250.0) ]
    subscribe data [ dissolvedOxygenPct, culturePh ]
  }
  providesControlCapabilities {
    fireable commands : START_STIRRING, SET_DO_CASCADE_SETPOINT, DOSE_ACID_TITRANT, DOSE_BASE_TITRANT, INJECT_ANTIFOAM
    receivable events : InoculationReadyEvent, DissolvedOxygenStabilizedEvent, BatchGrowthHarvestReadyEvent
    raised alarms : DissolvedOxygenLowAlarm, FoamLevelExceededAlarm
    subscribable DataPoints : dissolvedOxygenPct, culturePh, vesselTemperatureC
  }
}
"""
            },
            {
                "filename": "Bioreactor_Ops.op",
                "file_type": "operation",
                "content": """Operation RegulateDissolvedOxygenCascadeOperation(float targetDOPct) {
  execute "biostat_cascade.adjust_sparge_gas_and_rpm(targetDOPct)"
  return boolean doCascadeLocked
}
Operation DoseAcidBaseTitrantOperation(string titrantType, float volumeMl) {
  execute "watson_marlow_pump.micro_dispense(titrantType, volumeMl)"
  return boolean doseDelivered
}
"""
            },
            {
                "filename": "Bioreactor.activity",
                "file_type": "activity",
                "content": """ActivityDiagram BioreactorBatchWorkflow
uses Objects [ float targetDOPct, boolean doCascadeLocked ]
on context BioreactorProcessTelemetry
produces results ( boolean batchHarvested )
has activities {
    Activity SterilizeAndInoculate {
        description : "Verify vessel sterility, calibrate optical probes, and inoculate seed cells"
        requireCapability : "BioreactorControlCap" { InoculationReadyEvent }
        nextActivity : EngageDissolvedOxygenCascade
        time : 10.0 mins
    }
    Activity EngageDissolvedOxygenCascade {
        description : "Modulate agitation RPM and oxygen enrichment to maintain critical 40% DO"
        requireOperation ( RegulateDissolvedOxygenCascadeOperation )
        conditions {
            if outcome doCascadeLocked is ( = (true) ) => nextActivity : MonitorExponentialGrowth,
            if outcome doCascadeLocked is ( = (false) ) => nextActivity : TriggerHypoxiaAlarm
        }
        time : 2.0 mins
    }
    Activity MonitorExponentialGrowth {
        description : "Maintain steady-state pH, glucose feeding, and dissolved oxygen over cell growth cycle"
        requireCapability : "BioreactorControlCap" { DissolvedOxygenStabilizedEvent }
        nextActivity : HarvestBiologicalBroth
        time : 120.0 hrs
    }
    Activity HarvestBiologicalBroth {
        description : "Chill vessel to 4 C to arrest metabolism and transfer broth to centrifuge"
        requireCapability : "BioreactorControlCap" { BatchGrowthHarvestReadyEvent }
        time : 45.0 mins
    }
    Activity TriggerHypoxiaAlarm {
        description : "Alert laboratory technicians to oxygen starvation in bioreactor core"
        requireCapability : "BioreactorControlCap" { DissolvedOxygenLowAlarm }
    }
}
"""
            }
        ]
    },

    # -------------------------------------------------------------------------
    # 16. KEYENCE CV-X400 MACHINE VISION INSPECTION
    # -------------------------------------------------------------------------
    {
        "id": "keyence_vision_inspection",
        "name": "Keyence CV-X400 High-Speed Optical Vision System",
        "category": "Quality Control & Vision Inspection",
        "thesis_reference": "ISO 9001 Zero-Defect Optical Quality Verification",
        "description": "High-speed in-line automated optical inspection (AOI) system utilizing Keyence CV-X480F image processor, 2-megapixel high-speed camera, strobe illumination, and automated pattern/dimension defect analysis.",
        "tags": ["Keyence", "Machine Vision", "Optical Inspection", "Quality Control", "Defect Detection", "OCR"],
        "devices": ["Keyence CV-X480F Vision Controller", "Keyence CA-HX200M Monochrome Camera", "Keyence CA-DRW10 Ring Light Strobe", "Banner Engineering Q4X Laser Distance Sensor"],
        "files": [
            {
                "filename": "KeyenceVision_DML.dml",
                "file_type": "dml",
                "content": """Package KeyenceVisionPackage
DataModel InspectionResultData {
  primitives {
    int inspectionTriggerId = 1045,
    float defectMatchScore = 98.4,
    float measuredWidthMm = 25.42,
    boolean isDimensionWithinTolerance = true,
    string decodedDatamatrix = "BATCH-2026-X99",
    float processingTimeMs = 18.5
  }
}
"""
            },
            {
                "filename": "KeyenceVision_Cap.cap",
                "file_type": "capability",
                "content": """Capability KeyenceVisionCap compatible component interface KeyenceVisionInterface {
  Init {
    fire Commands [ SELECT_INSPECTION_PROGRAM(1) ]
    subscribe events [ ImageAcquisitionCompleteEvent ]
  }
  providesControlCapabilities {
    fireable commands : TRIGGER_INSPECTION, SELECT_INSPECTION_PROGRAM, ADJUST_STROBE_INTENSITY, REJECT_PART_PNEUMATIC
    receivable events : ImageAcquisitionCompleteEvent, InspectionPassedEvent, DefectDetectedEvent
    raised alarms : VisionProcessingTimeoutAlarm, StrobeLampFaultAlarm
    subscribable DataPoints : defectMatchScore, measuredWidthMm, processingTimeMs
  }
}
"""
            },
            {
                "filename": "KeyenceVision_Ops.op",
                "file_type": "operation",
                "content": """Operation TriggerVisionCaptureOperation(int programSlot) {
  execute "keyence_cvx.trigger_shutter_sync(programSlot)"
  return boolean inspectionValid
}
Operation ActuateRejectDiverterOperation() {
  execute "reject_air_cylinder.fire_solenoid(50)"
  return boolean partEjected
}
"""
            },
            {
                "filename": "KeyenceVision.activity",
                "file_type": "activity",
                "content": """ActivityDiagram OpticalQualityInspectionWorkflow
uses Objects [ int programSlot, boolean inspectionValid ]
on context InspectionResultData
produces results ( boolean partInspectionApproved )
has activities {
    Activity DetectPartAtInspectionStation {
        description : "Banner laser distance sensor detects arrival of stamped component on conveyor"
        requireCapability : "KeyenceVisionCap" { ImageAcquisitionCompleteEvent }
        nextActivity : TriggerStrobeExposure
        time : 0.05 secs
    }
    Activity TriggerStrobeExposure {
        description : "Synchronously fire white LED ring light strobe and capture 2-megapixel image"
        requireOperation ( TriggerVisionCaptureOperation )
        conditions {
            if outcome inspectionValid is ( = (true) ) => nextActivity : AcceptPartToShipping,
            if outcome inspectionValid is ( = (false) ) => nextActivity : EjectDefectPart
        }
        time : 0.02 secs
    }
    Activity AcceptPartToShipping {
        description : "Part verified defect-free; update serial tracking database and permit transit"
        requireCapability : "KeyenceVisionCap" { InspectionPassedEvent }
        time : 0.1 secs
    }
    Activity EjectDefectPart {
        description : "Fire high-speed pneumatic blow-off nozzle to reject non-conforming part into scrap bin"
        requireOperation ( ActuateRejectDiverterOperation )
        time : 0.08 secs
    }
}
"""
            }
        ]
    },

    # -------------------------------------------------------------------------
    # 17. SMA SUNNY TRIPOWER & BESS ENERGY STORAGE MICROGRID
    # -------------------------------------------------------------------------
    {
        "id": "sma_solar_bess_inverter",
        "name": "SMA Sunny Tripower & BESS Energy Storage",
        "category": "Renewable Energy & Microgrids",
        "thesis_reference": "SunSpec Modbus Alliance Standard, IEEE 1547 Grid Interconnection",
        "description": "Utility-scale hybrid solar PV and battery energy storage system (BESS) managing active power curtailment, peak shaving, frequency stabilization, and LiFePO4 battery rack health balancing.",
        "tags": ["SMA", "Solar", "Inverter", "BESS", "Battery Storage", "SunSpec", "Microgrid", "Renewables"],
        "devices": ["SMA Sunny Tripower CORE1 Inverter", "BYD Battery-Box Commercial LiFePO4 Rack", "SMA Data Manager M Gateway", "Socomec Diris A-40 Grid Meter"],
        "files": [
            {
                "filename": "SolarBESS_DML.dml",
                "file_type": "dml",
                "content": """Package SolarBESSPackage
DataModel MicrogridEnergyTelemetry {
  primitives {
    float pvGenerationPowerKw = 48.5,
    float batteryChargeDischargeKw = -15.0,
    float batteryStateOfChargePct = 78.5,
    float gridExportPowerKw = 33.5,
    float gridFrequencyHz = 50.02,
    boolean isGridConnected = true
  }
}
"""
            },
            {
                "filename": "SolarBESS_Cap.cap",
                "file_type": "capability",
                "content": """Capability SolarBESSCap compatible component interface SolarBESSInterface {
  Init {
    fire Commands [ CONNECT_GRID_SYNCHRONOUS() ]
    subscribe data [ pvGenerationPowerKw, batteryStateOfChargePct ]
  }
  providesControlCapabilities {
    fireable commands : CONNECT_GRID_SYNCHRONOUS, SET_CURTAILMENT_LIMIT, DISCHARGE_BATTERY_PEAK, CHARGE_BATTERY_SOLAR, ISLAND_MICROGRID
    receivable events : GridSynchronizedEvent, BatteryFullEvent, PeakShavingActiveEvent
    raised alarms : GridOverfrequencyAlarm, BatteryCellThermalRunawayAlarm
    subscribable DataPoints : pvGenerationPowerKw, batteryStateOfChargePct, gridExportPowerKw
  }
}
"""
            },
            {
                "filename": "SolarBESS_Ops.op",
                "file_type": "operation",
                "content": """Operation SetActivePowerCurtailmentOperation(float maxExportKw) {
  execute "sunspec_modbus.write_wmaxlim_pct(maxExportKw)"
  return boolean limitApplied
}
Operation DispatchBatteryPowerOperation(float chargeRateKw) {
  execute "bms_canbus.command_power_setpoint(chargeRateKw)"
  return boolean batteryDispatched
}
"""
            },
            {
                "filename": "SolarBESS.activity",
                "file_type": "activity",
                "content": """ActivityDiagram RenewableEnergyDispatchWorkflow
uses Objects [ float maxExportKw, boolean limitApplied ]
on context MicrogridEnergyTelemetry
produces results ( boolean energyDispatched )
has activities {
    Activity SampleGridFrequency {
        description : "Monitor utility interconnect frequency and voltage vector via Socomec meter"
        requireCapability : "SolarBESSCap" { GridSynchronizedEvent }
        nextActivity : AssessSolarGenerationHeadroom
        time : 0.5 secs
    }
    Activity AssessSolarGenerationHeadroom {
        description : "Determine whether midday solar surplus should charge BESS or export to utility"
        requireCapability : "SolarBESSCap" { PeakShavingActiveEvent }
        nextActivity : DispatchBatteryCharging
        time : 1.0 secs
    }
    Activity DispatchBatteryCharging {
        description : "Direct excess 15 kW DC solar energy into LiFePO4 battery rack"
        requireOperation ( DispatchBatteryPowerOperation )
        conditions {
            if outcome limitApplied is ( = (true) ) => nextActivity : EnforceExportCap,
            if outcome limitApplied is ( = (false) ) => nextActivity : TriggerOverfrequencyFault
        }
        time : 30.0 mins
    }
    Activity EnforceExportCap {
        description : "Clamp grid export power below contractual 35 kW export limit"
        requireOperation ( SetActivePowerCurtailmentOperation )
        time : 60.0 mins
    }
    Activity TriggerOverfrequencyFault {
        description : "Isolate microgrid circuit breaker on severe grid frequency excursion"
        requireCapability : "SolarBESSCap" { GridOverfrequencyAlarm }
    }
}
"""
            }
        ]
    },

    # -------------------------------------------------------------------------
    # 18. FESTO CPX-AP-I MODULAR PNEUMATICS & VALVE TERMINAL
    # -------------------------------------------------------------------------
    {
        "id": "festo_cpx_pneumatics",
        "name": "Festo CPX-AP-I Decentralized Modular Valve Terminal",
        "category": "Pneumatics & Fluid Power",
        "thesis_reference": "ISO 19973 Pneumatic Reliability, IO-Link Community Standard",
        "description": "IP65 decentralized pneumatic valve terminal and IO-Link master controlling high-cycle linear cylinders, proportional pressure regulators, and automated predictive air leak detection.",
        "tags": ["Festo", "Pneumatics", "Valve Terminal", "IO-Link", "Proportional Valve", "Fluid Power"],
        "devices": ["Festo CPX-AP-I-EP Bus Interface", "Festo MPA-L Pneumatic Directional Valve Terminal", "Festo VPPM Proportional Pressure Regulator", "Festo SPAN Digital Pressure Sensor"],
        "files": [
            {
                "filename": "FestoPneumatics_DML.dml",
                "file_type": "dml",
                "content": """Package FestoPneumaticsPackage
DataModel PneumaticSystemTelemetry {
  primitives {
    float mainSupplyPressureBar = 6.2,
    float regulatedPressureBar = 4.0,
    float airFlowRateLpm = 120.0,
    float totalVolumeConsumedNm3 = 14.5,
    int valveCoilSwitchCount = 38400,
    boolean isLeakageDetected = false
  }
}
"""
            },
            {
                "filename": "FestoPneumatics_Cap.cap",
                "file_type": "capability",
                "content": """Capability FestoPneumaticsCap compatible component interface FestoValveTerminalInterface {
  Init {
    fire Commands [ ACTUATE_CYLINDER_RETRACT() ]
    subscribe data [ mainSupplyPressureBar, airFlowRateLpm ]
  }
  providesControlCapabilities {
    fireable commands : ACTUATE_CYLINDER_EXTEND, ACTUATE_CYLINDER_RETRACT, SET_PROPORTIONAL_PRESSURE, RUN_LEAKAGE_DIAGNOSTIC
    receivable events : CylinderExtendedEvent, CylinderRetractedEvent, RegulatedPressureReachedEvent
    raised alarms : MainAirPressureLowAlarm, ValveCoilStuckAlarm
    subscribable DataPoints : mainSupplyPressureBar, regulatedPressureBar, airFlowRateLpm
  }
}
"""
            },
            {
                "filename": "FestoPneumatics_Ops.op",
                "file_type": "operation",
                "content": """Operation SetProportionalPressureOperation(float targetPressureBar) {
  execute "vppm_iolink.write_target_bar(targetPressureBar)"
  return boolean pressureLocked
}
Operation RunAirLeakageTestOperation() {
  execute "span_sensor.measure_pressure_drop_over_time(10.0)"
  return boolean leakageWithinSpec
}
"""
            },
            {
                "filename": "FestoPneumatics.activity",
                "file_type": "activity",
                "content": """ActivityDiagram PneumaticActuationWorkflow
uses Objects [ float targetPressureBar, boolean pressureLocked ]
on context PneumaticSystemTelemetry
produces results ( boolean pneumaticCycleComplete )
has activities {
    Activity CheckHeaderAirSupply {
        description : "Ensure plant compressed air main supply exceeds 5.5 bar threshold"
        requireCapability : "FestoPneumaticsCap" { RegulatedPressureReachedEvent }
        nextActivity : SetActuatorClampingPressure
        time : 0.5 secs
    }
    Activity SetActuatorClampingPressure {
        description : "Command Festo VPPM regulator to dial in precise 4.0 bar gripping force"
        requireOperation ( SetProportionalPressureOperation )
        conditions {
            if outcome pressureLocked is ( = (true) ) => nextActivity : ExtendPneumaticCylinder,
            if outcome pressureLocked is ( = (false) ) => nextActivity : RaisePressureLowAlarm
        }
        time : 0.3 secs
    }
    Activity ExtendPneumaticCylinder {
        description : "Energize directional valve pilot solenoid to extend clamping cylinder"
        requireCapability : "FestoPneumaticsCap" { CylinderExtendedEvent }
        nextActivity : RetractPneumaticCylinder
        time : 1.2 secs
    }
    Activity RetractPneumaticCylinder {
        description : "Exhaust forward chamber and retract cylinder back to park home reed switch"
        requireCapability : "FestoPneumaticsCap" { CylinderRetractedEvent }
        nextActivity : RunLeakCheckInterval
        time : 1.0 secs
    }
    Activity RunLeakCheckInterval {
        description : "Execute 10-second decay test to detect seal leakage or hose splits"
        requireOperation ( RunAirLeakageTestOperation )
        time : 10.0 secs
    }
    Activity RaisePressureLowAlarm {
        description : "Interlock manufacturing machine and alert maintenance of low pneumatic pressure"
        requireCapability : "FestoPneumaticsCap" { MainAirPressureLowAlarm }
    }
}
"""
            }
        ]
    },

    # -------------------------------------------------------------------------
    # 19. HAAS VF-2 CNC 3-AXIS VERTICAL MACHINING CENTER
    # -------------------------------------------------------------------------
    {
        "id": "haas_cnc_machining",
        "name": "Haas VF-2 3-Axis CNC Machining Center",
        "category": "CNC Machining & Subtractive Manufacturing",
        "thesis_reference": "ISO 6983 Numerical Control (G-Code), MTConnect Standard",
        "description": "3-axis precision CNC vertical milling center with Haas NextGen Control, 10,000 RPM inline direct-drive spindle, wireless Renishaw optical workpiece/tool probe, and through-spindle coolant system.",
        "tags": ["Haas", "CNC", "Machining", "Milling", "Renishaw", "Spindle", "MTConnect", "G-Code"],
        "devices": ["Haas VF-2 Vertical Machining Center", "Renishaw OMP40-2 Optical Machine Probe", "Haas 10000 RPM Vector Drive Spindle", "LNS Turbo Chip Conveyor & Coolant"],
        "files": [
            {
                "filename": "HaasCNC_DML.dml",
                "file_type": "dml",
                "content": """Package HaasCNCPackage
DataModel CNCMachineTelemetry {
  primitives {
    float spindleSpeedRpm = 8500.0,
    float spindleLoadPct = 42.0,
    float feedRateMmMin = 1200.0,
    int activeToolPocket = 4,
    float coolantPressurePsi = 280.0,
    boolean probeDeflection = false
  }
}
"""
            },
            {
                "filename": "HaasCNC_Cap.cap",
                "file_type": "capability",
                "content": """Capability HaasCNCCap compatible component interface HaasCNCInterface {
  Init {
    fire Commands [ START_SPINDLE(8500.0) ]
    subscribe events [ ToolChangeCompleteEvent ]
  }
  providesControlCapabilities {
    fireable commands : START_SPINDLE, STOP_SPINDLE, ENGAGE_COOLANT_PUMP, EXECUTE_TOOL_PROBE, RUN_NC_PROGRAM
    receivable events : SpindleAtSpeedEvent, ToolChangeCompleteEvent, WorkpieceProbedEvent
    raised alarms : SpindleThermalOverloadAlarm, CoolantPressureLossAlarm
    subscribable DataPoints : spindleSpeedRpm, spindleLoadPct, feedRateMmMin
  }
}
"""
            },
            {
                "filename": "HaasCNC_Ops.op",
                "file_type": "operation",
                "content": """Operation MeasureWorkpieceDatumOperation() {
  execute "renishaw_probe.run_wcs_g54_probe_cycle()"
  return boolean datumSet
}
Operation RunHighPressureCoolantOperation(boolean enable) {
  execute "coolant_pump.set_through_spindle_flow(enable)"
  return boolean coolantActive
}
"""
            },
            {
                "filename": "HaasCNC.activity",
                "file_type": "activity",
                "content": """ActivityDiagram CNCMachiningWorkflow
uses Objects [ float spindleSpeedRpm, boolean datumSet ]
on context CNCMachineTelemetry
produces results ( boolean partMachinedSuccessfully )
has activities {
    Activity ProbeWorkpieceDatum {
        description : "Use Renishaw optical touch probe to establish G54 X-Y-Z work coordinates"
        requireOperation ( MeasureWorkpieceDatumOperation )
        conditions {
            if outcome datumSet is ( = (true) ) => nextActivity : SpinUpSpindleWithCoolant,
            if outcome datumSet is ( = (false) ) => nextActivity : AbortProbingAlarm
        }
        time : 8.0 secs
    }
    Activity SpinUpSpindleWithCoolant {
        description : "Accelerate 30 HP vector drive spindle to 8500 RPM and activate 280 PSI through-tool coolant"
        requireOperation ( RunHighPressureCoolantOperation )
        nextActivity : ExecuteMillingProfile
        time : 2.5 secs
    }
    Activity ExecuteMillingProfile {
        description : "Execute high-speed dynamic adaptive toolpath across aerospace aluminum stock"
        requireCapability : "HaasCNCCap" { SpindleAtSpeedEvent }
        nextActivity : StopSpindleAndInspect
        time : 12.0 mins
    }
    Activity StopSpindleAndInspect {
        description : "Orient spindle, deactivate coolant, and signal automated door open for part unclamp"
        requireCapability : "HaasCNCCap" { ToolChangeCompleteEvent }
        time : 3.0 secs
    }
    Activity AbortProbingAlarm {
        description : "Halt cycle due to missing workpiece raw stock or unexpected probe crash"
        requireCapability : "HaasCNCCap" { SpindleThermalOverloadAlarm }
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
        """Returns the complete list of equipment templates without file contents."""
        return [
            {
                "id": item["id"],
                "name": item["name"],
                "category": item["category"],
                "thesis_reference": item["thesis_reference"],
                "description": item["description"],
                "tags": item["tags"],
                "devices": item["devices"],
                "file_count": len(item.get("files", []))
            }
            for item in EQUIPMENT_CATALOG
        ]

    @classmethod
    def get_catalog_item(cls, catalog_id: str) -> Optional[Dict[str, Any]]:
        """Returns a specific equipment catalog item with its complete files."""
        for item in EQUIPMENT_CATALOG:
            if item["id"] == catalog_id:
                return item
        return None

    @classmethod
    async def import_to_project(
        cls,
        db: AsyncSession,
        project_id: int,
        catalog_id: str
    ) -> List[Dict[str, Any]]:
        """Imports all files from a catalog item into a specific project."""
        item = cls.get_catalog_item(catalog_id)
        if not item:
            raise ValueError(f"Catalog item '{catalog_id}' not found")

        imported_files = []
        for f in item.get("files", []):
            # Check if file already exists in project
            res = await db.execute(select(ProjectFile).where(
                ProjectFile.project_id == project_id,
                ProjectFile.filename == f["filename"]
            ))
            existing = res.scalars().first()

            if existing:
                existing.content = f["content"]
                await db.commit()
                imported_files.append({"id": existing.id, "name": existing.filename, "filename": existing.filename, "file_type": f["file_type"], "action": "updated"})
            else:
                new_file = ProjectFile(
                    project_id=project_id,
                    filename=f["filename"],
                    file_type=f["file_type"],
                    content=f["content"]
                )
                db.add(new_file)
                await db.commit()
                await db.refresh(new_file)
                imported_files.append({"id": new_file.id, "name": new_file.filename, "filename": new_file.filename, "file_type": f["file_type"], "action": "created"})

        return imported_files

    @classmethod
    async def import_single_file_to_project(
        cls,
        db: AsyncSession,
        project_id: int,
        filename: str,
        file_type: str,
        content: str
    ) -> Dict[str, Any]:
        """Imports a single reusable file/entity directly into a project."""
        res = await db.execute(select(ProjectFile).where(
            ProjectFile.project_id == project_id,
            ProjectFile.filename == filename
        ))
        existing = res.scalars().first()

        if existing:
            existing.content = content
            await db.commit()
            return {"id": existing.id, "name": existing.filename, "filename": existing.filename, "file_type": file_type, "action": "updated"}
        else:
            new_file = ProjectFile(
                project_id=project_id,
                filename=filename,
                file_type=file_type,
                content=content
            )
            db.add(new_file)
            await db.commit()
            await db.refresh(new_file)
            return {"id": new_file.id, "name": new_file.filename, "filename": new_file.filename, "file_type": file_type, "action": "created"}

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

class KnowledgeCatalogHelper:
    def __init__(self):
        import re
        self.capabilities: Dict[str, Any] = {}
        self.operations: Dict[str, Any] = {}
        self.activities: Dict[str, Any] = {}
        self.datamodels: Dict[str, Any] = {}

        for item in EQUIPMENT_CATALOG:
            for f in item.get("files", []):
                fname = f.get("filename", "")
                content = f.get("content", "")
                if fname.endswith(".cap") or fname.endswith(".capability"):
                    m = re.search(r"Capability\s+([A-Za-z0-9_]+)", content)
                    if m:
                        self.capabilities[m.group(1)] = {"catalog_item": item, "file": f}
                elif fname.endswith(".op") or fname.endswith(".operation"):
                    for m in re.finditer(r"Operation\s+([A-Za-z0-9_]+)", content):
                        self.operations[m.group(1)] = {"catalog_item": item, "file": f}
                elif fname.endswith(".activity"):
                    m = re.search(r"ActivityDiagram\s+([A-Za-z0-9_]+)", content)
                    if m:
                        self.activities[m.group(1)] = {"catalog_item": item, "file": f}
                elif fname.endswith(".dml"):
                    for m in re.finditer(r"DataModel\s+([A-Za-z0-9_]+)", content):
                        self.datamodels[m.group(1)] = {"catalog_item": item, "file": f}

_knowledge_catalog_helper = None

def get_knowledge_hub_service() -> KnowledgeCatalogHelper:
    global _knowledge_catalog_helper
    if _knowledge_catalog_helper is None:
        _knowledge_catalog_helper = KnowledgeCatalogHelper()
    return _knowledge_catalog_helper
