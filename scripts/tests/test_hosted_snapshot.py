"""PR90: recovery proof, adversarial ZIPs, and regression-safe restore."""
import importlib.util
import json
import pathlib
import stat
import tempfile
import unittest
import zipfile
from unittest.mock import patch

ROOT = pathlib.Path(__file__).resolve().parents[2]
SPEC = importlib.util.spec_from_file_location("kide_hosted_snapshot", ROOT / "scripts/hosted_snapshot.py")
snapshot = importlib.util.module_from_spec(SPEC)
SPEC.loader.exec_module(snapshot)

PROJECT_A = "11111111-1111-4111-8111-111111111111"
PROJECT_B = "22222222-2222-4222-8222-222222222222"
WORKSPACE_A = "33333333-3333-4333-8333-333333333333"
WORKSPACE_B = "44444444-4444-4444-8444-444444444444"


def populate(root, project_id=PROJECT_A, workspace_id=WORKSPACE_A, owner="firebase:test#alice"):
    project = root / project_id / "project"
    workspace = root / project_id / "workspace"
    (project / ".kide").mkdir(parents=True)
    (workspace / ".metadata" / ".plugins" / "com.kide.enterprise.context").mkdir(parents=True)
    project_descriptor = (
        "schema.version=1\n"
        "organization.id=kide:organization:aaaa\n"
        "portfolio.id=kide:portfolio:bbbb\n"
        f"project.id=kide:project:{project_id}\n"
        f"project.meta.hosted.ownerPrincipalId={owner}\n"
    )
    workspace_descriptor = (
        "schema.version=1\n"
        "organization.id=kide:organization:aaaa\n"
        "portfolio.id=kide:portfolio:bbbb\n"
        f"project.id=kide:project:{project_id}\n"
        f"workspace.id=kide:workspace:{workspace_id}\n"
    )
    (project / ".kide" / "enterprise-context.properties").write_text(project_descriptor)
    (workspace / ".metadata" / ".plugins" /
     "com.kide.enterprise.context" / "workspace.properties").write_text(workspace_descriptor)
    (project / "workflow.activity").write_text(
        "ActivityDiagram GoldenWorkflow has activities {}\n")
    (project / ".kide" / "knowledge.json").write_text('{"revision":3}\n')
    (project / ".kide" / "history").mkdir()
    (project / ".kide" / "history" / "audit.log").write_bytes(b"event 1\n")
    return project


