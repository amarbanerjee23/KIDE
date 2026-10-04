import importlib.util
import tempfile
import unittest
from pathlib import Path
from unittest.mock import patch

ROOT = Path(__file__).resolve().parents[2]
SCRIPT = ROOT / "scripts" / "verify_product_journey.py"


def load_module():
    spec = importlib.util.spec_from_file_location("verify_product_journey", SCRIPT)
    module = importlib.util.module_from_spec(spec)
    assert spec.loader is not None
    spec.loader.exec_module(module)
    return module


class VerifyProductJourneyTests(unittest.TestCase):
    def test_repository_contract_is_currently_valid(self):
        verifier = load_module()
        self.assertEqual(verifier.main(), 0)

    def test_missing_required_contract_fails_closed(self):
        verifier = load_module()
        with tempfile.TemporaryDirectory() as directory:
            root = Path(directory)
            (root / "web").mkdir()
            with patch.object(verifier, "ROOT", root):
                with self.assertRaises(verifier.VerificationFailure):
                    verifier.require("web/e2e/golden-journey.spec.ts", "Synthesize")


if __name__ == "__main__":
    unittest.main()
