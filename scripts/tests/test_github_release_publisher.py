from pathlib import Path
import importlib.util
import tempfile
import unittest


ROOT = Path(__file__).resolve().parents[2]
PUBLISHER = ROOT / "scripts" / "publish_github_release.py"


def load_module():
    spec = importlib.util.spec_from_file_location("kide_release_publisher", PUBLISHER)
    module = importlib.util.module_from_spec(spec)
    assert spec.loader is not None
    spec.loader.exec_module(module)
    return module


class GitHubReleasePublisherTest(unittest.TestCase):
    def test_script_compiles(self):
        source = PUBLISHER.read_text(encoding="utf-8")
        compile(source, str(PUBLISHER), "exec")

    def test_collect_assets_expands_globs_and_deduplicates(self):
        module = load_module()
        with tempfile.TemporaryDirectory() as temp_dir:
            root = Path(temp_dir)
            first = root / "a.zip"
            second = root / "b.tar.gz"
            first.write_bytes(b"a")
            second.write_bytes(b"b")

            assets = module.collect_assets(
                [
                    str(root / "*.zip"),
                    str(root / "*.tar.gz"),
                    str(first),
                ]
            )

            self.assertEqual([path.name for path in assets], ["a.zip", "b.tar.gz"])

    def test_collect_assets_rejects_missing_glob(self):
        module = load_module()
        with self.assertRaises(FileNotFoundError):
            module.collect_assets(["/definitely/not/here/*.zip"])

    def test_token_file_is_removed_immediately_after_read(self):
        module = load_module()
        with tempfile.TemporaryDirectory() as temp_dir:
            token_file = Path(temp_dir) / "token"
            token_file.write_text("secret-token\n", encoding="utf-8")

            token = module.load_token_and_remove(token_file)

            self.assertEqual(token, "secret-token")
            self.assertFalse(token_file.exists())


if __name__ == "__main__":
    unittest.main()
