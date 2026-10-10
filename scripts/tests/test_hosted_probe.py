import importlib.util
import pathlib
import unittest
from unittest.mock import patch

ROOT = pathlib.Path(__file__).resolve().parents[2]
spec = importlib.util.spec_from_file_location("kide_hosted_probe", ROOT / "scripts/hosted_probe.py")
probe = importlib.util.module_from_spec(spec)
spec.loader.exec_module(probe)


class HostedProbeTest(unittest.TestCase):
    def test_coherent_healthy_deployment(self):
        with (patch.object(probe, "get_json", side_effect=[
            ({"status": "UP"}, 31),
            ({"buildId": "staging-sha"}, 24),
            ({"component": "web", "buildId": "staging-sha"}, 18),
        ]), patch.object(probe, "check_web_health", return_value=22)):
            result = probe.check("https://api-staging.example.test",
                                 "https://web-staging.example.test")
        self.assertEqual("PASS", result["status"])
        self.assertEqual(31, result["maxLatencyMs"])

    def test_incoherent_deployment_fails_closed(self):
        with (patch.object(probe, "get_json", side_effect=[
            ({"status": "UP"}, 11),
            ({"buildId": "api-build"}, 12),
            ({"component": "web", "buildId": "stale-web"}, 12),
        ]), patch.object(probe, "check_web_health", return_value=10)):
            with self.assertRaisesRegex(probe.ProbeFailure, "disagree"):
                probe.check("https://api-staging.example", "https://web-staging.example")

    def test_probe_reports_failed_latency(self):
        with (patch.object(probe, "get_json", side_effect=[
            ({"status": "UP"}, 11),
            ({"buildId": "same"}, 6001),
            ({"component": "web", "buildId": "same"}, 11),
        ]), patch.object(probe, "check_web_health", return_value=10)):
            with self.assertRaisesRegex(probe.ProbeFailure, "latency"):
                probe.check("https://api-staging.example", "https://web-staging.example")

    def test_never_allow_credentials_or_insecure_remote_origins(self):
        invalid = [
            "http://api.example", "https://a.example/path",
            "https://a.example?token=secret", "https://bob:password@api.example",
            "file:///etc/passwd", "https://a.example/#fragment",
        ]
        for item in invalid:
            with self.subTest(item=item):
                with self.assertRaises(probe.ProbeFailure):
                    probe.origin(item)

    def test_empty_or_wrong_health_never_presents_success(self):
        with (patch.object(probe, "get_json", side_effect=[
            ({"status": "DOWN"}, 11),
            ({"buildId": "same"}, 12),
            ({"component": "web", "buildId": "same"}, 11),
        ]), patch.object(probe, "check_web_health", return_value=10)):
            with self.assertRaisesRegex(probe.ProbeFailure, "unhealthy"):
                probe.check("https://api-staging.example", "https://web-staging.example")

    def test_probe_fails_invalid_thresholds_without_network(self):
        self.assertEqual(2, probe.main(["--api", "https://api.example",
                                         "--web", "https://web.example",
                                         "--max-latency-ms", "0"]))


if __name__ == "__main__":
    unittest.main()
