from __future__ import annotations

import tempfile
import unittest
from pathlib import Path

from scripts.verify_workflow_pins import verify


class WorkflowPinTests(unittest.TestCase):
    def test_accepts_full_commit_sha_and_local_action(self) -> None:
        with tempfile.TemporaryDirectory() as directory:
            root = Path(directory)
            workflows = root / ".github" / "workflows"
            workflows.mkdir(parents=True)
            workflows.joinpath("ci.yml").write_text(
                "steps:\n"
                "  - uses: actions/checkout@0123456789abcdef0123456789abcdef01234567 # v7\n"
                "  - uses: ./some-local-action\n",
                encoding="utf-8",
            )
            self.assertEqual([], verify(root))

    def test_rejects_mutable_major_tag(self) -> None:
        with tempfile.TemporaryDirectory() as directory:
            root = Path(directory)
            workflows = root / ".github" / "workflows"
            workflows.mkdir(parents=True)
            workflows.joinpath("ci.yaml").write_text(
                "steps:\n  - uses: actions/checkout@v7\n",
                encoding="utf-8",
            )
            errors = verify(root)
            self.assertEqual(1, len(errors))
            self.assertIn("40-character commit SHA", errors[0])


if __name__ == "__main__":
    unittest.main()
