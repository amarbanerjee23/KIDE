#!/usr/bin/env python3
"""Offline KIDE hosted registry snapshots and fail-closed restore.

This is an operator tool, not an online consistent-backup service. Stop all
REST/LSP/GLSP writers before snapshot and before restore. Never use a live
Cloud Storage FUSE mount without separately qualifying atomic rename.
"""
from __future__ import annotations

import argparse
import ctypes
import datetime as dt
import hashlib
import json
import os
import pathlib
import re
import shutil
import stat
import sys
import tempfile
import zipfile

SCHEMA = 1
MANIFEST = "manifest.json"
PREFIX = "payload/"
UUID = re.compile(r"[0-9a-f]{8}-(?:[0-9a-f]{4}-){3}[0-9a-f]{12}\Z")
PROJECT_ID = re.compile(r"kide:project:([0-9a-f]{8}-(?:[0-9a-f]{4}-){3}[0-9a-f]{12})\Z")
WORKSPACE_ID = re.compile(r"kide:workspace:[0-9a-f]{8}-(?:[0-9a-f]{4}-){3}[0-9a-f]{12}\Z")
PROJECT_DESCRIPTOR = "project/.kide/enterprise-context.properties"
WORKSPACE_DESCRIPTOR = "workspace/.metadata/.plugins/com.kide.enterprise.context/workspace.properties"
MAX_FILES = 50_000
MAX_BYTES = 256 * 1024 * 1024
MAX_FILE_BYTES = 32 * 1024 * 1024
MAX_MANIFEST_BYTES = 32 * 1024 * 1024


class RecoveryError(ValueError):
    pass


def sha(data: bytes) -> str:
    return hashlib.sha256(data).hexdigest()


def safe_relative(name: str) -> bool:
    if not isinstance(name, str) or not name or "\\" in name or "\x00" in name:
        return False
    parts = name.split("/")
    return all(part not in ("", ".", "..") and not any(ord(c) < 32 for c in part)
               for part in parts)


def descriptor(data: bytes) -> dict[str, str]:
    try:
        text = data.decode("utf-8")
    except UnicodeDecodeError as exc:
        raise RecoveryError("enterprise descriptor must be UTF-8") from exc
    result: dict[str, str] = {}
    for line in text.splitlines():
        line = line.strip()
        if not line or line.startswith(("#", "!")):
            continue
        if "=" not in line:
            raise RecoveryError("invalid enterprise context descriptor")
        key, value = line.split("=", 1)
        if key in result:
            raise RecoveryError("duplicate enterprise context descriptor key")
        result[key] = value
    return result


def validate_contexts(files: dict[str, bytes], directories: set[str]) -> int:
    slots = {name.split("/", 1)[0] for name in files}
    all_roots = {name.split("/", 1)[0] for name in files} | {
        name.split("/", 1)[0] for name in directories
    }
    if not all_roots.issubset(slots):
        raise RecoveryError("registry has unregistered directory roots")
    if not slots:
        raise RecoveryError("empty registry is not a recovery-qualified snapshot")
    if len(slots) > 512:
        raise RecoveryError("registry exceeds 512 project slots")
    workspace_ids: set[str] = set()
    for slot in slots:
        if not UUID.fullmatch(slot):
            raise RecoveryError("registry contains a non-canonical project slot")
        if slot + "/project" not in directories or slot + "/workspace" not in directories:
            raise RecoveryError("registry slot is missing a project/workspace root")
        project_file = slot + "/" + PROJECT_DESCRIPTOR
        workspace_file = slot + "/" + WORKSPACE_DESCRIPTOR
        if project_file not in files or workspace_file not in files:
            raise RecoveryError("registry context descriptors are missing")
        project = descriptor(files[project_file])
        workspace = descriptor(files[workspace_file])
        identifier = project.get("project.id", "")
        match = PROJECT_ID.fullmatch(identifier)
        if not match or match.group(1) != slot:
            raise RecoveryError("project descriptor and registry slot identity disagree")
        if workspace.get("project.id") != identifier:
            raise RecoveryError("workspace project binding differs from restored project")
        workspace_id = workspace.get("workspace.id", "")
        if not WORKSPACE_ID.fullmatch(workspace_id):
            raise RecoveryError("workspace identity is invalid")
        if workspace_id in workspace_ids:
            raise RecoveryError("registry contains duplicate workspace identities")
        workspace_ids.add(workspace_id)
        for key in ("organization.id", "portfolio.id"):
            if not project.get(key) or project[key] != workspace.get(key):
                raise RecoveryError("enterprise context ancestry is inconsistent")
    return len(slots)


