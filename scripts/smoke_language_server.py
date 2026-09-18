#!/usr/bin/env python3
"""Golden-corpus protocol qualification for the packaged KIDE language server."""

from __future__ import annotations

import argparse
import json
import os
import queue
import subprocess
import tempfile
import threading
import time
from pathlib import Path
from typing import Any, Callable

HEADLESS_LINUX_LAUNCHER = "kide-languageserver-headless"


class SmokeFailure(RuntimeError):
    pass


class JsonRpcPeer:
    def __init__(self, process: subprocess.Popen[bytes]):
        self.process = process
        self.messages: queue.Queue[dict[str, Any]] = queue.Queue()
        self.reader_error: BaseException | None = None
        self.thread = threading.Thread(target=self._read_loop, name="kide-lsp-reader", daemon=True)
        self.thread.start()

    def send(self, message: dict[str, Any]) -> None:
        payload = json.dumps(message, separators=(",", ":")).encode("utf-8")
        frame = f"Content-Length: {len(payload)}\r\n\r\n".encode("ascii") + payload
        assert self.process.stdin is not None
        self.process.stdin.write(frame)
        self.process.stdin.flush()

    def wait_for(
        self,
        predicate: Callable[[dict[str, Any]], bool],
        timeout: float,
        description: str,
    ) -> dict[str, Any]:
        deadline = time.monotonic() + timeout
        while time.monotonic() < deadline:
            if self.reader_error is not None:
                raise SmokeFailure(f"language-server stdout reader failed: {self.reader_error}")
            remaining = max(0.01, deadline - time.monotonic())
            try:
                message = self.messages.get(timeout=remaining)
            except queue.Empty:
                break
            if predicate(message):
                return message
        if self.process.poll() is not None:
            raise SmokeFailure(
                f"language server exited with status {self.process.returncode} while waiting for {description}"
            )
        raise SmokeFailure(f"timed out waiting for {description}")

    def _read_loop(self) -> None:
        try:
            assert self.process.stdout is not None
            while True:
                headers: dict[str, str] = {}
                while True:
                    line = self.process.stdout.readline()
                    if not line:
                        return
                    if line in (b"\r\n", b"\n"):
                        break
                    text = line.decode("ascii").strip()
                    if ":" not in text:
                        raise SmokeFailure(f"invalid JSON-RPC header: {text!r}")
                    key, value = text.split(":", 1)
                    headers[key.lower()] = value.strip()
                length = int(headers.get("content-length", "0"))
                if length <= 0:
                    raise SmokeFailure("JSON-RPC frame is missing Content-Length")
                payload = self.process.stdout.read(length)
                if len(payload) != length:
                    raise SmokeFailure("truncated JSON-RPC payload")
                self.messages.put(json.loads(payload.decode("utf-8")))
        except BaseException as exception:
            self.reader_error = exception


def load_languages(registry_path: Path) -> list[dict[str, Any]]:
    registry_path = registry_path.resolve()
    try:
        data = json.loads(registry_path.read_text(encoding="utf-8"))
    except (OSError, json.JSONDecodeError) as exc:
        raise SmokeFailure(f"cannot load language registry {registry_path}: {exc}") from exc
    languages = data.get("languages")
    if data.get("schema_version") != 1 or not isinstance(languages, list) or not languages:
        raise SmokeFailure("language registry is missing schema_version 1 or languages")
    repo_root = registry_path.parent.parent
    required = ("id", "extension", "language_id", "valid_fixture", "invalid_fixture")
    result: list[dict[str, Any]] = []
    for language in languages:
        if not isinstance(language, dict) or any(field not in language for field in required):
            raise SmokeFailure("language registry contains an incomplete entry")
        entry = dict(language)
        for kind in ("valid_fixture", "invalid_fixture"):
            relative = Path(entry[kind])
            if relative.is_absolute() or ".." in relative.parts:
                raise SmokeFailure(f"unsafe {kind} path for .{entry['extension']}: {relative}")
            path = repo_root / relative
            if not path.is_file():
                raise SmokeFailure(f"missing {kind} for .{entry['extension']}: {path}")
            entry[kind + "_text"] = path.read_text(encoding="utf-8")
        result.append(entry)
    return result


def find_linux_launcher(products: Path) -> Path:
    candidates = [
        path
        for path in products.rglob(HEADLESS_LINUX_LAUNCHER)
        if path.is_file()
        and "linux" in {part.lower() for part in path.parts}
        and os.access(path, os.X_OK)
        and "plugins" not in {part.lower() for part in path.parts}
    ]
    if len(candidates) != 1:
        rendered = ", ".join(str(path) for path in candidates) or "none"
        raise SmokeFailure(
            "expected exactly one executable display-free Linux language-server launcher "
            f"named {HEADLESS_LINUX_LAUNCHER!r}, found: {rendered}"
        )
    return candidates[0]


def request(
    peer: JsonRpcPeer,
    request_id: int,
    method: str,
    params: dict[str, Any],
    timeout: float = 20.0,
) -> dict[str, Any]:
    peer.send({"jsonrpc": "2.0", "id": request_id, "method": method, "params": params})
    response = peer.wait_for(
        lambda message: message.get("id") == request_id,
        timeout,
        f"response to {method}",
    )
    if "error" in response:
        raise SmokeFailure(f"{method} failed: {response['error']}")
    return response


def open_document(
    peer: JsonRpcPeer,
    path: Path,
    language_id: str,
    text: str,
) -> str:
    uri = path.as_uri()
    peer.send({
        "jsonrpc": "2.0",
        "method": "textDocument/didOpen",
        "params": {
            "textDocument": {
                "uri": uri,
                "languageId": language_id,
                "version": 1,
                "text": text,
            }
        },
    })
    return uri