class HostedSnapshotTest(unittest.TestCase):
    def setUp(self):
        self.temp = tempfile.TemporaryDirectory()
        self.addCleanup(self.temp.cleanup)
        self.base = pathlib.Path(self.temp.name)
        self.registry = self.base / "registry"
        self.registry.mkdir()
        self.archive = self.base / "recovery.zip"
        self.project = populate(self.registry)

    def test_full_registry_roundtrip_retains_hidden_context_owner_and_history(self):
        populate(self.registry, PROJECT_B, WORKSPACE_B, "firebase:test#bob")
        original_directories, original_files = snapshot.walk_registry(self.registry)
        original_fingerprint = snapshot.fingerprint(original_directories, original_files)
        backup = snapshot.snapshot(self.registry, self.archive)
        self.assertEqual(2, backup["projectCount"])
        self.assertEqual(original_fingerprint, backup["fingerprint"])
        self.assertEqual(0o600, stat.S_IMODE(self.archive.stat().st_mode))
        target = self.base / "restored"
        self.assertEqual(backup, snapshot.restore(self.archive, target))
        self.assertEqual(backup["fingerprint"], snapshot.fingerprint(*snapshot.walk_registry(target)))
        self.assertEqual(
            (self.registry / PROJECT_A / "project" / ".kide" /
             "enterprise-context.properties").read_bytes(),
            (target / PROJECT_A / "project" / ".kide" /
             "enterprise-context.properties").read_bytes(),
        )
        self.assertTrue((target / PROJECT_A / "project" / ".kide" /
                         "history" / "audit.log").is_file())
        self.assertEqual(2, snapshot.verify(self.archive)["projectCount"])

    def test_nonempty_restore_destination_is_never_modified(self):
        snapshot.snapshot(self.registry, self.archive)
        target = self.base / "restored"
        target.mkdir()
        original = target / "customer.txt"
        original.write_text("must survive")
        with self.assertRaisesRegex(snapshot.RecoveryError, "already exists"):
            snapshot.restore(self.archive, target)
        self.assertEqual("must survive", original.read_text())

    def test_restore_malformed_archive_fails_without_publish(self):
        snapshot.snapshot(self.registry, self.archive)
        fake = self.base / "corrupted.zip"
        with zipfile.ZipFile(self.archive) as source, zipfile.ZipFile(fake, "w") as dest:
            for item in source.infolist():
                contents = source.read(item.filename)
                if item.filename.endswith("workflow.activity"):
                    contents += b"corruption"
                dest.writestr(item.filename, contents)
        target = self.base / "restored"
        with self.assertRaises(snapshot.RecoveryError):
            snapshot.restore(fake, target)
        self.assertFalse(target.exists())

    def test_rejects_unsafe_zip_and_unknown_payload_entries(self):
        snapshot.snapshot(self.registry, self.archive)
        fake = self.base / "injected.zip"
        with zipfile.ZipFile(self.archive) as source, zipfile.ZipFile(fake, "w") as dest:
            for entry in source.infolist():
                dest.writestr(entry.filename, source.read(entry.filename))
            dest.writestr("payload/../../outside.txt", b"escape")
        with self.assertRaisesRegex(snapshot.RecoveryError, "unknown"):
            snapshot.verify(fake)
        self.assertFalse((self.base / "outside.txt").exists())

    def test_refuses_symlinks_and_hardlinks(self):
        (self.project / "escape.activity").symlink_to(self.base / "outside.activity")
        with self.assertRaisesRegex(snapshot.RecoveryError, "symbolic link"):
            snapshot.snapshot(self.registry, self.archive)
        (self.project / "escape.activity").unlink()
        (self.project / "copy.activity").hardlink_to(self.project / "workflow.activity")
        with self.assertRaisesRegex(snapshot.RecoveryError, "hard-linked"):
            snapshot.snapshot(self.registry, self.archive)
        self.assertFalse(self.archive.exists())

    def test_refuses_snapshot_overwrite_and_output_under_registry(self):
        snapshot.snapshot(self.registry, self.archive)
        with self.assertRaisesRegex(snapshot.RecoveryError, "already exists"):
            snapshot.snapshot(self.registry, self.archive)
        with self.assertRaisesRegex(snapshot.RecoveryError, "outside"):
            snapshot.snapshot(self.registry, self.project / "snapshot.zip")

    def test_refuses_identity_and_owner_corruption(self):
        project_meta = self.project / ".kide" / "enterprise-context.properties"
        project_meta.write_text(project_meta.read_text().replace(PROJECT_A, PROJECT_B))
        with self.assertRaisesRegex(snapshot.RecoveryError, "identity"):
            snapshot.snapshot(self.registry, self.archive)
        self.assertFalse(self.archive.exists())

    def test_refuses_writer_activity_during_capture(self):
        original = snapshot.walk_registry
        calls = 0

        def changed(root):
            nonlocal calls
            calls += 1
            if calls == 2:
                (self.project / "workflow.activity").write_text("changed by concurrent writer")
            return original(root)

        with patch.object(snapshot, "walk_registry", side_effect=changed):
            with self.assertRaisesRegex(snapshot.RecoveryError, "changed during snapshot"):
                snapshot.snapshot(self.registry, self.archive)
        self.assertFalse(self.archive.exists())

    def test_rejects_duplicate_archived_entries(self):
        snapshot.snapshot(self.registry, self.archive)
        corrupt = self.base / "duplicate.zip"
        with zipfile.ZipFile(self.archive) as source, zipfile.ZipFile(corrupt, "w") as dest:
            for entry in source.infolist():
                dest.writestr(entry.filename, source.read(entry.filename))
            import warnings
            with warnings.catch_warnings():
                warnings.simplefilter("ignore", UserWarning)
                dest.writestr("manifest.json", "{}")
        with self.assertRaisesRegex(snapshot.RecoveryError, "duplicate"):
            snapshot.verify(corrupt)

    def test_refuses_missing_offline_operator_confirmation(self):
        self.assertEqual(2, snapshot.main(
            ["snapshot", "--registry", str(self.registry), "--archive", str(self.archive)]))
        self.assertFalse(self.archive.exists())
        self.assertEqual(0, snapshot.main([
            "snapshot", "--registry", str(self.registry),
            "--archive", str(self.archive), "--offline-confirmed",
        ]))
        self.assertEqual(2, snapshot.main([
            "restore", "--registry", str(self.base / "target"),
            "--archive", str(self.archive),
        ]))
        self.assertFalse((self.base / "target").exists())

    def test_recovery_fingerprint_detects_added_or_removed_project(self):
        before = snapshot.fingerprint(*snapshot.walk_registry(self.registry))
        populate(self.registry, PROJECT_B, WORKSPACE_B)
        after = snapshot.fingerprint(*snapshot.walk_registry(self.registry))
        self.assertNotEqual(before, after)


if __name__ == "__main__":
    unittest.main()
