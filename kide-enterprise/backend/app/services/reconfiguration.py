import re
import difflib
from typing import Any, Dict, List, Optional

from app.schemas.ai import ProposedPatch
from app.schemas.traceability import ReconfigurationRequest, ReconfigurationProposal
from app.services.semantic_validator import validate_project_semantics

class SemanticReconfigurationService:
    """
    Automated Semantic Reconfiguration Engine (KIDE Architecture Specification §5).
    Enables deterministic capability and device substitution across supervisory workflows.
    Generates unified diffs, performs safety verification, and checks that no
    deadlocks or dangling references are introduced.
    """

    @classmethod
    def compute_reconfiguration_plan(
        cls,
        raw_files: List[Any],
        req: ReconfigurationRequest
    ) -> ReconfigurationProposal:
        deprecated = req.deprecated_capability.strip()
        replacement = req.replacement_capability.strip()
        target_acts = set(req.target_activities or [])

        # Normalize raw files to dict format
        normalized_files: List[Dict[str, Any]] = []
        for f in raw_files:
            if isinstance(f, dict):
                normalized_files.append({
                    "id": str(f.get("id", "")),
                    "name": str(f.get("name", "")),
                    "content": str(f.get("content", ""))
                })
            elif hasattr(f, "id") and hasattr(f, "name") and hasattr(f, "content"):
                normalized_files.append({
                    "id": str(f.id),
                    "name": str(f.name),
                    "content": str(f.content)
                })

        patches: List[ProposedPatch] = []
        affected_files: List[str] = []
        simulated_files: List[Dict[str, Any]] = []

        pattern = re.compile(
            rf'(requireCapability\s*:\s*["\']?)' + re.escape(deprecated) + r'(["\']?)',
            re.IGNORECASE
        )

        total_substitutions = 0

        for file_info in normalized_files:
            fname = file_info["name"]
            content = file_info["content"]

            if not fname.endswith(".activity") and not fname.endswith(".smr"):
                simulated_files.append(file_info)
                continue

            # Check if this file contains the deprecated capability
            if not pattern.search(content):
                simulated_files.append(file_info)
                continue

            # If target_activities specified, filter by activity block
            if target_acts:
                # Replace selectively per activity block
                lines = content.splitlines(keepends=True)
                new_lines = []
                in_target_activity = False
                file_subs = 0

                for line in lines:
                    act_match = re.search(r'Activity\s+([A-Za-z0-9_]+)', line)
                    if act_match:
                        current_act = act_match.group(1)
                        in_target_activity = current_act in target_acts

                    if in_target_activity and pattern.search(line):
                        new_line = pattern.sub(rf'\g<1>{replacement}\g<2>', line)
                        new_lines.append(new_line)
                        file_subs += 1
                    else:
                        new_lines.append(line)

                new_content = "".join(new_lines)
            else:
                new_content, file_subs = pattern.subn(rf'\g<1>{replacement}\g<2>', content)

            if file_subs > 0:
                total_substitutions += file_subs
                affected_files.append(fname)

                # Generate unified diff
                diff_lines = list(difflib.unified_diff(
                    content.splitlines(),
                    new_content.splitlines(),
                    fromfile=f"a/{fname}",
                    tofile=f"b/{fname}",
                    lineterm=""
                ))
                diff_text = "\n".join(diff_lines)

                patches.append(ProposedPatch(
                    filename=fname,
                    action="modify",
                    diff=diff_text,
                    new_content=new_content,
                    rationale=f"Reconfigured {file_subs} reference(s) from deprecated capability '{deprecated}' to '{replacement}'"
                ))

                simulated_files.append({
                    "id": file_info["id"],
                    "name": fname,
                    "content": new_content
                })
            else:
                simulated_files.append(file_info)

        if not patches:
            return ReconfigurationProposal(
                status="no_change_needed",
                explanation=f"Deprecated capability '{deprecated}' was not found in any project activity workflows.",
                deprecated_capability=deprecated,
                replacement_capability=replacement,
                affected_files=[],
                patches=[],
                safety_verification={"is_safe": True, "hazards_detected": 0}
            )

        # Invariant and Safety Verification using Deep Semantic Validator
        _, diagnostics = validate_project_semantics(simulated_files)
        errors = [d for d in diagnostics if d.severity.value == "error"]
        warnings = [d for d in diagnostics if d.severity.value == "warning"]

        # Check if the replacement capability triggered unresolved symbol error
        has_unresolved_replacement = any(
            d.rule_id == "SEM-001" and replacement.lower() in (d.symbol or "").lower()
            for d in errors
        )

        is_safe = (len(errors) == 0) and not has_unresolved_replacement

        safety_verification = {
            "is_safe": is_safe,
            "errors_count": len(errors),
            "warnings_count": len(warnings),
            "hazards_detected": len([d for d in errors if "HAZ" in d.rule_id or "FLW" in d.rule_id]),
            "unresolved_replacement": has_unresolved_replacement,
            "reconfiguration_approved": is_safe
        }

        if is_safe:
            status = "success"
            explanation = (
                f"Automated reconfiguration verified: {total_substitutions} reference(s) to '{deprecated}' "
                f"safely migrated to '{replacement}' across {len(affected_files)} file(s). "
                f"Semantic validation passed with 0 errors."
            )
        elif has_unresolved_replacement:
            status = "warning"
            explanation = (
                f"Reconfiguration patches prepared for '{deprecated}' -> '{replacement}', but replacement "
                f"capability '{replacement}' is not yet declared in project or Knowledge Hub. "
                f"Add '{replacement}.cap' or import it to resolve SEM-001."
            )
        else:
            status = "incompatible"
            explanation = (
                f"Reconfiguration introduced {len(errors)} semantic error(s). Review proposed diffs before applying."
            )

        return ReconfigurationProposal(
            status=status,
            explanation=explanation,
            deprecated_capability=deprecated,
            replacement_capability=replacement,
            affected_files=affected_files,
            patches=patches,
            safety_verification=safety_verification
        )
