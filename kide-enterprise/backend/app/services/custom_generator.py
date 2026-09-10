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
            "description": "Synthesized formal M&CML specification matching thesis grammar",
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
            if "do_generate" in local_scope:
                local_scope["do_generate"](model, fsa)
        except Exception as e:
            err_msg = f"Error executing custom generator: {e}\\n{traceback.format_exc()}"
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