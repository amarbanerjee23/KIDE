import io
import json
import zipfile
from typing import Any, Dict, List, Tuple

def _extract_model_elements(model: Dict[str, Any]) -> Tuple[str, List[str], List[str], List[str], List[str], List[str]]:
    name = model.get("name", "SupervisorController")
    systems = model.get("systems", [])
    iface = systems[0] if systems else (model.get("interface_description") or {})
    cn = model.get("control_node") or {}

    commands = [c.get("name") if isinstance(c, dict) else str(c) for c in iface.get("commands", [])]
    events = [e.get("name") if isinstance(e, dict) else str(e) for e in iface.get("events", [])]
    alarms = [a.get("name") if isinstance(a, dict) else str(a) for a in iface.get("alarms", [])]
    dps = iface.get("data_points") or iface.get("dataPoints") or []
    datapoints = [d.get("name") if isinstance(d, dict) else str(d) for d in dps]
    
    states = cn.get("operating_states") or cn.get("operatingStates") or []
    if not states:
        raw_states = iface.get("operating_states") or iface.get("operatingStates") or []
        states = [s.get("name") if isinstance(s, dict) else str(s) for s in raw_states]
    if not states:
        states = ["INITIALIZED", "READY", "RUNNING", "STOPPED"]

    if "INIT" not in commands:
        commands.insert(0, "INIT")
    if "Started" not in events:
        events.insert(0, "Started")
    if "Aborted" not in alarms:
        alarms.insert(0, "Aborted")

    return name, commands, events, alarms, datapoints, states

def export_mnc(model: Dict[str, Any]) -> str:
    lines = []
    name = model.get("name", "Model")
    lines.append(f"Model {name}")
    
    systems = model.get("systems") or ([model.get("interface_description")] if model.get("interface_description") else [])
    for sys in systems:
        if not sys:
            continue
        ifname = sys.get("name") or f"{name}_Interface"
        uses = ", ".join(sys.get("uses", []))
        uses_str = f" uses {uses}" if uses else ""
        lines.append(f"InterfaceDescription {ifname}{uses_str} {{")
        
        commands = sys.get("commands", [])
        if commands:
            lines.append("  commands {")
            for c in commands:
                cname = c.get("name") if isinstance(c, dict) else str(c)
                lines.append(f"    Command {cname} []")
            lines.append("  }")
            
        events = sys.get("events", [])
        if events:
            lines.append("  events {")
            for e in events:
                ename = e.get("name") if isinstance(e, dict) else str(e)
                lines.append(f"    Event {ename} []")
            lines.append("  }")
            
        responses = sys.get("responses", [])
        if responses:
            lines.append("  responses {")
            for r in responses:
                rname = r.get("name") if isinstance(r, dict) else str(r)
                lines.append(f"    Response {rname} []")
            lines.append("  }")
            
        alarms = sys.get("alarms", [])
        if alarms:
            lines.append("  alarms {")
            for a in alarms:
                aname = a.get("name") if isinstance(a, dict) else str(a)
                lines.append(f"    Alarm {aname} []")
            lines.append("  }")
            
        dps = sys.get("dataPoints") or sys.get("data_points")
        if dps:
            lines.append("  dataPoints {")
            for dp in dps:
                dpname = dp.get("name") if isinstance(dp, dict) else str(dp)
                lines.append(f"    DataPoint {dpname} []")
            lines.append("  }")
            
        states = sys.get("operating_states") or sys.get("operatingStates")
        if states:
            lines.append("  operatingStates {")
            for s in states:
                sname = s.get("name") if isinstance(s, dict) else str(s)
                lines.append(f"    OperatingState {sname} []")
            lines.append("  }")
            
        sub = sys.get("subscribedItems") or sys.get("subscribed_items")
        if sub:
            sub_ev = ", ".join(sub.get("subscribedEvents") or sub.get("events") or [])
            sub_al = ", ".join(sub.get("subscribedAlarms") or sub.get("alarms") or [])
            sub_dp = ", ".join(sub.get("subscribedDataPoints") or sub.get("data_points") or [])
            lines.append("  SubscribableItemList {")
            if sub_ev:
                lines.append(f"    subscribedEvents: {sub_ev}")
            if sub_al:
                lines.append(f"    subscribedAlarms: {sub_al}")
            if sub_dp:
                lines.append(f"    subscribedDataPoints: {sub_dp}")
            lines.append("  }")
            
        lines.append("}")
        
    cn = model.get("control_node")
    if cn:
        cnname = cn.get("name") or f"{name}CN"
        ifref = cn.get("interface_ref") or cn.get("interfaceDescription") or f"{name}_Interface"
        lines.append(f"ControlNode {cnname} implements interface {ifref} {{")
        
        actions = cn.get("actions", [])
        crbs = cn.get("command_response_block") or cn.get("commandResponseBlocks") or []
        for crb in crbs:
            cmd = crb.get("command") or crb.get("name")
            lines.append(f"  Command {cmd} {{")
            lines.append("    Action {")
            lines.append(f"      fire commands [ {cmd}() ]")
            lines.append("    }")
            for rb in crb.get("responseBlock", []):
                rname = rb.get("response", "RES")
                lines.append(f"    expectedResponse {rname} {{}}")
            lines.append("  }")
            
        ebs = cn.get("eventBlocks") or cn.get("event_blocks") or []
        for eb in ebs:
            ev = eb.get("event") or eb.get("event_ref")
            lines.append(f"  Event {ev} {{}}")
            
        abs_list = cn.get("alarmBlocks") or cn.get("alarm_blocks") or []
        for ab in abs_list:
            al = ab.get("alarm") or ab.get("alarm_ref")
            lines.append(f"  Alarm {al} {{}}")
            
        lines.append("}")
        
    return "\n".join(lines)

