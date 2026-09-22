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
