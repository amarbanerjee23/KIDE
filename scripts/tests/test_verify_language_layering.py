import sys
import tempfile
import unittest
from pathlib import Path

SCRIPTS = Path(__file__).resolve().parents[1]
sys.path.insert(0, str(SCRIPTS))

import verify_language_layering as layering


class VerifyLanguageLayeringTest(unittest.TestCase):

    def _module(self, root: Path, name: str, manifest: str, source: str = "") -> Path:
        module = root / name
        (module / "META-INF").mkdir(parents=True)
        (module / "META-INF" / "MANIFEST.MF").write_text(manifest, encoding="utf-8")
        if source:
            source_file = module / "src" / "example" / "Example.java"
            source_file.parent.mkdir(parents=True)
            source_file.write_text(source, encoding="utf-8")
        return module

    def test_valid_runtime_and_ide_layers_pass(self):
        with tempfile.TemporaryDirectory() as temp:
            root = Path(temp)
            self._module(
                root,
                "com.example.dsl",
                "Manifest-Version: 1.0\nRequire-Bundle: org.eclipse.xtext\n",
                "package example;\nimport org.eclipse.xtext.resource.XtextResource;\n",
            )
            self._module(
                root,
                "com.example.dsl.ide",
                "Manifest-Version: 1.0\nRequire-Bundle: com.example.dsl,\n org.eclipse.xtext.ide\n",
                "package example;\nimport org.eclipse.xtext.ide.server.LanguageServerImpl;\n",
            )
            self.assertEqual([], layering.verify(root))

    def test_forbidden_ui_import_is_reported(self):
        with tempfile.TemporaryDirectory() as temp:
            root = Path(temp)
            self._module(
                root,
                "com.example.dsl.ide",
                "Manifest-Version: 1.0\nRequire-Bundle: org.eclipse.xtext.ide\n",
                "package example;\nimport org.eclipse.swt.widgets.Display;\n",
            )
            violations = layering.verify(root)
            self.assertTrue(any("org.eclipse.swt.widgets.Display" in item for item in violations))

    def test_forbidden_ui_bundle_is_reported(self):
        with tempfile.TemporaryDirectory() as temp:
            root = Path(temp)
            self._module(
                root,
                "com.example.dsl.ide",
                "Manifest-Version: 1.0\nRequire-Bundle: com.example.dsl,\n org.eclipse.xtext.ui\n",
            )
            violations = layering.verify(root)
            self.assertTrue(any("org.eclipse.xtext.ui" in item for item in violations))

    def test_runtime_must_not_depend_on_ide_layer(self):
        with tempfile.TemporaryDirectory() as temp:
            root = Path(temp)
            self._module(
                root,
                "com.example.dsl",
                "Manifest-Version: 1.0\nRequire-Bundle: com.example.dsl.ide\n",
            )
            violations = layering.verify(root)
            self.assertTrue(any("must not depend on IDE bundle" in item for item in violations))


if __name__ == "__main__":
    unittest.main()
