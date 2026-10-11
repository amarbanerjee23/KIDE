"""PR102 negative acceptance for hosted storage-mode fencing and staging."""
import importlib.util
import json
from pathlib import Path
import tempfile
import unittest
from unittest.mock import patch

ROOT = Path(__file__).resolve().parents[2]


def load(name, path):
    spec = importlib.util.spec_from_file_location(name, ROOT / path)
    module = importlib.util.module_from_spec(spec)
    spec.loader.exec_module(module)
    return module


guard = load("pr102_guard", "scripts/guard_hosted_storage.py")
stage = load("pr102_stage", "scripts/staging_registry_policy.py")
qualify = load("pr102_qualify", "scripts/qualify_registry_storage.py")


def cloud_service(*, mode="", root="", nfs=False):
    env = [
        {"name": "KIDE_STORAGE_MODE", "value": mode},
        {"name": "KIDE_FIREBASE_PROJECT_ID", "value": "kide-staging"},
        {"name": "KIDE_HOSTED_PROJECTS_ROOT", "value": root},
    ]
    vol = ({"name": "data", "nfs": {"server": "10.1.2.3", "path": "/registry"}}
           if nfs else {"name": "data", "csi": {
               "driver": "gcsfuse.run.googleapis.com"
           }})
    return {"spec": {"template": {"spec": {
        "containers": [{"env": env, "volumeMounts": [
            {"name": "data", "mountPath": "/data"}
        ]}],
        "volumes": [vol],
    }}}}


class HostedStorageGuardTest(unittest.TestCase):
    def test_normal_legacy_backend_remains_deployable(self):
        guard.protect_legacy_service(cloud_service())
        guard.protect_legacy_service(cloud_service(mode="legacy-gcs"))

    def test_guard_rejects_registry_or_nfs_replacement(self):
        for service in (
            cloud_service(mode="registry-posix", root="/data/hosted-projects", nfs=True),
            cloud_service(mode="", root="/data/hosted-projects"),
            cloud_service(nfs=True),
            cloud_service(mode="registry-posix"),
        ):
            with self.subTest(service=service):
                with self.assertRaises(guard.StorageModeError):
                    guard.protect_legacy_service(service)

    def test_existing_staging_update_requires_same_nfs_export_and_mode(self):
        service = cloud_service(
            mode="registry-posix", root="/data/hosted-projects", nfs=True
        )
        stage.verify_existing(service, "10.1.2.3", "/registry", "kide-staging")
        with self.assertRaises(stage.StagingRefusal):
            stage.verify_existing(service, "10.1.2.4", "/registry", "kide-staging")
        with self.assertRaises(stage.StagingRefusal):
            stage.verify_existing(service, "10.1.2.3", "/another", "kide-staging")
        with self.assertRaises(stage.StagingRefusal):
            stage.verify_existing(cloud_service(), "10.1.2.3", "/registry", "kide-staging")
        with self.assertRaises(stage.StagingRefusal):
            stage.verify_existing(service, "10.1.2.3", "/registry", "wrong-tenant")

    def test_staging_requires_separate_service_rfc1918_and_pinned_digest(self):
        image = "asia-south1-docker.pkg.dev/stage/kide/backend@sha256:" + "a" * 64
        stage.validate("kide-registry-staging", "10.1.2.3", "/registry",
                       image, "private-vpc", "private-subnet", "kide-staging")
        for kwargs in (
            {"service": "kide"},
            {"ip": "203.0.113.1"},
            {"ip": "127.0.0.1"},
            {"ip": "169.254.1.1"},
            {"path": "/../prod"},
            {"image": "example.com/kide:latest"},
            {"network": "--dangerous"},
            {"firebase": ""},
        ):
            args = dict(service="kide-registry-staging", ip="10.1.2.3",
                        path="/registry", image=image, network="private-vpc",
                        subnet="private-subnet", firebase="kide-staging")
            args.update(kwargs)
            with self.subTest(kwargs=kwargs), self.assertRaises(stage.StagingRefusal):
                stage.validate(**args)

    def test_atomic_staging_qualification_and_marker(self):
        with tempfile.TemporaryDirectory() as temp:
            root = Path(temp) / "hosted-projects"
            root.mkdir()
            record = qualify.qualify(root, fs_type="ext4")
            self.assertTrue(record["atomicNoReplace"])
            self.assertEqual(
                qualify.SENTINEL + "\n",
                (Path(temp) / qualify.MARKER).read_text()
            )
            self.assertEqual(sorted([root.name, qualify.MARKER]),
                             sorted(p.name for p in Path(temp).iterdir()))

    def test_qualification_rejects_unapproved_filesystems_and_symlinks(self):
        with tempfile.TemporaryDirectory() as temp:
            root = Path(temp) / "hosted-projects"
            root.mkdir()
            for kind in ("fuse.gcsfuse", "overlay", "tmpfs"):
                with self.subTest(kind=kind), self.assertRaises(qualify.StorageRefusal):
                    qualify.qualify(root, fs_type=kind)
            (Path(temp) / qualify.MARKER).symlink_to("nowhere")
            with self.assertRaises(qualify.StorageRefusal):
                qualify.qualify(root, fs_type="ext4")

    def test_regression_guards_are_present_in_all_release_paths(self):
        canonical = (
            "cloudbuild.yaml", "deploy/gcp/cloudbuild.yaml",
            "deploy/gcp/cloudbuild-release.yaml"
        )
        contents = [(ROOT / name).read_text() for name in canonical]
        self.assertEqual(contents[0], contents[1])
        self.assertEqual(contents[0], contents[2])
        for name in (*canonical, "deploy/gcp/cloudbuild-deploy.yaml"):
            content = (ROOT / name).read_text()
            self.assertIn("scripts/guard_hosted_storage.py", content)
            self.assertLess(content.index("scripts/guard_hosted_storage.py"),
                            content.index('gcloud run deploy "${_KIDE_SERVICE_NAME}"'))
        entrypoint = (ROOT / "deploy/gcp/entrypoint.sh").read_text()
        self.assertIn("KIDE-POSIX-REGISTRY-STAGING-v1", entrypoint)
        self.assertIn('if [[ "${storage_mode}" == "legacy-gcs" ]]; then', entrypoint)
        self.assertIn('export KIDE_API_ROLE_BINDINGS="${role_bindings}"', entrypoint)
        stage_script = (ROOT / "deploy/gcp/deploy-registry-staging.sh").read_text()
        self.assertIn("--no-traffic", stage_script)
        self.assertIn("--concurrency 1", stage_script)
        self.assertIn("--max 1", stage_script)
        self.assertIn("type=nfs", stage_script)
        self.assertIn("verify-existing", stage_script)


if __name__ == "__main__":
    unittest.main()
