import json
import re
from typing import Any, Dict, List, Optional, Tuple, Union
from ..schemas.engineering_ir import (
    SymbolType, DiagnosticSeverity, Diagnostic, SymbolRecord, CrossReference,
    ProjectIRSummary, ProjectIR
)
from ..parsers.dml_parser import parse as parse_dml
from ..parsers.capability_parser import parse as parse_capability
from ..parsers.operation_parser import parse as parse_operation
from ..parsers.activity_parser import parse as parse_activity
from ..parsers.mnc_parser import parse as parse_mnc

def normalize_files(files_input: Union[List[Dict[str, Any]], Dict[str, Any]]) -> List[Dict[str, Any]]:
    """Normalizes input files to a standard list of {id, name, content, language} dictionaries."""
    normalized = []
    if isinstance(files_input, list):
        for item in files_input:
            if isinstance(item, dict):
                fid = str(item.get("id") or item.get("file_id") or item.get("name") or "")
                fname = str(item.get("name") or item.get("filename") or item.get("id") or "unnamed")
                content = str(item.get("content", ""))
                lang = str(item.get("language") or "")
                normalized.append({
                    "id": fid,
                    "name": fname,
                    "content": content,
                    "language": lang
                })
    elif isinstance(files_input, dict):
        for fname, content in files_input.items():
            content_str = content if isinstance(content, str) else json.dumps(content)
            normalized.append({
                "id": fname,
                "name": fname,
                "content": content_str,
                "language": ""
            })
    return normalized

def _find_line_number(text: str, keyword: str) -> int:
    """Finds the 1-based line number of a keyword or symbol in source text."""
    if not text or not keyword:
        return 1
    lines = text.splitlines()
    pattern = re.compile(rf"\b{re.escape(keyword)}\b")
    for idx, line in enumerate(lines, start=1):
        if pattern.search(line):
            return idx
    for idx, line in enumerate(lines, start=1):
        if keyword in line:
            return idx
    return 1