def walk_registry(root: pathlib.Path) -> tuple[list[str], dict[str, bytes]]:
    if root.is_symlink() or not root.is_dir():
        raise RecoveryError("registry root must be an existing real directory")
    directories: list[str] = []
    files: dict[str, bytes] = {}
    total = 0
    for current, dirnames, filenames in os.walk(root, topdown=True, followlinks=False):
        dirnames.sort()
        filenames.sort()
        parent = pathlib.Path(current)
        for name in dirnames + filenames:
            target = parent / name
            relative = target.relative_to(root).as_posix()
            if not safe_relative(relative):
                raise RecoveryError("unsafe registry path")
            mode = target.lstat().st_mode
            if stat.S_ISLNK(mode):
                raise RecoveryError("registry contains a symbolic link")
            if name in dirnames:
                if not stat.S_ISDIR(mode):
                    raise RecoveryError("registry contains a non-directory entry")
                directories.append(relative)
                continue
            if not stat.S_ISREG(mode) or target.stat().st_nlink != 1:
                raise RecoveryError("registry contains a non-regular or hard-linked file")
            if target.stat().st_size > MAX_FILE_BYTES:
                raise RecoveryError("registry contains a file exceeding the recovery bound")
            # O_NOFOLLOW prevents an attacker swapping a link between lstat and open.
            fd = os.open(target, os.O_RDONLY | getattr(os, "O_NOFOLLOW", 0))
            try:
                with os.fdopen(fd, "rb") as reader:
                    data = reader.read(MAX_FILE_BYTES + 1)
            except BaseException:
                raise
            if len(data) > MAX_FILE_BYTES:
                raise RecoveryError("registry contains a file exceeding the recovery bound")
            files[relative] = data
            total += len(data)
            if len(files) > MAX_FILES or total > MAX_BYTES:
                raise RecoveryError("registry exceeds the bounded snapshot capacity")
    validate_contexts(files, set(directories))
    return sorted(directories), files


def inventory(directories: list[str], files: dict[str, bytes]) -> dict:
    return {
        "schemaVersion": SCHEMA,
        "createdAt": dt.datetime.now(dt.timezone.utc).isoformat(),
        "projectCount": validate_contexts(files, set(directories)),
        "directories": sorted(directories),
        "files": [
            {"path": name, "bytes": len(data), "sha256": sha(data)}
            for name, data in sorted(files.items())
        ],
    }


def fingerprint(directories: list[str], files: dict[str, bytes]) -> str:
    data = {
        "directories": directories,
        "files": [{"path": name, "sha256": sha(value), "bytes": len(value)}
                  for name, value in sorted(files.items())],
    }
    return sha(json.dumps(data, sort_keys=True, separators=(",", ":")).encode())


def parent_safe(path: pathlib.Path) -> None:
    candidate = path.absolute()
    if candidate.is_symlink():
        raise RecoveryError("snapshot or restore path contains a symbolic link")
    for part in (candidate.parent, *candidate.parent.parents):
        if part.is_symlink():
            raise RecoveryError("snapshot or restore parent contains a symbolic link")
    if not candidate.parent.is_dir():
        raise RecoveryError("destination parent must already exist")


