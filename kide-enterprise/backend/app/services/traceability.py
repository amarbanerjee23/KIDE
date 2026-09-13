from typing import Any, Dict, List, Optional, Set, Tuple
from collections import deque

from app.schemas.engineering_ir import SymbolType, SymbolRecord, CrossReference, ProjectIR
from app.services.engineering_ir import compile_project_ir
from app.services.transformer import transform_workspace
from app.schemas.traceability import (
    TraceLinkType,
    ImpactRiskLevel,
    TraceabilityLink,
    TraceabilityMatrixRow,
    TraceabilityMatrixResponse,
    ImpactedItem,
    ImpactAnalysisRequest,
    ImpactAnalysisResponse
)

STAGE_MAPPING: Dict[str, int] = {
    # Stage 1: Data Modeling
    SymbolType.DATA_PACKAGE.value: 1,
    SymbolType.DATA_MODEL.value: 1,
    SymbolType.PRIMITIVE.value: 1,
    SymbolType.COMPOSITE.value: 1,
    SymbolType.ARRAY.value: 1,
    # Stage 2: Capabilities & Ops
    SymbolType.CAPABILITY.value: 2,
    SymbolType.OPERATION.value: 2,
    SymbolType.INTERFACE_DESCRIPTION.value: 2,
    SymbolType.CONTROL_NODE.value: 2,
    SymbolType.COMMAND.value: 2,
    SymbolType.EVENT.value: 2,
    SymbolType.RESPONSE.value: 2,
    SymbolType.ALARM.value: 2,
    SymbolType.DATA_POINT.value: 2,
    # Stage 3: Supervisory Workflow
    SymbolType.ACTIVITY_DIAGRAM.value: 3,
    SymbolType.ACTIVITY.value: 3,
    # Stage 4: Automated Synthesis
    SymbolType.OPERATING_STATE.value: 4,
    SymbolType.TRANSITION.value: 4,
}

CODE_GENERATORS = [
    "PythonController",
    "ROS2Node",
    "JavaController",
    "PLC_IEC61131",
    "EmbeddedCpp"
]

