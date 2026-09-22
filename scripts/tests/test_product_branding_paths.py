from pathlib import Path
import xml.etree.ElementTree as ET
import unittest

ROOT = Path(__file__).resolve().parents[2]
PRODUCT = ROOT / "releng/com.kide.repository/kide.product"


class ProductBrandingPathTest(unittest.TestCase):
    def test_launcher_branding_paths_resolve_from_product_file(self):
        tree = ET.parse(PRODUCT)
        root = tree.getroot()
        launcher = root.find("launcher")
        self.assertIsNotNone(launcher)

        paths = [
            launcher.find("linux").attrib["icon"],
            launcher.find("macosx").attrib["icon"],
            launcher.find("win/ico").attrib["path"],
        ]

        for raw in paths:
            self.assertFalse(
                raw.startswith("/"),
                msg=f"launcher branding path must be product-relative: {raw}",
            )
            resolved = (PRODUCT.parent / raw).resolve()
            self.assertTrue(
                resolved.is_file(),
                msg=f"launcher branding asset does not resolve: {raw} -> {resolved}",
            )


if __name__ == "__main__":
    unittest.main()