def rename_no_replace(source: pathlib.Path, target: pathlib.Path) -> None:
    """Linux renameat2(RENAME_NOREPLACE): atomic publish without replacement.

    We never downgrade to os.rename (which can replace an existing empty
    directory when another process creates it after our preflight).
    """
    if sys.platform != "linux":
        raise RecoveryError("atomic no-replace restore requires Linux renameat2")
    libc = ctypes.CDLL(None, use_errno=True)
    operation = getattr(libc, "renameat2", None)
    if operation is None:
        raise RecoveryError("filesystem cannot provide atomic no-replace restore")
    operation.argtypes = [ctypes.c_int, ctypes.c_char_p,
                          ctypes.c_int, ctypes.c_char_p, ctypes.c_uint]
    operation.restype = ctypes.c_int
    result = operation(-100, os.fsencode(source), -100, os.fsencode(target), 1)
    if result:
        err = ctypes.get_errno()
        raise RecoveryError("atomic no-replace restore failed: " + os.strerror(err))


def snapshot(root: pathlib.Path, output: pathlib.Path) -> dict:
    root, output = root.absolute(), output.absolute()
    parent_safe(output)
    if output.exists() or output.is_symlink():
        raise RecoveryError("snapshot output already exists; refusing overwrite")
    if output == root or root in output.parents or output in root.parents:
        raise RecoveryError("snapshot output must be outside the registry tree")
    directories, files = walk_registry(root)
    manifest = inventory(directories, files)
    before = fingerprint(directories, files)
    fd, temporary = tempfile.mkstemp(prefix=".kide-backup-", suffix=".zip", dir=output.parent)
    os.fchmod(fd, 0o600)
    try:
        with os.fdopen(fd, "wb") as writer:
            with zipfile.ZipFile(writer, "w", compression=zipfile.ZIP_DEFLATED,
                                 compresslevel=6) as archive:
                archive.writestr(MANIFEST, json.dumps(manifest, sort_keys=True))
                for name, data in sorted(files.items()):
                    archive.writestr(PREFIX + name, data)
            writer.flush()
            os.fsync(writer.fileno())
        # Detect source modifications during capture, including file additions.
        directories_after, files_after = walk_registry(root)
        if before != fingerprint(directories_after, files_after):
            raise RecoveryError("registry changed during snapshot; stop all writers")
        summary = verify(pathlib.Path(temporary))
        # Atomic, no-clobber publication; backup destination must support hard links.
        os.link(temporary, output, follow_symlinks=False)
        return summary
    finally:
        pathlib.Path(temporary).unlink(missing_ok=True)


def verify(archive_path: pathlib.Path) -> dict:
    if archive_path.is_symlink() or not archive_path.is_file():
        raise RecoveryError("snapshot must be an existing regular file")
    with zipfile.ZipFile(archive_path, "r") as archive:
        infos = archive.infolist()
        names = [info.filename for info in infos]
        if len(names) != len(set(names)) or MANIFEST not in names:
            raise RecoveryError("duplicate or missing archive manifest entry")
        info = archive.getinfo(MANIFEST)
        if info.file_size > MAX_MANIFEST_BYTES:
            raise RecoveryError("snapshot manifest exceeds limit")
        try:
            manifest = json.loads(archive.read(MANIFEST))
        except (ValueError, UnicodeDecodeError) as exc:
            raise RecoveryError("invalid snapshot manifest") from exc
        if not isinstance(manifest, dict) or manifest.get("schemaVersion") != SCHEMA:
            raise RecoveryError("unsupported snapshot schema version")
        dirs = manifest.get("directories")
        entries = manifest.get("files")
        if not isinstance(dirs, list) or not isinstance(entries, list):
            raise RecoveryError("invalid snapshot inventory")
        if not all(safe_relative(name) for name in dirs) or len(set(dirs)) != len(dirs):
            raise RecoveryError("unsafe or duplicate registry directories")
        if len(entries) > MAX_FILES:
            raise RecoveryError("snapshot file count exceeds bound")
        files: dict[str, bytes] = {}
        total = 0
        for item in entries:
            if not isinstance(item, dict):
                raise RecoveryError("invalid snapshot file metadata")
            name, size, digest = item.get("path"), item.get("bytes"), item.get("sha256")
            if (not safe_relative(name) or name in files
                    or type(size) is not int or size < 0 or size > MAX_FILE_BYTES
                    or not isinstance(digest, str) or not re.fullmatch("[0-9a-f]{64}", digest)):
                raise RecoveryError("invalid file inventory record")
            stored = archive.getinfo(PREFIX + name) if PREFIX + name in names else None
            if stored is None or stored.is_dir() or stored.file_size != size:
                raise RecoveryError("snapshot file is missing or has the wrong size")
            mode = (stored.external_attr >> 16) & 0o170000
            if mode not in (0, stat.S_IFREG):
                raise RecoveryError("snapshot contains a non-regular file")
            data = archive.read(stored)
            if len(data) != size or sha(data) != digest:
                raise RecoveryError("snapshot file checksum mismatch")
            files[name] = data
            total += size
            if total > MAX_BYTES:
                raise RecoveryError("snapshot exceeds maximum uncompressed bytes")
        if set(names) != {MANIFEST} | {PREFIX + name for name in files}:
            raise RecoveryError("snapshot contains unknown entries or unsafe paths")
        project_count = validate_contexts(files, set(dirs))
        if manifest.get("projectCount") != project_count:
            raise RecoveryError("snapshot project count does not match contexts")
        return {
            "schemaVersion": SCHEMA, "projectCount": project_count,
            "fileCount": len(files), "bytes": total,
            "fingerprint": fingerprint(sorted(dirs), files),
        }


