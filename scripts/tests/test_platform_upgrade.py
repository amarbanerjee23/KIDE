import importlib.util
from pathlib import Path
import unittest

ROOT = Path(__file__).resolve().parents[2]
MODULE_PATH = ROOT / "scripts" / "verify_platform_upgrade.py"
SPEC = importlib.util.spec_from_file_location("verify_platform_upgrade", MODULE_PATH)
MODULE = importlib.util.module_from_spec(SPEC)
assert SPEC.loader is not None
SPEC.loader.exec_module(MODULE)


class PlatformUpgradeContractTest(unittest.TestCase):
    def test_contract_is_satisfied(self):
        MODULE.verify()


if __name__ == "__main__":
    unittest.main()
