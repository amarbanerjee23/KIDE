"""
Custom Code Generator Engine for KIDE Enterprise.
Mirrors the Eclipse Xtext AbstractGenerator and IFileSystemAccess2 concepts:
class CustomGenerator extends AbstractGenerator {
    override void doGenerate(Resource resource, IFileSystemAccess2 fsa, IGeneratorContext context) { ... }
}
"""

import sys
import io
import traceback
from typing import Any, Dict, List, Optional
from .exporter import export_python, export_ros2, export_java, export_plc_st, export_cpp, export_mnc, export_zip_bundle

DEFAULT_GENERATOR_TEMPLATE = """# Custom Code Generator for KIDE
# Mirrors Eclipse Xtext AbstractGenerator:
# def do_generate(model, fsa):
#     fsa.generate_file("output.txt", "Content")

def do_generate(model, fsa):
    name = model.get("name", "CustomSystem")
    systems = model.get("systems", [])
    iface = systems[0] if systems else model.get("interface_description", {})
    commands = [c.get("name") if isinstance(c, dict) else str(c) for c in iface.get("commands", [])]
    states = model.get("control_node", {}).get("operating_states", ["INITIALIZED", "READY"])

    report_lines = [
        f"# System Specification & Deployment Manifest: {name}",
        "Generated automatically by project custom generator.",
        "",
        "## Supported Operating States:",
    ]
    for s in states:
        report_lines.append(f"- **{s}**")
    
    report_lines.extend([
        "",
        "## Available Commands:",
    ])
    for c in commands:
        report_lines.append(f"- `execute_{c.lower()}()`")
    
    # Emit file using FileSystemAccess
    fsa.generate_file(f"{name}_manifest.md", "\\n".join(report_lines))
"""

class FileSystemAccess:
    """Mirrors Eclipse Xtext IFileSystemAccess2."""
    def __init__(self):
        self.files: Dict[str, str] = {}

    def generate_file(self, filename: str, content: str):
        self.files[filename] = str(content)

    def get_files(self) -> Dict[str, str]:
        return self.files

