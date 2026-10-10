"""PR93 stable publication/retention adversarial regression tests."""
from __future__ import annotations

import pathlib
import sys
import unittest
from unittest.mock import patch

sys.path.insert(0, str(pathlib.Path(__file__).resolve().parents[1]))
from retain_latest_stable_release import (  # noqa: E402
    RetentionError, retention_plan, required_assets, stable_version, run,
)
from verify_stable_build import qualify  # noqa: E402
from check_stable_release_version import check  # noqa: E402

SHA = "a" * 40
TAG = "v1.2.3"


def keeper():
    return {
        "id": 100,
        "tag_name": TAG,
        "target_commitish": SHA,
        "draft": False,
        "prerelease": False,
        "assets": [
            {"name": name, "size": 100, "state": "uploaded"}
            for name in sorted(required_assets(TAG))
        ],
    }


def old_release(release_id=50, tag="branch-build-a"):
    return {
        "id": release_id, "tag_name": tag, "draft": False,
        "prerelease": not tag.startswith("v"),
        "assets": [],
    }


def good_run(name, sha=SHA, status="completed", conclusion="success",
             event="push", branch="main", run_id=5):
    return {
        "id": run_id, "name": name, "head_sha": sha, "head_branch": branch,
        "event": event, "created_at": f"2026-10-10T00:00:{run_id:02d}Z",
        "run_attempt": 1, "status": status, "conclusion": conclusion,
    }


class StableReleaseTest(unittest.TestCase):
    def test_versions_are_strict_stable_semver(self):
        self.assertEqual((1, 2, 3), stable_version(TAG))
        for bad in ("1.2.3", "v1.2.3-rc.1", "v01.2.3", "v1.2", "preview",
                    "branch-build-aa"):
            self.assertIsNone(stable_version(bad))

    def test_clean_twenty_old_previews_only_after_real_stable_exists(self):
        old = [old_release(i, f"branch-build-{i}") for i in range(20)]
        self.assertEqual(old, retention_plan(old + [keeper()], keeper(), TAG, SHA))

    def test_retention_refuses_missing_stable_and_other_forged_keepers(self):
        for changes in (
            {"draft": True}, {"prerelease": True},
            {"tag_name": "branch-build-123"}, {"target_commitish": "b" * 40},
            {"assets": []}, {"id": "invalid"},
        ):
            with self.subTest(changes=changes):
                latest = dict(keeper(), **changes)
                with self.assertRaises(RetentionError):
                    retention_plan([old_release(), latest], latest, TAG, SHA)

    def test_missing_or_empty_any_signed_artifact_stops_pruning(self):
        for name in sorted(required_assets(TAG)):
            latest = keeper()
            for item in latest["assets"]:
                if item["name"] == name:
                    item["size"] = 0
            with self.subTest(asset=name), self.assertRaises(RetentionError):
                retention_plan([latest, old_release()], latest, TAG, SHA)

    def test_refuses_stale_or_newer_stable_version(self):
        for candidate in ("v1.2.3", "v1.2.4", "v2.0.0"):
            with self.subTest(candidate=candidate), self.assertRaises(RetentionError):
                retention_plan([keeper(), old_release(3, candidate)], keeper(), TAG, SHA)
        self.assertEqual([], check(TAG, [old_release(2, "v1.2.4")])[:0])
        self.assertEqual(1, len(check(TAG, [old_release(2, "v1.2.4")])))

    def test_no_pagination_partial_inventory_or_duplicate_ids(self):
        with self.assertRaises(RetentionError):
            retention_plan([old_release()], keeper(), TAG, SHA)
        with self.assertRaises(RetentionError):
            retention_plan([keeper(), old_release(100)], keeper(), TAG, SHA)
        with self.assertRaises(RetentionError):
            retention_plan([keeper(), {"id": "not-int"}], keeper(), TAG, SHA)

    def test_default_is_dry_run_with_zero_delete_requests(self):
        instance = FakeAPI()
        with patch("retain_latest_stable_release.GitHubAPI", return_value=instance):
            self.assertEqual(0, run("owner/repo", "token", TAG, SHA))
        self.assertEqual([], instance.deleted)

    def test_apply_deletes_only_old_release_records_never_git_tags(self):
        instance = FakeAPI()
        with patch("retain_latest_stable_release.GitHubAPI", return_value=instance):
            self.assertEqual(0, run("owner/repo", "token", TAG, SHA, apply=True))
        self.assertEqual(["/releases/2", "/releases/3"], instance.deleted)

    def test_changed_latest_stops_cleanup_mid_run(self):
        instance = FakeAPI(change_latest=True)
        with patch("retain_latest_stable_release.GitHubAPI", return_value=instance):
            with self.assertRaises(RetentionError):
                run("owner/repo", "token", TAG, SHA, apply=True)
        self.assertEqual([], instance.deleted)

    def test_green_ci_must_match_main_head_commit(self):
        runs = [good_run(name) for name in (
            "Build KIDE", "KIDE Enterprise CI Pipeline",
        )]
        self.assertEqual([], qualify(runs, SHA))
        self.assertTrue(qualify([], SHA))
        self.assertTrue(qualify([good_run("Build KIDE"),
                                 good_run("KIDE Enterprise CI Pipeline", event="pull_request")], SHA))
        self.assertTrue(qualify([good_run("Build KIDE"),
                                 good_run("KIDE Enterprise CI Pipeline", sha="b" * 40)], SHA))
        self.assertTrue(qualify([good_run("Build KIDE", branch="feature"),
                                 good_run("KIDE Enterprise CI Pipeline")], SHA))
        self.assertTrue(qualify([good_run("Build KIDE", status="in_progress", conclusion=None),
                                 good_run("KIDE Enterprise CI Pipeline")], SHA))

    def test_latest_failed_ci_retry_blocks_even_if_older_passed(self):
        runs = [good_run("Build KIDE", run_id=4),
                good_run("Build KIDE", run_id=8, conclusion="failure"),
                good_run("KIDE Enterprise CI Pipeline")]
        self.assertTrue(any("Build KIDE" in issue for issue in qualify(runs, SHA)))

    def test_stable_version_must_advance(self):
        self.assertEqual([], check("v1.2.3", [old_release(3, "v1.2.2")]))
        self.assertTrue(check("v1.2.3", [old_release(3, "v1.2.3")]))
        self.assertTrue(check("v1.2.3", [old_release(3, "v2.0.0")]))
        self.assertTrue(check("v1.2.3-rc.1", []))


class FakeAPI:
    def __init__(self, change_latest=False):
        self.deleted = []
        self.change_latest = change_latest
        self.latest_calls = 0

    def latest(self):
        self.latest_calls += 1
        result = keeper()
        if self.change_latest and self.latest_calls > 1:
            result["id"] = 999
        return result

    def releases(self):
        return [keeper(), old_release(2), old_release(3)]

    def request(self, path, method):
        if method == "DELETE":
            self.deleted.append(path)


if __name__ == "__main__":
    unittest.main()
