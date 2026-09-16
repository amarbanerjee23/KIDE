#!/usr/bin/env python3
"""Protocol smoke test for the packaged KIDE multi-DSL language server."""

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

LANGUAGES = ("dml", "cap", "mncspec", "op", "activity")
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

    def wait_for(self, predicate: Callable[[dict[str, Any]], bool], timeout: float, description: str) -> dict[str, Any]:
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
        except BaseException as exception:  # surfaced on the main smoke-test thread
            self.reader_error = exception


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


def request(peer: JsonRpcPeer, request_id: int, method: str, params: dict[str, Any], timeout: float = 20.0) -> dict[str, Any]:
    peer.send({"jsonrpc": "2.0", "id": request_id, "method": method, "params": params})
    response = peer.wait_for(lambda message: message.get("id") == request_id, timeout, f"response to {method}")
    if "error" in response:
        raise SmokeFailure(f"{method} failed: {response['error']}")
    return response


def run_smoke(products: Path) -> None:
    launcher = find_linux_launcher(products)
    with tempfile.TemporaryDirectory(prefix="kide-lsp-") as temp_dir:
        workspace = Path(temp_dir).resolve()
        stderr_log = workspace / "server-stderr.log"
        environment = os.environ.copy()
        # This is intentional: the qualification must prove the packaged entrypoint
        # is independent of X11/Wayland rather than inheriting a runner display.
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
                    "workspaceFolders": [{"uri": root_uri, "name": "kide-smoke"}],
                }, timeout=30.0)
                capabilities = initialized.get("result", {}).get("capabilities")
                if not isinstance(capabilities, dict) or not capabilities:
                    raise SmokeFailure("initialize returned no language-server capabilities")

                peer.send({"jsonrpc": "2.0", "method": "initialized", "params": {}})

                for index, extension in enumerate(LANGUAGES, start=1):
                    source = workspace / f"invalid-{index}.{extension}"
                    source.write_text("§\n", encoding="utf-8")
                    uri = source.as_uri()
                    peer.send({
                        "jsonrpc": "2.0",
                        "method": "textDocument/didOpen",
                        "params": {
                            "textDocument": {
                                "uri": uri,
                                "languageId": f"kide-{extension}",
                                "version": 1,
                                "text": "§\n",
                            }
                        },
                    })
                    peer.wait_for(
                        lambda message, expected=uri: message.get("method") == "textDocument/publishDiagnostics"
                        and message.get("params", {}).get("uri") == expected
                        and bool(message.get("params", {}).get("diagnostics")),
                        20.0,
                        f"non-empty diagnostics for .{extension}",
                    )
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

    print("KIDE language-server smoke test passed for: " + ", ".join(f".{ext}" for ext in LANGUAGES))


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--products", type=Path, required=True)
    args = parser.parse_args()
    if not args.products.is_dir():
        raise SmokeFailure(f"products directory does not exist: {args.products}")
    run_smoke(args.products.resolve())
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
