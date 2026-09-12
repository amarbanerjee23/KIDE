"""
Knowledge Graph Service for KIDE Enterprise.
Implements the thesis Knowledge Repository ontology and Property Knowledge Graph.

Structures:
- Domain / Case Study Categories (Access Control, Robotics, Process, Chemical, Smart Building)
- Physical Devices & Equipment (Sensors, Actuators, Controllers, Transmitters)
- Interface Descriptions (Ports, Protocols, Endpoints)
- Capabilities (Fireable Commands, Receivable Events, Alarms, DataPoints)
- Executable Operations (Driver scripts, Input/Output parameters)
- Data Models & Schema Primitives (DML Types)
- Supervisory Activities & Workflows (Sequences, Conditions, Outcomes)
- Synthesized Operating States & Transitions (Formal MNC-ML Automata)
"""

from typing import Any, Dict, List, Optional, Set, Tuple
import re

from .knowledge_hub import EQUIPMENT_CATALOG, KnowledgeHubService
from ..parsers.dml_parser import parse as parse_dml
from ..parsers.capability_parser import parse as parse_capability
from ..parsers.operation_parser import parse as parse_operation
from ..parsers.activity_parser import parse as parse_activity
from ..parsers.mnc_parser import parse as parse_mnc


class KnowledgeGraphService:
    """Builds, queries, and exports the KIDE Thesis Knowledge Graph."""

    @classmethod
    def build_global_catalog_graph(cls) -> Dict[str, Any]:
        """
        Builds the global cross-domain Knowledge Repository graph from all 
        pre-indexed equipment and ontology specifications in the thesis.
        """
        nodes: List[Dict[str, Any]] = []
        edges: List[Dict[str, Any]] = []
        node_ids: Set[str] = set()

        def add_node(node_id: str, name: str, node_type: str, category: str, 
                     properties: Optional[Dict[str, Any]] = None, 
                     thesis_ref: Optional[str] = None):
            if node_id in node_ids:
                return
            node_ids.add(node_id)
            nodes.append({
                "id": node_id,
                "name": name,
                "type": node_type,
                "category": category,
                "thesis_reference": thesis_ref or "KIDE Knowledge Repository",
                "properties": properties or {}
            })

        def add_edge(source: str, target: str, edge_type: str, label: str, properties: Optional[Dict[str, Any]] = None):
            if source not in node_ids or target not in node_ids:
                return
            edge_id = f"{source}-{edge_type}-{target}"
            edges.append({
                "id": edge_id,
                "source": source,
                "target": target,
                "type": edge_type,
                "label": label,
                "properties": properties or {}
            })

        # Root Repository Node
        root_id = "repo_kide_root"
        add_node(
            root_id,
            "KIDE Knowledge Repository",
            "repository",
            "Knowledge Repository",
            {"description": "Central ontology repository for industrial automation and supervisory control"}
        )

        # Process each catalog domain
        for item in EQUIPMENT_CATALOG:
            cat_id = f"cat_{item['id']}"
            thesis_ref = item.get("thesis_reference", "Thesis Chapter 8")
            
            # Domain / System Node
            add_node(
                cat_id,
                item["name"],
                "domain",
                item.get("category", "Industrial Automation"),
                {
                    "description": item.get("description", ""),
                    "tags": item.get("tags", []),
                    "raw_id": item["id"]
                },
                thesis_ref=thesis_ref
            )
            add_edge(root_id, cat_id, "CONTAINS_DOMAIN", "contains domain")

            # Physical Devices
            for dev_name in item.get("devices", []):
                dev_id = f"dev_{item['id']}_{re.sub(r'[^a-zA-Z0-9_]', '_', dev_name.lower())}"
                add_node(
                    dev_id,
                    dev_name,
                    "device",
                    "Physical Equipment",
                    {"parent_domain": item["name"]},
                    thesis_ref=thesis_ref
                )
                add_edge(cat_id, dev_id, "INCLUDES_EQUIPMENT", "includes equipment")

            # Parse files to extract formal entities
            for file_info in item.get("files", []):
                content = file_info.get("content", "")
                ftype = file_info.get("file_type", "")
                fname = file_info.get("filename", "")

                if ftype == "dml" or fname.endswith(".dml"):
                    try:
                        parsed = parse_dml(content)
                        pkg_name = parsed.get("name") or "DefaultPkg"
                        for dm in parsed.get("data_models", []):
                            dm_name = dm.get("name")
                            if dm_name:
                                dm_id = f"dm_{item['id']}_{dm_name}"
                                add_node(
                                    dm_id,
                                    dm_name,
                                    "datamodel",
                                    "Domain Data Model",
                                    {
                                        "package": pkg_name,
                                        "primitives": dm.get("primitives", []),
                                        "composites": dm.get("composites", [])
                                    },
                                    thesis_ref=thesis_ref
                                )
                                add_edge(cat_id, dm_id, "DEFINES_DATA_MODEL", "defines schema")
                    except Exception:
                        pass

                elif ftype == "capability" or fname.endswith(".cap"):
                    try:
                        parsed = parse_capability(content)
                        cap_name = parsed.get("name")
                        if cap_name:
                            cap_id = f"cap_{item['id']}_{cap_name}"
                            iface_name = parsed.get("component_interface", "")
                            
                            add_node(
                                cap_id,
                                cap_name,
                                "capability",
                                "Semantic Capability",
                                {
                                    "component_interface": iface_name,
                                    "commands": parsed.get("control_capabilities", {}).get("commands", []),
                                    "events": parsed.get("control_capabilities", {}).get("events", []),
                                    "alarms": parsed.get("control_capabilities", {}).get("alarms", []),
                                    "data_points": parsed.get("control_capabilities", {}).get("data_points", [])
                                },
                                thesis_ref=thesis_ref
                            )
                            add_edge(cat_id, cap_id, "PROVIDES_CAPABILITY", "provides capability")

                            # Add Interface Description Node
                            if iface_name:
                                iface_id = f"iface_{item['id']}_{iface_name}"
                                add_node(
                                    iface_id,
                                    iface_name,
                                    "interface",
                                    "Component Interface",
                                    {"binding_capability": cap_name},
                                    thesis_ref=thesis_ref
                                )
                                add_edge(cap_id, iface_id, "COMPATIBLE_WITH", "compatible with")

                            # Link capabilities to device nodes
                            for dev_name in item.get("devices", []):
                                dev_id = f"dev_{item['id']}_{re.sub(r'[^a-zA-Z0-9_]', '_', dev_name.lower())}"
                                add_edge(dev_id, cap_id, "IMPLEMENTS_CAPABILITY", "implements capability")
                    except Exception:
                        pass

                elif ftype == "operation" or fname.endswith(".op"):
                    try:
                        parsed = parse_operation(content)
                        for op in parsed.get("operations", []):
                            op_name = op.get("name")
                            if op_name:
                                op_id = f"op_{item['id']}_{op_name}"
                                add_node(
                                    op_id,
                                    op_name,
                                    "operation",
                                    "Device Operation",
                                    {
                                        "inputs": op.get("input_parameters", []),
                                        "output": op.get("output_parameter", {}),
                                        "script": op.get("executable_script", "")
                                    },
                                    thesis_ref=thesis_ref
                                )
                                add_edge(cat_id, op_id, "OFFERS_OPERATION", "offers operation")
                    except Exception:
                        pass

        return {
            "scope": "global",
            "nodes": nodes,
            "edges": edges,
            "stats": {
                "total_nodes": len(nodes),
                "total_edges": len(edges),
                "domains": len(EQUIPMENT_CATALOG),
                "capabilities": sum(1 for n in nodes if n["type"] == "capability"),
                "devices": sum(1 for n in nodes if n["type"] == "device"),
                "operations": sum(1 for n in nodes if n["type"] == "operation"),
                "data_models": sum(1 for n in nodes if n["type"] == "datamodel")
            }
        }

    @classmethod
    def build_project_knowledge_graph(
        cls, 
        project_id: int, 
        project_name: str, 
        files: List[Dict[str, Any]], 
        synthesized_model: Optional[Dict[str, Any]] = None
    ) -> Dict[str, Any]:
        """
        Builds the live connected knowledge graph for a specific project workspace.
        Connects project-defined data models, capabilities, operations, activities, 
        and synthesized supervisory operating states.
        """
        nodes: List[Dict[str, Any]] = []
        edges: List[Dict[str, Any]] = []
        node_ids: Set[str] = set()

        def add_node(node_id: str, name: str, node_type: str, category: str, 
                     properties: Optional[Dict[str, Any]] = None, 
                     source_file: Optional[str] = None):
            if node_id in node_ids:
                return
            node_ids.add(node_id)
            nodes.append({
                "id": node_id,
                "name": name,
                "type": node_type,
                "category": category,
                "source_file": source_file,
                "properties": properties or {}
            })

        def add_edge(source: str, target: str, edge_type: str, label: str, properties: Optional[Dict[str, Any]] = None):
            if source not in node_ids or target not in node_ids:
                return
            edge_id = f"{source}-{edge_type}-{target}"
            edges.append({
                "id": edge_id,
                "source": source,
                "target": target,
                "type": edge_type,
                "label": label,
                "properties": properties or {}
            })

        # Project Node
        proj_node_id = f"proj_{project_id}"
        add_node(
            proj_node_id,
            project_name,
            "project",
            "Project Root",
            {"project_id": project_id, "file_count": len(files)}
        )

        cap_nodes: List[str] = []
        op_nodes: List[str] = []
        act_nodes: List[str] = []
        state_nodes: List[str] = []

        # 1. Process Project Files (DML, Cap, Op, Activity, MNC)
        for f in files:
            fname = f.get("filename") or f.get("name") or ""
            content = f.get("content") or ""

            # DML
            if fname.endswith(".dml"):
                try:
                    parsed = parse_dml(content)
                    pkg = parsed.get("name", "DomainPackage")
                    for dm in parsed.get("data_models", []):
                        dm_name = dm.get("name")
                        if dm_name:
                            dm_id = f"p_dm_{dm_name}"
                            add_node(
                                dm_id,
                                dm_name,
                                "datamodel",
                                "Data Schema",
                                {"package": pkg, "primitives": dm.get("primitives", [])},
                                source_file=fname
                            )
                            add_edge(proj_node_id, dm_id, "DEFINES_DATA_MODEL", "defines schema")
                except Exception:
                    pass

            # Capability
            elif fname.endswith(".cap") or fname.endswith(".capability"):
                try:
                    parsed = parse_capability(content)
                    cap_name = parsed.get("name")
                    if cap_name:
                        cap_id = f"p_cap_{cap_name}"
                        cap_nodes.append(cap_id)
                        cc = parsed.get("control_capabilities", {})
                        add_node(
                            cap_id,
                            cap_name,
                            "capability",
                            "Device Capability",
                            {
                                "interface": parsed.get("component_interface", ""),
                                "commands": cc.get("commands", []),
                                "events": cc.get("events", []),
                                "alarms": cc.get("alarms", []),
                                "data_points": cc.get("data_points", [])
                            },
                            source_file=fname
                        )
                        add_edge(proj_node_id, cap_id, "HAS_CAPABILITY", "has capability")

                        # Interface node
                        iface_name = parsed.get("component_interface")
                        if iface_name:
                            iface_id = f"p_iface_{iface_name}"
                            add_node(
                                iface_id,
                                iface_name,
                                "interface",
                                "Hardware Interface",
                                {"bound_capability": cap_name},
                                source_file=fname
                            )
                            add_edge(cap_id, iface_id, "COMPATIBLE_WITH", "compatible with")
                except Exception:
                    pass

            # Operation
            elif fname.endswith(".op") or fname.endswith(".operation"):
                try:
                    parsed = parse_operation(content)
                    for op in parsed.get("operations", []):
                        op_name = op.get("name")
                        if op_name:
                            op_id = f"p_op_{op_name}"
                            op_nodes.append(op_id)
                            add_node(
                                op_id,
                                op_name,
                                "operation",
                                "Driver Operation",
                                {
                                    "inputs": op.get("input_parameters", []),
                                    "output": op.get("output_parameter", {}),
                                    "script": op.get("executable_script", "")
                                },
                                source_file=fname
                            )
                            add_edge(proj_node_id, op_id, "HAS_OPERATION", "has operation")
                except Exception:
                    pass

            # Activity Diagram
            elif fname.endswith(".activity") or fname.endswith(".json"):
                try:
                    parsed = parse_activity(content) if fname.endswith(".activity") else None
                    if not parsed and content.strip().startswith("{"):
                        import json
                        parsed = json.loads(content)
                    
                    if parsed:
                        diag_name = parsed.get("name", "ProcessWorkflow")
                        diag_id = f"p_diag_{diag_name}"
                        add_node(
                            diag_id,
                            diag_name,
                            "workflow",
                            "Supervisory Workflow",
                            {"activity_count": len(parsed.get("activities", []))},
                            source_file=fname
                        )
                        add_edge(proj_node_id, diag_id, "ORCHESTRATES", "orchestrates")

                        activities = parsed.get("activities", [])
                        prev_act_id = None
                        for act in activities:
                            act_name = act.get("name")
                            if act_name:
                                act_id = f"p_act_{act_name}"
                                act_nodes.append(act_id)
                                req_cap = act.get("required_capability") or act.get("require_capability") or act.get("requireCapability")
                                req_op = act.get("required_operation") or act.get("require_operation") or act.get("requireOperation")

                                add_node(
                                    act_id,
                                    act_name,
                                    "activity",
                                    "Process Activity",
                                    {
                                        "description": act.get("description", ""),
                                        "required_capability": req_cap,
                                        "required_operation": req_op,
                                        "conditions": act.get("conditions", [])
                                    },
                                    source_file=fname
                                )
                                add_edge(diag_id, act_id, "CONTAINS_STEP", "contains step")

                                # Sequence edge
                                if prev_act_id:
                                    add_edge(prev_act_id, act_id, "NEXT_STEP", "next step")
                                prev_act_id = act_id

                                # Link activity to capability if declared
                                if req_cap:
                                    # check if in project
                                    for cn in cap_nodes:
                                        if req_cap.lower() in cn.lower():
                                            add_edge(act_id, cn, "REQUIRES_CAPABILITY", "requires capability")
                                
                                # Link activity to operation if declared
                                if req_op:
                                    op_str = str(req_op)
                                    for on in op_nodes:
                                        if op_str.lower() in on.lower():
                                            add_edge(act_id, on, "REQUIRES_OPERATION", "requires operation")
                except Exception:
                    pass

        # 2. Process Synthesized MNC Supervisory Automata
        if synthesized_model:
            model_name = synthesized_model.get("name", "SynthesizedSupervisor")
            sm_id = f"p_sm_{model_name}"
            add_node(
                sm_id,
                f"{model_name} State Machine",
                "supervisor",
                "Synthesized Supervisor",
                {"formal_grammar": "MNC-ML Chapter 5"}
            )
            add_edge(proj_node_id, sm_id, "SYNTHESIZED_SUPERVISOR", "synthesized supervisor")

            # Extract operating states
            systems = synthesized_model.get("systems", [])
            iface = systems[0] if systems else synthesized_model.get("interface_description", {})
            ctrl = systems[1] if len(systems) > 1 else synthesized_model.get("control_node", {})
            
            raw_states = iface.get("operating_states", []) or iface.get("operatingStates", [])
            if isinstance(raw_states, dict):
                raw_states = raw_states.get("operatingStates", [])
            
            for st in raw_states:
                sname = st.get("name") if isinstance(st, dict) else str(st)
                if sname:
                    st_id = f"p_state_{sname}"
                    state_nodes.append(st_id)
                    is_start = sname in ["INITIALIZED", "INIT"]
                    is_ready = sname in ["READY", "STANDBY", "NORMAL"]
                    is_alarm = sname in ["EMERGENCY", "ABORTED", "FAULT"]
                    role = "Start" if is_start else ("Operational" if is_ready else ("Alarm" if is_alarm else "Activity State"))

                    add_node(
                        st_id,
                        sname,
                        "operating_state",
                        "Automata State",
                        {"role": role, "is_start": is_start, "is_alarm": is_alarm}
                    )
                    add_edge(sm_id, st_id, "HAS_STATE", "has state")

            # State transitions
            transitions = ctrl.get("transitions", []) if isinstance(ctrl, dict) else []
            for tr in transitions:
                if isinstance(tr, dict):
                    src = tr.get("current_state") or tr.get("currentState")
                    tgt = tr.get("next_state") or tr.get("nextState")
                    if src and tgt:
                        src_id = f"p_state_{src}"
                        tgt_id = f"p_state_{tgt}"
                        add_edge(src_id, tgt_id, "TRANSITIONS_TO", "transitions to", {
                            "trigger": tr.get("trigger", ""),
                            "condition": tr.get("condition", "")
                        })

        return {
            "scope": "project",
            "project_id": project_id,
            "project_name": project_name,
            "nodes": nodes,
            "edges": edges,
            "stats": {
                "total_nodes": len(nodes),
                "total_edges": len(edges),
                "capabilities": len(cap_nodes),
                "operations": len(op_nodes),
                "activities": len(act_nodes),
                "states": len(state_nodes)
            }
        }

    @classmethod
    def match_capabilities(cls, query: str) -> List[Dict[str, Any]]:
        """
        Semantic Capability Recommendation:
        Matches an activity, natural language task, or device requirement against
        the Knowledge Repository graph.
        """
        q = query.lower().strip()
        matches: List[Dict[str, Any]] = []

        for item in EQUIPMENT_CATALOG:
            item_text = f"{item['name']} {item['description']} {' '.join(item.get('tags', []))} {' '.join(item.get('devices', []))}".lower()
            
            score = 0
            keywords = q.split()
            for kw in keywords:
                if len(kw) > 2 and kw in item_text:
                    score += 25
            
            if score > 0 or not q:
                # Find matching capabilities
                caps_list = []
                for f in item.get("files", []):
                    if f.get("file_type") == "capability" or f.get("filename", "").endswith(".cap"):
                        try:
                            parsed = parse_capability(f.get("content", ""))
                            cname = parsed.get("name")
                            if cname:
                                caps_list.append({
                                    "name": cname,
                                    "interface": parsed.get("component_interface", ""),
                                    "commands": parsed.get("control_capabilities", {}).get("commands", []),
                                    "events": parsed.get("control_capabilities", {}).get("events", [])
                                })
                        except Exception:
                            pass

                matches.append({
                    "catalog_id": item["id"],
                    "system_name": item["name"],
                    "category": item.get("category", "Automation"),
                    "thesis_reference": item.get("thesis_reference", ""),
                    "score": min(100, score + 20),
                    "devices": item.get("devices", []),
                    "capabilities": caps_list
                })

        matches.sort(key=lambda m: m["score"], reverse=True)
        return matches

    @classmethod
    def export_graph_to_rdf_turtle(cls, graph: Dict[str, Any]) -> str:
        """
        Serializes the Knowledge Graph into W3C RDF Turtle ontology syntax.
        Conforms to Thesis semantic web specification.
        """
        lines = [
            "@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .",
            "@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> .",
            "@prefix xsd: <http://www.w3.org/2001/XMLSchema#> .",
            "@prefix kide: <http://kide.thesis/ontology#> .",
            "",
            f"# KIDE Thesis Knowledge Graph Export ({graph.get('scope', 'system')})",
            f"# Total Entities: {len(graph.get('nodes', []))}, Total Predicates: {len(graph.get('edges', []))}",
            ""
        ]

        # Entity declarations
        for n in graph.get("nodes", []):
            nid = re.sub(r'[^a-zA-Z0-9_]', '_', n["id"])
            ntype = n.get("type", "Entity").capitalize()
            nname = n.get("name", "Unnamed").replace('"', '\\"')
            ncat = n.get("category", "General").replace('"', '\\"')
            
            lines.append(f"kide:{nid} rdf:type kide:{ntype} ;")
            lines.append(f'    rdfs:label "{nname}" ;')
            lines.append(f'    kide:hasCategory "{ncat}" .')
            lines.append("")

        # Relationship assertions
        lines.append("# Semantic Predicates & Cross-Links")
        for e in graph.get("edges", []):
            sid = re.sub(r'[^a-zA-Z0-9_]', '_', e["source"])
            tid = re.sub(r'[^a-zA-Z0-9_]', '_', e["target"])
            etype = e.get("type", "relatedTo")
            lines.append(f"kide:{sid} kide:{etype} kide:{tid} .")

        return "\n".join(lines)