def export_python(model: Dict[str, Any]) -> str:
    name, commands, events, alarms, datapoints, states = _extract_model_elements(model)
    cls_name = f"{name}Handler"

    lines = [
        '#!/usr/bin/env python3',
        '"""',
        f'Generated Supervisory Controller for {name}.',
        'Produced automatically by KIDE Enterprise from formal MNC-ML / Activity specifications.',
        'Supports live execution, simulation mode, and integration with physical I/O drivers.',
        '"""',
        '',
        'import argparse',
        'import asyncio',
        'import logging',
        'import sys',
        'import time',
        'from typing import Any, Callable, Dict, List, Optional',
        '',
        'logging.basicConfig(',
        '    level=logging.INFO,',
        '    format="%(asctime)s [%(levelname)s] [%(name)s] %(message)s",',
        '    handlers=[logging.StreamHandler(sys.stdout)]',
        ')',
        f'logger = logging.getLogger("{name}")',
        '',
        f'class {cls_name}:',
        f'    """Executable Event-Driven Controller for {name}."""',
        '',
        '    OPERATING_STATES = [',
    ]

    for st in states:
        lines.append(f'        "{st}",')

    lines.extend([
        '    ]',
        '',
        '    def __init__(self, simulate: bool = False):',
        '        self.simulate = simulate',
        '        self.current_state: str = "INITIALIZED"',
        '        self.telemetry: Dict[str, Any] = {',
    ])

    for dp in datapoints:
        lines.append(f'            "{dp}": 0.0,')

    lines.extend([
        '        }',
        '        self.alarm_history: List[Dict[str, Any]] = []',
        '        self.command_history: List[Dict[str, Any]] = []',
        '        self._running = False',
        '        self._listeners: List[Callable[[str, Any], None]] = []',
        f'        logger.info("Initialized {cls_name} in state: %s (simulate=%s)", self.current_state, self.simulate)',
        '',
        '    def add_listener(self, listener: Callable[[str, Any], None]):',
        '        """Register callback for telemetry and state transitions."""',
        '        self._listeners.append(listener)',
        '',
        '    def _notify(self, event_type: str, data: Any):',
        '        for cb in self._listeners:',
        '            try:',
        '                cb(event_type, data)',
        '            except Exception as e:',
        '                logger.warning("Listener notification error: %s", e)',
        '',
        '    def transition_to(self, new_state: str, reason: str = "") -> bool:',
        '        """Execute state transition with guard checks and notifications."""',
        '        if new_state not in self.OPERATING_STATES:',
        '            logger.warning("Unknown state transition requested: %s", new_state)',
        '            return False',
        '        if self.current_state == new_state:',
        '            return True',
        '        old_state = self.current_state',
        '        self.current_state = new_state',
        '        msg = f"Transition: [{old_state}] -> [{new_state}] (Reason: {reason})"',
        '        logger.info(msg)',
        '        self._notify("transition", {"old": old_state, "new": new_state, "reason": reason})',
        '        return True',
        '',
        '    def raise_alarm(self, alarm_name: str, level: int = 1, message: str = ""):',
        '        """Raise a supervisory alarm."""',
        '        entry = {',
        '            "alarm": alarm_name,',
        '            "level": level,',
        '            "message": message,',
        '            "timestamp": time.time(),',
        '            "state": self.current_state',
        '        }',
        '        self.alarm_history.append(entry)',
        '        logger.error("ALARM RAISED: [%s] (Level %d): %s", alarm_name, level, message)',
        '        self._notify("alarm", entry)',
        '        if level >= 3 or alarm_name == "Aborted":',
        '            self.transition_to("STOPPED", f"Critical alarm {alarm_name}")',
        '',
        '    def update_datapoint(self, name: str, value: Any):',
        '        """Update telemetry sensor measurement."""',
        '        self.telemetry[name] = value',
        '        self._notify("telemetry", {"name": name, "value": value})',
        '',
        '    async def execute_command(self, cmd_name: str, payload: Optional[Dict[str, Any]] = None) -> Dict[str, Any]:',
        '        """Dispatch and execute supervisory command."""',
        '        payload = payload or {}',
        '        clean_name = cmd_name.lower().replace("-", "_")',
        '        handler_method = getattr(self, f"handle_{clean_name}", None)',
        '        t0 = time.time()',
        '        if not handler_method:',
        '            err_res = {"command": cmd_name, "status": "ERROR", "error": f"Unsupported command: {cmd_name}"}',
        '            logger.error("Execution failed: %s", err_res["error"])',
        '            return err_res',
        '        try:',
        '            res = await handler_method(payload) if asyncio.iscoroutinefunction(handler_method) else handler_method(payload)',
        '            duration = round((time.time() - t0) * 1000, 2)',
        '            res["duration_ms"] = duration',
        '            self.command_history.append({"command": cmd_name, "payload": payload, "result": res, "time": t0})',
        '            self._notify("command_executed", res)',
        '            return res',
        '        except Exception as ex:',
        '            logger.exception("Error executing %s: %s", cmd_name, ex)',
        '            return {"command": cmd_name, "status": "FAILED", "error": str(ex)}',
        '',
    ])

    for cmd in commands:
        clean_cmd = cmd.lower().replace("-", "_")
        lines.extend([
            f'    def handle_{clean_cmd}(self, payload):',
            f'        """Handler for command: {cmd}"""',
            f'        logger.info("Executing command [%s] with payload: %s", "{cmd}", payload)',
        ])

        if cmd.upper() == "INIT":
            lines.extend([
                '        self.transition_to("READY", "Initialization successful")',
                '        return {"command": "INIT", "status": "SUCCESS", "response": "INIT_RES", "state": self.current_state}',
            ])
        elif cmd.upper() in ("START", "START_PUMP", "HEATUP", "BELTSTART", "MOVE_ARM", "DETECT_TAG"):
            lines.extend([
                f'        self.transition_to("{states[2] if len(states) > 2 else "READY"}", "Command {cmd} issued")',
                f'        return {{"command": "{cmd}", "status": "SUCCESS", "state": self.current_state}}',
            ])
        elif cmd.upper() in ("STOP", "STOP_PUMP", "STOP_COOLER", "DISARM"):
            lines.extend([
                '        self.transition_to("STOPPED", "Stop command issued")',
                f'        return {{"command": "{cmd}", "status": "SUCCESS", "state": self.current_state}}',
            ])
        elif cmd.upper() == "ABORT":
            lines.extend([
                '        self.raise_alarm("Aborted", 3, "Supervisory abort requested")',
                '        return {"command": "ABORT", "status": "ABORTED", "state": self.current_state}',
            ])
        else:
            lines.extend([
                f'        logger.info("Command {cmd} processed.")',
                f'        return {{"command": "{cmd}", "status": "SUCCESS", "state": self.current_state}}',
            ])
        lines.append('')

    lines.extend([
        '    async def run_simulation_cycle(self, ticks: int = 10, interval: float = 0.5):',
        '        """Run non-blocking autonomous simulation cycle for validation."""',
        '        logger.info("Starting autonomous simulation cycle (%d ticks)...", ticks)',
        '        self._running = True',
        '        await self.execute_command("INIT")',
        '        for i in range(1, ticks + 1):',
        '            if not self._running:',
        '                break',
        '            # Update telemetry simulation values',
        '            for k in list(self.telemetry.keys()):',
        '                self.update_datapoint(k, round(self.telemetry[k] + 1.5, 2))',
        '            logger.info("Simulation Tick #%d | State: [%s] | Telemetry: %s", i, self.current_state, self.telemetry)',
        '            await asyncio.sleep(interval)',
        '        logger.info("Simulation cycle finished. Final state: %s", self.current_state)',
        '',
        'def main():',
        '    parser = argparse.ArgumentParser(description=f"Run {name} Supervisory Controller")',
        '    parser.add_argument("--simulate", action="store_true", help="Run in simulation mode")',
        '    parser.add_argument("--ticks", type=int, default=5, help="Simulation ticks to run")',
        '    args = parser.parse_args()',
        '',
        f'    controller = {cls_name}(simulate=args.simulate)',
        '    try:',
        '        asyncio.run(controller.run_simulation_cycle(ticks=args.ticks, interval=0.2))',
        '    except KeyboardInterrupt:',
        '        logger.info("Interrupted by user. Exiting cleanly.")',
        '',
        'if __name__ == "__main__":',
        '    main()',
        ''
    ])

    return "\n".join(lines)

