"""
Live Supervisory Controller Simulation Engine.
Provides interactive in-memory state machine execution, telemetry emulation,
command dispatching, and event injection for synthesized controllers.
"""

import time
from datetime import datetime
from typing import Any, Dict, List, Optional

class SimulationSession:
    def __init__(self, project_id: int, model: Dict[str, Any]):
        self.project_id = project_id
        self.model = model
        self.model_name = model.get("name", "SupervisorController")

        cn = model.get("control_node", {})
        iface = model.get("interface_description") or (model.get("systems", [{}])[0] if model.get("systems") else {})

        raw_states = cn.get("operating_states") or cn.get("operatingStates") or []
        if not raw_states:
            raw_states = iface.get("operating_states") or ["INITIALIZED", "READY", "RUNNING", "STOPPED"]
        self.states = [s.get("name") if isinstance(s, dict) else str(s) for s in raw_states]
        if "INITIALIZED" not in self.states:
            self.states.insert(0, "INITIALIZED")
        if "READY" not in self.states and len(self.states) > 1:
            self.states.insert(1, "READY")

        raw_cmds = iface.get("commands", [])
        self.commands = [c.get("name") if isinstance(c, dict) else str(c) for c in raw_cmds]
        if "INIT" not in self.commands:
            self.commands.insert(0, "INIT")

        raw_evts = iface.get("events", [])
        self.events = [e.get("name") if isinstance(e, dict) else str(e) for e in raw_evts]

        raw_alms = iface.get("alarms", [])
        self.alarms = [a.get("name") if isinstance(a, dict) else str(a) for a in raw_alms]

        dps = iface.get("data_points") or iface.get("dataPoints") or []
        self.datapoint_names = [d.get("name") if isinstance(d, dict) else str(d) for d in dps]

        self.transitions = cn.get("transitions", [])

        # Live state variables
        self.current_state = "INITIALIZED"
        self.telemetry: Dict[str, Any] = {dp: 20.0 for dp in self.datapoint_names}
        self.alarm_history: List[Dict[str, Any]] = []
        self.command_history: List[Dict[str, Any]] = []
        self.logs: List[Dict[str, Any]] = []
        self.created_at = time.time()
        self.last_active = time.time()

        self._log("INFO", f"Simulation engine initialized for {self.model_name}. Initial state: [INITIALIZED]")

    def _log(self, level: str, message: str):
        entry = {
            "timestamp": datetime.now().strftime("%H:%M:%S.%f")[:-3],
            "level": level,
            "message": message,
            "state": self.current_state
        }
        self.logs.append(entry)
        if len(self.logs) > 200:
            self.logs = self.logs[-200:]

    def transition_to(self, new_state: str, reason: str = "") -> bool:
        if new_state not in self.states:
            # Dynamically register state if from synthesized actions
            self.states.append(new_state)
        old_state = self.current_state
        self.current_state = new_state
        self._log("TRANSITION", f"State transition: [{old_state}] -> [{new_state}] (Reason: {reason or 'unspecified'})")
        return True

    def execute_command(self, cmd_name: str, payload: Optional[Dict[str, Any]] = None) -> Dict[str, Any]:
        self.last_active = time.time()
        payload = payload or {}
        clean_cmd = cmd_name.upper()
        self._log("CMD", f"Executing supervisory command: {clean_cmd} with params: {payload}")

        status_str = "SUCCESS"
        response_str = "ACK"

        if clean_cmd == "INIT":
            self.transition_to("READY", "Initialization handshake verified")
            response_str = "INIT_RES"
        elif clean_cmd in ("STOP", "STOP_PUMP", "STOP_COOLER", "DISARM", "CLOSE_GATE"):
            self.transition_to("STOPPED", f"Command {clean_cmd} issued")
        elif clean_cmd == "ABORT":
            self.raise_alarm("Aborted", 3, "Operator or guard condition triggered ABORT")
            self.transition_to("STOPPED", "System aborted")
            status_str = "ABORTED"
        elif clean_cmd in ("START", "START_PUMP", "POLL_TAG", "ENGAGE_VACUUM", "OPEN_GATE", "HEATUP"):
            next_target = self.states[2] if len(self.states) > 2 else "READY"
            self.transition_to(next_target, f"Execution of {clean_cmd}")
        else:
            # Check if any explicit transition matches this command
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

        # Telemetry drift
        for k in self.telemetry:
            self.telemetry[k] = round(self.telemetry[k] + 0.5, 2)

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
        self._log("EVENT", f"External event received: [{event_name}] (Payload: {payload})")

        # Check transition rules triggered by event
        matched = False
        for t in self.transitions:
            cond = t.get("condition", "").lower()
            if event_name.lower() in cond or cond == "ontrue" or cond == "always":
                if t.get("currentState") == self.current_state or t.get("currentState") == "any":
                    self.transition_to(t.get("nextState"), f"Event triggered transition ({event_name})")
                    matched = True
                    break

        if not matched and "temp" in event_name.lower() or "high" in event_name.lower():
            if "CoolDown" in self.states:
                self.transition_to("CoolDown", f"Emergency trigger from {event_name}")
        elif not matched and "detect" in event_name.lower():
            if "Validate" in self.states or "Detect" in self.states:
                target = "Validate" if "Validate" in self.states else "Detect"
                self.transition_to(target, f"Event {event_name}")

        return {
            "event": event_name,
            "matched_transition": matched,
            "current_state": self.current_state,
            "telemetry": self.telemetry
        }

    def raise_alarm(self, alarm_name: str, level: int = 1, message: str = ""):
        self.last_active = time.time()
        entry = {
            "alarm": alarm_name,
            "level": level,
            "message": message or f"Supervisory alarm {alarm_name}",
            "timestamp": datetime.now().strftime("%H:%M:%S"),
            "state": self.current_state
        }
        self.alarm_history.append(entry)
        self._log("ALARM", f"ALARM TRIGGERED: [{alarm_name}] Level {level} - {entry['message']}")
        if level >= 3 or alarm_name == "Aborted":
            self.transition_to("STOPPED", f"Critical alarm {alarm_name}")
        return entry

    def step_simulation(self) -> Dict[str, Any]:
        """Performs a single simulation step / time tick."""
        self.last_active = time.time()
        for k in self.telemetry:
            self.telemetry[k] = round(self.telemetry[k] + 1.2, 2)
        self._log("INFO", f"Simulation tick executed | Telemetry updated: {self.telemetry}")
        return self.get_status()

    def get_status(self) -> Dict[str, Any]:
        return {
            "project_id": self.project_id,
            "model_name": self.model_name,
            "current_state": self.current_state,
            "states": self.states,
            "commands": self.commands,
            "events": self.events,
            "alarms": self.alarms,
            "telemetry": self.telemetry,
            "alarm_history": self.alarm_history[-10:],
            "command_history": self.command_history[-10:],
            "logs": self.logs[-50:]
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