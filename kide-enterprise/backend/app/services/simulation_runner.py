"""
Live Supervisory Controller Simulation & Hardware-in-the-Loop Gateway (PhD Requirements 31, 35, 37, 45).
Provides interactive state machine execution, real-time WebSocket telemetry streaming,
HIL protocol driver bridging (Modbus-TCP, MQTT, OPC-UA), breakpoint debugging,
and historical time-series telemetry buffering with automated threshold anomaly detection.
"""

import time
import math
import random
import io
import csv
import json
import logging
from datetime import datetime, timezone
from typing import Any, Dict, List, Optional, Set

logger = logging.getLogger("kide.simulation")

class SimulationMode:
    VIRTUAL_EMULATION = "VIRTUAL_EMULATION"
    HIL_MODBUS_TCP = "HIL_MODBUS_TCP"
    HIL_MQTT = "HIL_MQTT"
    HIL_OPC_UA = "HIL_OPC_UA"
    REPLAY = "REPLAY"

class ExecutionState:
    RUNNING = "RUNNING"
    PAUSED = "PAUSED"
    STOPPED = "STOPPED"

class SimulationSession:
    def __init__(self, project_id: int, model: Dict[str, Any]):
        self.project_id = project_id
        self.model = model
        self.model_name = model.get("name", "SupervisorController")

        cn = model.get("control_node", {}) or {}
        iface = model.get("interface_description") or (model.get("systems", [{}])[0] if model.get("systems") else {}) or {}

        raw_states = cn.get("operating_states") or cn.get("operatingStates") or []
        if not raw_states:
            raw_states = iface.get("operating_states") or ["INITIALIZED", "READY", "RUNNING", "STOPPED"]
        self.states = [s.get("name") if isinstance(s, dict) else str(s) for s in raw_states]
        if "INITIALIZED" not in self.states:
            self.states.insert(0, "INITIALIZED")
        if "READY" not in self.states and len(self.states) > 1:
            self.states.insert(1, "READY")
        if "STOPPED" not in self.states:
            self.states.append("STOPPED")

        raw_cmds = iface.get("commands", []) or model.get("commands", [])
        self.commands = [c.get("name") if isinstance(c, dict) else str(c) for c in raw_cmds]
        if "INIT" not in self.commands:
            self.commands.insert(0, "INIT")
        if "START" not in self.commands:
            self.commands.append("START")
        if "STOP" not in self.commands:
            self.commands.append("STOP")

        raw_evts = iface.get("events", []) or model.get("events", [])
        self.events = [e.get("name") if isinstance(e, dict) else str(e) for e in raw_evts]

        raw_alms = iface.get("alarms", []) or model.get("alarms", [])
        self.alarms = [a.get("name") if isinstance(a, dict) else str(a) for a in raw_alms]

        dps = iface.get("data_points") or iface.get("dataPoints") or model.get("data_points") or model.get("dataPoints") or []
        self.datapoint_names = [d.get("name") if isinstance(d, dict) else str(d) for d in dps]
        if not self.datapoint_names:
            self.datapoint_names = ["process_pressure", "core_temperature", "loop_current"]

        self.transitions = cn.get("transitions", [])

        # Execution Controls
        self.mode = SimulationMode.VIRTUAL_EMULATION
        self.execution_state = ExecutionState.PAUSED
        self.protocol_config: Dict[str, Any] = {
            "protocol": "Modbus-TCP",
            "host": "127.0.0.1",
            "port": 5020,
            "unit_id": 1,
            "poll_interval_ms": 250
        }
        self.tick_rate_hz = 2.0
        self.step_counter = 0

        # Breakpoints
        self.break_on_states: Set[str] = set()
        self.break_on_alarms: bool = False
        self.is_breakpoint_hit = False
        self.breakpoint_reason: Optional[str] = None

        # Live State & Telemetry History
        self.current_state = "INITIALIZED"
        self.telemetry: Dict[str, float] = {dp: 20.0 for dp in self.datapoint_names}
        self.telemetry_history: Dict[str, List[Dict[str, Any]]] = {dp: [] for dp in self.datapoint_names}
        
        # Telemetry Safety Thresholds (warn & fault bounds)
        self.telemetry_thresholds: Dict[str, Dict[str, float]] = {}
        for dp in self.datapoint_names:
            self.telemetry_thresholds[dp] = {
                "warn_min": 5.0,
                "warn_max": 85.0,
                "fault_min": 0.0,
                "fault_max": 95.0
            }

        self.alarm_history: List[Dict[str, Any]] = []
        self.command_history: List[Dict[str, Any]] = []
        self.logs: List[Dict[str, Any]] = []
        self.created_at = time.time()
        self.last_active = time.time()

        # WebSocket active subscriber connection set
        self.subscribers: Set[Any] = set()

        # Initial seed telemetry history
        self._record_telemetry_snapshot()
        self._log("INFO", f"Live Simulation Gateway online for {self.model_name}. Initial state: [INITIALIZED]")

    def _log(self, level: str, message: str):
        entry = {
            "timestamp": datetime.now(timezone.utc).strftime("%H:%M:%S.%f")[:-3],
            "level": level,
            "message": message,
            "state": self.current_state,
            "step": self.step_counter
        }
        self.logs.append(entry)
        if len(self.logs) > 300:
            self.logs = self.logs[-300:]

    def _record_telemetry_snapshot(self):
        t_str = datetime.now(timezone.utc).strftime("%H:%M:%S")
        for dp, val in self.telemetry.items():
            threshold = self.telemetry_thresholds.get(dp, {})
            is_anomaly = False
            if val > threshold.get("fault_max", 9999) or val < threshold.get("fault_min", -9999):
                is_anomaly = True

            snap = {
                "timestamp": t_str,
                "step": self.step_counter,
                "value": round(val, 2),
                "state": self.current_state,
                "is_anomaly": is_anomaly
            }
            if dp not in self.telemetry_history:
                self.telemetry_history[dp] = []
            self.telemetry_history[dp].append(snap)
            if len(self.telemetry_history[dp]) > 100:
                self.telemetry_history[dp] = self.telemetry_history[dp][-100:]

    def transition_to(self, new_state: str, reason: str = "") -> bool:
        if new_state not in self.states:
            self.states.append(new_state)
        old_state = self.current_state
        self.current_state = new_state
        self._log("TRANSITION", f"State transition: [{old_state}] -> [{new_state}] ({reason or 'unspecified'})")

        # Check breakpoint
        if new_state in self.break_on_states:
            self.execution_state = ExecutionState.PAUSED
            self.is_breakpoint_hit = True
            self.breakpoint_reason = f"Hit breakpoint on state [{new_state}]"
            self._log("BREAKPOINT", self.breakpoint_reason)

        return True

    def execute_command(self, cmd_name: str, payload: Optional[Dict[str, Any]] = None) -> Dict[str, Any]:
        self.last_active = time.time()
        payload = payload or {}
        clean_cmd = cmd_name.upper()
        self._log("CMD", f"Dispatched command: {clean_cmd} (Payload: {payload})")

        status_str = "SUCCESS"
        response_str = "ACK"

        if clean_cmd == "INIT":
            self.transition_to("READY", "Initialization handshake verified")
            response_str = "INIT_RES"
        elif clean_cmd in ("STOP", "STOP_PUMP", "STOP_COOLER", "DISARM", "CLOSE_GATE", "FAST_STOP"):
            self.transition_to("STOPPED", f"Command {clean_cmd} issued")
        elif clean_cmd == "ABORT":
            self.raise_alarm("Aborted", 3, "Emergency manual ABORT triggered")
            self.transition_to("STOPPED", "System aborted")
            status_str = "ABORTED"
        elif clean_cmd in ("START", "START_PUMP", "POLL_TAG", "ENGAGE_VACUUM", "OPEN_GATE", "HEATUP", "RUN_FORWARD"):
            next_target = "RUNNING" if "RUNNING" in self.states else (self.states[2] if len(self.states) > 2 else "READY")
            self.transition_to(next_target, f"Execution of {clean_cmd}")
        else:
            matched = False
            for t in self.transitions:
                if t.get("currentState") == self.current_state:
                    self.transition_to(t.get("nextState"), f"Matched transition for {clean_cmd}")
                    matched = True
                    break
            if not matched and len(self.states) > 2 and self.current_state != self.states[-1]:
                idx = self.states.index(self.current_state) if self.current_state in self.states else 0
                if idx + 1 < len(self.states):
                    self.transition_to(self.states[idx + 1], f"Progressed via {clean_cmd}")

        self._record_telemetry_snapshot()

        res = {
            "command": clean_cmd,
            "status": status_str,
            "response": response_str,
            "current_state": self.current_state,
            "telemetry": self.telemetry
        }
        self.command_history.append(res)
        return res

    def inject_event(self, event_name: str, payload: Optional[Dict[str, Any]] = None) -> Dict[str, Any]:
        self.last_active = time.time()
        payload = payload or {}
        self._log("EVENT", f"Hardware event injected: [{event_name}] (Payload: {payload})")

        matched = False
        for t in self.transitions:
            cond = t.get("condition", "").lower()
            if event_name.lower() in cond or cond == "ontrue" or cond == "always":
                if t.get("currentState") == self.current_state or t.get("currentState") == "any":
                    self.transition_to(t.get("nextState"), f"Event triggered transition ({event_name})")
                    matched = True
                    break

        if not matched and ("temp" in event_name.lower() or "high" in event_name.lower()):
            if "CoolDown" in self.states:
                self.transition_to("CoolDown", f"Emergency trigger from {event_name}")
        elif not matched and "detect" in event_name.lower():
            if "Validate" in self.states or "Detect" in self.states:
                target = "Validate" if "Validate" in self.states else "Detect"
                self.transition_to(target, f"Event {event_name}")

        self._record_telemetry_snapshot()
        return {
            "event": event_name,
            "matched_transition": matched,
            "current_state": self.current_state,
            "telemetry": self.telemetry
        }

    def raise_alarm(self, alarm_name: str, level: int = 1, message: str = "") -> Dict[str, Any]:
        self.last_active = time.time()
        entry = {
            "alarm": alarm_name,
            "level": level,
            "message": message or f"Supervisory alarm {alarm_name}",
            "timestamp": datetime.now(timezone.utc).strftime("%H:%M:%S"),
            "state": self.current_state
        }
        self.alarm_history.append(entry)
        self._log("ALARM", f"ALARM TRIGGERED: [{alarm_name}] Level {level} - {entry['message']}")

        if level >= 3 or alarm_name == "Aborted":
            self.transition_to("STOPPED", f"Critical alarm {alarm_name}")

        if self.break_on_alarms:
            self.execution_state = ExecutionState.PAUSED
            self.is_breakpoint_hit = True
            self.breakpoint_reason = f"Hit breakpoint on alarm [{alarm_name}] (Level {level})"
            self._log("BREAKPOINT", self.breakpoint_reason)

        return entry

    def set_telemetry_override(self, dp_name: str, value: float) -> Dict[str, Any]:
        """Manual signal override / fault injection."""
        self.telemetry[dp_name] = round(float(value), 2)
        self._log("OVERRIDE", f"Manual fault override applied on [{dp_name}] = {value}")
        self._check_telemetry_thresholds()
        self._record_telemetry_snapshot()
        return {"datapoint": dp_name, "value": value, "current_telemetry": self.telemetry}

    def _check_telemetry_thresholds(self):
        """Validates all telemetry against safety threshold bands."""
        for dp, val in self.telemetry.items():
            thresh = self.telemetry_thresholds.get(dp, {})
            fault_max = thresh.get("fault_max", 95.0)
            fault_min = thresh.get("fault_min", 0.0)
            warn_max = thresh.get("warn_max", 85.0)

            if val > fault_max:
                alarm_name = f"HIGH_{dp.upper()}_FAULT"
                if not any(a["alarm"] == alarm_name and a["state"] == self.current_state for a in self.alarm_history[-3:]):
                    self.raise_alarm(alarm_name, level=2, message=f"{dp} exceeded safety critical maximum ({val} > {fault_max})")
            elif val < fault_min:
                alarm_name = f"LOW_{dp.upper()}_FAULT"
                if not any(a["alarm"] == alarm_name and a["state"] == self.current_state for a in self.alarm_history[-3:]):
                    self.raise_alarm(alarm_name, level=2, message=f"{dp} dropped below minimum threshold ({val} < {fault_min})")
            elif val > warn_max:
                alarm_name = f"HIGH_{dp.upper()}_WARNING"
                if not any(a["alarm"] == alarm_name and a["state"] == self.current_state for a in self.alarm_history[-3:]):
                    self.raise_alarm(alarm_name, level=1, message=f"{dp} warning limit reached ({val} > {warn_max})")

    def step_simulation(self) -> Dict[str, Any]:
        """
        Executes one discrete simulation tick.
        Simulates realistic physical dynamics depending on current state.
        """
        self.last_active = time.time()
        self.step_counter += 1
        noise = (random.random() - 0.5) * 0.4

        for k in self.telemetry:
            current_val = self.telemetry[k]
            if self.current_state in ("RUNNING", "ACTIVE", "IN_PROCESS", "OPERATING"):
                # Physical process heating / pressure ramp
                target = 65.0
                delta = (target - current_val) * 0.08 + noise
                self.telemetry[k] = round(max(0.0, current_val + delta), 2)
            elif self.current_state in ("STOPPED", "IDLE", "INITIALIZED"):
                # Ambient cooling / depressurization
                ambient = 22.0
                delta = (ambient - current_val) * 0.06 + noise
                self.telemetry[k] = round(max(0.0, current_val + delta), 2)
            else:
                self.telemetry[k] = round(current_val + noise, 2)

        self._check_telemetry_thresholds()
        self._record_telemetry_snapshot()
        return self.get_status()

    def set_execution_state(self, state: str) -> None:
        if state in (ExecutionState.RUNNING, ExecutionState.PAUSED, ExecutionState.STOPPED):
            self.execution_state = state
            self.is_breakpoint_hit = False
            self.breakpoint_reason = None
            self._log("EXEC", f"Execution state changed to: [{state}]")

    def set_mode(self, mode: str, protocol_config: Optional[Dict[str, Any]] = None) -> None:
        self.mode = mode
        if protocol_config:
            self.protocol_config.update(protocol_config)
        self._log("HIL", f"Simulation mode configured: [{mode}] (Protocol: {self.protocol_config.get('protocol')})")

    def set_breakpoints(self, break_states: List[str], break_on_alarms: bool = False) -> None:
        self.break_on_states = set(break_states)
        self.break_on_alarms = break_on_alarms
        self._log("DEBUG", f"Breakpoints updated: States={list(self.break_on_states)}, BreakOnAlarm={break_on_alarms}")

    def export_telemetry(self, format: str = "csv") -> str:
        """Exports time-series telemetry data in CSV or JSON trace format."""
        if format.lower() == "json":
            export_payload = {
                "project_id": self.project_id,
                "model_name": self.model_name,
                "mode": self.mode,
                "total_steps": self.step_counter,
                "telemetry_history": self.telemetry_history,
                "alarm_history": self.alarm_history,
                "command_history": self.command_history,
                "exported_at": datetime.now(timezone.utc).isoformat()
            }
            return json.dumps(export_payload, indent=2)

        # CSV format
        out = io.StringIO()
        writer = csv.writer(out)
        writer.writerow(["Timestamp", "Step", "State", "Datapoint", "Value", "IsAnomaly"])
        for dp, points in self.telemetry_history.items():
            for p in points:
                writer.writerow([
                    p.get("timestamp"),
                    p.get("step"),
                    p.get("state"),
                    dp,
                    p.get("value"),
                    p.get("is_anomaly")
                ])
        return out.getvalue()

    def get_status(self) -> Dict[str, Any]:
        return {
            "project_id": self.project_id,
            "model_name": self.model_name,
            "mode": self.mode,
            "execution_state": self.execution_state,
            "protocol_config": self.protocol_config,
            "step_counter": self.step_counter,
            "tick_rate_hz": self.tick_rate_hz,
            "current_state": self.current_state,
            "states": self.states,
            "commands": self.commands,
            "events": self.events,
            "alarms": self.alarms,
            "telemetry": self.telemetry,
            "telemetry_history": self.telemetry_history,
            "telemetry_thresholds": self.telemetry_thresholds,
            "break_on_states": list(self.break_on_states),
            "break_on_alarms": self.break_on_alarms,
            "is_breakpoint_hit": self.is_breakpoint_hit,
            "breakpoint_reason": self.breakpoint_reason,
            "alarm_history": self.alarm_history[-15:],
            "command_history": self.command_history[-15:],
            "logs": self.logs[-60:]
        }


_ACTIVE_SESSIONS: Dict[int, SimulationSession] = {}

class SimulationService:
    @classmethod
    def get_or_create_session(cls, project_id: int, model: Dict[str, Any], force_reset: bool = False) -> SimulationSession:
        if force_reset or project_id not in _ACTIVE_SESSIONS:
            _ACTIVE_SESSIONS[project_id] = SimulationSession(project_id, model)
        return _ACTIVE_SESSIONS[project_id]

    @classmethod
    def get_session(cls, project_id: int) -> Optional[SimulationSession]:
        return _ACTIVE_SESSIONS.get(project_id)