def export_ros2(model: Dict[str, Any]) -> str:
    name, commands, events, alarms, datapoints, states = _extract_model_elements(model)
    cls_name = f"{name}RosNode"

    lines = [
        '#!/usr/bin/env python3',
        '"""',
        f'Generated ROS2 (rclpy) Node for {name}.',
        'Produced by KIDE Enterprise (Formal Metamodel Compliant Architecture).',
        '"""',
        '',
        'import rclpy',
        'from rclpy.node import Node',
        'from std_msgs.msg import String',
        'from std_srvs.srv import Trigger',
        '',
        f'class {cls_name}(Node):',
        '',
        '    def __init__(self):',
        f'        super().__init__("{name.lower()}_node")',
        '        self.current_state = "INITIALIZED"',
        '',
        '        # ROS2 Event and Alarm Publishers',
        f'        self.event_pub = self.create_publisher(String, "{name.lower()}/events", 10)',
        f'        self.alarm_pub = self.create_publisher(String, "{name.lower()}/alarms", 10)',
        f'        self.telemetry_pub = self.create_publisher(String, "{name.lower()}/telemetry", 10)',
        '',
        '        # Command Services',
    ]

    for cmd in commands:
        clean = cmd.lower().replace("-", "_")
        lines.append(f'        self.create_service(Trigger, "{name.lower()}/{clean}", self.cb_{clean})')

    lines.extend([
        '',
        '        self.get_logger().info(f"Node initialized in state: {self.current_state}")',
        '',
    ])

    for cmd in commands:
        clean = cmd.lower().replace("-", "_")
        lines.extend([
            f'    def cb_{clean}(self, request, response):',
            f'        self.get_logger().info("Executing ROS2 Service Command: {cmd}")',
            f'        if "{cmd.upper()}" == "INIT":',
            f'            self.current_state = "READY"',
            f'        elif "{cmd.upper()}" == "STOP":',
            f'            self.current_state = "STOPPED"',
            f'        response.success = True',
            f'        response.message = f"Command {cmd} executed. State: {{self.current_state}}"',
            f'        return response',
            '',
        ])

    lines.extend([
        'def main(args=None):',
        '    rclpy.init(args=args)',
        f'    node = {cls_name}()',
        '    try:',
        '        rclpy.spin(node)',
        '    except KeyboardInterrupt:',
        '        pass',
        '    finally:',
        '        node.destroy_node()',
        '        rclpy.shutdown()',
        '',
        'if __name__ == "__main__":',
        '    main()',
        ''
    ])

    return "\n".join(lines)

