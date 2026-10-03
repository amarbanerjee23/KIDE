from __future__ import annotations

import json
import subprocess
import sys
import unittest
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]


class DiagramParityContractTest(unittest.TestCase):
    def test_parity_verifier_accepts_repository_contract(self) -> None:
        result = subprocess.run(
            [sys.executable, str(ROOT / "scripts" / "verify_diagram_parity.py")],
            cwd=ROOT,
            text=True,
            stdout=subprocess.PIPE,
            stderr=subprocess.STDOUT,
            check=False,
        )
        self.assertEqual(0, result.returncode, result.stdout)
        self.assertIn("GLSP/Sirius semantic parity verified", result.stdout)

    def test_mnc_contract_includes_desktop_state_diagram_semantics(self) -> None:
        data = json.loads(
            (ROOT / "product" / "diagram-semantics.json").read_text(encoding="utf-8")
        )
        mnc = next(diagram for diagram in data["diagrams"] if diagram["id"] == "kide-mnc-diagram")
        concepts = {concept["id"]: concept for concept in mnc["concepts"]}
        self.assertEqual(
            "mncModel.OperatingState",
            concepts["kide:mnc-operating-state"]["emf_class"],
        )
        self.assertEqual(
            "OperatingState",
            concepts["kide:mnc-operating-state"]["sirius_mapping"],
        )
        self.assertEqual(
            "mncModel.Transition",
            concepts["kide:mnc-state-transition"]["emf_class"],
        )
        self.assertEqual(
            "StateTransition",
            concepts["kide:mnc-state-transition"]["sirius_mapping"],
        )

    def test_browser_semantic_contract_is_server_owned(self) -> None:
        data = json.loads(
            (ROOT / "product" / "diagram-semantics.json").read_text(encoding="utf-8")
        )
        self.assertIn(
            "Browser code renders GLSP GModel and sends protocol actions; it does not implement Activity or MNC model semantics.",
            data["invariants"],
        )


if __name__ == "__main__":
    unittest.main()
