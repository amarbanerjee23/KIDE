import tempfile
import unittest
from pathlib import Path
from unittest.mock import patch

import verify_product_journey as verifier


class VerifyProductJourneyTests(unittest.TestCase):
    def test_repository_contract_is_currently_valid(self):
        self.assertEqual(verifier.main(), 0)

    def test_missing_required_contract_fails_closed(self):
        with tempfile.TemporaryDirectory() as directory:
            root = Path(directory)
            (root / "web").mkdir()
            with patch.object(verifier, "ROOT", root):
                with self.assertRaises(verifier.VerificationFailure):
                    verifier.require("web/e2e/golden-journey.spec.ts", "Synthesize")


if __name__ == "__main__":
    unittest.main()