def restore(archive_path: pathlib.Path, target: pathlib.Path) -> dict:
    target = target.absolute()
    parent_safe(target)
    if target.exists() or target.is_symlink():
        raise RecoveryError("restore target already exists; refusing to overwrite any data")
    if archive_path.absolute() == target or target in archive_path.absolute().parents:
        raise RecoveryError("restore target conflicts with source archive")
    summary = verify(archive_path)
    staging = pathlib.Path(tempfile.mkdtemp(prefix=".kide-restore-", dir=target.parent))
    try:
        with zipfile.ZipFile(archive_path, "r") as archive:
            manifest = json.loads(archive.read(MANIFEST))
            for directory in sorted(manifest["directories"], key=lambda n: (n.count("/"), n)):
                (staging / directory).mkdir(parents=True, exist_ok=True)
            for item in manifest["files"]:
                path = staging / item["path"]
                path.parent.mkdir(parents=True, exist_ok=True)
                fd = os.open(path, os.O_WRONLY | os.O_CREAT | os.O_EXCL, 0o600)
                with os.fdopen(fd, "wb") as writer:
                    writer.write(archive.read(PREFIX + item["path"]))
                    writer.flush()
                    os.fsync(writer.fileno())
        directories, files = walk_registry(staging)
        if fingerprint(directories, files) != summary["fingerprint"]:
            raise RecoveryError("restored bytes or enterprise identity do not match snapshot")
        # Empty-destination + sibling staging. Filesystem must support atomic rename.
        if target.exists() or target.is_symlink():
            raise RecoveryError("restore destination appeared during extraction")
        rename_no_replace(staging, target)
        return summary
    finally:
        if staging.exists():
            shutil.rmtree(staging)


def main(argv: list[str] | None = None) -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    commands = parser.add_subparsers(dest="command", required=True)
    for command in ("snapshot", "verify", "restore"):
        p = commands.add_parser(command)
        p.add_argument("--archive", required=True, type=pathlib.Path)
        if command in ("snapshot", "restore"):
            p.add_argument("--registry", required=True, type=pathlib.Path)
            p.add_argument("--offline-confirmed", action="store_true",
                           help="operator confirms ALL API/LSP/GLSP writers have stopped")
    args = parser.parse_args(argv)
    try:
        if args.command in ("snapshot", "restore") and not args.offline_confirmed:
            raise RecoveryError("stop all hosted writers and pass --offline-confirmed")
        if args.command == "snapshot":
            result = snapshot(args.registry, args.archive)
        elif args.command == "restore":
            result = restore(args.archive, args.registry)
        else:
            result = verify(args.archive)
        print(json.dumps({"status": "PASS", "operation": args.command, **result}, sort_keys=True))
        return 0
    except (RecoveryError, OSError, zipfile.BadZipFile, KeyError, TypeError) as exc:
        print(json.dumps({"status": "FAIL", "operation": args.command,
                          "reason": str(exc)}, sort_keys=True), file=sys.stderr)
        return 2


if __name__ == "__main__":
    raise SystemExit(main())
