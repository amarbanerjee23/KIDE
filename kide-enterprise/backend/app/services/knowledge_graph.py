"""
Knowledge Graph Service for KIDE Enterprise.
Implements the thesis Knowledge Repository ontology and Property Knowledge Graph.

Structures:
- Domain / Case Study Categories (Access Control, Robotics, Process, Chemical, Smart Building, Automotive, Water)
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
from sqlalchemy.ext.asyncio import AsyncSession

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

                # 1. DML DataModels
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
                                        "parent_domain": item["name"],
                                        "primitives": dm.get("primitives", []),
                                        "composites": dm.get("composites", [])
                                    },
                                    thesis_ref=thesis_ref
                                )
                                add_edge(cat_id, dm_id, "DEFINES_DATA_MODEL", "defines schema")

                                # Fine-grained thesis ontology: Primitive Parameters / Fields
                                for prim in dm.get("primitives", []):
                                    pname = prim.get("name") if isinstance(prim, dict) else str(prim)
                                    ptype = prim.get("type") if isinstance(prim, dict) else "string"
                                    if pname:
                                        param_id = f"param_{item['id']}_{dm_name}_{pname}"
                                        add_node(
                                            param_id,
                                            pname,
                                            "parameter",
                                            "Data Primitive / Field",
                                            {"data_type": ptype, "parent_model": dm_name, "parent_domain": item["name"]},
                                            thesis_ref=thesis_ref
                                        )
                                        add_edge(dm_id, param_id, "HAS_FIELD", "has field")
                    except Exception:
                        pass

                # 2. Capabilities
                elif ftype == "capability" or fname.endswith(".cap"):
                    try:
                        parsed = parse_capability(content)
                        cap_name = parsed.get("name")
                        if cap_name:
                            cap_id = f"cap_{item['id']}_{cap_name}"
                            cc = parsed.get("provides_control_capabilities") or parsed.get("providesControlCapabilities") or parsed.get("control_capabilities") or {}
                            ifaces = parsed.get("compatible_component_interfaces") or parsed.get("componentInterface") or []
                            if isinstance(ifaces, list) and ifaces:
                                iface_name = ifaces[0]
                            else:
                                iface_name = parsed.get("component_interface") or (str(ifaces) if ifaces else "")

                            init_block = parsed.get("required_init_process") or parsed.get("requiredINITProcess") or {}
                            init_cmds = [c.get("command") for c in init_block.get("fireCommands", []) if isinstance(c, dict) and c.get("command")]
                            all_cmds = list(dict.fromkeys(list(cc.get("commands", [])) + init_cmds))
                            init_evts = [e.get("event") for e in init_block.get("publishEvents", []) if isinstance(e, dict) and e.get("event")]
                            all_evts = list(dict.fromkeys(list(cc.get("events", [])) + init_evts))
                            init_alms = [a.get("alarm") for a in init_block.get("raiseAlarms", []) if isinstance(a, dict) and a.get("alarm")]
                            all_alms = list(dict.fromkeys(list(cc.get("alarms", [])) + init_alms))
                            cc_dps = cc.get("dataPoints", []) or cc.get("data_points", [])
                            init_dps = [d.get("dataPoint") for d in init_block.get("triggerDataPoints", []) if isinstance(d, dict) and d.get("dataPoint")]
                            all_dps = list(dict.fromkeys(list(cc_dps) + init_dps))
                            
                            add_node(
                                cap_id,
                                cap_name,
                                "capability",
                                "Semantic Capability",
                                {
                                    "parent_domain": item["name"],
                                    "component_interface": iface_name,
                                    "commands": all_cmds,
                                    "events": all_evts,
                                    "alarms": all_alms,
                                    "data_points": all_dps
                                },
                                thesis_ref=thesis_ref
                            )
                            add_edge(cat_id, cap_id, "PROVIDES_CAPABILITY", "provides capability")

                            # Add Interface Description Node
                            iface_id = None
                            if iface_name:
                                iface_id = f"iface_{item['id']}_{iface_name}"
                                add_node(
                                    iface_id,
                                    iface_name,
                                    "interface",
                                    "Component Interface",
                                    {"binding_capability": cap_name, "parent_domain": item["name"]},
                                    thesis_ref=thesis_ref
                                )
                                add_edge(cap_id, iface_id, "COMPATIBLE_WITH", "compatible with")

                            # Link capabilities to device nodes
                            for dev_name in item.get("devices", []):
                                dev_id = f"dev_{item['id']}_{re.sub(r'[^a-zA-Z0-9_]', '_', dev_name.lower())}"
                                add_edge(dev_id, cap_id, "IMPLEMENTS_CAPABILITY", "implements capability")

                            # Fine-grained thesis ontology entities: Commands
                            for cmd in all_cmds:
                                cname = cmd.get("name") if isinstance(cmd, dict) else str(cmd)
                                if cname:
                                    cmd_id = f"cmd_{item['id']}_{cap_name}_{cname}"
                                    add_node(
                                        cmd_id,
                                        cname,
                                        "command",
                                        "Fireable Command",
                                        {"parent_capability": cap_name, "parent_domain": item["name"], "parent_interface": iface_name},
                                        thesis_ref=thesis_ref
                                    )
                                    add_edge(cap_id, cmd_id, "OFFERS_COMMAND", "offers command")
                                    if iface_id:
                                        add_edge(iface_id, cmd_id, "OFFERS_COMMAND", "offers command")

                            # Fine-grained thesis ontology entities: Events
                            for evt in all_evts:
                                ename = evt.get("name") if isinstance(evt, dict) else str(evt)
                                if ename:
                                    evt_id = f"evt_{item['id']}_{cap_name}_{ename}"
                                    add_node(
                                        evt_id,
                                        ename,
                                        "event",
                                        "Receivable Event",
                                        {"parent_capability": cap_name, "parent_domain": item["name"], "parent_interface": iface_name},
                                        thesis_ref=thesis_ref
                                    )
                                    add_edge(cap_id, evt_id, "RECEIVES_EVENT", "receives event")
                                    if iface_id:
                                        add_edge(iface_id, evt_id, "RECEIVES_EVENT", "receives event")

                            # Fine-grained thesis ontology entities: Alarms
                            for alm in all_alms:
                                aname = alm.get("name") if isinstance(alm, dict) else str(alm)
                                if aname:
                                    alm_id = f"alm_{item['id']}_{cap_name}_{aname}"
                                    add_node(
                                        alm_id,
                                        aname,
                                        "alarm",
                                        "Raised Alarm",
                                        {"parent_capability": cap_name, "parent_domain": item["name"], "parent_interface": iface_name},
                                        thesis_ref=thesis_ref
                                    )
                                    add_edge(cap_id, alm_id, "RAISES_ALARM", "raises alarm")
                                    if iface_id:
                                        add_edge(iface_id, alm_id, "RAISES_ALARM", "raises alarm")

                            # Fine-grained thesis ontology entities: DataPoints
                            for dp in all_dps:
                                dpname = dp.get("name") if isinstance(dp, dict) else str(dp)
                                if dpname:
                                    dp_id = f"dp_{item['id']}_{cap_name}_{dpname}"
                                    add_node(
                                        dp_id,
                                        dpname,
                                        "datapoint",
                                        "Telemetry DataPoint",
                                        {"parent_capability": cap_name, "parent_domain": item["name"], "parent_interface": iface_name},
                                        thesis_ref=thesis_ref
                                    )
                                    add_edge(cap_id, dp_id, "PROVIDES_DATAPOINT", "provides datapoint")
                                    if iface_id:
                                        add_edge(iface_id, dp_id, "PROVIDES_DATAPOINT", "provides datapoint")
                    except Exception:
                        pass

                # 3. Operations
                elif ftype == "operation" or fname.endswith(".op"):
                    try:
                        parsed = parse_operation(content)
                        for op in parsed.get("operations", []):
                            op_name = op.get("name")
                            if op_name:
                                op_id = f"op_{item['id']}_{op_name}"
                                inps = op.get("input_parameters", [])
                                outp = op.get("output_parameter", {})
                                add_node(
                                    op_id,
                                    op_name,
                                    "operation",
                                    "Device Operation",
                                    {
                                        "parent_domain": item["name"],
                                        "inputs": inps,
                                        "output": outp,
                                        "script": op.get("executable_script", "")
                                    },
                                    thesis_ref=thesis_ref
                                )
                                add_edge(cat_id, op_id, "OFFERS_OPERATION", "offers operation")

                                # Extract Operation Parameters
                                for inp in inps:
                                    pname = inp.get("name") if isinstance(inp, dict) else str(inp)
                                    if pname:
                                        param_id = f"param_{item['id']}_{op_name}_{pname}"
                                        add_node(
                                            param_id,
                                            pname,
                                            "parameter",
                                            "Operation Parameter",
                                            {"parent_operation": op_name, "direction": "input", "parent_domain": item["name"]},
                                            thesis_ref=thesis_ref
                                        )
                                        add_edge(op_id, param_id, "HAS_PARAMETER", "has input parameter")
                                
                                if outp:
                                    pname = outp.get("name") if isinstance(outp, dict) else str(outp)
                                    if pname:
                                        param_id = f"param_{item['id']}_{op_name}_{pname}"
                                        add_node(
                                            param_id,
                                            pname,
                                            "parameter",
                                            "Operation Parameter",
                                            {"parent_operation": op_name, "direction": "output", "parent_domain": item["name"]},
                                            thesis_ref=thesis_ref
                                        )
                                        add_edge(op_id, param_id, "HAS_PARAMETER", "has output parameter")
                    except Exception:
                        pass

                # 4. Activity Diagrams & Workflows
                elif ftype == "activity" or fname.endswith(".activity"):
                    try:
                        parsed = parse_activity(content)
                        diag_name = parsed.get("name")
                        if diag_name:
                            diag_id = f"wf_{item['id']}_{diag_name}"
                            add_node(
                                diag_id,
                                diag_name,
                                "workflow",
                                "Supervisory Workflow",
                                {
                                    "parent_domain": item["name"],
                                    "activity_count": len(parsed.get("activities", []))
                                },
                                thesis_ref=thesis_ref
                            )
                            add_edge(cat_id, diag_id, "DEFINES_WORKFLOW", "defines workflow")

                            # Link context data model if specified
                            ctx_dm = parsed.get("context_data_model") or parsed.get("contextDataModel")
                            if ctx_dm:
                                for dm_node in [n for n in nodes if n["type"] == "datamodel"]:
                                    if str(ctx_dm).lower() in dm_node["name"].lower():
                                        add_edge(diag_id, dm_node["id"], "ON_CONTEXT", "on context")

                            prev_act_id = None
                            for act in parsed.get("activities", []):
                                act_name = act.get("name")
                                if act_name:
                                    act_id = f"act_{item['id']}_{act_name}"
                                    req_cap = act.get("required_capability") or act.get("requireCapability")
                                    req_op = act.get("required_operation") or act.get("requireOperation")

                                    add_node(
                                        act_id,
                                        act_name,
                                        "activity",
                                        "Process Activity",
                                        {
                                            "parent_domain": item["name"],
                                            "parent_workflow": diag_name,
                                            "description": act.get("description", ""),
                                            "required_capability": req_cap,
                                            "required_operation": req_op,
                                            "conditions": act.get("conditions", [])
                                        },
                                        thesis_ref=thesis_ref
                                    )
                                    add_edge(diag_id, act_id, "CONTAINS_STEP", "contains step")

                                    if prev_act_id:
                                        add_edge(prev_act_id, act_id, "NEXT_STEP", "next step")
                                    prev_act_id = act_id

                                    # Cross-link to capability in catalog
                                    if req_cap:
                                        for cnode in [n for n in nodes if n["type"] == "capability"]:
                                            if req_cap.lower() in cnode["name"].lower():
                                                add_edge(act_id, cnode["id"], "REQUIRES_CAPABILITY", "requires capability")

                                    # Cross-link to operation in catalog
                                    if req_op:
                                        for onode in [n for n in nodes if n["type"] == "operation"]:
                                            if str(req_op).lower() in onode["name"].lower():
                                                add_edge(act_id, onode["id"], "REQUIRES_OPERATION", "requires operation")
                    except Exception:
                        pass

                # 5. MNC-ML Automata & Interfaces
                elif ftype == "mnc" or fname.endswith(".mnc"):
                    try:
                        parsed = parse_mnc(content)
                        mname = parsed.get("name", "SystemModel")
                        for sys_item in parsed.get("systems", []):
                            if isinstance(sys_item, dict) and ("commands" in sys_item or sys_item.get("kind") == "InterfaceDescription"):
                                iface_name = sys_item.get("name", f"{mname}Interface")
                                iface_id = f"iface_{item['id']}_{iface_name}"
                                add_node(
                                    iface_id,
                                    iface_name,
                                    "interface",
                                    "Component Interface",
                                    {"parent_domain": item["name"]},
                                    thesis_ref=thesis_ref
                                )
                                add_edge(cat_id, iface_id, "DEFINES_INTERFACE", "defines interface")

                                for cmd in sys_item.get("commands", []):
                                    cname = cmd.get("name") if isinstance(cmd, dict) else str(cmd)
                                    if cname:
                                        cmd_id = f"cmd_{item['id']}_{iface_name}_{cname}"
                                        add_node(cmd_id, cname, "command", "Interface Command", {"parent_interface": iface_name, "parent_domain": item["name"]}, thesis_ref=thesis_ref)
                                        add_edge(iface_id, cmd_id, "OFFERS_COMMAND", "offers command")

                                for evt in sys_item.get("events", []):
                                    ename = evt.get("name") if isinstance(evt, dict) else str(evt)
                                    if ename:
                                        evt_id = f"evt_{item['id']}_{iface_name}_{ename}"
                                        add_node(evt_id, ename, "event", "Interface Event", {"parent_interface": iface_name, "parent_domain": item["name"]}, thesis_ref=thesis_ref)
                                        add_edge(iface_id, evt_id, "RECEIVES_EVENT", "receives event")

                                for alm in sys_item.get("alarms", []):
                                    aname = alm.get("name") if isinstance(alm, dict) else str(alm)
                                    if aname:
                                        alm_id = f"alm_{item['id']}_{iface_name}_{aname}"
                                        add_node(alm_id, aname, "alarm", "Interface Alarm", {"parent_interface": iface_name, "parent_domain": item["name"]}, thesis_ref=thesis_ref)
                                        add_edge(iface_id, alm_id, "RAISES_ALARM", "raises alarm")

                                for dp in sys_item.get("data_points", []):
                                    dpname = dp.get("name") if isinstance(dp, dict) else str(dp)
                                    if dpname:
                                        dp_id = f"dp_{item['id']}_{iface_name}_{dpname}"
                                        add_node(dp_id, dpname, "datapoint", "Interface DataPoint", {"parent_interface": iface_name, "parent_domain": item["name"]}, thesis_ref=thesis_ref)
                                        add_edge(iface_id, dp_id, "PROVIDES_DATAPOINT", "provides datapoint")
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
                "devices": sum(1 for n in nodes if n["type"] == "device"),
                "interfaces": sum(1 for n in nodes if n["type"] == "interface"),
                "capabilities": sum(1 for n in nodes if n["type"] == "capability"),
                "operations": sum(1 for n in nodes if n["type"] == "operation"),
                "data_models": sum(1 for n in nodes if n["type"] == "datamodel"),
                "workflows": sum(1 for n in nodes if n["type"] == "workflow"),
                "activities": sum(1 for n in nodes if n["type"] == "activity"),
                "commands": sum(1 for n in nodes if n["type"] == "command"),
                "events": sum(1 for n in nodes if n["type"] == "event"),
                "alarms": sum(1 for n in nodes if n["type"] == "alarm"),
                "data_points": sum(1 for n in nodes if n["type"] == "datapoint"),
                "parameters": sum(1 for n in nodes if n["type"] == "parameter")
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

                            # Fine-grained Parameters
                            for prim in dm.get("primitives", []):
                                pname = prim.get("name") if isinstance(prim, dict) else str(prim)
                                ptype = prim.get("type") if isinstance(prim, dict) else "string"
                                if pname:
                                    param_id = f"p_param_{dm_name}_{pname}"
                                    add_node(param_id, pname, "parameter", "Data Primitive / Field", {"data_type": ptype, "parent_model": dm_name}, source_file=fname)
                                    add_edge(dm_id, param_id, "HAS_FIELD", "has field")
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
                        cc = parsed.get("provides_control_capabilities") or parsed.get("providesControlCapabilities") or parsed.get("control_capabilities") or {}
                        ifaces = parsed.get("compatible_component_interfaces") or parsed.get("componentInterface") or []
                        if isinstance(ifaces, list) and ifaces:
                            iface_name = ifaces[0]
                        else:
                            iface_name = parsed.get("component_interface") or (str(ifaces) if ifaces else "")

                        init_block = parsed.get("required_init_process") or parsed.get("requiredINITProcess") or {}
                        init_cmds = [c.get("command") for c in init_block.get("fireCommands", []) if isinstance(c, dict) and c.get("command")]
                        all_cmds = list(dict.fromkeys(list(cc.get("commands", [])) + init_cmds))
                        init_evts = [e.get("event") for e in init_block.get("publishEvents", []) if isinstance(e, dict) and e.get("event")]
                        all_evts = list(dict.fromkeys(list(cc.get("events", [])) + init_evts))
                        init_alms = [a.get("alarm") for a in init_block.get("raiseAlarms", []) if isinstance(a, dict) and a.get("alarm")]
                        all_alms = list(dict.fromkeys(list(cc.get("alarms", [])) + init_alms))
                        cc_dps = cc.get("dataPoints", []) or cc.get("data_points", [])
                        init_dps = [d.get("dataPoint") for d in init_block.get("triggerDataPoints", []) if isinstance(d, dict) and d.get("dataPoint")]
                        all_dps = list(dict.fromkeys(list(cc_dps) + init_dps))

                        add_node(
                            cap_id,
                            cap_name,
                            "capability",
                            "Device Capability",
                            {
                                "interface": iface_name,
                                "commands": all_cmds,
                                "events": all_evts,
                                "alarms": all_alms,
                                "data_points": all_dps
                            },
                            source_file=fname
                        )
                        add_edge(proj_node_id, cap_id, "HAS_CAPABILITY", "has capability")

                        # Interface node
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

                        # Fine-grained Commands
                        for cmd in all_cmds:
                            cname = cmd.get("name") if isinstance(cmd, dict) else str(cmd)
                            if cname:
                                cmd_id = f"p_cmd_{cap_name}_{cname}"
                                add_node(cmd_id, cname, "command", "Fireable Command", {"parent_capability": cap_name}, source_file=fname)
                                add_edge(cap_id, cmd_id, "OFFERS_COMMAND", "offers command")
                                if iface_name:
                                    add_edge(f"p_iface_{iface_name}", cmd_id, "OFFERS_COMMAND", "offers command")

                        # Fine-grained Events
                        for evt in all_evts:
                            ename = evt.get("name") if isinstance(evt, dict) else str(evt)
                            if ename:
                                evt_id = f"p_evt_{cap_name}_{ename}"
                                add_node(evt_id, ename, "event", "Receivable Event", {"parent_capability": cap_name}, source_file=fname)
                                add_edge(cap_id, evt_id, "RECEIVES_EVENT", "receives event")
                                if iface_name:
                                    add_edge(f"p_iface_{iface_name}", evt_id, "RECEIVES_EVENT", "receives event")

                        # Fine-grained Alarms
                        for alm in all_alms:
                            aname = alm.get("name") if isinstance(alm, dict) else str(alm)
                            if aname:
                                alm_id = f"p_alm_{cap_name}_{aname}"
                                add_node(alm_id, aname, "alarm", "Raised Alarm", {"parent_capability": cap_name}, source_file=fname)
                                add_edge(cap_id, alm_id, "RAISES_ALARM", "raises alarm")
                                if iface_name:
                                    add_edge(f"p_iface_{iface_name}", alm_id, "RAISES_ALARM", "raises alarm")

                        # Fine-grained DataPoints
                        for dp in all_dps:
                            dpname = dp.get("name") if isinstance(dp, dict) else str(dp)
                            if dpname:
                                dp_id = f"p_dp_{cap_name}_{dpname}"
                                add_node(dp_id, dpname, "datapoint", "Telemetry DataPoint", {"parent_capability": cap_name}, source_file=fname)
                                add_edge(cap_id, dp_id, "PROVIDES_DATAPOINT", "provides datapoint")
                                if iface_name:
                                    add_edge(f"p_iface_{iface_name}", dp_id, "PROVIDES_DATAPOINT", "provides datapoint")
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
                            inps = op.get("input_parameters", [])
                            outp = op.get("output_parameter", {})
                            add_node(
                                op_id,
                                op_name,
                                "operation",
                                "Driver Operation",
                                {
                                    "inputs": inps,
                                    "output": outp,
                                    "script": op.get("executable_script", "")
                                },
                                source_file=fname
                            )
                            add_edge(proj_node_id, op_id, "HAS_OPERATION", "has operation")

                            for inp in inps:
                                pname = inp.get("name") if isinstance(inp, dict) else str(inp)
                                if pname:
                                    param_id = f"p_param_{op_name}_{pname}"
                                    add_node(param_id, pname, "parameter", "Operation Parameter", {"parent_operation": op_name, "direction": "input"}, source_file=fname)
                                    add_edge(op_id, param_id, "HAS_PARAMETER", "has input parameter")
                            if outp:
                                pname = outp.get("name") if isinstance(outp, dict) else str(outp)
                                if pname:
                                    param_id = f"p_param_{op_name}_{pname}"
                                    add_node(param_id, pname, "parameter", "Operation Parameter", {"parent_operation": op_name, "direction": "output"}, source_file=fname)
                                    add_edge(op_id, param_id, "HAS_PARAMETER", "has output parameter")
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

                        # Link context data model if specified
                        ctx_dm = parsed.get("context_data_model") or parsed.get("contextDataModel")
                        if ctx_dm:
                            for dm_node in [n for n in nodes if n["type"] == "datamodel"]:
                                if str(ctx_dm).lower() in dm_node["name"].lower():
                                    add_edge(diag_id, dm_node["id"], "ON_CONTEXT", "on context")

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

            # MNC-ML File
            elif fname.endswith(".mnc"):
                try:
                    parsed = parse_mnc(content)
                    mname = parsed.get("name", "ProjectMnc")
                    for sys_item in parsed.get("systems", []):
                        if isinstance(sys_item, dict) and ("commands" in sys_item or sys_item.get("kind") == "InterfaceDescription"):
                            iface_name = sys_item.get("name", f"{mname}Interface")
                            iface_id = f"p_iface_{iface_name}"
                            add_node(iface_id, iface_name, "interface", "Hardware Interface", source_file=fname)
                            add_edge(proj_node_id, iface_id, "DEFINES_INTERFACE", "defines interface")

                            for cmd in sys_item.get("commands", []):
                                cname = cmd.get("name") if isinstance(cmd, dict) else str(cmd)
                                if cname:
                                    cmd_id = f"p_cmd_{iface_name}_{cname}"
                                    add_node(cmd_id, cname, "command", "Interface Command", source_file=fname)
                                    add_edge(iface_id, cmd_id, "OFFERS_COMMAND", "offers command")

                            for evt in sys_item.get("events", []):
                                ename = evt.get("name") if isinstance(evt, dict) else str(evt)
                                if ename:
                                    evt_id = f"p_evt_{iface_name}_{ename}"
                                    add_node(evt_id, ename, "event", "Interface Event", source_file=fname)
                                    add_edge(iface_id, evt_id, "RECEIVES_EVENT", "receives event")

                            for alm in sys_item.get("alarms", []):
                                aname = alm.get("name") if isinstance(alm, dict) else str(alm)
                                if aname:
                                    alm_id = f"p_alm_{iface_name}_{aname}"
                                    add_node(alm_id, aname, "alarm", "Interface Alarm", source_file=fname)
                                    add_edge(iface_id, alm_id, "RAISES_ALARM", "raises alarm")

                            for dp in sys_item.get("data_points", []):
                                dpname = dp.get("name") if isinstance(dp, dict) else str(dp)
                                if dpname:
                                    dp_id = f"p_dp_{iface_name}_{dpname}"
                                    add_node(dp_id, dpname, "datapoint", "Interface DataPoint", source_file=fname)
                                    add_edge(iface_id, dp_id, "PROVIDES_DATAPOINT", "provides datapoint")
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
                "data_models": sum(1 for n in nodes if n["type"] == "datamodel"),
                "capabilities": sum(1 for n in nodes if n["type"] == "capability"),
                "operations": sum(1 for n in nodes if n["type"] == "operation"),
                "workflows": sum(1 for n in nodes if n["type"] == "workflow"),
                "activities": sum(1 for n in nodes if n["type"] == "activity"),
                "interfaces": sum(1 for n in nodes if n["type"] == "interface"),
                "commands": sum(1 for n in nodes if n["type"] == "command"),
                "events": sum(1 for n in nodes if n["type"] == "event"),
                "alarms": sum(1 for n in nodes if n["type"] == "alarm"),
                "data_points": sum(1 for n in nodes if n["type"] == "datapoint"),
                "parameters": sum(1 for n in nodes if n["type"] == "parameter"),
                "states": sum(1 for n in nodes if n["type"] == "operating_state")
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
            "@prefix owl: <http://www.w3.org/2002/07/owl#> .",
            "@prefix xsd: <http://www.w3.org/2001/XMLSchema#> .",
            "@prefix kide: <http://kide.thesis/ontology#> .",
            "",
            "kide:Ontology rdf:type owl:Ontology ;",
            '    rdfs:label "KIDE Supervisory Control Synthesis Metamodel Ontology" ;',
            '    rdfs:comment "Formal Web Ontology Language (OWL) definition for model-driven engineering of industrial supervisory systems as described in the PhD thesis." .',
            "",
            "# OWL Class Definitions as per Thesis Metamodel",
            "kide:Domain rdf:type owl:Class ; rdfs:label \"Domain\" .",
            "kide:Device rdf:type owl:Class ; rdfs:label \"Device\" .",
            "kide:Interface rdf:type owl:Class ; rdfs:label \"Interface\" .",
            "kide:Capability rdf:type owl:Class ; rdfs:label \"Capability\" .",
            "kide:Operation rdf:type owl:Class ; rdfs:label \"Operation\" .",
            "kide:DataModel rdf:type owl:Class ; rdfs:label \"DataModel\" .",
            "kide:Parameter rdf:type owl:Class ; rdfs:label \"Parameter\" .",
            "kide:Workflow rdf:type owl:Class ; rdfs:label \"Workflow\" .",
            "kide:Activity rdf:type owl:Class ; rdfs:label \"Activity\" .",
            "kide:Command rdf:type owl:Class ; rdfs:label \"Command\" .",
            "kide:Event rdf:type owl:Class ; rdfs:label \"Event\" .",
            "kide:Alarm rdf:type owl:Class ; rdfs:label \"Alarm\" .",
            "kide:DataPoint rdf:type owl:Class ; rdfs:label \"DataPoint\" .",
            "kide:OperatingState rdf:type owl:Class ; rdfs:label \"OperatingState\" .",
            "kide:Supervisor rdf:type owl:Class ; rdfs:label \"Supervisor\" .",
            "",
            "# OWL Object Properties as per Thesis Semantics",
            "kide:containsDomain rdf:type owl:ObjectProperty .",
            "kide:includesEquipment rdf:type owl:ObjectProperty .",
            "kide:definesDataModel rdf:type owl:ObjectProperty .",
            "kide:hasField rdf:type owl:ObjectProperty .",
            "kide:providesCapability rdf:type owl:ObjectProperty .",
            "kide:compatibleWith rdf:type owl:ObjectProperty .",
            "kide:implementsCapability rdf:type owl:ObjectProperty .",
            "kide:offersCommand rdf:type owl:ObjectProperty .",
            "kide:receivesEvent rdf:type owl:ObjectProperty .",
            "kide:raisesAlarm rdf:type owl:ObjectProperty .",
            "kide:providesDataPoint rdf:type owl:ObjectProperty .",
            "kide:offersOperation rdf:type owl:ObjectProperty .",
            "kide:hasParameter rdf:type owl:ObjectProperty .",
            "kide:definesWorkflow rdf:type owl:ObjectProperty .",
            "kide:containsStep rdf:type owl:ObjectProperty .",
            "kide:nextStep rdf:type owl:ObjectProperty .",
            "kide:requiresCapability rdf:type owl:ObjectProperty .",
            "kide:requiresOperation rdf:type owl:ObjectProperty .",
            "kide:onContext rdf:type owl:ObjectProperty .",
            "kide:hasState rdf:type owl:ObjectProperty .",
            "kide:transitionsTo rdf:type owl:ObjectProperty .",
            "",
            f"# KIDE Thesis Knowledge Graph Export ({graph.get('scope', 'system')})",
            f"# Total Entities: {len(graph.get('nodes', []))}, Total Predicates: {len(graph.get('edges', []))}",
            ""
        ]

        def to_pascal(s: str) -> str:
            return "".join(part.capitalize() for part in re.split(r'[^a-zA-Z0-9]', s) if part)

        def to_camel(s: str) -> str:
            parts = [part for part in re.split(r'[^a-zA-Z0-9]', s) if part]
            if not parts:
                return "relatedTo"
            return parts[0].lower() + "".join(p.capitalize() for p in parts[1:])

        # Entity declarations
        for n in graph.get("nodes", []):
            nid = re.sub(r'[^a-zA-Z0-9_]', '_', n["id"])
            raw_type = n.get("type", "Entity")
            ntype = to_pascal(raw_type)
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
            raw_etype = e.get("type", "relatedTo")
            etype = to_camel(raw_etype)
            lines.append(f"kide:{sid} kide:{etype} kide:{tid} .")

        return "\n".join(lines)


class KnowledgeStoreService:
    """Dedicated Knowledge Graph Store and Ontology Query Service."""

    _cached_graph: Optional[Dict[str, Any]] = None

    @classmethod
    def get_graph(cls, force_refresh: bool = False) -> Dict[str, Any]:
        if cls._cached_graph is None or force_refresh:
            cls._cached_graph = KnowledgeGraphService.build_global_catalog_graph()
        return cls._cached_graph

    @classmethod
    def get_summary(cls) -> Dict[str, Any]:
        graph = cls.get_graph(force_refresh=True)
        stats = graph.get("stats", {})
        domains = []
        for cat in EQUIPMENT_CATALOG:
            domains.append({
                "id": cat["id"],
                "name": cat["name"],
                "category": cat["category"],
                "thesis_reference": cat.get("thesis_reference", ""),
                "description": cat.get("description", ""),
                "devices": cat.get("devices", []),
                "tags": cat.get("tags", []),
                "files_count": len(cat.get("files", []))
            })
        return {
            "stats": stats,
            "domains": domains
        }

    @classmethod
    def get_entities(
        cls,
        entity_type: Optional[str] = None,
        domain_id: Optional[str] = None,
        query: Optional[str] = None
    ) -> List[Dict[str, Any]]:
        graph = cls.get_graph()
        nodes = graph.get("nodes", [])
        results = []

        q = query.lower().strip() if query else None

        for n in nodes:
            if n["type"] in ("repository",):
                continue
            if entity_type and entity_type.lower() != "all" and n["type"].lower() != entity_type.lower():
                continue
            
            parent_dom = n.get("properties", {}).get("parent_domain", "")
            if domain_id:
                if domain_id not in n["id"] and domain_id.lower() not in parent_dom.lower():
                    continue

            if q:
                text = f"{n['name']} {n['type']} {n.get('category', '')} {parent_dom} {n.get('properties', '')}".lower()
                if q not in text:
                    continue

            # Find matching file content snippet
            snippet = None
            source_filename = None
            p_cap = n.get("properties", {}).get("parent_capability")
            p_model = n.get("properties", {}).get("parent_model")
            p_op = n.get("properties", {}).get("parent_operation")

            for cat in EQUIPMENT_CATALOG:
                for f in cat.get("files", []):
                    if n["name"] in f["content"]:
                        snippet = f["content"]
                        source_filename = f["filename"]
                        break
                    if (p_cap and p_cap in f["content"]) or (p_model and p_model in f["content"]) or (p_op and p_op in f["content"]):
                        snippet = f["content"]
                        source_filename = f["filename"]
                        break
                if snippet:
                    break

            results.append({
                "id": n["id"],
                "name": n["name"],
                "type": n["type"],
                "category": n.get("category", "General"),
                "thesis_reference": n.get("thesis_reference", ""),
                "properties": n.get("properties", {}),
                "source_file": source_filename,
                "dsl_snippet": snippet
            })

        return results

    @classmethod
    def get_entity_detail(cls, entity_id: str) -> Optional[Dict[str, Any]]:
        graph = cls.get_graph()
        nodes = graph.get("nodes", [])
        edges = graph.get("edges", [])

        node = next((n for n in nodes if n["id"] == entity_id), None)
        if not node:
            return None

        connected_edges = [e for e in edges if e["source"] == entity_id or e["target"] == entity_id]
        neighbor_ids = {e["target"] if e["source"] == entity_id else e["source"] for e in connected_edges}
        neighbors = [n for n in nodes if n["id"] in neighbor_ids]

        declaring_file = None
        catalog_item = None
        p_cap = node.get("properties", {}).get("parent_capability")
        p_model = node.get("properties", {}).get("parent_model")
        p_op = node.get("properties", {}).get("parent_operation")

        for cat in EQUIPMENT_CATALOG:
            for f in cat.get("files", []):
                if node["name"] in f["content"]:
                    declaring_file = f
                    catalog_item = cat
                    break
                if (p_cap and p_cap in f["content"]) or (p_model and p_model in f["content"]) or (p_op and p_op in f["content"]):
                    declaring_file = f
                    catalog_item = cat
                    break
            if declaring_file:
                break

        return {
            "entity": node,
            "connected_edges": connected_edges,
            "neighbors": neighbors,
            "declaring_file": declaring_file,
            "catalog_item": {
                "id": catalog_item["id"],
                "name": catalog_item["name"],
                "category": catalog_item["category"]
            } if catalog_item else None
        }

    @classmethod
    async def import_entity_into_project(
        cls,
        db: AsyncSession,
        project_id: int,
        entity_id: str
    ) -> Dict[str, Any]:
        """Imports the specific DSL artifact declaring the requested entity into the target project."""
        detail = cls.get_entity_detail(entity_id)
        if not detail or not detail.get("declaring_file"):
            raise ValueError(f"No declaring DSL specification found for entity: {entity_id}")

        f = detail["declaring_file"]
        imported = await KnowledgeHubService.import_single_file_to_project(
            db=db,
            project_id=project_id,
            filename=f["filename"],
            file_type=f["file_type"],
            content=f["content"]
        )
        return {
            "message": f"Successfully imported {f['filename']} into project",
            "imported_file": imported,
            "entity_name": detail["entity"]["name"],
            "entity_type": detail["entity"]["type"]
        }