class TraceabilityService:
    """
    Constructs multi-layer bidirectional Traceability Matrix across the 5 DSL stages:
    Stage 1: Data Models (DML)
    Stage 2: Capabilities & Operations
    Stage 3: Supervisory Workflows (Activities)
    Stage 4: Automated Synthesis (MNC Automata & Operating States)
    Stage 5: Code Generation Targets (Python, ROS2, Java, PLC, C++)
    """

    @classmethod
    def build_traceability_matrix(
        cls,
        raw_files: List[Any],
        project_id: Optional[int] = None
    ) -> TraceabilityMatrixResponse:
        project_ir = compile_project_ir(raw_files, project_id=str(project_id or ""))
        symbols = project_ir.symbols
        cross_refs = project_ir.cross_references

        links: List[TraceabilityLink] = []
        downstream_map: Dict[str, Set[str]] = {s_name: set() for s_name in symbols}
        upstream_map: Dict[str, Set[str]] = {s_name: set() for s_name in symbols}

        # 1. Map cross-references into TraceabilityLinks
        for ref in cross_refs:
            src_sym = symbols.get(ref.source_symbol)
            tgt_sym = symbols.get(ref.target_symbol)
            src_type = src_sym.symbol_type.value if src_sym else "unknown"
            tgt_type = tgt_sym.symbol_type.value if tgt_sym else "unknown"
            src_file = src_sym.filename if src_sym else ref.filename
            tgt_file = tgt_sym.filename if tgt_sym else ""

            link_type = TraceLinkType.SPECIFIES
            desc = f"{ref.source_symbol} references {ref.target_symbol}"

            if ref.reference_kind == "requires_capability":
                link_type = TraceLinkType.REQUIRES_CAPABILITY
                desc = f"Activity '{ref.source_symbol}' requires capability '{ref.target_symbol}'"
            elif ref.reference_kind == "requires_operation":
                link_type = TraceLinkType.REQUIRES_OPERATION
                desc = f"Activity '{ref.source_symbol}' requires operation '{ref.target_symbol}'"
            elif ref.reference_kind == "context_model":
                link_type = TraceLinkType.CONTEXT_MODEL
                desc = f"Workflow '{ref.source_symbol}' executes on context data model '{ref.target_symbol}'"
            elif ref.reference_kind == "implements_interface":
                link_type = TraceLinkType.IMPLEMENTS_INTERFACE
                desc = f"Control Node '{ref.source_symbol}' implements interface '{ref.target_symbol}'"
            elif ref.reference_kind == "compatible_interface":
                link_type = TraceLinkType.IMPLEMENTS_INTERFACE
                desc = f"Capability '{ref.source_symbol}' compatible with interface '{ref.target_symbol}'"
            elif ref.reference_kind == "transitions_to":
                link_type = TraceLinkType.TRANSITIONS_TO
                desc = f"State '{ref.source_symbol}' transitions to '{ref.target_symbol}'"

            # In traceability:
            # If Activity requires Capability:
            # Upstream of Activity is Capability.
            # Downstream of Capability is Activity (Activity depends on Capability).
            if ref.reference_kind in ("requires_capability", "requires_operation", "context_model"):
                # source depends on target: target -> source
                downstream_map.setdefault(ref.target_symbol, set()).add(ref.source_symbol)
                upstream_map.setdefault(ref.source_symbol, set()).add(ref.target_symbol)
                links.append(TraceabilityLink(
                    source_symbol=ref.target_symbol,
                    source_type=tgt_type,
                    source_file=tgt_file,
                    target_symbol=ref.source_symbol,
                    target_type=src_type,
                    target_file=src_file,
                    link_type=link_type,
                    description=desc
                ))
            else:
                # source -> target
                downstream_map.setdefault(ref.source_symbol, set()).add(ref.target_symbol)
                upstream_map.setdefault(ref.target_symbol, set()).add(ref.source_symbol)
                links.append(TraceabilityLink(
                    source_symbol=ref.source_symbol,
                    source_type=src_type,
                    source_file=src_file,
                    target_symbol=ref.target_symbol,
                    target_type=tgt_type,
                    target_file=tgt_file,
                    link_type=link_type,
                    description=desc
                ))

        # 2. Derive Stage 3 -> Stage 4 Synthesis Links (Activity -> OperatingState)
        # Check activities and operating states
        activity_syms = [s for s in symbols.values() if s.symbol_type == SymbolType.ACTIVITY]
        state_syms = [s for s in symbols.values() if s.symbol_type == SymbolType.OPERATING_STATE]

        for act in activity_syms:
            for st in state_syms:
                # Match either by identical name or by prefix/subsystem correlation
                if act.name.lower() in st.name.lower() or st.name.lower() in act.name.lower():
                    downstream_map.setdefault(act.name, set()).add(st.name)
                    upstream_map.setdefault(st.name, set()).add(act.name)
                    links.append(TraceabilityLink(
                        source_symbol=act.name,
                        source_type=SymbolType.ACTIVITY.value,
                        source_file=act.filename,
                        target_symbol=st.name,
                        target_type=SymbolType.OPERATING_STATE.value,
                        target_file=st.filename,
                        link_type=TraceLinkType.SYNTHESIZES_STATE,
                        description=f"Activity '{act.name}' synthesizes operating state '{st.name}'"
                    ))

        # 3. Derive Stage 4 -> Stage 5 Code Generation Links
        for st in state_syms:
            for gen in CODE_GENERATORS:
                downstream_map.setdefault(st.name, set()).add(gen)
                upstream_map.setdefault(gen, set()).add(st.name)
                links.append(TraceabilityLink(
                    source_symbol=st.name,
                    source_type=SymbolType.OPERATING_STATE.value,
                    source_file=st.filename,
                    target_symbol=gen,
                    target_type="code_generator",
                    target_file=f"{gen}.target",
                    link_type=TraceLinkType.GENERATES_TARGET,
                    description=f"Operating state '{st.name}' generates target controller '{gen}'"
                ))

        # Build Rows
        rows: List[TraceabilityMatrixRow] = []
        total_symbols_count = len(symbols)
        connected_symbols_count = 0
        layer_counts: Dict[str, int] = {f"stage_{i}": 0 for i in range(1, 6)}

        # Add code generators to symbols collection for complete view
        for gen in CODE_GENERATORS:
            if gen not in symbols:
                symbols[gen] = SymbolRecord(
                    name=gen,
                    fqn=gen,
                    symbol_type=SymbolType.CONTROL_NODE, # mapped to target node
                    filename=f"{gen}.codegen",
                    properties={"is_generator_target": True}
                )

        seen_symbols = set()
        for sym_name, sym in symbols.items():
            if sym.name in seen_symbols:
                continue
            seen_symbols.add(sym.name)
            simple_name = sym.name

            st_val = sym.symbol_type.value if hasattr(sym.symbol_type, "value") else str(sym.symbol_type)
            if sym.properties.get("is_generator_target"):
                stage = 5
            else:
                stage = STAGE_MAPPING.get(st_val, 2)

            layer_counts[f"stage_{stage}"] = layer_counts.get(f"stage_{stage}", 0) + 1

            up_syms = sorted(list(upstream_map.get(simple_name, set())))
            down_syms = sorted(list(downstream_map.get(simple_name, set())))
            sym_links = [l for l in links if l.source_symbol == simple_name or l.target_symbol == simple_name]

            if up_syms or down_syms:
                connected_symbols_count += 1

            rows.append(TraceabilityMatrixRow(
                symbol=sym.name,
                symbol_type=st_val,
                filename=sym.filename,
                stage=stage,
                upstream_symbols=up_syms,
                downstream_symbols=down_syms,
                links=sym_links
            ))

        # Sort rows by stage then name
        rows.sort(key=lambda r: (r.stage, r.symbol))

        total_symbols_count = len(rows)
        coverage = (connected_symbols_count / total_symbols_count * 100) if total_symbols_count > 0 else 100.0

        return TraceabilityMatrixResponse(
            project_id=project_id,
            total_symbols=total_symbols_count,
            total_links=len(links),
            coverage_percentage=round(coverage, 2),
            rows=rows,
            layer_counts=layer_counts
        )

    @classmethod
    def analyze_change_impact(
        cls,
        raw_files: List[Any],
        request: ImpactAnalysisRequest
    ) -> ImpactAnalysisResponse:
        matrix = cls.build_traceability_matrix(raw_files)
        target_name = (request.target_symbol or "").strip()
        action = (request.action or "modify").lower()

        # If target_symbol was not provided, look for symbols in target_file
        target_row: Optional[TraceabilityMatrixRow] = None
        if target_name:
            for r in matrix.rows:
                if r.symbol.lower() == target_name.lower():
                    target_row = r
                    break
        elif request.target_file:
            for r in matrix.rows:
                if r.filename.lower() == request.target_file.lower():
                    target_row = r
                    target_name = r.symbol
                    break

        if not target_row:
            return ImpactAnalysisResponse(
                target_symbol=target_name or "Unknown",
                target_type="unknown",
                action=action,
                risk_level=ImpactRiskLevel.LOW,
                impacted_symbols_count=0,
                impacted_items=[],
                affected_activities=[],
                affected_states=[],
                broken_transitions=[],
                affected_code_generators=[],
                breaking_hazards=[],
                recommended_mitigations=["Symbol not found in project IR. No downstream impact."]
            )

        # BFS downstream traversal to collect full blast radius
        visited: Set[str] = set()
        queue: deque = deque([target_row.symbol])
        impacted_items: List[ImpactedItem] = []
        affected_activities: Set[str] = set()
        affected_states: Set[str] = set()
        broken_transitions: Set[str] = set()
        affected_code_generators: Set[str] = set()
        breaking_hazards: List[str] = []

        row_map = {r.symbol: r for r in matrix.rows}

        while queue:
            curr = queue.popleft()
            curr_row = row_map.get(curr)
            if not curr_row:
                continue

            for down_sym in curr_row.downstream_symbols:
                if down_sym not in visited:
                    visited.add(down_sym)
                    queue.append(down_sym)

                    down_row = row_map.get(down_sym)
                    d_stage = down_row.stage if down_row else 3
                    d_type = down_row.symbol_type if down_row else "unknown"
                    d_file = down_row.filename if down_row else ""

                    # Classify impacted item
                    reason = f"Depends directly or transitively on '{curr}'"
                    item_risk = ImpactRiskLevel.MEDIUM

                    if d_stage == 3: # Activity
                        affected_activities.add(down_sym)
                        reason = f"Workflow step '{down_sym}' requires or utilizes '{target_row.symbol}'"
                        item_risk = ImpactRiskLevel.HIGH
                    elif d_stage == 4: # OperatingState / Transition
                        affected_states.add(down_sym)
                        reason = f"Synthesized supervisory state '{down_sym}' derived from altered workflow"
                        item_risk = ImpactRiskLevel.HIGH
                        broken_transitions.add(f"Transitions involving {down_sym}")
                    elif d_stage == 5 or down_sym in CODE_GENERATORS:
                        affected_code_generators.add(down_sym)
                        reason = f"Target code generator '{down_sym}' requires regeneration"
                        item_risk = ImpactRiskLevel.MEDIUM

                    impacted_items.append(ImpactedItem(
                        symbol=down_sym,
                        symbol_type=d_type,
                        filename=d_file,
                        stage=d_stage,
                        impact_reason=reason,
                        risk=item_risk
                    ))

        # Check for specific hazards
        if action == "delete":
            if target_row.symbol_type == SymbolType.CAPABILITY.value:
                breaking_hazards.append(f"SEM-001: Deleting capability '{target_name}' breaks {len(affected_activities)} activities requiring it.")
            elif target_row.symbol_type == SymbolType.OPERATION.value:
                breaking_hazards.append(f"SEM-002: Deleting operation '{target_name}' breaks {len(affected_activities)} activities requiring it.")
            elif target_row.symbol_type == SymbolType.DATA_MODEL.value:
                breaking_hazards.append(f"SEM-003: Deleting data model '{target_name}' breaks activities and interface payload types.")
            elif target_row.symbol_type == SymbolType.OPERATING_STATE.value:
                breaking_hazards.append(f"HAZ-002: Deleting state '{target_name}' creates dangling transitions and potential deadlocks.")

        # Determine overall Risk Level
        if len(affected_states) >= 2 or len(breaking_hazards) >= 2:
            overall_risk = ImpactRiskLevel.CRITICAL
        elif len(affected_activities) >= 1 or len(affected_states) >= 1:
            overall_risk = ImpactRiskLevel.HIGH
        elif len(impacted_items) >= 1:
            overall_risk = ImpactRiskLevel.MEDIUM
        else:
            overall_risk = ImpactRiskLevel.LOW

        # Generate Mitigations
        mitigations: List[str] = []
        if affected_activities:
            acts_str = ", ".join(sorted(list(affected_activities))[:3])
            mitigations.append(f"Update or substitute capability references in activities: {acts_str}.")
        if affected_states:
            mitigations.append("Execute Automated Workspace Synthesis to re-derive consistent state machine automata.")
        if affected_code_generators:
            gens_str = ", ".join(sorted(list(affected_code_generators))[:3])
            mitigations.append(f"Regenerate downstream code targets ({gens_str}).")
        if not mitigations:
            mitigations.append("Change is fully isolated; no downstream supervisory models are impacted.")

        return ImpactAnalysisResponse(
            target_symbol=target_row.symbol,
            target_type=target_row.symbol_type,
            action=action,
            risk_level=overall_risk,
            impacted_symbols_count=len(impacted_items),
            impacted_items=impacted_items,
            affected_activities=sorted(list(affected_activities)),
            affected_states=sorted(list(affected_states)),
            broken_transitions=sorted(list(broken_transitions)),
            affected_code_generators=sorted(list(affected_code_generators)),
            breaking_hazards=breaking_hazards,
            recommended_mitigations=mitigations
        )