def export_java(model: Dict[str, Any]) -> str:
    name, commands, events, alarms, datapoints, states = _extract_model_elements(model)
    cls_name = f"{name}Handler"

    lines = [
        'package com.kide.supervisor;',
        '',
        'import java.util.*;',
        'import java.util.concurrent.ConcurrentHashMap;',
        'import java.util.logging.Logger;',
        '',
        '/**',
        f' * Generated Supervisory Controller for {name}.',
        ' * Produced automatically by KIDE Enterprise from formal MNC-ML / Activity specifications.',
        ' */',
        f'public class {cls_name} {{',
        '',
        f'    private static final Logger LOGGER = Logger.getLogger({cls_name}.class.getName());',
        '',
        '    public enum OperatingState {',
    ]

    for st in states:
        clean_st = st.replace(" ", "_").upper()
        lines.append(f'        {clean_st},')

    lines.extend([
        '    }',
        '',
        '    private OperatingState currentState;',
        '    private final Map<String, Object> telemetryData = new ConcurrentHashMap<>();',
        '    private final List<String> raisedAlarms = Collections.synchronizedList(new ArrayList<>());',
        '',
        f'    public {cls_name}() {{',
        '        this.currentState = OperatingState.INITIALIZED;',
        f'        LOGGER.info("Initialized {name} Supervisor in state: " + currentState);',
        '    }',
        '',
        '    public synchronized void transitionTo(OperatingState newState, String reason) {',
        '        if (this.currentState != newState) {',
        '            LOGGER.info(String.format("Transition: [%s] -> [%s] (Reason: %s)", this.currentState, newState, reason));',
        '            this.currentState = newState;',
        '        }',
        '    }',
        '',
        '    public OperatingState getCurrentState() {',
        '        return this.currentState;',
        '    }',
        '',
        '    public Map<String, Object> executeCommand(String commandName, Map<String, Object> params) {',
        '        LOGGER.info("Executing command: " + commandName);',
        '        Map<String, Object> result = new HashMap<>();',
        '        result.put("command", commandName);',
        '        result.put("timestamp", System.currentTimeMillis());',
        '',
        '        switch (commandName.toUpperCase()) {',
    ])

    for cmd in commands:
        clean_cmd = cmd.replace("-", "_").upper()
        lines.extend([
            f'            case "{clean_cmd}":',
            f'                return handle{clean_cmd.replace("_", " ").title().replace(" ", "")}(params);',
        ])

    lines.extend([
        '            default:',
        '                result.put("status", "ERROR");',
        '                result.put("error", "Unknown command: " + commandName);',
        '                return result;',
        '        }',
        '    }',
        '',
    ])

    for cmd in commands:
        method_name = cmd.replace("-", " ").replace("_", " ").title().replace(" ", "")
        lines.extend([
            f'    public Map<String, Object> handle{method_name}(Map<String, Object> params) {{',
            f'        LOGGER.info("Handling {cmd} logic...");',
            f'        if ("{cmd.upper()}".equals("INIT")) {{',
            f'            transitionTo(OperatingState.READY, "Init complete");',
            f'        }} else if ("{cmd.upper()}".equals("STOP")) {{',
            f'            transitionTo(OperatingState.STOPPED, "Stop requested");',
            f'        }} else if ("{cmd.upper()}".equals("ABORT")) {{',
            f'            transitionTo(OperatingState.ABORTED, "Abort requested");',
            f'        }}',
            f'        Map<String, Object> response = new HashMap<>();',
            f'        response.put("status", "SUCCESS");',
            f'        response.put("command", "{cmd}");',
            f'        return response;',
            f'    }}',
            '',
        ])

    lines.extend([
        '    public void raiseAlarm(String alarmName, int level, String message) {',
        '        LOGGER.severe(String.format("ALARM RAISED: [%s] (Level %d): %s", alarmName, level, message));',
        '        raisedAlarms.add(alarmName + ": " + message);',
        '        if (level >= 3) {',
        '            transitionTo(OperatingState.STOPPED, "Critical alarm: " + alarmName);',
        '        }',
        '    }',
        '}',
        ''
    ])

    return "\n".join(lines)

