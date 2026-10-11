#!/usr/bin/env python3
"""PR102: offline atomic publish qualification for an isolated POSIX registry.

Run from a privileged *staging maintenance host* on the real NFS/POSIX mount.
Never run qualification against production or a live writer. It creates only
a hidden temporary sibling, then publishes an approval marker after checks.
"""
from __future__ import annotations

import argparse
import ctypes
import json
import os
from pathlib import Path
import shutil
import stat
import sys
import tempfile

SENTINEL = "KIDE-POSIX-REGISTRY-STAGING-v1"
MARKER = ".kide-registry-staging-qualified"
ALLOWED_FSTYPES = {"nfs", "nfs4", "ext4", "xfs", "btrfs"}
RENAME_NOREPLACE = 1


class StorageRefusal(ValueError):
    pass


def mount_type(path: Path, mountinfo: str | None = None) -> str:
    """Find the *actual* containing filesystem, never guess from path naming."""
    if mountinfo is None:
        mountinfo = Path("/proc/self/mountinfo").read_text(encoding="utf-8")
    resolved = path.resolve(strict=True)
    best = (-1, "")
    for line in mountinfo.splitlines():
        parts = line.split(" - ", 1)
        if len(parts) != 2:
            continue
        columns = parts[0].split()
        right = parts[1].split()
        if len(columns) < 5 or not right:
            continue
        point = Path(columns[4].replace("\\040", " "))
        try:
            resolved.relative_to(point)
        except ValueError:
            continue
        if len(point.parts) > best[0]:
            best = (len(point.parts), right[0])
    if not best[1]:
        raise StorageRefusal("Cannot determine filesystem type")
    return best[1]


def path_guard(root: Path) -> Path:
    # Do not resolve first: otherwise a symlink can bypass validation.
    if root.name != "hosted-projects" or not root.is_absolute():
        raise StorageRefusal("Registry must be an absolute hosted-projects directory")
    parent = root.parent
    if not root.exists() or not parent.exists() or root.is_symlink() or parent.is_symlink():
        raise StorageRefusal("Registry and parent must exist and cannot be symlinks")
    if not root.is_dir() or not parent.is_dir():
        raise StorageRefusal("Registry and parent must be directories")
    if root.stat().st_dev != parent.stat().st_dev:
        raise StorageRefusal("Registry must share the exact mounted filesystem with its parent")
    return parent


def rename_noreplace(source: Path, target: Path) -> None:
    libc = ctypes.CDLL(None, use_errno=True)
    call = getattr(libc, "renameat2", None)
    if call is None:
        raise StorageRefusal("Linux renameat2(RENAME_NOREPLACE) is unavailable")
    call.argtypes = [ctypes.c_int, ctypes.c_char_p, ctypes.c_int, ctypes.c_char_p, ctypes.c_uint]
    call.restype = ctypes.c_int
    result = call(-100, os.fsencode(source), -100, os.fsencode(target), RENAME_NOREPLACE)
    if result != 0:
        number = ctypes.get_errno()
        raise OSError(number, os.strerror(number))


def qualify(root: Path, fs_type: str | None = None) -> dict:
    parent = path_guard(root)
    kind = fs_type if fs_type is not None else mount_type(parent)
    if kind not in ALLOWED_FSTYPES:
        raise StorageRefusal("Unsupported registry filesystem type; GCS FUSE/overlay/tmpfs refused")
    if not os.access(root, os.R_OK | os.W_OK | os.X_OK):
        raise StorageRefusal("Registry is not accessible for the current operator")
    # Explicitly test an atomic rename and the no-replace requirement with
    # 2 distinct directories on the *same* filesystem as the registry.
    with tempfile.TemporaryDirectory(prefix=".kide-qualify-", dir=parent) as temp:
        base = Path(temp)
        source = base / "draft"
        target = base / "published"
        source.mkdir(mode=0o700)
        payload = source / "id"
        with payload.open("xb") as stream:
            stream.write(b"kide-registry-atomic-qualification")
            stream.flush()
            os.fsync(stream.fileno())
        fd = os.open(source, os.O_RDONLY | os.O_DIRECTORY)
        try:
            os.fsync(fd)
        finally:
            os.close(fd)
        rename_noreplace(source, target)
        if source.exists() or (target / "id").read_bytes() != b"kide-registry-atomic-qualification":
            raise StorageRefusal("Atomic rename did not preserve directory content")
        # It must refuse an existing destination instead of replacing it.
        source.mkdir(mode=0o700)
        try:
            rename_noreplace(source, target)
        except OSError as exc:
            if exc.errno != 17:
                raise StorageRefusal("No-replace behavior is not supported") from exc
        else:
            raise StorageRefusal("No-replace rename unexpectedly replaced an existing directory")
    # The staging operator still must qualify application write/restart
    # behavior independently; atomic rename alone is not a DR guarantee.
    marker = parent / MARKER
    if marker.exists() and (marker.is_symlink() or not marker.is_file()):
        raise StorageRefusal("Qualification marker path has an unsafe type")
    fd, name = tempfile.mkstemp(prefix=".kide-marker-", dir=parent)
    try:
        with os.fdopen(fd, "w", encoding="utf-8") as output:
            os.fchmod(output.fileno(), stat.S_IRUSR | stat.S_IWUSR)
            output.write(SENTINEL + "\n")
            output.flush()
            os.fsync(output.fileno())
        os.replace(name, marker)
        parent_fd = os.open(parent, os.O_RDONLY | os.O_DIRECTORY)
        try:
            os.fsync(parent_fd)
        finally:
            os.close(parent_fd)
    finally:
        if os.path.exists(name):
            os.unlink(name)
    return {"status": "QUALIFIED_FOR_ISOLATED_STAGING", "filesystem": kind,
            "atomicNoReplace": True, "root": str(root), "marker": str(marker)}


def main(argv: list[str] | None = None) -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--root", required=True)
    parser.add_argument("--approval", required=True, help="must be staging-only-offline")
    args = parser.parse_args(argv)
    try:
        if args.approval != "staging-only-offline":
            raise StorageRefusal("Explicit isolated offline staging approval is required")
        result = qualify(Path(args.root))
        print(json.dumps(result, sort_keys=True))
        return 0
    except (StorageRefusal, OSError) as exc:
        print("Registry storage qualification refused: " + str(exc), file=sys.stderr)
        return 2


if __name__ == "__main__":
    raise SystemExit(main())