def wait_for_diagnostics(peer: JsonRpcPeer, uri: str, description: str) -> list[dict[str, Any]]:
    message = peer.wait_for(
        lambda candidate: candidate.get("method") == "textDocument/publishDiagnostics"
        and candidate.get("params", {}).get("uri") == uri,
        25.0,
        description,
    )
    diagnostics = message.get("params", {}).get("diagnostics")
    if not isinstance(diagnostics, list):
        raise SmokeFailure(f"{description} returned malformed diagnostics")
    return diagnostics


def is_error(diagnostic: dict[str, Any]) -> bool:
    # LSP severity 1 is Error. Xtext parser/linker errors are expected to carry it.
    return diagnostic.get("severity") == 1


def run_smoke(products: Path, registry_path: Path) -> None:
    languages = load_languages(registry_path)
    launcher = find_linux_launcher(products)

    with tempfile.TemporaryDirectory(prefix="kide-lsp-") as temp_dir:
        workspace = Path(temp_dir).resolve()
        valid_paths: dict[str, Path] = {}
        invalid_paths: dict[str, Path] = {}
        for language in languages:
            extension = language["extension"]
            valid = workspace / f"golden-valid.{extension}"
            invalid = workspace / f"golden-invalid.{extension}"
            valid.write_text(language["valid_fixture_text"], encoding="utf-8")
            invalid.write_text(language["invalid_fixture_text"], encoding="utf-8")
            valid_paths[extension] = valid
            invalid_paths[extension] = invalid

        stderr_log = workspace / "server-stderr.log"
        environment = os.environ.copy()
        environment.pop("DISPLAY", None)
        environment.pop("WAYLAND_DISPLAY", None)

        with stderr_log.open("wb") as stderr_stream:
            process = subprocess.Popen(
                [str(launcher)],
                stdin=subprocess.PIPE,
                stdout=subprocess.PIPE,
                stderr=stderr_stream,
                cwd=workspace,
                env=environment,
            )
            peer = JsonRpcPeer(process)
            try:
                root_uri = workspace.as_uri()
                initialized = request(peer, 1, "initialize", {
                    "processId": None,
                    "rootUri": root_uri,
                    "capabilities": {"workspace": {"workspaceFolders": True}},
                    "workspaceFolders": [{"uri": root_uri, "name": "kide-golden"}],
                }, timeout=30.0)
                capabilities = initialized.get("result", {}).get("capabilities")
                if not isinstance(capabilities, dict) or not capabilities:
                    raise SmokeFailure("initialize returned no language-server capabilities")
                peer.send({"jsonrpc": "2.0", "method": "initialized", "params": {}})

                # Keep dependency documents open in registry order so Capability and Activity
                # linking exercises the same shared Xtext workspace/index as real clients.
                open_valid_uris: list[str] = []
                for language in languages:
                    extension = language["extension"]
                    uri = open_document(
                        peer,
                        valid_paths[extension],
                        language["language_id"],
                        language["valid_fixture_text"],
                    )
                    diagnostics = wait_for_diagnostics(
                        peer, uri, f"valid diagnostics for .{extension}"
                    )
                    errors = [item for item in diagnostics if is_error(item)]
                    if errors:
                        raise SmokeFailure(
                            f"valid .{extension} golden fixture produced error diagnostics: {errors}"
                        )
                    open_valid_uris.append(uri)

                for language in languages:
                    extension = language["extension"]
                    uri = open_document(
                        peer,
                        invalid_paths[extension],
                        language["language_id"],
                        language["invalid_fixture_text"],
                    )
                    diagnostics = wait_for_diagnostics(
                        peer, uri, f"invalid diagnostics for .{extension}"
                    )
                    if not any(is_error(item) for item in diagnostics):
                        raise SmokeFailure(
                            f"invalid .{extension} golden fixture produced no error diagnostic: {diagnostics}"
                        )
                    peer.send({
                        "jsonrpc": "2.0",
                        "method": "textDocument/didClose",
                        "params": {"textDocument": {"uri": uri}},
                    })

                for uri in open_valid_uris:
                    peer.send({
                        "jsonrpc": "2.0",
                        "method": "textDocument/didClose",
                        "params": {"textDocument": {"uri": uri}},
                    })

                request(peer, 100, "shutdown", {}, timeout=20.0)
                peer.send({"jsonrpc": "2.0", "method": "exit", "params": {}})
                assert process.stdin is not None
                process.stdin.close()
                return_code = process.wait(timeout=15.0)
                if return_code != 0:
                    raise SmokeFailure(f"language server exited with status {return_code}")
            except BaseException:
                process.terminate()
                try:
                    process.wait(timeout=5.0)
                except subprocess.TimeoutExpired:
                    process.kill()
                    process.wait(timeout=5.0)
                stderr_stream.flush()
                details = stderr_log.read_text(encoding="utf-8", errors="replace")
                if details:
                    print("--- language server stderr ---")
                    print(details)
                raise

    print(
        "KIDE packaged LSP golden corpus passed for: "
        + ", ".join(f".{language['extension']}" for language in languages)
    )


def main() -> int:
    root = Path(__file__).resolve().parents[1]
    parser = argparse.ArgumentParser()
    parser.add_argument("--products", type=Path, required=True)
    parser.add_argument("--registry", type=Path, default=root / "product" / "languages.json")
    args = parser.parse_args()
    if not args.products.is_dir():
        raise SmokeFailure(f"products directory does not exist: {args.products}")
    run_smoke(args.products.resolve(), args.registry.resolve())
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