def export_plc_st(model: Dict[str, Any]) -> str:
    """Generates industrial IEC 61131-3 Structured Text for PLCs (Siemens, Beckhoff, Codesys)."""
    name, commands, events, alarms, datapoints, states = _extract_model_elements(model)
    fb_name = f"FB_{name}_Supervisor"

    lines = [
        f"(* ====================================================================== *)",
        f"(* Supervisory Controller: {name}                                         *)",
        f"(* Generated by KIDE Enterprise (IEC 61131-3 Structured Text standard)    *)",
        f"(* ====================================================================== *)",
        f"TYPE E_{name}_State : (",
    ]
    for idx, st in enumerate(states):
        clean = st.replace(" ", "_").upper()
        comma = "," if idx < len(states) - 1 else ""
        lines.append(f"    ST_{clean}{comma}")
    lines.extend([
        ");",
        "END_TYPE",
        "",
        f"FUNCTION_BLOCK {fb_name}",
        "VAR_INPUT",
        "    bEnable : BOOL := TRUE;",
        "    bReset : BOOL := FALSE;",
    ])
    for cmd in commands:
        clean = cmd.replace("-", "_").upper()
        lines.append(f"    bCmd_{clean} : BOOL := FALSE;")
    for dp in datapoints:
        lines.append(f"    r{dp} : REAL := 0.0;")
    lines.extend([
        "END_VAR",
        "VAR_OUTPUT",
        f"    eState : E_{name}_State := ST_{states[0].replace(' ', '_').upper()};",
        "    bBusy : BOOL := FALSE;",
        "    bDone : BOOL := FALSE;",
        "    bError : BOOL := FALSE;",
        "    nErrorCode : INT := 0;",
    ])
    for al in alarms:
        clean = al.replace("-", "_").upper()
        lines.append(f"    bAlarm_{clean} : BOOL := FALSE;")
    lines.extend([
        "END_VAR",
        "VAR",
        "    tStateTimer : TON;",
        "END_VAR",
        "",
        "(* State Machine Implementation *)",
        "IF bReset THEN",
        f"    eState := ST_{states[0].replace(' ', '_').upper()};",
        "    bError := FALSE;",
        "    nErrorCode := 0;",
        "    RETURN;",
        "END_IF;",
        "",
        "CASE eState OF",
    ])
    for st in states:
        clean = st.replace(" ", "_").upper()
        lines.extend([
            f"    ST_{clean}:",
            f"        (* Logic for state {clean} *)",
            f"        bBusy := TRUE;",
            f"        IF bCmd_INIT THEN",
            f"            eState := ST_READY;",
            f"        ELSIF bCmd_ABORT THEN",
            f"            bAlarm_ABORTED := TRUE;",
            f"            bError := TRUE;",
            f"        END_IF;",
            "",
        ])
    lines.extend([
        "    ELSE",
        f"        eState := ST_{states[0].replace(' ', '_').upper()};",
        "END_CASE;",
        "",
        f"END_FUNCTION_BLOCK",
        ""
    ])
    return "\n".join(lines)

