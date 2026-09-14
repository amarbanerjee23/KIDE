"""
Enterprise Document Extraction & DSL Synthesis Engine.
Extracts device parameters, registers, electrical ratings, commands, and alarms
from industrial datasheets, manuals, CSV register maps, and JSON specifications,
and synthesizes canonical DML, MNC-ML, Capability, and Operation DSL artifacts.
"""

import re
import csv
import json
import io
from typing import Any, Dict, List, Optional, Tuple

class DocumentExtractor:
    """
    Intelligent extraction engine for industrial equipment documentation.
    """

    SAMPLE_TEMPLATES: Dict[str, Dict[str, Any]] = {
        "honeywell_st700": {
            "id": "honeywell_st700",
            "name": "Honeywell ST700 SmartLine Pressure Transmitter",
            "manufacturer": "Honeywell Process Solutions",
            "category": "Pressure & Level Measurement",
            "protocol": "HART",
            "file_type": "txt",
            "description": "High-performance piezoresistive pressure transmitter designed for differential, gauge, and absolute pressure measurement in harsh petrochemical environments.",
            "device_metadata": {
                "manufacturer": "Honeywell Process Solutions",
                "model": "ST700-SmartLine",
                "part_number": "STD720-E1AC4AS",
                "power_supply": "10.8 to 42.4 VDC",
                "operating_temperature": "-40 to +85 °C",
                "enclosure_rating": "IP66 / IP67 NEMA 4X",
                "accuracy": "±0.05% of span",
                "communication": "HART 7 / 4-20mA"
            },
            "datapoints": [
                {"name": "pressure_pv", "type": "float", "unit": "bar", "min_val": 0.0, "max_val": 100.0, "description": "Primary measured process variable pressure"},
                {"name": "temperature_sv", "type": "float", "unit": "degC", "min_val": -40.0, "max_val": 125.0, "description": "Secondary sensor temperature"},
                {"name": "loop_current", "type": "float", "unit": "mA", "min_val": 4.0, "max_val": 20.0, "description": "Analog output loop current"},
                {"name": "sensor_health_status", "type": "int", "unit": "enum", "min_val": 0, "max_val": 5, "description": "Diagnostic health state index"}
            ],
            "commands": [
                {"name": "ZERO_TRIM", "parameters": [], "return_type": "boolean", "description": "Perform zero elevation/suppression trim calibration"},
                {"name": "SET_DAMPING", "parameters": [{"name": "seconds", "type": "float"}], "return_type": "boolean", "description": "Configure digital output damping filter time constant"},
                {"name": "CALIBRATE_SPAN", "parameters": [{"name": "pressure", "type": "float"}], "return_type": "boolean", "description": "Apply high pressure calibration reference span"},
                {"name": "RESET_SENSOR", "parameters": [], "return_type": "boolean", "description": "Soft reset microprocessor and diagnostic registers"}
            ],
            "alarms": [
                {"name": "OVERPRESSURE_ALARM", "level": 2, "condition": "PV > 105.0 bar", "description": "Process pressure exceeded calibrated maximum range"},
                {"name": "SENSOR_FAULT_ALARM", "level": 1, "condition": "HealthStatus != 0", "description": "Internal sensor bridge or ASIC hardware failure"},
                {"name": "LOOP_CURRENT_FAULT", "level": 2, "condition": "Current < 3.8mA or > 20.5mA", "description": "Loop current out of NAMUR NE43 compliance boundaries"}
            ]
        },
        "emerson_dvc6200": {
            "id": "emerson_dvc6200",
            "name": "Fisher FIELDVUE DVC6200 Digital Valve Controller",
            "manufacturer": "Emerson Automation Solutions",
            "category": "Valves & Actuation",
            "protocol": "HART",
            "file_type": "txt",
            "description": "Microprocessor-based digital valve positioner with linkage-less non-contact travel feedback for control valves.",
            "device_metadata": {
                "manufacturer": "Emerson Automation Solutions",
                "model": "DVC6200-HART",
                "part_number": "DVC6200-HC-AC",
                "power_supply": "11 to 30 VDC",
                "operating_temperature": "-52 to +85 °C",
                "enclosure_rating": "IP66 Type 4X",
                "air_supply_pressure": "1.4 to 7.0 bar",
                "communication": "HART 7"
            },
            "datapoints": [
                {"name": "travel_percent", "type": "float", "unit": "pct", "min_val": 0.0, "max_val": 100.0, "description": "Valve stem actual position percentage"},
                {"name": "drive_signal", "type": "float", "unit": "pct", "min_val": 0.0, "max_val": 100.0, "description": "Internal I/P converter drive current percentage"},
                {"name": "supply_pressure", "type": "float", "unit": "bar", "min_val": 0.0, "max_val": 10.0, "description": "Instrument air supply input pressure"},
                {"name": "air_relay_pressure", "type": "float", "unit": "bar", "min_val": 0.0, "max_val": 10.0, "description": "Pneumatic relay output pressure to actuator chamber"},
                {"name": "cycle_count", "type": "int", "unit": "cycles", "min_val": 0, "max_val": 10000000, "description": "Cumulative valve direction reversal cycles"}
            ],
            "commands": [
                {"name": "STROKE_VALVE", "parameters": [{"name": "targetPercent", "type": "float"}], "return_type": "boolean", "description": "Command valve stem to target position setpoint"},
                {"name": "PARTIAL_STROKE_TEST", "parameters": [], "return_type": "boolean", "description": "Execute automated online partial stroke test for safety valves"},
                {"name": "CALIBRATE_TRAVEL", "parameters": [], "return_type": "boolean", "description": "Run auto-travel calibration routine across end-stops"},
                {"name": "ABORT_TRAVEL", "parameters": [], "return_type": "boolean", "description": "Abort travel and lock valve in fail-safe position"}
            ],
            "alarms": [
                {"name": "TRAVEL_DEVIATION_ALARM", "level": 2, "condition": "|Setpoint - Travel| > 5%", "description": "Valve stem travel deviates beyond acceptable deadband"},
                {"name": "SUPPLY_PRESSURE_LOW", "level": 1, "condition": "SupplyPressure < 2.0 bar", "description": "Instrument air supply pressure below operating minimum"},
                {"name": "VALVE_STUCK_ALARM", "level": 1, "condition": "DriveSignal == 100% and TravelDelta == 0", "description": "Valve stem mechanically seized or packing friction excessive"}
            ]
        },
        "schneider_atv320": {
            "id": "schneider_atv320",
            "name": "Schneider Electric Altivar Machine ATV320 Variable Speed Drive",
            "manufacturer": "Schneider Electric",
            "category": "Motor Drives & Motion",
            "protocol": "Modbus-RTU",
            "file_type": "txt",
            "description": "Variable frequency inverter drive for 3-phase asynchronous and synchronous motors in industrial machine automation.",
            "device_metadata": {
                "manufacturer": "Schneider Electric",
                "model": "ATV320U15N4B",
                "rated_power_kw": "1.5 kW",
                "power_supply": "380 to 500 VAC 3-phase",
                "operating_temperature": "-10 to +60 °C",
                "enclosure_rating": "IP20",
                "communication": "Modbus-RTU RS485 / CANopen"
            },
            "datapoints": [
                {"name": "motor_speed_rpm", "type": "float", "unit": "rpm", "min_val": 0.0, "max_val": 3600.0, "description": "Calculated motor shaft rotation speed"},
                {"name": "output_frequency_hz", "type": "float", "unit": "Hz", "min_val": 0.0, "max_val": 500.0, "description": "Inverter output stator excitation frequency"},
                {"name": "motor_current_amps", "type": "float", "unit": "A", "min_val": 0.0, "max_val": 30.0, "description": "Motor RMS stator output current"},
                {"name": "dc_bus_voltage", "type": "float", "unit": "V", "min_val": 0.0, "max_val": 800.0, "description": "Intermediate DC bus link voltage"},
                {"name": "drive_thermal_state", "type": "float", "unit": "pct", "min_val": 0.0, "max_val": 150.0, "description": "Thermal overload accumulator percentage"},
                {"name": "drive_state_word", "type": "int", "unit": "hex", "min_val": 0, "max_val": 65535, "description": "CiA 402 drive status word"}
            ],
            "commands": [
                {"name": "RUN_FORWARD", "parameters": [], "return_type": "boolean", "description": "Command drive transition to Operation Enabled with clockwise rotation"},
                {"name": "RUN_REVERSE", "parameters": [], "return_type": "boolean", "description": "Command drive transition with counter-clockwise rotation"},
                {"name": "STOP_RAMP", "parameters": [], "return_type": "boolean", "description": "Decelerate motor to stop along configured deceleration ramp"},
                {"name": "FAST_STOP", "parameters": [], "return_type": "boolean", "description": "Execute fast emergency braking deceleration"},
                {"name": "SET_FREQUENCY_REF", "parameters": [{"name": "targetHz", "type": "float"}], "return_type": "boolean", "description": "Set speed reference setpoint in Hertz"},
                {"name": "FAULT_RESET", "parameters": [], "return_type": "boolean", "description": "Acknowledge and reset latched drive faults"}
            ],
            "alarms": [
                {"name": "OVERCURRENT_ALARM", "level": 1, "condition": "Current > 25.0 A", "description": "Instantaneous inverter bridge overcurrent protection trip"},
                {"name": "MOTOR_OVERLOAD_ALARM", "level": 2, "condition": "ThermalState > 100%", "description": "Motor I2t thermal overload protection triggered"},
                {"name": "UNDERVOLTAGE_ALARM", "level": 2, "condition": "DCBusVoltage < 350V", "description": "Mains supply phase sag or input undervoltage fault"}
            ]
        },
        "keyence_iv3": {
            "id": "keyence_iv3",
            "name": "Keyence IV3 Vision Sensor with Built-in AI",
            "manufacturer": "Keyence Corporation",
            "category": "Machine Vision & Inspection",
            "protocol": "EtherNet/IP",
            "file_type": "json",
            "description": "Vision sensor with auto-focus lens and built-in edge AI for automated part presence, orientation, and defect inspection.",
            "device_metadata": {
                "manufacturer": "Keyence Corporation",
                "model": "IV3-G500MA",
                "resolution": "1280 x 960 monochrome CMOS",
                "power_supply": "24 VDC ±10%",
                "operating_temperature": "0 to +50 °C",
                "enclosure_rating": "IP67",
                "communication": "EtherNet/IP / PROFINET / TCP"
            },
            "datapoints": [
                {"name": "inspection_result", "type": "boolean", "unit": "pass_fail", "min_val": 0, "max_val": 1, "description": "Overall OK/NG inspection verdict"},
                {"name": "similarity_score", "type": "float", "unit": "pct", "min_val": 0.0, "max_val": 100.0, "description": "AI feature match similarity score percentage"},
                {"name": "part_count", "type": "int", "unit": "pcs", "min_val": 0, "max_val": 999999, "description": "Total inspected part batch count"},
                {"name": "cycle_time_ms", "type": "float", "unit": "ms", "min_val": 0.0, "max_val": 5000.0, "description": "Image capture and AI inference processing time"}
            ],
            "commands": [
                {"name": "CAPTURE_AND_INSPECT", "parameters": [], "return_type": "boolean", "description": "Trigger electronic shutter and perform AI classification"},
                {"name": "SWITCH_PROGRAM", "parameters": [{"name": "programId", "type": "int"}], "return_type": "boolean", "description": "Switch active inspection job recipe and model weights"},
                {"name": "TEACH_BACKGROUND", "parameters": [], "return_type": "boolean", "description": "Calibrate background illumination cancellation matrix"},
                {"name": "CLEAR_PART_COUNTER", "parameters": [], "return_type": "boolean", "description": "Reset batch inspected part counters"}
            ],
            "alarms": [
                {"name": "INSPECTION_FAIL_ALARM", "level": 2, "condition": "InspectionResult == false", "description": "Consecutive defective parts detected on conveyor"},
                {"name": "LIGHTING_FAULT_ALARM", "level": 1, "condition": "IlluminationDuty < 50%", "description": "Strobe LED lighting intensity failure detected"}
            ]
        },
        "endress_promass": {
            "id": "endress_promass",
            "name": "Endress+Hauser Promass F 300 Coriolis Flowmeter",
            "manufacturer": "Endress+Hauser",
            "category": "Flow & Mass Measurement",
            "protocol": "Modbus-TCP",
            "file_type": "txt",
            "description": "Multi-variable Coriolis flowmeter for continuous, precise mass flow, volume flow, fluid density, and temperature measurement.",
            "device_metadata": {
                "manufacturer": "Endress+Hauser",
                "model": "Promass F 300",
                "part_number": "8F3B50-AAIC",
                "nominal_diameter": "DN 50 (2 inch)",
                "power_supply": "24 VDC / 100-240 VAC",
                "operating_temperature": "-50 to +200 °C",
                "enclosure_rating": "IP67 Type 4X",
                "communication": "Modbus-TCP / PROFINET"
            },
            "datapoints": [
                {"name": "mass_flow_rate", "type": "float", "unit": "kg_h", "min_val": 0.0, "max_val": 70000.0, "description": "Real-time fluid mass flow rate"},
                {"name": "volume_flow_rate", "type": "float", "unit": "m3_h", "min_val": 0.0, "max_val": 70.0, "description": "Calculated volumetric flow rate"},
                {"name": "fluid_density", "type": "float", "unit": "kg_m3", "min_val": 500.0, "max_val": 2000.0, "description": "Oscillating measuring tube resonance fluid density"},
                {"name": "fluid_temperature", "type": "float", "unit": "degC", "min_val": -50.0, "max_val": 200.0, "description": "Process media temperature via internal PT1000 sensor"},
                {"name": "totalizer_mass", "type": "float", "unit": "kg", "min_val": 0.0, "max_val": 100000000.0, "description": "Non-volatile cumulative totalized mass"}
            ],
            "commands": [
                {"name": "ZERO_POINT_ADJUST", "parameters": [], "return_type": "boolean", "description": "Execute automated zero-flow calibration adjustment"},
                {"name": "RESET_TOTALIZER", "parameters": [], "return_type": "boolean", "description": "Clear cumulative batch totalizer counters"},
                {"name": "RUN_HEARTBEAT_VERIFICATION", "parameters": [], "return_type": "boolean", "description": "Trigger in-situ SIL diagnostic verification routine"}
            ],
            "alarms": [
                {"name": "EMPTY_PIPE_ALARM", "level": 2, "condition": "Density < 400.0 kg/m3", "description": "Air pockets or empty measuring tube condition detected"},
                {"name": "TUBE_CORROSION_WARNING", "level": 3, "condition": "ResonanceFrequencyShift > 2%", "description": "Coriolis oscillation frequency drift indicating wall erosion"}
            ]
        }
    }

    @classmethod
    def get_sample_templates(cls) -> List[Dict[str, Any]]:
        """Returns list of pre-configured industrial equipment templates."""
        return [
            {
                "id": t["id"],
                "name": t["name"],
                "category": t["category"],
                "protocol": t["protocol"],
                "manufacturer": t["manufacturer"],
                "description": t["description"],
                "file_type": t["file_type"]
            }
            for t in cls.SAMPLE_TEMPLATES.values()
        ]

    @classmethod
    def extract_from_sample(cls, sample_id: str) -> Dict[str, Any]:
        """Loads a pre-configured industrial sample and synthesizes all DSL files."""
        if sample_id not in cls.SAMPLE_TEMPLATES:
            raise ValueError(f"Unknown sample template: {sample_id}")
        data = cls.SAMPLE_TEMPLATES[sample_id]
        return cls._build_extracted_artifact(data)

    @classmethod
    def extract_document(
        cls,
        filename: str,
        file_type: str,
        content: str
    ) -> List[Dict[str, Any]]:
        """
        Parses uploaded file content (TXT, CSV, JSON, Markdown) and extracts
        structured device specs, registers, commands, alarms, and synthesized DSLs.
        """
        file_type_clean = (file_type or "txt").lower().replace(".", "")
        
        # 1. Try parsing JSON specification
        if file_type_clean == "json" or content.strip().startswith("{"):
            try:
                parsed_json = json.loads(content)
                return [cls._extract_from_json(filename, parsed_json)]
            except json.JSONDecodeError:
                pass # fallback to text extraction

        # 2. Try parsing CSV register list
        if file_type_clean == "csv" or ("," in content and "\n" in content and any(kw in content.lower() for kw in ["register", "address", "parameter", "datapoint"])):
            try:
                csv_artifact = cls._extract_from_csv(filename, content)
                if csv_artifact:
                    return [csv_artifact]
            except Exception:
                pass

        # 3. Default: Intelligent NLP / Regex extraction from technical text / datasheet
        text_artifact = cls._extract_from_text(filename, content)
        return [text_artifact]

    @classmethod
    def _extract_from_json(cls, filename: str, data: Dict[str, Any]) -> Dict[str, Any]:
        """Extracts specification from JSON structure."""
        base_name = data.get("name") or data.get("device_name") or filename.rsplit(".", 1)[0].replace("_", " ").title()
        manufacturer = data.get("manufacturer") or data.get("vendor", "Generic Industrial")
        category = data.get("category", "Industrial Equipment")
        protocol = data.get("protocol", "Modbus-RTU")

        metadata = data.get("device_metadata") or data.get("metadata") or {
            "manufacturer": manufacturer,
            "model": data.get("model", base_name),
            "protocol": protocol
        }

        # Extract datapoints
        raw_dps = data.get("datapoints") or data.get("parameters") or data.get("registers") or []
        datapoints = []
        for i, dp in enumerate(raw_dps):
            if isinstance(dp, dict):
                dp_name = cls._sanitize_identifier(dp.get("name", f"param_{i+1}"))
                dp_type = dp.get("type", "float").lower()
                if dp_type not in ["float", "int", "boolean", "string"]:
                    dp_type = "float" if "float" in dp_type or "double" in dp_type or "analog" in dp_type else "int"
                datapoints.append({
                    "name": dp_name,
                    "type": dp_type,
                    "unit": dp.get("unit", "raw"),
                    "min_val": float(dp.get("min_val", dp.get("min", 0.0))),
                    "max_val": float(dp.get("max_val", dp.get("max", 100.0))),
                    "description": dp.get("description", f"Extracted parameter {dp_name}")
                })

        if not datapoints:
            datapoints = [
                {"name": "process_value", "type": "float", "unit": "pct", "min_val": 0.0, "max_val": 100.0, "description": "Primary Process Value"},
                {"name": "status_code", "type": "int", "unit": "code", "min_val": 0, "max_val": 255, "description": "Device Status Code"}
            ]

        # Extract commands
        raw_cmds = data.get("commands") or data.get("methods") or data.get("actions") or []
        commands = []
        for cmd in raw_cmds:
            if isinstance(cmd, dict):
                cname = cls._sanitize_identifier(cmd.get("name", "EXECUTE_CMD")).upper()
                commands.append({
                    "name": cname,
                    "parameters": cmd.get("parameters", []),
                    "return_type": cmd.get("return_type", "boolean"),
                    "description": cmd.get("description", f"Execute {cname}")
                })
            elif isinstance(cmd, str):
                cname = cls._sanitize_identifier(cmd).upper()
                commands.append({
                    "name": cname,
                    "parameters": [],
                    "return_type": "boolean",
                    "description": f"Execute {cname}"
                })

        if not commands:
            commands = [
                {"name": "INITIALIZE", "parameters": [], "return_type": "boolean", "description": "Initialize device"},
                {"name": "START", "parameters": [], "return_type": "boolean", "description": "Start normal operation"},
                {"name": "STOP", "parameters": [], "return_type": "boolean", "description": "Stop operation and safe state"}
            ]

        # Extract alarms
        raw_alarms = data.get("alarms") or data.get("faults") or []
        alarms = []
        for alm in raw_alarms:
            if isinstance(alm, dict):
                aname = cls._sanitize_identifier(alm.get("name", "FAULT_ALARM")).upper()
                alarms.append({
                    "name": aname,
                    "level": int(alm.get("level", 2)),
                    "condition": alm.get("condition", "ErrorState == true"),
                    "description": alm.get("description", f"Alarm condition for {aname}")
                })
            elif isinstance(alm, str):
                aname = cls._sanitize_identifier(alm).upper()
                alarms.append({
                    "name": aname,
                    "level": 2,
                    "condition": "Error == true",
                    "description": f"Alarm {aname}"
                })

        if not alarms:
            alarms = [
                {"name": "DEVICE_FAULT_ALARM", "level": 1, "condition": "Status != 0", "description": "General device failure alarm"},
                {"name": "COMMUNICATION_TIMEOUT", "level": 2, "condition": "PingLost == true", "description": "Communication link loss"}
            ]

        spec_data = {
            "name": base_name,
            "category": category,
            "protocol": protocol,
            "device_metadata": metadata,
            "datapoints": datapoints,
            "commands": commands,
            "alarms": alarms
        }
        return cls._build_extracted_artifact(spec_data)

    @classmethod
    def _extract_from_csv(cls, filename: str, content: str) -> Optional[Dict[str, Any]]:
        """Parses CSV register / parameter tables."""
        reader = csv.reader(io.StringIO(content))
        rows = list(reader)
        if len(rows) < 2:
            return None

        header = [c.strip().lower() for c in rows[0]]
        name_idx = next((i for i, h in enumerate(header) if any(k in h for k in ["name", "parameter", "register", "signal", "point"])), 0)
        type_idx = next((i for i, h in enumerate(header) if "type" in h or "format" in h), -1)
        unit_idx = next((i for i, h in enumerate(header) if "unit" in h or "dimension" in h), -1)
        min_idx = next((i for i, h in enumerate(header) if "min" in h or "low" in h), -1)
        max_idx = next((i for i, h in enumerate(header) if "max" in h or "high" in h), -1)
        desc_idx = next((i for i, h in enumerate(header) if "desc" in h or "comment" in h), -1)

        datapoints = []
        for row in rows[1:]:
            if not row or len(row) <= name_idx or not row[name_idx].strip():
                continue
            pname = cls._sanitize_identifier(row[name_idx].strip())
            ptype = "float"
            if type_idx != -1 and len(row) > type_idx:
                raw_type = row[type_idx].lower()
                if "int" in raw_type or "word" in raw_type or "short" in raw_type:
                    ptype = "int"
                elif "bool" in raw_type or "bit" in raw_type:
                    ptype = "boolean"
                elif "str" in raw_type or "char" in raw_type or "ascii" in raw_type:
                    ptype = "string"

            punit = row[unit_idx].strip() if unit_idx != -1 and len(row) > unit_idx else "raw"
            pmin = 0.0
            pmax = 100.0
            if min_idx != -1 and len(row) > min_idx:
                try:
                    pmin = float(re.sub(r"[^\d.-]", "", row[min_idx]))
                except ValueError:
                    pmin = 0.0
            if max_idx != -1 and len(row) > max_idx:
                try:
                    pmax = float(re.sub(r"[^\d.-]", "", row[max_idx]))
                except ValueError:
                    pmax = 100.0

            pdesc = row[desc_idx].strip() if desc_idx != -1 and len(row) > desc_idx else f"Parameter {pname}"
            datapoints.append({
                "name": pname,
                "type": ptype,
                "unit": punit or "raw",
                "min_val": pmin,
                "max_val": pmax,
                "description": pdesc
            })

        if not datapoints:
            return None

        base_name = filename.rsplit(".", 1)[0].replace("_", " ").title()
        spec_data = {
            "name": f"{base_name} Controller",
            "category": "Industrial Modbus Equipment",
            "protocol": "Modbus-RTU",
            "device_metadata": {
                "manufacturer": "Industrial Specifier",
                "model": base_name,
                "source_format": "CSV Register Map",
                "register_count": len(datapoints)
            },
            "datapoints": datapoints,
            "commands": [
                {"name": "READ_ALL_REGISTERS", "parameters": [], "return_type": "boolean", "description": "Batch read all holding registers"},
                {"name": "WRITE_SETPOINT", "parameters": [{"name": "setpoint", "type": "float"}], "return_type": "boolean", "description": "Write control setpoint"},
                {"name": "RESET_DEVICE", "parameters": [], "return_type": "boolean", "description": "Reset device registers"}
            ],
            "alarms": [
                {"name": "OUT_OF_BOUNDS_ALARM", "level": 2, "condition": "Value < Min or Value > Max", "description": "Register value out of nominal engineering limits"},
                {"name": "COMMUNICATION_ERROR", "level": 1, "condition": "CRCError == true", "description": "Modbus parity/CRC transmission check error"}
            ]
        }
        return cls._build_extracted_artifact(spec_data)

    @classmethod
    def _extract_from_text(cls, filename: str, content: str) -> Dict[str, Any]:
        """Extracts technical parameters using NLP and pattern heuristics from datasheet text."""
        # 1. Device name extraction
        base_name = filename.rsplit(".", 1)[0].replace("_", " ").title()
        title_match = re.search(r"(?:Device|Product|Model|Title|Datasheet)[\s:]+([^\n\r]+)", content, re.IGNORECASE)
        if title_match:
            candidate_name = title_match.group(1).strip()
            if len(candidate_name) > 3 and len(candidate_name) < 80:
                base_name = candidate_name

        # 2. Manufacturer extraction
        mfg = "Industrial Equipment"
        mfg_patterns = [
            r"\b(Siemens|Honeywell|Emerson|Schneider Electric|Rockwell Automation|ABB|Omron|Festo|Endress\+Hauser|Keyence|Beckhoff|Mitsubishi|Yokogawa)\b",
            r"(?:Manufacturer|Vendor|Brand)[\s:]+([^\n\r]+)"
        ]
        for pat in mfg_patterns:
            m = re.search(pat, content, re.IGNORECASE)
            if m:
                mfg = m.group(1).strip()
                break

        # 3. Protocol extraction
        protocol = "Modbus-RTU"
        proto_patterns = [
            (r"\b(HART\s*\d*)\b", "HART"),
            (r"\b(Modbus[- ]?TCP)\b", "Modbus-TCP"),
            (r"\b(Modbus[- ]?RTU|RS[- ]?485)\b", "Modbus-RTU"),
            (r"\b(PROFINET)\b", "PROFINET"),
            (r"\b(EtherNet/?IP)\b", "EtherNet/IP"),
            (r"\b(OPC[- ]?UA)\b", "OPC-UA"),
            (r"\b(CANopen|CAN)\b", "CANopen"),
            (r"\b(MQTT)\b", "MQTT"),
            (r"\b(IO-Link)\b", "IO-Link")
        ]
        for pat, proto_name in proto_patterns:
            if re.search(pat, content, re.IGNORECASE):
                protocol = proto_name
                break

        # 4. Electrical and physical ratings
        power_match = re.search(r"(\d+(?:\.\d+)?\s*(?:to|-)\s*\d+(?:\.\d+)?\s*(?:VDC|VAC|V)|24\s*VDC)", content, re.IGNORECASE)
        power_supply = power_match.group(1) if power_match else "24 VDC"

        temp_match = re.search(r"(-?\d+\s*(?:to|-)\s*\+?\d+\s*°?C)", content, re.IGNORECASE)
        operating_temp = temp_match.group(1) if temp_match else "-20 to +70 °C"

        ip_match = re.search(r"\b(IP\s*6[5-8]|IP\s*20|NEMA\s*4X)\b", content, re.IGNORECASE)
        ip_rating = ip_match.group(1).replace(" ", "") if ip_match else "IP67"

        # 5. Extract DataPoints / parameters from text
        # Look for patterns like "Parameter: ...", "Signal: ...", lines with units (bar, °C, mA, V, rpm, Hz)
        datapoints = []
        dp_regex = re.compile(r"([A-Za-z][A-Za-z0-9_]{2,25})\s*[:\-=]\s*([^\n\r]+)")
        for line in content.splitlines():
            line_str = line.strip()
            if not line_str or line_str.startswith("#"):
                continue
            m = dp_regex.match(line_str)
            if m:
                raw_name = m.group(1)
                raw_desc = m.group(2)
                # Ignore common words
                if raw_name.lower() in ["manufacturer", "device", "model", "protocol", "version", "date", "status", "page", "section"]:
                    continue
                # Determine type and unit
                ptype = "float"
                punit = "raw"
                if any(u in raw_desc.lower() for u in ["bar", "psi", "kpa"]):
                    punit = "bar"
                elif any(u in raw_desc.lower() for u in ["°c", "degc", "temp"]):
                    punit = "degC"
                elif any(u in raw_desc.lower() for u in ["ma", "milliamp"]):
                    punit = "mA"
                elif any(u in raw_desc.lower() for u in ["rpm", "speed"]):
                    punit = "rpm"
                elif any(u in raw_desc.lower() for u in ["hz", "freq"]):
                    punit = "Hz"
                elif any(u in raw_desc.lower() for u in ["v", "volt"]):
                    punit = "V"
                elif any(u in raw_desc.lower() for u in ["bool", "flag", "state", "enabled"]):
                    ptype = "boolean"
                    punit = "state"
                elif any(u in raw_desc.lower() for u in ["int", "count", "code", "index"]):
                    ptype = "int"
                    punit = "count"

                datapoints.append({
                    "name": cls._sanitize_identifier(raw_name),
                    "type": ptype,
                    "unit": punit,
                    "min_val": 0.0,
                    "max_val": 100.0,
                    "description": raw_desc[:120].strip()
                })
                if len(datapoints) >= 8:
                    break

        if not datapoints:
            datapoints = [
                {"name": "primary_pv", "type": "float", "unit": "bar", "min_val": 0.0, "max_val": 100.0, "description": "Primary measurement value"},
                {"name": "temperature", "type": "float", "unit": "degC", "min_val": -40.0, "max_val": 125.0, "description": "Internal temperature"},
                {"name": "device_status", "type": "int", "unit": "code", "min_val": 0, "max_val": 255, "description": "Device operating status"}
            ]

        # 6. Extract commands from text
        commands = [
            {"name": "START_PROCESS", "parameters": [], "return_type": "boolean", "description": "Start normal operating sequence"},
            {"name": "STOP_PROCESS", "parameters": [], "return_type": "boolean", "description": "Halt operating sequence safely"},
            {"name": "CALIBRATE_ZERO", "parameters": [], "return_type": "boolean", "description": "Perform zero calibration routine"}
        ]
        cmd_matches = re.findall(r"(?:Command|Function|Action)[\s:]+([A-Za-z0-9_]{3,30})", content, re.IGNORECASE)
        for cm in cmd_matches:
            cname = cls._sanitize_identifier(cm).upper()
            if not any(c["name"] == cname for c in commands):
                commands.append({"name": cname, "parameters": [], "return_type": "boolean", "description": f"Command {cname}"})

        # 7. Extract alarms from text
        alarms = [
            {"name": "HIGH_LIMIT_ALARM", "level": 2, "condition": "PV > MaxLimit", "description": "Process variable high limit exceeded"},
            {"name": "HARDWARE_FAULT_ALARM", "level": 1, "condition": "Status != 0", "description": "Hardware diagnostic fault detected"}
        ]
        alarm_matches = re.findall(r"(?:Alarm|Fault|Warning)[\s:]+([A-Za-z0-9_]{3,30})", content, re.IGNORECASE)
        for am in alarm_matches:
            aname = cls._sanitize_identifier(am).upper()
            if not aname.endswith("ALARM") and not aname.endswith("FAULT"):
                aname = f"{aname}_ALARM"
            if not any(a["name"] == aname for a in alarms):
                alarms.append({"name": aname, "level": 2, "condition": "TripCondition == true", "description": f"Alarm {aname}"})

        category = "Sensors & Measurement"
        if any(w in content.lower() for w in ["valve", "actuator"]):
            category = "Valves & Actuation"
        elif any(w in content.lower() for w in ["drive", "inverter", "motor", "motion"]):
            category = "Motor Drives & Motion"
        elif any(w in content.lower() for w in ["vision", "camera", "image"]):
            category = "Machine Vision & Inspection"
        elif any(w in content.lower() for w in ["robot", "arm", "gripper"]):
            category = "Robotics & Automation"

        spec_data = {
            "name": base_name,
            "category": category,
            "protocol": protocol,
            "device_metadata": {
                "manufacturer": mfg,
                "model": base_name,
                "power_supply": power_supply,
                "operating_temperature": operating_temp,
                "enclosure_rating": ip_rating,
                "protocol": protocol
            },
            "datapoints": datapoints,
            "commands": commands,
            "alarms": alarms
        }
        return cls._build_extracted_artifact(spec_data)

    @classmethod
    def _build_extracted_artifact(cls, spec: Dict[str, Any]) -> Dict[str, Any]:
        """Synthesizes all 4 KIDE DSLs (DML, MNC, Cap, Op) and packages the artifact."""
        name = cls._sanitize_identifier(spec["name"]).replace("_", "")
        if not name or name[0].isdigit():
            name = f"Device{name}"

        datapoints = spec.get("datapoints", [])
        commands = spec.get("commands", [])
        alarms = spec.get("alarms", [])
        protocol = spec.get("protocol", "Modbus-RTU")

        dml_code = cls._synthesize_dml(name, datapoints)
        mnc_code = cls._synthesize_mnc(name, datapoints, commands, alarms, protocol)
        cap_code = cls._synthesize_capability(name, commands, alarms, datapoints)
        op_code = cls._synthesize_operation(name, commands)

        capabilities_summary = [{
            "name": f"{name}Cap",
            "interface": f"{name}Interface",
            "fireable_commands": [c["name"] for c in commands],
            "raised_alarms": [a["name"] for a in alarms],
            "subscribable_datapoints": [d["name"] for d in datapoints]
        }]

        return {
            "name": spec["name"],
            "category": spec.get("category", "Industrial Equipment"),
            "protocol": protocol,
            "device_metadata": spec.get("device_metadata", {}),
            "datapoints": datapoints,
            "commands": commands,
            "alarms": alarms,
            "capabilities": capabilities_summary,
            "generated_dml": dml_code,
            "generated_mnc": mnc_code,
            "generated_cap": cap_code,
            "generated_op": op_code
        }

    # =========================================================================
    # DSL Code Synthesis (Guaranteed valid KIDE syntax)
    # =========================================================================

    @classmethod
    def _synthesize_dml(cls, device_name: str, datapoints: List[Dict[str, Any]]) -> str:
        """Generates valid DML DataPackage and DataModel."""
        lines = [
            f"Package {device_name}Package",
            f"DataModel {device_name}Data {{",
            "  primitives {"
        ]
        param_lines = []
        for dp in datapoints:
            ptype = dp.get("type", "float")
            pname = dp.get("name", "val")
            if ptype == "float":
                default_val = f"{float(dp.get('min_val', 0.0)):.1f}"
            elif ptype == "int":
                default_val = str(int(dp.get('min_val', 0)))
            elif ptype == "boolean":
                default_val = "false"
            else:
                default_val = '"OK"'
            param_lines.append(f"    {ptype} {pname} = {default_val}")

        if not param_lines:
            param_lines.append("    float processValue = 0.0")

        lines.append(",\n".join(param_lines))
        lines.append("  }")
        lines.append("}")
        return "\n".join(lines) + "\n"

    @classmethod
    def _synthesize_mnc(
        cls,
        device_name: str,
        datapoints: List[Dict[str, Any]],
        commands: List[Dict[str, Any]],
        alarms: List[Dict[str, Any]],
        protocol: str
    ) -> str:
        """Generates valid MNC InterfaceDescription."""
        lines = [
            f"Model {device_name}Model",
            f"InterfaceDescription {device_name}Interface {{",
            f"  port {device_name}Port = 502",
            "  dataPoints {"
        ]
        for dp in datapoints:
            ptype = dp.get("type", "float")
            pname = dp.get("name", "dp")
            lines.append(f"    Publish {ptype} {pname} []")
        lines.append("  }")

        lines.append("  alarms {")
        for alm in alarms:
            aname = alm.get("name", "ALARM")
            level = alm.get("level", 2)
            lines.append(f"    Publish {aname} [] level = {level}")
        lines.append("  }")

        lines.append("  commands {")
        for cmd in commands:
            cname = cmd.get("name", "CMD")
            lines.append(f"    {cname} []")
        lines.append("  }")

        lines.append("  responses {")
        for cmd in commands:
            cname = cmd.get("name", "CMD")
            lines.append(f"    {cname}_RESPONSE []")
        lines.append("  }")

        lines.append("}")
        return "\n".join(lines) + "\n"

    @classmethod
    def _synthesize_capability(
        cls,
        device_name: str,
        commands: List[Dict[str, Any]],
        alarms: List[Dict[str, Any]],
        datapoints: List[Dict[str, Any]]
    ) -> str:
        """Generates valid Capability DSL definition."""
        cmd_names = ", ".join([c["name"] for c in commands]) if commands else "INIT"
        alm_names = ", ".join([a["name"] for a in alarms]) if alarms else "FAULT"
        dp_names = ", ".join([d["name"] for d in datapoints]) if datapoints else "PV"

        lines = [
            f"Capability {device_name}Cap compatible component interface {device_name}Interface {{",
            "  Init {",
            f"    fire Commands [ {commands[0]['name'] if commands else 'INIT'}() ]",
            "  }",
            "  providesControlCapabilities {",
            f"    fireable commands : {cmd_names}",
            f"    raised alarms : {alm_names}",
            f"    subscribable DataPoints : {dp_names}",
            "  }",
            "  providesOutcomes {",
            f"    receivable responses : {commands[0]['name'] if commands else 'INIT'}_RESPONSE",
            "  }",
            "}"
        ]
        return "\n".join(lines) + "\n"

    @classmethod
    def _synthesize_operation(cls, device_name: str, commands: List[Dict[str, Any]]) -> str:
        """Generates valid Operation DSL definitions."""
        lines = []
        for cmd in commands:
            cname = cmd.get("name", "OP").title().replace("_", "")
            lines.append(f"Operation {cname}Op() {{")
            lines.append(f'  execute "device_driver.invoke(\'{cmd.get("name", "CMD")}\')"\n  return boolean success')
            lines.append("}")
        return "\n".join(lines) + "\n"

    @staticmethod
    def _sanitize_identifier(name: str) -> str:
        """Converts strings into clean alphanumeric identifiers."""
        clean = re.sub(r"[^A-Za-z0-9_]", "_", name.strip())
        clean = re.sub(r"_+", "_", clean).strip("_")
        if clean and clean[0].isdigit():
            clean = f"dev_{clean}"
        return clean or "identifier"
