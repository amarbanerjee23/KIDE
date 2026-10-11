"""PR104 negative CI mutation coverage: never silently lose the write-integrity gates."""
from __future__ import annotations

import importlib.util
from pathlib import Path
import unittest
from unittest.mock import patch

ROOT = Path(__file__).resolve().parents[2]
SPEC = importlib.util.spec_from_file_location(
    "pr104_governance", ROOT / "scripts/verify_pr104_durability.py"
)
gate = importlib.util.module_from_spec(SPEC)
SPEC.loader.exec_module(gate)


class DurabilityGovernanceTest(unittest.TestCase):
    def test_full_qualified_repository_surface(self):
        self.assertEqual([], gate.verify())

    def test_browser_stale_overwrite_regression_fails(self):
        original = gate.read

        def replace(path):
            value = original(path)
            if path == "web/src/autosave.ts":
                return value.replace(
                    "if (this.pending === undefined) this.pending = content;",
                    "this.pending = content;"
                )
            return value

        with patch.object(gate, "read", side_effect=replace):
            self.assertTrue(any("autosave" in error for error in gate.verify()))

    def test_absent_cross_process_writer_guard_fails(self):
        original = gate.read

        def replace(path):
            value = original(path)
            if path.endswith("/FileModelRepository.java"):
                return value.replace("Files.createDirectory(writerGuardDir)",
                                     "Files.createDirectories(writerGuardDir)")
            return value

        with patch.object(gate, "read", side_effect=replace):
            self.assertTrue(any("write fence" in error for error in gate.verify()))

    def test_backup_containing_writer_guard_fails(self):
        original = gate.read

        def replace(path):
            value = original(path)
            if path == "scripts/hosted_snapshot.py":
                return value.replace('components[4] == "writer.guard"',
                                     'components[4] == "not-a-guard"')
            return value

        with patch.object(gate, "read", side_effect=replace):
            self.assertTrue(any("snapshot" in error for error in gate.verify()))

    def test_mocked_staging_soak_fails(self):
        original = gate.read

        def replace(path):
            value = original(path)
            if path == "web/e2e/live-hosted.spec.ts":
                return value + "\nawait page.route('**/api', fake);"
            return value

        with patch.object(gate, "read", side_effect=replace):
            self.assertTrue(any("mocks" in error for error in gate.verify()))


if __name__ == "__main__":
    unittest.main()
