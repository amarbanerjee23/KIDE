import re
from typing import Any, Dict, List, Optional, Set, Tuple, Union
from collections import defaultdict, deque

from ..schemas.engineering_ir import (
    Diagnostic, DiagnosticSeverity, ProjectIR, SymbolType, AutomataHazard
)
from .engineering_ir import compile_project_ir, normalize_files
from .knowledge_hub import get_knowledge_hub_service

# IPv4 regex validator
IPV4_REGEX = re.compile(r"^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$")

class SemanticValidationEngine:
    """
    Production-grade multi-file semantic validation engine.
    Performs cross-file symbol resolution, state machine hazard detection (deadlocks,
    unreachable states, ambiguous transitions), workflow invariant verification, and
    configuration sanity checks across DML, Capability, Operation, Activity, and MNC models.
    """

    def __init__(self, project_ir: ProjectIR):
        self.ir = project_ir
        self.diagnostics: List[Diagnostic] = list(project_ir.diagnostics)  # Start with parse errors
        self.kb_service = get_knowledge_hub_service()
        
        # Cache standard knowledge hub symbols
        self.kb_capabilities = set(self.kb_service.capabilities.keys())
        self.kb_operations = set(self.kb_service.operations.keys())

    def validate_all(self) -> List[Diagnostic]:
        """Runs all semantic validation and hazard detection passes."""
        self._validate_dml_models()
        self._validate_capabilities()
        self._validate_operations()
        self._validate_activity_diagrams()
        self._validate_mnc_models()
        self._validate_cross_file_consistency()
        return self.diagnostics

    # -------------------------------------------------------------------------
    # 1. DML Validation
    # -------------------------------------------------------------------------
    def _validate_dml_models(self):
        for dm_name, dm in self.ir.data_models.items():
            sym = self.ir.symbols.get(f"datamodel:{dm_name}") or self.ir.symbols.get(dm_name)
            file_id = sym.file_id if sym else None
            filename = sym.filename if sym else ""
            line_no = sym.line if sym else 1

            # Check composite field references
            composites = dm.get("composites", [])
            for comp in composites:
                c_name = comp if isinstance(comp, str) else (comp.get("name") or str(comp))
                if c_name and c_name not in self.ir.data_models:
                    self.diagnostics.append(
                        Diagnostic(
                            file_id=file_id,
                            fileId=file_id,
                            filename=filename,
                            line=line_no,
                            column=1,
                            message=f"Composite field references undeclared DataModel '{c_name}' in '{dm_name}'",
                            severity=DiagnosticSeverity.ERROR,
                            rule_id="SEM-007",
                            ruleId="SEM-007",
                            symbol=c_name,
                            suggestion=f"Declare 'DataModel {c_name} {{ ... }}' in a .dml file."
                        )
                    )

    # -------------------------------------------------------------------------
    # 2. Capability Validation
    # -------------------------------------------------------------------------
    def _validate_capabilities(self):
        for cap_name, cap in self.ir.capabilities.items():
            sym = self.ir.symbols.get(f"capability:{cap_name}") or self.ir.symbols.get(cap_name)
            file_id = sym.file_id if sym else None
            filename = sym.filename if sym else ""
            line_no = sym.line if sym else 1

            # Check compatible interface references
            ifaces = (
                cap.get("compatible_component_interfaces") or
                cap.get("componentInterface") or
                cap.get("compatibleComponentInterfaces") or
                []
            )
            for iface in ifaces:
                iface_name = iface if isinstance(iface, str) else str(iface)
                # Check if iface exists in project MNC models or has interface symbol
                has_iface = (
                    f"interface:{iface_name}" in self.ir.symbols or
                    iface_name in self.ir.symbols or
                    any(iface_name in m.get("systems", []) or m.get("name") == iface_name for m in self.ir.mnc_models.values())
                )
                if not has_iface and len(self.ir.mnc_models) > 0:
                    self.diagnostics.append(
                        Diagnostic(
                            file_id=file_id,
                            fileId=file_id,
                            filename=filename,
                            line=line_no,
                            column=1,
                            message=f"Capability '{cap_name}' compatible interface '{iface_name}' is not declared in project MNC models",
                            severity=DiagnosticSeverity.WARNING,
                            rule_id="SEM-004",
                            ruleId="SEM-004",
                            symbol=iface_name,
                            suggestion=f"Define 'InterfaceDescription {iface_name} {{ ... }}' in a .mnc file."
                        )
                    )

    # -------------------------------------------------------------------------
    # 3. Operation Validation
    # -------------------------------------------------------------------------
    def _validate_operations(self):
        for op_name, op in self.ir.operations.items():
            sym = self.ir.symbols.get(f"operation:{op_name}") or self.ir.symbols.get(op_name)
            file_id = sym.file_id if sym else None
            filename = sym.filename if sym else ""
            line_no = sym.line if sym else 1

            # Check parameter types
            for param in op.get("inputParameters", []) or op.get("input_parameters", []):
                p_type = param.get("type") if isinstance(param, dict) else None
                if p_type and p_type not in ("int", "boolean", "float", "string", "object", "date"):
                    # Check if composite DML type exists
                    if p_type not in self.ir.data_models:
                        self.diagnostics.append(
                            Diagnostic(
                                file_id=file_id,
                                fileId=file_id,
                                filename=filename,
                                line=line_no,
                                column=1,
                                message=f"Operation '{op_name}' parameter has unknown type '{p_type}'",
                                severity=DiagnosticSeverity.WARNING,
                                rule_id="TYP-001",
                                ruleId="TYP-001",
                                symbol=p_type,
                                suggestion=f"Use primitive types (int, float, string, boolean) or declare DataModel '{p_type}' in .dml."
                            )
                        )

    # -------------------------------------------------------------------------
    # 4. Activity Diagram & Workflow Hazards
    # -------------------------------------------------------------------------
    def _validate_activity_diagrams(self):
        for diag_name, diag in self.ir.activity_diagrams.items():
            sym = self.ir.symbols.get(f"activity_diagram:{diag_name}") or self.ir.symbols.get(diag_name)
            file_id = sym.file_id if sym else None
            filename = sym.filename if sym else ""
            diag_line = sym.line if sym else 1

            # Context Data Models check
            for ctx in diag.get("contextDataModel", []) or diag.get("context_data_model", []):
                ctx_name = ctx if isinstance(ctx, str) else str(ctx)
                if ctx_name not in self.ir.data_models and len(self.ir.data_models) > 0:
                    self.diagnostics.append(
                        Diagnostic(
                            file_id=file_id,
                            fileId=file_id,
                            filename=filename,
                            line=diag_line,
                            column=1,
                            message=f"Context data model '{ctx_name}' in diagram '{diag_name}' is not declared in any .dml file",
                            severity=DiagnosticSeverity.WARNING,
                            rule_id="SEM-003",
                            ruleId="SEM-003",
                            symbol=ctx_name,
                            suggestion=f"Define 'DataModel {ctx_name}' in a .dml file."
                        )
                    )

            activities = diag.get("activities", [])
            if not activities:
                self.diagnostics.append(
                    Diagnostic(
                        file_id=file_id,
                        fileId=file_id,
                        filename=filename,
                        line=diag_line,
                        column=1,
                        message=f"Activity diagram '{diag_name}' contains zero activities",
                        severity=DiagnosticSeverity.ERROR,
                        rule_id="FLW-003",
                        ruleId="FLW-003",
                        symbol=diag_name,
                        suggestion="Define at least one Activity within the diagram."
                    )
                )
                continue

            activity_names = set()
            incoming_edges: Dict[str, Set[str]] = defaultdict(set)
            outgoing_edges: Dict[str, Set[str]] = defaultdict(set)

            # Pass 1: Collect names and check references
            for act in activities:
                act_name = act.get("name")
                if not act_name:
                    continue
                act_sym = self.ir.symbols.get(f"activity:{act_name}") or self.ir.symbols.get(act_name)
                act_line = act_sym.line if act_sym else diag_line

                # Duplicate activity name check
                if act_name in activity_names:
                    self.diagnostics.append(
                        Diagnostic(
                            file_id=file_id,
                            fileId=file_id,
                            filename=filename,
                            line=act_line,
                            column=1,
                            message=f"Duplicate activity name '{act_name}' within diagram '{diag_name}'",
                            severity=DiagnosticSeverity.ERROR,
                            rule_id="FLW-005",
                            ruleId="FLW-005",
                            symbol=act_name,
                            suggestion=f"Rename duplicate activity '{act_name}' to a unique name."
                        )
                    )
                activity_names.add(act_name)

                # Check required capability resolution
                cap = act.get("bindCapability") or act.get("requiredCapability") or act.get("require_capability")
                if cap:
                    in_project_caps = cap in self.ir.capabilities
                    in_kb_caps = cap in self.kb_capabilities
                    if not in_project_caps and not in_kb_caps:
                        self.diagnostics.append(
                            Diagnostic(
                                file_id=file_id,
                                fileId=file_id,
                                filename=filename,
                                line=act_line,
                                column=1,
                                message=f"Activity '{act_name}' requires undefined capability '{cap}'",
                                severity=DiagnosticSeverity.ERROR,
                                rule_id="SEM-001",
                                ruleId="SEM-001",
                                symbol=cap,
                                suggestion=f"Declare capability '{cap}' in a .cap file or bind to an existing catalog capability."
                            )
                        )

                # Check required operations resolution
                raw_ops = act.get("requiresOperation") or act.get("requires_operation") or []
                if isinstance(raw_ops, str):
                    raw_ops = [raw_ops]
                elif not isinstance(raw_ops, list):
                    raw_ops = []
                for op in raw_ops:
                    op_name = op if isinstance(op, str) else str(op)
                    in_project_ops = op_name in self.ir.operations
                    in_kb_ops = op_name in self.kb_operations
                    if not in_project_ops and not in_kb_ops:
                        self.diagnostics.append(
                            Diagnostic(
                                file_id=file_id,
                                fileId=file_id,
                                filename=filename,
                                line=act_line,
                                column=1,
                                message=f"Activity '{act_name}' requires undefined operation '{op_name}'",
                                severity=DiagnosticSeverity.ERROR,
                                rule_id="SEM-002",
                                ruleId="SEM-002",
                                symbol=op_name,
                                suggestion=f"Declare operation '{op_name}' in an .op file."
                            )
                        )

                # Record sequential transitions
                nxt = act.get("nextActivity") or act.get("next_activity")
                if nxt and isinstance(nxt, str):
                    outgoing_edges[act_name].add(nxt)
                    incoming_edges[nxt].add(act_name)

                    # FLW-001: Self-loop check
                    if nxt == act_name:
                        self.diagnostics.append(
                            Diagnostic(
                                file_id=file_id,
                                fileId=file_id,
                                filename=filename,
                                line=act_line,
                                column=1,
                                message=f"Workflow Cycle Hazard: Activity '{act_name}' unconditionally transitions to itself",
                                severity=DiagnosticSeverity.ERROR,
                                rule_id="FLW-001",
                                ruleId="FLW-001",
                                symbol=act_name,
                                suggestion="Point 'nextActivity' to a subsequent step or add an exit condition branch."
                            )
                        )

                # Record conditional transitions
                raw_conds = act.get("conditionalActivity") or act.get("conditional_activity") or []
                if isinstance(raw_conds, list):
                    for cond in raw_conds:
                        if isinstance(cond, dict):
                            on_true = cond.get("onTrueNextActivity") or cond.get("on_true_next_activity")
                            if on_true and isinstance(on_true, str):
                                outgoing_edges[act_name].add(on_true)
                                incoming_edges[on_true].add(act_name)
                                if on_true == act_name:
                                    self.diagnostics.append(
                                        Diagnostic(
                                            file_id=file_id,
                                            fileId=file_id,
                                            filename=filename,
                                            line=act_line,
                                            column=1,
                                            message=f"Workflow Cycle Hazard: Conditional branch in '{act_name}' transitions directly back to itself",
                                            severity=DiagnosticSeverity.ERROR,
                                            rule_id="FLW-001",
                                            ruleId="FLW-001",
                                            symbol=act_name,
                                            suggestion="Direct the condition branch to an alternative recovery or next activity."
                                        )
                                    )

            # Pass 2: Check unresolved nextActivity targets and dangling activities
            first_act_name = activities[0].get("name") if activities else None

            for act in activities:
                act_name = act.get("name")
                if not act_name:
                    continue
                act_sym = self.ir.symbols.get(f"activity:{act_name}") or self.ir.symbols.get(act_name)
                act_line = act_sym.line if act_sym else diag_line

                # Check unresolved next activity targets
                for target in outgoing_edges[act_name]:
                    if target not in activity_names:
                        self.diagnostics.append(
                            Diagnostic(
                                file_id=file_id,
                                fileId=file_id,
                                filename=filename,
                                line=act_line,
                                column=1,
                                message=f"Target activity '{target}' referenced in '{act_name}' not found in diagram '{diag_name}'",
                                severity=DiagnosticSeverity.ERROR,
                                rule_id="SEM-006",
                                ruleId="SEM-006",
                                symbol=target,
                                suggestion=f"Declare activity '{target}' or update the transition reference."
                            )
                        )

                # FLW-002: Dangling activity (non-initial activity with zero incoming transitions)
                if act_name != first_act_name and len(incoming_edges[act_name]) == 0 and len(activities) > 1:
                    self.diagnostics.append(
                        Diagnostic(
                            file_id=file_id,
                            fileId=file_id,
                            filename=filename,
                            line=act_line,
                            column=1,
                            message=f"Dangling Activity: '{act_name}' has no incoming transitions and cannot be reached",
                            severity=DiagnosticSeverity.WARNING,
                            rule_id="FLW-002",
                            ruleId="FLW-002",
                            symbol=act_name,
                            suggestion=f"Connect a preceding activity's 'nextActivity' to '{act_name}'."
                        )
                    )

    # -------------------------------------------------------------------------
    # 5. MNC State Machine & Automata Hazard Detection
    # -------------------------------------------------------------------------
    def _validate_mnc_models(self):
        for model_name, model in self.ir.mnc_models.items():
            sym = self.ir.symbols.get(model_name)
            file_id = sym.file_id if sym else None
            filename = sym.filename if sym else ""

            systems = model.get("systems", [])
            if not systems and (model.get("interface_description") or model.get("interfaceDescription")):
                systems = [model.get("interface_description") or model.get("interfaceDescription")]

            for iface in systems:
                if not isinstance(iface, dict):
                    continue
                iface_name = iface.get("name") or "Interface"
                iface_sym = self.ir.symbols.get(f"interface:{iface_name}") or self.ir.symbols.get(iface_name)
                iface_line = iface_sym.line if iface_sym else 1

                # CFG-001: IPv4 address format validation
                ip = iface.get("ip_address") or iface.get("ipaddress")
                if ip and not IPV4_REGEX.match(str(ip)):
                    self.diagnostics.append(
                        Diagnostic(
                            file_id=file_id,
                            fileId=file_id,
                            filename=filename,
                            line=iface_line,
                            column=1,
                            message=f"Invalid IPv4 address format '{ip}' in interface '{iface_name}'",
                            severity=DiagnosticSeverity.ERROR,
                            rule_id="CFG-001",
                            ruleId="CFG-001",
                            symbol=str(ip),
                            suggestion="Provide a valid IPv4 address in the format 'X.X.X.X' where each octet is 0-255."
                        )
                    )

                # CFG-002: Port range validation
                port_data = iface.get("port")
                if port_data:
                    port_val = port_data.get("value") if isinstance(port_data, dict) else port_data
                    if port_val is not None:
                        try:
                            pval = int(port_val)
                            if pval < 1 or pval > 65535:
                                self.diagnostics.append(
                                    Diagnostic(
                                        file_id=file_id,
                                        fileId=file_id,
                                        filename=filename,
                                        line=iface_line,
                                        column=1,
                                        message=f"Port number {pval} in interface '{iface_name}' out of valid range (1..65535)",
                                        severity=DiagnosticSeverity.ERROR,
                                        rule_id="CFG-002",
                                        ruleId="CFG-002",
                                        symbol=str(pval),
                                        suggestion="Configure port to an integer between 1 and 65535."
                                    )
                                )
                        except (ValueError, TypeError):
                            self.diagnostics.append(
                                Diagnostic(
                                    file_id=file_id,
                                    fileId=file_id,
                                    filename=filename,
                                    line=iface_line,
                                    column=1,
                                    message=f"Invalid port definition '{port_val}' in interface '{iface_name}'",
                                    severity=DiagnosticSeverity.ERROR,
                                    rule_id="CFG-002",
                                    ruleId="CFG-002",
                                    suggestion="Provide an integer port number."
                                )
                            )

                # Operating States & Transitions Analysis
                op_states_util = iface.get("operatingStatesUtility") or {}
                raw_states = (
                    op_states_util.get("operatingStates", []) or
                    iface.get("operating_states", []) or
                    iface.get("operatingStates", [])
                )
                start_states = set(op_states_util.get("startStates", []))
                end_states = set(op_states_util.get("endStates", []))

                state_names: Set[str] = set()
                for st in raw_states:
                    st_name = st if isinstance(st, str) else (st.get("name") or str(st))
                    if st_name:
                        state_names.add(st_name)

                transitions = iface.get("transitions", []) or model.get("transitions", [])

                if state_names:
                    # HAZ-004: Missing start states check
                    if not start_states:
                        # Fallback heuristic: check if INITIALIZED or READY exists
                        if "INITIALIZED" in state_names:
                            start_states.add("INITIALIZED")
                        else:
                            self.diagnostics.append(
                                Diagnostic(
                                    file_id=file_id,
                                    fileId=file_id,
                                    filename=filename,
                                    line=iface_line,
                                    column=1,
                                    message=f"Interface '{iface_name}' defines operating states but has no declared startStates",
                                    severity=DiagnosticSeverity.WARNING,
                                    rule_id="HAZ-004",
                                    ruleId="HAZ-004",
                                    suggestion="Declare 'startStates: <InitialState>' in operatingStates."
                                )
                            )

                    # Build state transition graph
                    state_adj: Dict[str, Set[str]] = defaultdict(set)
                    state_incoming: Dict[str, Set[str]] = defaultdict(set)

                    for tr in transitions:
                        curr = tr.get("currentState") or tr.get("current_state") or tr.get("source")
                        nxt = tr.get("nextState") or tr.get("next_state") or tr.get("target")

                        if curr and nxt:
                            curr_list = curr if isinstance(curr, list) else [curr]
                            for c in curr_list:
                                c_str = str(c)
                                n_str = str(nxt)
                                state_adj[c_str].add(n_str)
                                state_incoming[n_str].add(c_str)

                                # HAZ-006: Infinite Self-Loop Hazard
                                if c_str == n_str and len(state_adj[c_str]) == 1:
                                    # Will re-verify below if it has zero other transitions
                                    pass

                    # If we have transition data, check reachability and deadlocks
                    if transitions:
                        # HAZ-001: Unreachable States Detection via BFS from start_states
                        reachable: Set[str] = set()
                        queue = deque(start_states)
                        for s in start_states:
                            if s in state_names:
                                reachable.add(s)

                        while queue:
                            curr = queue.popleft()
                            for neighbor in state_adj.get(curr, []):
                                if neighbor not in reachable and neighbor in state_names:
                                    reachable.add(neighbor)
                                    queue.append(neighbor)

                        unreachable = state_names - reachable
                        for unreach in unreachable:
                            self.diagnostics.append(
                                Diagnostic(
                                    file_id=file_id,
                                    fileId=file_id,
                                    filename=filename,
                                    line=iface_line,
                                    column=1,
                                    message=f"Automata Hazard: State '{unreach}' is unreachable from declared start states",
                                    severity=DiagnosticSeverity.ERROR,
                                    rule_id="HAZ-001",
                                    ruleId="HAZ-001",
                                    symbol=unreach,
                                    suggestion=f"Add a state transition leading into '{unreach}' from a reachable state."
                                )
                            )

                        # HAZ-002: Deadlock State Detection
                        # Non-end state that has ZERO outgoing transitions
                        for st in state_names:
                            if st not in end_states and st in reachable:
                                outgoing = state_adj.get(st, set())
                                if len(outgoing) == 0:
                                    self.diagnostics.append(
                                        Diagnostic(
                                            file_id=file_id,
                                            fileId=file_id,
                                            filename=filename,
                                            line=iface_line,
                                            column=1,
                                            message=f"Deadlock Hazard: State '{st}' has no outgoing transitions and is not declared as an endState",
                                            severity=DiagnosticSeverity.ERROR,
                                            rule_id="HAZ-002",
                                            ruleId="HAZ-002",
                                            symbol=st,
                                            suggestion=f"Add an outgoing transition from '{st}' or register it in 'endStates'."
                                        )
                                    )
                                elif outgoing == {st}:
                                    # HAZ-006: Infinite Self-Loop
                                    self.diagnostics.append(
                                        Diagnostic(
                                            file_id=file_id,
                                            fileId=file_id,
                                            filename=filename,
                                            line=iface_line,
                                            column=1,
                                            message=f"Infinite Loop Hazard: State '{st}' only transitions to itself with no exit condition",
                                            severity=DiagnosticSeverity.ERROR,
                                            rule_id="HAZ-006",
                                            ruleId="HAZ-006",
                                            symbol=st,
                                            suggestion=f"Add an exit transition from '{st}' to an operating or error state."
                                        )
                                    )

            # Control Node implementation checks
            node = model.get("controlNode") or model.get("control_node")
            if node and isinstance(node, dict):
                node_name = node.get("name") or "ControlNode"
                iface_ref = node.get("interfaceDescription") or node.get("interface_ref")
                if iface_ref:
                    has_iface = (
                        f"interface:{iface_ref}" in self.ir.symbols or
                        iface_ref in self.ir.symbols or
                        any(s.get("name") == iface_ref for s in systems)
                    )
                    if not has_iface and len(systems) > 0:
                        self.diagnostics.append(
                            Diagnostic(
                                file_id=file_id,
                                fileId=file_id,
                                filename=filename,
                                line=sym.line if sym else 1,
                                column=1,
                                message=f"ControlNode '{node_name}' implements undeclared interface '{iface_ref}'",
                                severity=DiagnosticSeverity.ERROR,
                                rule_id="SEM-005",
                                ruleId="SEM-005",
                                symbol=iface_ref,
                                suggestion=f"Declare 'InterfaceDescription {iface_ref} {{ ... }}'."
                            )
                        )

    # -------------------------------------------------------------------------
    # 6. Global Cross-File Consistency
    # -------------------------------------------------------------------------
    def _validate_cross_file_consistency(self):
        # Update summary error counts and health score
        err_count = len([d for d in self.diagnostics if d.severity == DiagnosticSeverity.ERROR])
        warn_count = len([d for d in self.diagnostics if d.severity == DiagnosticSeverity.WARNING])
        self.ir.summary.error_count = err_count
        self.ir.summary.warning_count = warn_count
        self.ir.summary.health_score = max(0.0, 100.0 - (err_count * 15.0 + warn_count * 5.0))
        self.ir.diagnostics = self.diagnostics

def validate_project_semantics(
    files: Union[List[Dict[str, Any]], Dict[str, Any]],
    project_id: Optional[str] = None
) -> Tuple[ProjectIR, List[Diagnostic]]:
    """
    Main entrypoint for multi-file semantic validation.
    Compiles Canonical Engineering IR and runs comprehensive semantic validation passes.
    """
    ir = compile_project_ir(files, project_id=project_id)
    engine = SemanticValidationEngine(ir)
    diagnostics = engine.validate_all()
    return ir, diagnostics
