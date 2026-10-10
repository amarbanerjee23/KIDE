import importlib.util
import pathlib
import unittest
from unittest.mock import patch

ROOT = pathlib.Path(__file__).resolve().parents[2]
SPEC = importlib.util.spec_from_file_location(
    "verify_hosted_recovery", ROOT / "scripts/verify_hosted_recovery.py"
)


def module():
    result = importlib.util.module_from_spec(SPEC)
    SPEC.loader.exec_module(result)
    return result


class RecoveryGovernanceTest(unittest.TestCase):
    def test_core_contract_is_present(self):
        self.assertEqual([], module().verify())

    def test_removing_atomic_no_replace_is_rejected(self):
        checker = module()
        original = checker.read

        def altered(path):
            text = original(path)
            if path == "scripts/hosted_snapshot.py":
                return text.replace("rename_no_replace(", "unsafe_rename(")
            return text

        with patch.object(checker, "read", side_effect=altered):
            self.assertTrue(any("rename_no_replace" in issue for issue in checker.verify()))

    def test_removing_adversarial_recovery_test_is_rejected(self):
        checker = module()
        original = checker.read

        def altered(path):
            text = original(path)
            if path == "scripts/tests/test_hosted_snapshot.py":
                return text.replace("test_refuses_writer_activity_during_capture",
                                    "removed_writer_race_coverage")
            return text

        with patch.object(checker, "read", side_effect=altered):
            self.assertTrue(any("adversarial" in issue for issue in checker.verify()))


if __name__ == "__main__":
    unittest.main()