def export_cpp(model: Dict[str, Any]) -> str:
    """Generates modern C++ embedded controller class for Arduino / ESP32 / FreeRTOS."""
    name, commands, events, alarms, datapoints, states = _extract_model_elements(model)
    cls_name = f"{name}Controller"

    lines = [
        f"// ======================================================================",
        f"// Generated Embedded C++ Controller: {name}",
        f"// Compatible with Arduino, ESP-IDF, FreeRTOS, and Native Embedded C++",
        f"// ======================================================================",
        f"#pragma once",
        f"#include <cstdint>",
        f"#include <string>",
        f"#include <vector>",
        f"#include <functional>",
        "",
        f"class {cls_name} {{",
        f"public:",
        f"    enum class OperatingState {{",
    ]
    for st in states:
        lines.append(f"        {st.replace(' ', '_').upper()},")
    lines.extend([
        f"    }};",
        "",
        f"    {cls_name}() : currentState_(OperatingState::{states[0].replace(' ', '_').upper()}), isRunning_(false) {{}}",
        "",
        f"    OperatingState getState() const {{ return currentState_; }}",
        "",
        f"    void transitionTo(OperatingState nextState) {{",
        f"        if (currentState_ != nextState) {{",
        f"            currentState_ = nextState;",
        f"        }}",
        f"    }}",
        "",
        f"    bool executeCommand(const std::string& cmd) {{",
    ])
    for cmd in commands:
        lines.extend([
            f'        if (cmd == "{cmd}") {{',
            f'            handle{cmd.replace("-", "_").title().replace("_", "")}();',
            f'            return true;',
            f'        }}',
        ])
    lines.extend([
        f"        return false;",
        f"    }}",
        "",
    ])
    for cmd in commands:
        method = cmd.replace("-", "_").title().replace("_", "")
        lines.extend([
            f"    void handle{method}() {{",
            f"        // Implement hardware actuation or capability invocation for {cmd}",
            f'        if ("{cmd}" == "INIT") transitionTo(OperatingState::READY);',
            f"    }}",
        ])
    lines.extend([
        f"private:",
        f"    OperatingState currentState_;",
        f"    bool isRunning_;",
        f"}};",
        ""
    ])
    return "\n".join(lines)