class CustomGeneratorEngine:
    """Executes built-in and project-defined generators."""

    BUILTIN_GENERATORS = [
        {
            "id": "python",
            "name": "Python Controller",
            "description": "Event-driven asynchronous supervisory controller with simulation runner",
            "target_extension": ".py",
            "builtin": True
        },
        {
            "id": "ros2",
            "name": "ROS2 rclpy Node",
            "description": "Production ROS2 node with topics, services, and lifecycle handlers",
            "target_extension": ".py",
            "builtin": True
        },
        {
            "id": "java",
            "name": "Java Controller",
            "description": "Java controller class matching Eclipse Xtext src-gen/code/ specification",
            "target_extension": ".java",
            "builtin": True
        },
        {
            "id": "plc_st",
            "name": "PLC Structured Text (IEC 61131-3)",
            "description": "Industrial PLC code with FUNCTION_BLOCK, state CASE, and I/O mapping",
            "target_extension": ".st",
            "builtin": True
        },
        {
            "id": "cpp",
            "name": "Embedded C++ Class",
            "description": "Embedded C++ controller class for Arduino / ESP32 / FreeRTOS",
            "target_extension": ".hpp",
            "builtin": True
        },
        {
            "id": "mnc",
            "name": "MNC-ML Formal Spec",
            "description": "Synthesized formal M&CML specification matching KIDE metamodel grammar",
            "target_extension": ".mncspec",
            "builtin": True
        }
    ]

    @classmethod
    def list_generators(cls, project_files: Optional[List[Dict[str, Any]]] = None) -> List[Dict[str, Any]]:
        generators = list(cls.BUILTIN_GENERATORS)
        if project_files:
            for f in project_files:
                name = f.get("name", "")
                if name.endswith(".generator.py") or name.endswith(".generator.json"):
                    generators.append({
                        "id": f"project_{f.get('id', name)}",
                        "name": name,
                        "file_id": f.get("id"),
                        "file_name": name,
                        "description": f"Custom generator defined in project file {name}",
                        "target_extension": ".txt",
                        "builtin": False
                    })
        return generators

    @classmethod
    def run_builtin_generator(cls, generator_id: str, model: Dict[str, Any]) -> Dict[str, str]:
        name = model.get("name", "Supervisor")
        fsa = FileSystemAccess()
        
        if generator_id == "python":
            fsa.generate_file(f"{name}_controller.py", export_python(model))
        elif generator_id == "ros2":
            fsa.generate_file(f"{name}_ros2_node.py", export_ros2(model))
        elif generator_id == "java":
            fsa.generate_file(f"{name}SupervisorController.java", export_java(model))
        elif generator_id in ("plc_st", "plc"):
            fsa.generate_file(f"{name}_plc.st", export_plc_st(model))
        elif generator_id in ("cpp", "c++"):
            fsa.generate_file(f"{name}_controller.hpp", export_cpp(model))
        elif generator_id == "mnc":
            fsa.generate_file(f"{name}.mncspec", export_mnc(model))
        else:
            raise ValueError(f"Unknown builtin generator: {generator_id}")
            
        return fsa.get_files()

    @classmethod
    def run_custom_script(cls, script_content: str, model: Dict[str, Any]) -> Dict[str, str]:
        """Executes a user-defined generator script in a controlled namespace."""
        fsa = FileSystemAccess()
        local_scope: Dict[str, Any] = {
            "model": model,
            "fsa": fsa,
            "export_python": export_python,
            "export_ros2": export_ros2,
            "export_java": export_java,
            "export_plc_st": export_plc_st,
            "export_cpp": export_cpp,
            "export_mnc": export_mnc
        }

        try:
            exec(script_content, local_scope)
            res = None
            if "do_generate" in local_scope and callable(local_scope["do_generate"]):
                res = local_scope["do_generate"](model, fsa)
            elif "generate" in local_scope and callable(local_scope["generate"]):
                import inspect
                sig = inspect.signature(local_scope["generate"])
                if len(sig.parameters) >= 2:
                    res = local_scope["generate"](model, fsa)
                else:
                    res = local_scope["generate"](model)
            
            if isinstance(res, dict):
                for fname, fcontent in res.items():
                    fsa.generate_file(fname, str(fcontent))
        except Exception as e:
            err_msg = f"Error executing custom generator: {e}\n{traceback.format_exc()}"
            fsa.generate_file("generator_error.log", err_msg)

        return fsa.get_files()

    @classmethod
    def run_generator(
        cls, 
        generator_id: str, 
        model: Dict[str, Any], 
        custom_script: Optional[str] = None
    ) -> Dict[str, str]:
        if custom_script:
            return cls.run_custom_script(custom_script, model)
        if any(b["id"] == generator_id for b in cls.BUILTIN_GENERATORS):
            return cls.run_builtin_generator(generator_id, model)
        return cls.run_builtin_generator("python", model)

    @classmethod
    def get_template_source(cls, generator_id: str) -> Dict[str, Any]:
        """Returns the code generation template definition and source."""
        templates = cls.get_all_templates()
        if generator_id in templates:
            return templates[generator_id]
        return templates.get("python", {})

    @classmethod
    def get_all_templates(cls) -> Dict[str, Dict[str, Any]]:
        return {
            "python": {
                "id": "python",
                "name": "Python Controller Generator",
                "language": "python",
                "target_extension": ".py",
                "specification_module": "Chapter 7 & 8: Supervisory Controller Synthesis",
                "description": "Generates an asynchronous event-driven controller with OperatingState machine, command handlers, event bus, and telemetry logging.",
                "template_source": '''# ==============================================================================
# Python Controller Generator Template (Mirrors AbstractGenerator)
# Target: Python 3.10+ Async Event-Driven Controller
# ==============================================================================

class PythonSupervisorGenerator:
    """
    Template rules for synthesizing Python Supervisory Controllers:
    - Enum OperatingState: maps all interface & control node operating states
    - EventBus: subscribes to external equipment events and dispatches callbacks
    - Controller: implements command handlers (INIT, START, STOP, capability actions)
    - State Machine: verifies transitions and executes entry/exit hooks
    """
    
    def generate(model, fsa):
        name = model["name"]
        states = model["control_node"]["operating_states"]
        commands = [c["name"] for c in model["interface_description"]["commands"]]
        events = [e["name"] for e in model["interface_description"]["events"]]
        alarms = [a["name"] for a in model["interface_description"]["alarms"]]
        transitions = model["control_node"].get("transitions", [])
        
        code = f"""import asyncio
import logging
from enum import Enum, auto
from typing import Dict, List, Any, Optional, Callable

logging.basicConfig(level=logging.INFO, format="%(asctime)s [%(levelname)s] %(message)s")

class OperatingState(Enum):
    {chr(10).join(f"    {s} = '{s}'" for s in states)}

class {name}Controller:
    def __init__(self):
        self.current_state = OperatingState.{states[0] if states else "INITIALIZED"}
        self.active_alarms: List[str] = []
        self.telemetry: Dict[str, Any] = {{}}
        self.event_handlers: Dict[str, List[Callable]] = {{}}
        
    async def initialize(self):
        logging.info("Initializing supervisor [{name}]...")
        self.current_state = OperatingState.READY
        logging.info(f"State transitioned to [{{self.current_state.value}}]")

    async def execute_command(self, cmd_name: str, payload: Optional[Dict] = None):
        logging.info(f"Command received: [{{cmd_name}}]")
        if cmd_name == "INIT":
            await self.initialize()
        elif cmd_name == "START":
            # Transition from READY into operational workflow
            self.current_state = OperatingState.{states[2] if len(states) > 2 else states[-1]}
            logging.info(f"Workflow started. State: [{{self.current_state.value}}]")

    def handle_event(self, event_name: str, data: Optional[Dict] = None):
        logging.info(f"Equipment event received: [{{event_name}}]")
        for handler in self.event_handlers.get(event_name, []):
            handler(data)
"""
        fsa.generate_file(f"{name}_controller.py", code)
'''
            },
            "ros2": {
                "id": "ros2",
                "name": "ROS2 rclpy Node Generator",
                "language": "python",
                "target_extension": ".py",
                "specification_module": "Chapter 8.2: Robotic Systems Integration",
                "description": "Generates a complete ROS2 Node utilizing rclpy, with publishers for events, service servers for commands, and subscription hooks for sensory equipment.",
                "template_source": '''# ==============================================================================
# ROS2 Node Generator Template (Mirrors ROS2Generator.xtend)
# Target: ROS2 Humble / Iron / Rolling (rclpy)
# ==============================================================================

class ROS2NodeGenerator:
    """
    Template rules for synthesizing ROS2 Supervisory Nodes:
    - Node: class <Name>SupervisorNode(Node)
    - Publishers: topics for published events (/supervisor/<model>/events/<event>)
    - Subscribers: topics for equipment inputs (/supervisor/<model>/inputs/<data>)
    - Service Servers: services for commands (/supervisor/<model>/cmd/<command>)
    - Timer Loop: 10Hz status & telemetry broadcast
    """
    
    def generate(model, fsa):
        name = model["name"]
        commands = [c["name"] for c in model["interface_description"]["commands"]]
        events = [e["name"] for e in model["interface_description"]["events"]]
        
        code = f"""import rclpy
from rclpy.node import Node
from std_msgs.msg import String
from example_interfaces.srv import Trigger

class {name}SupervisorNode(Node):
    def __init__(self):
        super().__init__('{name.lower()}_supervisor')
        self.state_pub = self.create_publisher(String, '~/state', 10)
        
        # Service servers for supervisory commands
        {chr(10).join(f"        self.create_service(Trigger, '~/cmd/{c.lower()}', self.handle_{c.lower()})" for c in commands)}
        
        self.timer = self.create_timer(0.1, self.tick)
        self.get_logger().info('{name} Supervisor ROS2 Node started.')

    def tick(self):
        msg = String()
        msg.data = "STATE: ACTIVE"
        self.state_pub.publish(msg)
"""
        fsa.generate_file(f"{name}_ros2_node.py", code)
'''
            },
            "java": {
                "id": "java",
                "name": "Java Controller Generator",
                "language": "java",
                "target_extension": ".java",
                "specification_module": "Eclipse src-gen/code/ Java Synthesis",
                "description": "Generates an enterprise Java supervisory controller class conforming to the original Eclipse Xtext generator output.",
                "template_source": '''/* ==============================================================================
 * Java Controller Generator Template (Mirrors Eclipse Xtext src-gen)
 * Target: Java 17+ / OSGi / Spring Boot
 * ==============================================================================
 */
package com.kide.supervisor;

public class JavaControllerGenerator {
    /**
     * Template rules:
     * - enum State: all OperatingStates from InterfaceDescription & ControlNode
     * - executeCommand(String name, Map<String, Object> params)
     * - publishEvent(String eventName)
     * - raiseAlarm(String alarmName, int level)
     * - transition(State nextState)
     */
}
'''
            },
            "plc_st": {
                "id": "plc_st",
                "name": "PLC Structured Text Generator",
                "language": "pascal",
                "target_extension": ".st",
                "specification_module": "Industrial Automation & IEC 61131-3 Standard",
                "description": "Generates IEC 61131-3 compliant Structured Text FUNCTION_BLOCK for industrial PLCs (Siemens TIA Portal, Beckhoff TwinCAT, Rockwell Studio 5000).",
                "template_source": '''(* ==============================================================================
 * IEC 61131-3 PLC Structured Text Generator Template
 * Target: Siemens S7-1200/1500, Beckhoff TwinCAT 3, Rockwell ControlLogix
 * ==============================================================================
 *)
FUNCTION_BLOCK FB_Supervisor_Generator
VAR_INPUT
    bInitCmd   : BOOL;
    bStartCmd  : BOOL;
    bStopCmd   : BOOL;
    bResetAlm  : BOOL;
END_VAR
VAR_OUTPUT
    nState     : INT;
    bReady     : BOOL;
    bError     : BOOL;
    sAlarmMsg  : STRING[80];
END_VAR
VAR
    (* State Machine Case Encodings *)
    c_INIT     : INT := 0;
    c_READY    : INT := 1;
    c_RUNNING  : INT := 2;
    c_ALARM    : INT := 99;
END_VAR

CASE nState OF
    c_INIT:
        IF bInitCmd THEN
            nState := c_READY;
        END_IF;
    c_READY:
        bReady := TRUE;
        IF bStartCmd THEN
            nState := c_RUNNING;
        END_IF;
    c_RUNNING:
        (* Operational Activity Loop *)
    c_ALARM:
        bError := TRUE;
        IF bResetAlm THEN
            nState := c_INIT;
        END_IF;
END_CASE;
END_FUNCTION_BLOCK
'''
            },
            "cpp": {
                "id": "cpp",
                "name": "Embedded C++17 Generator",
                "language": "cpp",
                "target_extension": ".hpp",
                "specification_module": "High-Performance Embedded Systems",
                "description": "Generates a header-only, zero-heap or thread-safe C++17 supervisory state machine for ESP32, STM32, FreeRTOS, or Linux edge devices.",
                "template_source": '''// ==============================================================================
// Embedded C++17 Class Generator Template
// Target: C++17 (POSIX / FreeRTOS / Arduino / Embedded Linux)
// ==============================================================================
#pragma once
#include <string>
#include <vector>
#include <functional>
#include <iostream>

class CppSupervisorGenerator {
    // Generates:
    // enum class State { INITIALIZED, READY, ... };
    // class SupervisorController {
    //    void init();
    //    void dispatch(const std::string& command);
    // };
};
'''
            },
            "custom": {
                "id": "custom",
                "name": "Custom Xtend / Python Generator Script",
                "language": "python",
                "target_extension": ".py",
                "specification_module": "Eclipse Xtext IGenerator2 & IFileSystemAccess2",
                "description": "Write custom generator scripts to synthesize any custom target code, deployment configs, or documentation from the model.",
                "template_source": DEFAULT_GENERATOR_TEMPLATE
            }
        }