class EngineeringIRCompiler:
    """
    Compiles raw project DSL source files into the Canonical Engineering IR.
    Builds a unified Symbol Table, extracts Cross-References, and indexes
    Data Models, Capabilities, Operations, Activities, and MNC State Machines.
    """

    def __init__(self, project_id: Optional[str] = None, project_name: str = "Project"):
        self.project_id = project_id
        self.project_name = project_name
        self.symbols: Dict[str, SymbolRecord] = {}
        self.data_models: Dict[str, Any] = {}
        self.capabilities: Dict[str, Any] = {}
        self.operations: Dict[str, Any] = {}
        self.activity_diagrams: Dict[str, Any] = {}
        self.mnc_models: Dict[str, Any] = {}
        self.cross_references: List[CrossReference] = []
        self.diagnostics: List[Diagnostic] = []

    def _register_symbol(
        self,
        name: str,
        fqn: str,
        symbol_type: SymbolType,
        file_id: Optional[str] = None,
        filename: str = "",
        line: int = 1,
        column: int = 1,
        signature: Optional[str] = None,
        properties: Optional[Dict[str, Any]] = None,
        dependencies: Optional[List[str]] = None
    ) -> SymbolRecord:
        record = SymbolRecord(
            name=name,
            fqn=fqn,
            symbol_type=symbol_type,
            file_id=file_id,
            filename=filename,
            line=line,
            column=column,
            signature=signature,
            properties=properties or {},
            dependencies=dependencies or []
        )
        self.symbols[fqn] = record
        # Also register by simple name if not already registered (or if more specific)
        if name not in self.symbols:
            self.symbols[name] = record
        return record

    def _add_cross_ref(
        self,
        source: str,
        target: str,
        kind: str,
        file_id: Optional[str] = None,
        filename: str = "",
        line: Optional[int] = None
    ):
        self.cross_references.append(
            CrossReference(
                source_symbol=source,
                target_symbol=target,
                reference_kind=kind,
                file_id=file_id,
                filename=filename,
                line=line
            )
        )

    def compile(self, files_input: Union[List[Dict[str, Any]], Dict[str, Any]]) -> ProjectIR:
        files = normalize_files(files_input)

        # 1. Parse each file into its domain AST
        for f in files:
            fid = f["id"]
            fname = f["name"]
            content = f["content"]

            try:
                if fname.endswith(".dml") or fname.endswith(".datamodel"):
                    parsed = parse_dml(content)
                    self._index_dml(parsed, fid, fname, content)
                elif fname.endswith(".capability") or fname.endswith(".cap"):
                    parsed = parse_capability(content)
                    self._index_capability(parsed, fid, fname, content)
                elif fname.endswith(".operation") or fname.endswith(".op"):
                    parsed = parse_operation(content)
                    self._index_operation(parsed, fid, fname, content)
                elif fname.endswith(".activity"):
                    parsed = parse_activity(content)
                    self._index_activity(parsed, fid, fname, content)
                elif fname.endswith(".mnc") or fname.endswith(".mncml"):
                    parsed = parse_mnc(content)
                    self._index_mnc(parsed, fid, fname, content)
                elif fname.endswith(".json"):
                    try:
                        data = json.loads(content)
                        if "activities" in data:
                            self._index_activity(data, fid, fname, content)
                        elif "provides_control_capabilities" in data or "compatible_component_interfaces" in data:
                            self._index_capability(data, fid, fname, content)
                        elif "operations" in data or "executable_script" in data:
                            self._index_operation(data, fid, fname, content)
                        elif "systems" in data or "interfaceDescription" in data or "interface_description" in data:
                            self._index_mnc(data, fid, fname, content)
                        elif "dataModelCollections" in data or "dataModels" in data:
                            self._index_dml(data, fid, fname, content)
                    except Exception:
                        pass
            except Exception as e:
                # Syntax / parse failure recorded as diagnostic
                line_no = getattr(e, "line", None) or 1
                col_no = getattr(e, "column", None) or 1
                self.diagnostics.append(
                    Diagnostic(
                        file_id=fid,
                        fileId=fid,
                        filename=fname,
                        line=line_no,
                        column=col_no,
                        message=f"Syntax error parsing {fname}: {str(e)}",
                        severity=DiagnosticSeverity.ERROR,
                        rule_id="SYN-001",
                        ruleId="SYN-001",
                        suggestion=f"Check syntax around line {line_no} in {fname}"
                    )
                )

        # Calculate metrics
        total_symbols = len(self.symbols)
        total_interfaces = sum(len(m.get("systems", [])) or 1 for m in self.mnc_models.values())
        total_states = 0
        total_transitions = 0
        for m in self.mnc_models.values():
            sys_list = m.get("systems", [])
            if not sys_list and (m.get("interface_description") or m.get("interfaceDescription")):
                sys_list = [m.get("interface_description") or m.get("interfaceDescription")]
            for s in sys_list:
                if isinstance(s, dict):
                    total_states += len(s.get("operating_states", []))
                    total_transitions += len(s.get("transitions", []))
        total_activities = sum(len(d.get("activities", [])) for d in self.activity_diagrams.values())

        summary = ProjectIRSummary(
            total_files=len(files),
            total_symbols=total_symbols,
            total_interfaces=total_interfaces,
            total_states=total_states,
            total_transitions=total_transitions,
            total_activities=total_activities,
            total_cross_references=len(self.cross_references),
            error_count=len([d for d in self.diagnostics if d.severity == DiagnosticSeverity.ERROR]),
            warning_count=len([d for d in self.diagnostics if d.severity == DiagnosticSeverity.WARNING]),
            health_score=max(0.0, 100.0 - (len(self.diagnostics) * 10.0))
        )

        return ProjectIR(
            project_id=self.project_id,
            project_name=self.project_name,
            symbols=self.symbols,
            data_models=self.data_models,
            capabilities=self.capabilities,
            operations=self.operations,
            activity_diagrams=self.activity_diagrams,
            mnc_models=self.mnc_models,
            cross_references=self.cross_references,
            diagnostics=self.diagnostics,
            summary=summary
        )

    # -------------------------------------------------------------------------
    # Indexers
    # -------------------------------------------------------------------------

    def _index_dml(self, parsed: Dict[str, Any], file_id: str, filename: str, content: str):
        pkg_name = parsed.get("name") or "DefaultPackage"
        self._register_symbol(
            name=pkg_name,
            fqn=f"package:{pkg_name}",
            symbol_type=SymbolType.DATA_PACKAGE,
            file_id=file_id,
            filename=filename,
            line=_find_line_number(content, pkg_name)
        )

        models = parsed.get("dataModelCollections") or parsed.get("dataModels") or []
        for dm in models:
            dm_name = dm.get("name") if isinstance(dm, dict) else str(dm)
            if not dm_name:
                continue
            line_no = _find_line_number(content, dm_name)
            fqn = f"datamodel:{dm_name}"
            self.data_models[dm_name] = dm
            self._register_symbol(
                name=dm_name,
                fqn=fqn,
                symbol_type=SymbolType.DATA_MODEL,
                file_id=file_id,
                filename=filename,
                line=line_no,
                properties={"package": pkg_name}
            )

            # Primitives
            for p in dm.get("primitives", []):
                p_name = p.get("name") if isinstance(p, dict) else str(p)
                p_type = p.get("type", "string") if isinstance(p, dict) else "string"
                self._register_symbol(
                    name=p_name,
                    fqn=f"{dm_name}.{p_name}",
                    symbol_type=SymbolType.PRIMITIVE,
                    file_id=file_id,
                    filename=filename,
                    line=_find_line_number(content, p_name),
                    signature=f"{p_type} {p_name}"
                )

            # Composites
            for c in dm.get("composites", []):
                c_name = c if isinstance(c, str) else (c.get("name") or str(c))
                self._register_symbol(
                    name=c_name,
                    fqn=f"{dm_name}.composite.{c_name}",
                    symbol_type=SymbolType.COMPOSITE,
                    file_id=file_id,
                    filename=filename,
                    line=_find_line_number(content, c_name),
                    dependencies=[c_name]
                )
                self._add_cross_ref(dm_name, c_name, "composite_reference", file_id, filename)

    def _index_capability(self, parsed: Dict[str, Any], file_id: str, filename: str, content: str):
        cap_name = parsed.get("name")
        if not cap_name:
            return
        line_no = _find_line_number(content, cap_name)
        fqn = f"capability:{cap_name}"
        self.capabilities[cap_name] = parsed

        deps = []
        # Compatible component interfaces
        comp_ifaces = (
            parsed.get("compatible_component_interfaces") or
            parsed.get("componentInterface") or
            parsed.get("compatibleComponentInterfaces") or
            []
        )
        for iface in comp_ifaces:
            iface_name = iface if isinstance(iface, str) else str(iface)
            deps.append(iface_name)
            self._add_cross_ref(cap_name, iface_name, "compatible_interface", file_id, filename, line_no)

        self._register_symbol(
            name=cap_name,
            fqn=fqn,
            symbol_type=SymbolType.CAPABILITY,
            file_id=file_id,
            filename=filename,
            line=line_no,
            properties={"interfaces": comp_ifaces},
            dependencies=deps
        )

        # Control Capabilities
        ctrl = parsed.get("providesControlCapabilities") or parsed.get("provides_control_capabilities") or {}
        for cmd in ctrl.get("commands", []) or ctrl.get("fireableCommands", []) or ctrl.get("fireable_commands", []):
            cmd_name = cmd if isinstance(cmd, str) else (cmd.get("name") or str(cmd))
            self._register_symbol(
                name=cmd_name,
                fqn=f"{cap_name}.command.{cmd_name}",
                symbol_type=SymbolType.COMMAND,
                file_id=file_id,
                filename=filename,
                line=_find_line_number(content, cmd_name)
            )
            self._add_cross_ref(cap_name, cmd_name, "provides_command", file_id, filename)

        for ev in ctrl.get("events", []) or ctrl.get("receivableEvents", []) or ctrl.get("receivable_events", []):
            ev_name = ev if isinstance(ev, str) else (ev.get("name") or str(ev))
            self._register_symbol(
                name=ev_name,
                fqn=f"{cap_name}.event.{ev_name}",
                symbol_type=SymbolType.EVENT,
                file_id=file_id,
                filename=filename,
                line=_find_line_number(content, ev_name)
            )
            self._add_cross_ref(cap_name, ev_name, "receives_event", file_id, filename)

        for al in ctrl.get("alarms", []) or ctrl.get("raisedAlarms", []) or ctrl.get("raised_alarms", []):
            al_name = al if isinstance(al, str) else (al.get("name") or str(al))
            self._register_symbol(
                name=al_name,
                fqn=f"{cap_name}.alarm.{al_name}",
                symbol_type=SymbolType.ALARM,
                file_id=file_id,
                filename=filename,
                line=_find_line_number(content, al_name)
            )
            self._add_cross_ref(cap_name, al_name, "raises_alarm", file_id, filename)

    def _index_operation(self, parsed: Dict[str, Any], file_id: str, filename: str, content: str):
        ops = []
        if "operations" in parsed:
            ops = parsed["operations"]
        elif "name" in parsed:
            ops = [parsed]

        for op in ops:
            op_name = op.get("name")
            if not op_name:
                continue
            line_no = _find_line_number(content, op_name)
            fqn = f"operation:{op_name}"
            self.operations[op_name] = op

            inputs = op.get("inputParameters") or op.get("input_parameters") or []
            output = op.get("outputParameters") or op.get("output_parameters")

            self._register_symbol(
                name=op_name,
                fqn=fqn,
                symbol_type=SymbolType.OPERATION,
                file_id=file_id,
                filename=filename,
                line=line_no,
                properties={"script": op.get("executableScript") or op.get("executable_script")}
            )

            for p in inputs:
                p_name = p.get("name") if isinstance(p, dict) else str(p)
                p_type = p.get("type", "string") if isinstance(p, dict) else "string"
                self._register_symbol(
                    name=p_name,
                    fqn=f"{op_name}.input.{p_name}",
                    symbol_type=SymbolType.PRIMITIVE,
                    file_id=file_id,
                    filename=filename,
                    signature=f"{p_type} {p_name}"
                )

            if output:
                out_name = output.get("name") if isinstance(output, dict) else str(output)
                out_type = output.get("type", "string") if isinstance(output, dict) else "string"
                self._register_symbol(
                    name=out_name,
                    fqn=f"{op_name}.output.{out_name}",
                    symbol_type=SymbolType.PRIMITIVE,
                    file_id=file_id,
                    filename=filename,
                    signature=f"{out_type} {out_name}"
                )

    def _index_activity(self, parsed: Dict[str, Any], file_id: str, filename: str, content: str):
        diag_name = parsed.get("name") or "ActivityDiagram"
        line_no = _find_line_number(content, diag_name)
        fqn = f"activity_diagram:{diag_name}"
        self.activity_diagrams[diag_name] = parsed

        self._register_symbol(
            name=diag_name,
            fqn=fqn,
            symbol_type=SymbolType.ACTIVITY_DIAGRAM,
            file_id=file_id,
            filename=filename,
            line=line_no
        )

        # Context Data Models
        for ctx in parsed.get("contextDataModel", []) or parsed.get("context_data_model", []):
            ctx_name = ctx if isinstance(ctx, str) else str(ctx)
            self._add_cross_ref(diag_name, ctx_name, "context_model", file_id, filename, line_no)

        # Activities
        for act in parsed.get("activities", []):
            act_name = act.get("name")
            if not act_name:
                continue
            act_line = _find_line_number(content, act_name)
            act_fqn = f"activity:{act_name}"

            deps = []
            # Capability
            cap = act.get("bindCapability") or act.get("requiredCapability") or act.get("require_capability")
            if cap:
                deps.append(cap)
                self._add_cross_ref(act_name, cap, "requires_capability", file_id, filename, act_line)

            # Operations
            raw_ops = act.get("requiresOperation") or act.get("requires_operation") or []
            if isinstance(raw_ops, str):
                raw_ops = [raw_ops]
            elif not isinstance(raw_ops, list):
                raw_ops = []
            for op in raw_ops:
                op_name = op if isinstance(op, str) else str(op)
                deps.append(op_name)
                self._add_cross_ref(act_name, op_name, "requires_operation", file_id, filename, act_line)

            # Next activity
            nxt = act.get("nextActivity") or act.get("next_activity")
            if nxt and isinstance(nxt, str):
                self._add_cross_ref(act_name, nxt, "transitions_to", file_id, filename, act_line)

            # Conditions
            raw_conds = act.get("conditionalActivity") or act.get("conditional_activity") or []
            if isinstance(raw_conds, list):
                for cond in raw_conds:
                    if isinstance(cond, dict):
                        on_true = cond.get("onTrueNextActivity") or cond.get("on_true_next_activity")
                        if on_true:
                            self._add_cross_ref(act_name, str(on_true), "transitions_to_conditional", file_id, filename, act_line)

            self._register_symbol(
                name=act_name,
                fqn=act_fqn,
                symbol_type=SymbolType.ACTIVITY,
                file_id=file_id,
                filename=filename,
                line=act_line,
                properties={"time": act.get("time"), "unit": act.get("unit")},
                dependencies=deps
            )

    def _index_mnc(self, parsed: Dict[str, Any], file_id: str, filename: str, content: str):
        model_name = parsed.get("name") or "MncModel"
        self.mnc_models[model_name] = parsed

        systems = parsed.get("systems", [])
        if not systems and (parsed.get("interface_description") or parsed.get("interfaceDescription")):
            systems = [parsed.get("interface_description") or parsed.get("interfaceDescription")]

        for iface in systems:
            if not isinstance(iface, dict):
                continue
            iface_name = iface.get("name") or "Interface"
            line_no = _find_line_number(content, iface_name)
            fqn = f"interface:{iface_name}"

            self._register_symbol(
                name=iface_name,
                fqn=fqn,
                symbol_type=SymbolType.INTERFACE_DESCRIPTION,
                file_id=file_id,
                filename=filename,
                line=line_no,
                properties={"ip_address": iface.get("ip_address"), "port": iface.get("port")}
            )

            # Operating States
            states = (
                iface.get("operating_states", []) or
                iface.get("operatingStates", []) or
                (iface.get("operatingStatesUtility", {}).get("operatingStates", []))
            )
            for st in states:
                st_name = st if isinstance(st, str) else (st.get("name") or str(st))
                if st_name:
                    self._register_symbol(
                        name=st_name,
                        fqn=f"{iface_name}.state.{st_name}",
                        symbol_type=SymbolType.OPERATING_STATE,
                        file_id=file_id,
                        filename=filename,
                        line=_find_line_number(content, st_name)
                    )

            # Commands
            for cmd in iface.get("commands", []):
                cmd_name = cmd if isinstance(cmd, str) else (cmd.get("name") or str(cmd))
                self._register_symbol(
                    name=cmd_name,
                    fqn=f"{iface_name}.command.{cmd_name}",
                    symbol_type=SymbolType.COMMAND,
                    file_id=file_id,
                    filename=filename,
                    line=_find_line_number(content, cmd_name)
                )

            # Events
            for ev in iface.get("events", []):
                ev_name = ev if isinstance(ev, str) else (ev.get("name") or str(ev))
                self._register_symbol(
                    name=ev_name,
                    fqn=f"{iface_name}.event.{ev_name}",
                    symbol_type=SymbolType.EVENT,
                    file_id=file_id,
                    filename=filename,
                    line=_find_line_number(content, ev_name)
                )

            # Alarms
            for al in iface.get("alarms", []):
                al_name = al if isinstance(al, str) else (al.get("name") or str(al))
                self._register_symbol(
                    name=al_name,
                    fqn=f"{iface_name}.alarm.{al_name}",
                    symbol_type=SymbolType.ALARM,
                    file_id=file_id,
                    filename=filename,
                    line=_find_line_number(content, al_name)
                )

            # DataPoints
            for dp in iface.get("dataPoints", []) or iface.get("data_points", []):
                dp_name = dp if isinstance(dp, str) else (dp.get("name") or str(dp))
                self._register_symbol(
                    name=dp_name,
                    fqn=f"{iface_name}.datapoint.{dp_name}",
                    symbol_type=SymbolType.DATA_POINT,
                    file_id=file_id,
                    filename=filename,
                    line=_find_line_number(content, dp_name)
                )

        # Control Node
        node = parsed.get("controlNode") or parsed.get("control_node")
        if node and isinstance(node, dict):
            node_name = node.get("name") or "ControlNode"
            line_no = _find_line_number(content, node_name)
            iface_ref = node.get("interfaceDescription") or node.get("interface_ref")

            deps = [iface_ref] if iface_ref else []
            if iface_ref:
                self._add_cross_ref(node_name, iface_ref, "implements_interface", file_id, filename, line_no)

            self._register_symbol(
                name=node_name,
                fqn=f"control_node:{node_name}",
                symbol_type=SymbolType.CONTROL_NODE,
                file_id=file_id,
                filename=filename,
                line=line_no,
                dependencies=deps
            )

def compile_project_ir(
    files: Union[List[Dict[str, Any]], Dict[str, Any]],
    project_id: Optional[str] = None,
    project_name: str = "Project"
) -> ProjectIR:
    """Convenience entrypoint to compile files into a ProjectIR instance."""
    compiler = EngineeringIRCompiler(project_id, project_name)
    return compiler.compile(files)