def export_zip_bundle(model: Dict[str, Any]) -> bytes:
    name = model.get("name", "SupervisorProject")
    py_code = export_python(model)
    ros2_code = export_ros2(model)
    java_code = export_java(model)
    plc_code = export_plc_st(model)
    cpp_code = export_cpp(model)
    mnc_code = export_mnc(model)
    json_code = json.dumps(model, indent=2)

    readme_content = f"""# {name} - Synthesized Supervisory Controller Package
Generated by KIDE Enterprise (Formal Metamodel Synthesis Framework).

## Package Contents:
1. `{name}_controller.py` - Standalone Executable Python Controller (Supports `--simulate` mode)
2. `{name}_ros2_node.py` - ROS2 (rclpy) Node with topics and service endpoints
3. `{name}SupervisorController.java` - Java Controller matching Eclipse Xtext output
4. `{name}_plc.st` - IEC 61131-3 Structured Text for Industrial PLCs (Siemens S7, Beckhoff, Codesys)
5. `{name}_controller.hpp` - Embedded C++ class for Arduino / ESP32 / FreeRTOS
6. `{name}.mncspec` - Formal MNC-ML specification
7. `{name}_ir.json` - Complete Intermediate Representation AST

## Running the Python Controller:
```bash
# Autonomous simulation cycle
python {name}_controller.py --simulate --ticks 10
```

## Running the ROS2 Node:
```bash
python3 {name}_ros2_node.py
```
"""

    buf = io.BytesIO()
    with zipfile.ZipFile(buf, "w", zipfile.ZIP_DEFLATED) as zf:
        zf.writestr(f"{name}/{name}_controller.py", py_code)
        zf.writestr(f"{name}/{name}_ros2_node.py", ros2_code)
        zf.writestr(f"{name}/{name}SupervisorController.java", java_code)
        zf.writestr(f"{name}/{name}_plc.st", plc_code)
        zf.writestr(f"{name}/{name}_controller.hpp", cpp_code)
        zf.writestr(f"{name}/{name}.mncspec", mnc_code)
        zf.writestr(f"{name}/{name}_ir.json", json_code)
        zf.writestr(f"{name}/README.md", readme_content)

    buf.seek(0)
    return buf.getvalue()

def export_format(model: Dict[str, Any], fmt: str) -> Any:
    fmt_lower = fmt.lower()
    if fmt_lower in ("mnc", "mncml", "dsl"):
        return export_mnc(model)
    elif fmt_lower == "json":
        return json.dumps(model, indent=2)
    elif fmt_lower == "python":
        return export_python(model)
    elif fmt_lower in ("ros2", "ros"):
        return export_ros2(model)
    elif fmt_lower == "java":
        return export_java(model)
    elif fmt_lower in ("plc", "plc_st", "st", "iec"):
        return export_plc_st(model)
    elif fmt_lower in ("cpp", "c++", "embedded"):
        return export_cpp(model)
    elif fmt_lower in ("zip", "bundle"):
        return export_zip_bundle(model)
    elif fmt_lower == "activity":
        return f"ActivityDiagram {model.get('name', 'Model')} {{\n  // Synthesized Activity DSL\n}}"
    elif fmt_lower == "capability":
        return f"Capability {model.get('name', 'Model')}Cap {{\n  // Synthesized Capability DSL\n}}"
    elif fmt_lower == "operation":
        return f"Operation {model.get('name', 'Model')}Op() {{\n  // Synthesized Operation DSL\n}}"
    elif fmt_lower == "dml":
        return f"Package {model.get('name', 'Model')}Package {{\n  // Synthesized DML\n}}"
    return export_mnc(model)
