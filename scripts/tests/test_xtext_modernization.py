import importlib.util
from pathlib import Path
import unittest

ROOT = Path(__file__).resolve().parents[2]
MODULE_PATH = ROOT / "scripts" / "verify_xtext_modernization.py"
SPEC = importlib.util.spec_from_file_location("verify_xtext_modernization", MODULE_PATH)
MODULE = importlib.util.module_from_spec(SPEC)
assert SPEC.loader is not None
SPEC.loader.exec_module(MODULE)


class XtextModernizationContractTest(unittest.TestCase):
    def test_contract_is_satisfied(self):
        MODULE.verify()


if __name__ == "__main__":
    unittest.main()
