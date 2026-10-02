#!/usr/bin/env python3
"""PR13 packaged LSP feature-parity qualification for all production KIDE DSLs."""

from __future__ import annotations

import argparse
import json
import os
import subprocess
import tempfile
import time
from pathlib import Path
from typing import Any

from smoke_language_server import (
    JsonRpcPeer,
    SmokeFailure,
    announce_workspace_files,
    change_document,
    find_linux_launcher,
    load_languages,
    open_document,
    request,
    wait_for_clean_diagnostics,
    wait_for_diagnostics,
)


def load_matrix(path: Path) -> dict[str, Any]:
    try:
        value = json.loads(path.read_text(encoding="utf-8"))
    except (OSError, json.JSONDecodeError) as exc:
        raise SmokeFailure(f"cannot load LSP parity matrix {path}: {exc}") from exc
    if not isinstance(value, dict) or value.get("schema_version") != 1:
        raise SmokeFailure("LSP parity matrix must be a schema_version 1 object")
    return value


def position_of(text: str, token: str, occurrence: int = 1) -> dict[str, int]:
    if occurrence < 1:
        raise SmokeFailure("token occurrence must be >= 1")
    start = -1
    cursor = 0
    for _ in range(occurrence):
        start = text.find(token, cursor)
        if start < 0:
            raise SmokeFailure(f"token {token!r} was not found in parity fixture")
        cursor = start + len(token)
    before = text[:start]
    line = before.count("\n")
    last_newline = before.rfind("\n")
    character = start if last_newline < 0 else start - last_newline - 1
    return {"line": line, "character": character}


def end_position(text: str) -> dict[str, int]:
    lines = text.split("\n")
    return {
        "line": len(lines) - 1,
        "character": len(lines[-1]),
    }


def capability_enabled(capabilities: dict[str, Any], key: str) -> bool:
    value = capabilities.get(key)
    return value is not None and value is not False


def completion_items(result: Any) -> list[dict[str, Any]]:
    if isinstance(result, list):
        return [item for item in result if isinstance(item, dict)]
    if isinstance(result, dict) and isinstance(result.get("items"), list):
        return [item for item in result["items"] if isinstance(item, dict)]
    return []


def symbol_names(result: Any) -> set[str]:
    names: set[str] = set()

    def visit(value: Any) -> None:
        if isinstance(value, list):
            for item in value:
                visit(item)
            return
        if not isinstance(value, dict):
            return
        name = value.get("name")
        if isinstance(name, str):
            names.add(name)
        visit(value.get("children"))

    visit(result)
    return names


def definition_uris(result: Any) -> set[str]:
    uris: set[str] = set()
    values = result if isinstance(result, list) else [result]
    for value in values:
        if not isinstance(value, dict):
            continue
        uri = value.get("uri")
        target_uri = value.get("targetUri")
        if isinstance(uri, str):
            uris.add(uri)
        if isinstance(target_uri, str):
            uris.add(target_uri)
    return uris


def workspace_edit_uris(result: Any) -> set[str]:
    if not isinstance(result, dict):
        return set()
    uris: set[str] = set()
    changes = result.get("changes")
    if isinstance(changes, dict):
        uris.update(uri for uri in changes if isinstance(uri, str))
    document_changes = result.get("documentChanges")
    if isinstance(document_changes, list):
        for change in document_changes:
            if not isinstance(change, dict):
                continue
            document = change.get("textDocument")
            if isinstance(document, dict) and isinstance(document.get("uri"), str):
                uris.add(document["uri"])
            for field in ("oldUri", "newUri"):
                if isinstance(change.get(field), str):
                    uris.add(change[field])
    return uris


def wait_for_diagnostic_code(
    peer: JsonRpcPeer,
    uri: str,
    code: str,
    description: str,
    timeout: float = 25.0,
) -> dict[str, Any]:
    deadline = time.monotonic() + timeout
    last: list[dict[str, Any]] = []
    while time.monotonic() < deadline:
        remaining = max(0.01, deadline - time.monotonic())
        message = peer.wait_for(
            lambda candidate: candidate.get("method") == "textDocument/publishDiagnostics"
            and candidate.get("params", {}).get("uri") == uri,
            remaining,
            description,
        )
        diagnostics = message.get("params", {}).get("diagnostics")
        if not isinstance(diagnostics, list):
            raise SmokeFailure(f"{description} returned malformed diagnostics")
        last = [item for item in diagnostics if isinstance(item, dict)]
        for diagnostic in last:
            raw_code = diagnostic.get("code")
            if str(raw_code) == code:
                return diagnostic
    raise SmokeFailure(
        f"{description} never published diagnostic code {code!r}; last diagnostics: {last}"
    )


def workspace_edit_texts(action: Any, uri: str) -> list[str]:
    if not isinstance(action, dict):
        return []
    edit = action.get("edit")
    if not isinstance(edit, dict):
        return []
    values: list[str] = []
    changes = edit.get("changes")
    if isinstance(changes, dict):
        for item in changes.get(uri, []):
            if isinstance(item, dict) and isinstance(item.get("newText"), str):
                values.append(item["newText"])
    document_changes = edit.get("documentChanges")
    if isinstance(document_changes, list):
        for change in document_changes:
            if not isinstance(change, dict):
                continue
            document = change.get("textDocument")
            if not isinstance(document, dict) or document.get("uri") != uri:
                continue
            for item in change.get("edits", []):
                if isinstance(item, dict) and isinstance(item.get("newText"), str):
                    values.append(item["newText"])
    return values


def require_capabilities(
    capabilities: dict[str, Any], matrix: dict[str, Any]
) -> None:
    for feature in matrix.get("features", []):
        if not isinstance(feature, dict):
            continue
        qualification = feature.get("qualification")
        key = feature.get("capability")
        if qualification not in ("required", "required_where_applicable") or key is None:
            continue
        if not capability_enabled(capabilities, key):
            raise SmokeFailure(
                f"required PR13 capability {feature.get('id')} is not advertised as {key}"
            )


def run_parity(products: Path, registry_path: Path, matrix_path: Path) -> None:
    languages = load_languages(registry_path)
    matrix = load_matrix(matrix_path)
    probes = matrix.get("languages")
    if not isinstance(probes, dict):
        raise SmokeFailure("LSP parity matrix has no languages object")
    qualified_features = {
        item.get("id")
        for item in matrix.get("features", [])
        if isinstance(item, dict)
        and item.get("qualification") in ("required", "required_where_applicable")
    }

    launcher = find_linux_launcher(products)

    with tempfile.TemporaryDirectory(prefix="kide-lsp-parity-") as temp_dir:
        workspace = Path(temp_dir).resolve()
        paths: dict[str, Path] = {}
        by_id: dict[str, dict[str, Any]] = {}
        for language in languages:
            extension = language["extension"]
            path = workspace / f"parity.{extension}"
            path.write_text(language["valid_fixture_text"], encoding="utf-8")
            paths[extension] = path
            by_id[language["id"]] = language

        quickfix_texts = {
            "mncspec": (
                "Model QuickFix\n"
                "InterfaceDescription QuickDevice {\n"
                "  commands { Start[] }\n"
                "}\n"
                "ControlNode node implements interface QuickDevice {\n"
                "}\n"
            ),
            "cap": (
                "Capability BadCap compatible component interface QuickDevice {\n"
                "  providesControlCapabilities {\n"
                "    fireable commands : Stop\n"
                "  }\n"
                "}\n"
            ),
            "activity": (
                "ActivityDiagram BadWorkflow\n"
                "has activities {\n"
                "  Activity BadStep {\n"
                "    requireCapability : Observe { Stop }\n"
                "    nextActivity : BadStep\n"
                "  }\n"
                "}\n"
            ),
        }
        support_text = (
            "Model QuickSupport\n"
            "InterfaceDescription OtherDevice {\n"
            "  commands { Stop[] }\n"
            "}\n"
        )
        support_path = workspace / "quickfix-support.mncspec"
        support_path.write_text(support_text, encoding="utf-8")
        quickfix_paths: dict[str, Path] = {}
        for extension, text_value in quickfix_texts.items():
            quickfix_path = workspace / f"quickfix.{extension}"
            quickfix_path.write_text(text_value, encoding="utf-8")
            quickfix_paths[extension] = quickfix_path

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
            request_id = 1000

            def rpc(method: str, params: dict[str, Any], timeout: float = 20.0) -> dict[str, Any]:
                nonlocal request_id
                request_id += 1
                return request(peer, request_id, method, params, timeout=timeout)

            try:
                root_uri = workspace.as_uri()
                initialized = rpc(
                    "initialize",
                    {
                        "processId": None,
                        "rootUri": root_uri,
                        "capabilities": {
                            "workspace": {"workspaceFolders": True},
                            "textDocument": {
                                "foldingRange": {"lineFoldingOnly": True},
                                "rename": {"prepareSupport": True},
                                "documentSymbol": {"hierarchicalDocumentSymbolSupport": True},
                                "documentHighlight": {"dynamicRegistration": False},
                                "rangeFormatting": {"dynamicRegistration": False},
                                "codeAction": {"dynamicRegistration": False},
                                "signatureHelp": {
                                    "dynamicRegistration": False,
                                    "contextSupport": True,
                                },
                                "semanticTokens": {
                                    "dynamicRegistration": False,
                                    "requests": {"range": False, "full": True},
                                    "tokenTypes": [
                                        "namespace", "type", "class", "enum",
                                        "interface", "struct", "typeParameter",
                                        "parameter", "variable", "property",
                                        "enumMember", "event", "function", "method",
                                        "macro", "keyword", "modifier", "comment",
                                        "string", "number", "regexp", "operator",
                                        "decorator",
                                    ],
                                    "tokenModifiers": [
                                        "declaration", "definition", "readonly",
                                        "static", "deprecated", "abstract", "async",
                                        "modification", "documentation",
                                        "defaultLibrary",
                                    ],
                                    "formats": ["relative"],
                                },
                            },
                        },
                        "workspaceFolders": [{"uri": root_uri, "name": "kide-parity"}],
                    },
                    timeout=30.0,
                )
                capabilities = initialized.get("result", {}).get("capabilities")
                if not isinstance(capabilities, dict):
                    raise SmokeFailure("initialize returned malformed server capabilities")
                require_capabilities(capabilities, matrix)
                peer.send({"jsonrpc": "2.0", "method": "initialized", "params": {}})

                announce_workspace_files(
                    peer,
                    list(paths.values()) + [support_path] + list(quickfix_paths.values()),
                )
                uris: dict[str, str] = {}
                for language in languages:
                    extension = language["extension"]
                    uris[extension] = open_document(
                        peer,
                        paths[extension],
                        language["language_id"],
                        language["valid_fixture_text"],
                    )

                support_uri = open_document(
                    peer,
                    support_path,
                    by_id["mnc"]["language_id"],
                    support_text,
                )
                quickfix_uris: dict[str, str] = {}
                for language_id in ("mnc", "capability", "activity"):
                    language = by_id[language_id]
                    extension = language["extension"]
                    quickfix_uris[extension] = open_document(
                        peer,
                        quickfix_paths[extension],
                        language["language_id"],
                        quickfix_texts[extension],
                    )

                # Freeze tests against the fully linked workspace, not transient didOpen state.
                for language in languages:
                    extension = language["extension"]
                    change_document(
                        peer,
                        uris[extension],
                        language["valid_fixture_text"],
                        version=2,
                    )
                    wait_for_clean_diagnostics(
                        peer,
                        uris[extension],
                        f"PR13 linked diagnostics for .{extension}",
                    )

                for language_id, language in by_id.items():
                    extension = language["extension"]
                    text = language["valid_fixture_text"]
                    uri = uris[extension]
                    probe = probes.get(language_id)
                    if not isinstance(probe, dict):
                        raise SmokeFailure(f"missing PR13 probes for {language_id}")

                    # Completion must produce concrete proposals in a grammar-valid
                    # content-assist context. Most DSLs use blank root; DML supplies
                    # an explicit incomplete DataModel context because its top-level
                    # rule legally accepts the empty document.
                    completion_probe = probe.get("completion", {})
                    completion_text = completion_probe.get("text", "")
                    completion_position = completion_probe.get(
                        "position", {"line": 0, "character": 0}
                    )
                    if not isinstance(completion_text, str):
                        raise SmokeFailure(f".{extension} completion probe text is malformed")
                    if not isinstance(completion_position, dict):
                        raise SmokeFailure(f".{extension} completion probe position is malformed")
                    completion_path = workspace / f"completion.{extension}"
                    completion_path.write_text(completion_text, encoding="utf-8")
                    completion_uri = open_document(
                        peer,
                        completion_path,
                        language["language_id"],
                        completion_text,
                    )
                    # didOpen is asynchronous. Wait until Xtext has built the
                    # temporary document before asking for content assist.
                    wait_for_diagnostics(
                        peer,
                        completion_uri,
                        f"completion document diagnostics for .{extension}",
                    )
                    completion = rpc(
                        "textDocument/completion",
                        {
                            "textDocument": {"uri": completion_uri},
                            "position": completion_position,
                        },
                    ).get("result")
                    if not completion_items(completion):
                        raise SmokeFailure(f".{extension} completion returned no root proposals")
                    peer.send({
                        "jsonrpc": "2.0",
                        "method": "textDocument/didClose",
                        "params": {"textDocument": {"uri": completion_uri}},
                    })

                    hover = rpc(
                        "textDocument/hover",
                        {
                            "textDocument": {"uri": uri},
                            "position": position_of(text, probe["hover_token"]),
                        },
                    )
                    if "result" not in hover:
                        raise SmokeFailure(f".{extension} hover returned no result field")

                    definition_probe = probe.get("definition")
                    if isinstance(definition_probe, dict):
                        definition = rpc(
                            "textDocument/definition",
                            {
                                "textDocument": {"uri": uri},
                                "position": position_of(
                                text,
                                definition_probe["token"],
                                int(definition_probe.get("occurrence", 1)),
                            ),
                            },
                        ).get("result")
                        target_name = f"parity.{definition_probe['target_extension']}"
                        if not any(value.endswith("/" + target_name) for value in definition_uris(definition)):
                            raise SmokeFailure(
                                f".{extension} definition did not navigate to {target_name}: {definition}"
                            )

                    references = rpc(
                        "textDocument/references",
                        {
                            "textDocument": {"uri": uri},
                            "position": position_of(
                                text,
                                probe.get("document_highlight_token", probe["symbol"]),
                            ),
                            "context": {"includeDeclaration": False},
                        },
                    ).get("result")
                    if not isinstance(references, list):
                        raise SmokeFailure(f".{extension} references returned malformed result")
                    if len(references) < int(probe["references_min"]):
                        raise SmokeFailure(
                            f".{extension} references expected >= {probe['references_min']}, got {len(references)}"
                        )

                    highlights = rpc(
                        "textDocument/documentHighlight",
                        {
                            "textDocument": {"uri": uri},
                            "position": position_of(text, probe["symbol"]),
                        },
                    ).get("result")
                    if not isinstance(highlights, list):
                        raise SmokeFailure(
                            f".{extension} document highlights returned malformed result"
                        )
                    if len(highlights) < int(probe["document_highlights_min"]):
                        raise SmokeFailure(
                            f".{extension} document highlights expected >= "
                            f"{probe['document_highlights_min']}, got {len(highlights)}"
                        )

                    document_symbols = rpc(
                        "textDocument/documentSymbol",
                        {"textDocument": {"uri": uri}},
                    ).get("result")
                    expected_document_symbol = probe.get(
                        "document_symbol", probe["symbol"]
                    )
                    if expected_document_symbol not in symbol_names(document_symbols):
                        raise SmokeFailure(
                            f".{extension} document symbols missing "
                            f"{expected_document_symbol!r}: {document_symbols}"
                        )

                    workspace_symbols = rpc(
                        "workspace/symbol", {"query": probe["symbol"]}
                    ).get("result")
                    expected_workspace_symbol = probe.get(
                        "workspace_symbol", probe["symbol"]
                    )
                    if expected_workspace_symbol not in symbol_names(workspace_symbols):
                        raise SmokeFailure(
                            f".{extension} workspace symbols missing "
                            f"{expected_workspace_symbol!r}: {workspace_symbols}"
                        )

                    formatting = rpc(
                        "textDocument/formatting",
                        {
                            "textDocument": {"uri": uri},
                            "options": {"tabSize": 2, "insertSpaces": True},
                        },
                    ).get("result")
                    if not isinstance(formatting, list):
                        raise SmokeFailure(f".{extension} formatting did not return a text-edit list")

                    range_formatting = rpc(
                        "textDocument/rangeFormatting",
                        {
                            "textDocument": {"uri": uri},
                            "range": {
                                "start": {"line": 0, "character": 0},
                                "end": end_position(text),
                            },
                            "options": {"tabSize": 2, "insertSpaces": True},
                        },
                    ).get("result")
                    if not isinstance(range_formatting, list):
                        raise SmokeFailure(
                            f".{extension} range formatting did not return a text-edit list"
                        )

                    prepare_rename = rpc(
                        "textDocument/prepareRename",
                        {
                            "textDocument": {"uri": uri},
                            "position": position_of(text, probe["symbol"]),
                        },
                    ).get("result")
                    if prepare_rename is None:
                        raise SmokeFailure(
                            f".{extension} prepareRename rejected the qualified symbol"
                        )

                    new_name = probe["symbol"] + "PR13"
                    rename = rpc(
                        "textDocument/rename",
                        {
                            "textDocument": {"uri": uri},
                            "position": position_of(text, probe["symbol"]),
                            "newName": new_name,
                        },
                        timeout=30.0,
                    ).get("result")
                    rename_uris = workspace_edit_uris(rename)
                    serialized_rename = json.dumps(rename, sort_keys=True)
                    if new_name not in serialized_rename:
                        raise SmokeFailure(f".{extension} rename did not contain replacement {new_name}")
                    for target in probe["rename_targets"]:
                        if not any(value.endswith("/" + target) for value in rename_uris):
                            raise SmokeFailure(
                                f".{extension} rename did not include expected target {target}: {rename}"
                            )

                    if "folding" in qualified_features:
                        folding = rpc(
                            "textDocument/foldingRange",
                            {"textDocument": {"uri": uri}},
                        ).get("result")
                        if not isinstance(folding, list):
                            raise SmokeFailure(f".{extension} folding returned malformed result")
                        if len(folding) < int(probe["folding_min"]):
                            raise SmokeFailure(
                                f".{extension} folding expected >= {probe['folding_min']}, got {len(folding)}"
                            )

                    if "semantic_tokens" in qualified_features:
                        semantic_tokens = rpc(
                            "textDocument/semanticTokens/full",
                            {"textDocument": {"uri": uri}},
                        ).get("result")
                        if not isinstance(semantic_tokens, dict):
                            raise SmokeFailure(
                                f".{extension} semantic tokens returned malformed result"
                            )
                        token_data = semantic_tokens.get("data")
                        if not isinstance(token_data, list) or len(token_data) % 5 != 0:
                            raise SmokeFailure(
                                f".{extension} semantic token data is not an LSP token stream"
                            )
                        token_count = len(token_data) // 5
                        if token_count < int(probe["semantic_tokens_min"]):
                            raise SmokeFailure(
                                f".{extension} semantic tokens expected >= "
                                f"{probe['semantic_tokens_min']}, got {token_count}"
                            )

                    if "code_actions" in qualified_features:
                        action_probe = probe.get("code_action")
                        if isinstance(action_probe, dict):
                            action_uri = quickfix_uris[extension]
                            diagnostic = wait_for_diagnostic_code(
                                peer,
                                action_uri,
                                action_probe["diagnostic_code"],
                                f".{extension} quick-fix diagnostic",
                            )
                            actions = rpc(
                                "textDocument/codeAction",
                                {
                                    "textDocument": {"uri": action_uri},
                                    "range": diagnostic["range"],
                                    "context": {"diagnostics": [diagnostic]},
                                },
                            ).get("result")
                            if not isinstance(actions, list):
                                raise SmokeFailure(
                                    f".{extension} code actions returned malformed result"
                                )
                            matching = [
                                action
                                for action in actions
                                if isinstance(action, dict)
                                and action.get("title") == action_probe["title"]
                            ]
                            if not matching:
                                raise SmokeFailure(
                                    f".{extension} code actions missing "
                                    f"{action_probe['title']!r}: {actions}"
                                )
                            replacement = action_probe["replacement"]
                            if replacement not in workspace_edit_texts(
                                matching[0], action_uri
                            ):
                                raise SmokeFailure(
                                    f".{extension} quick fix {action_probe['title']!r} "
                                    f"did not contain replacement {replacement!r}: {matching[0]}"
                                )

                for uri in [support_uri, *quickfix_uris.values()]:
                    peer.send({
                        "jsonrpc": "2.0",
                        "method": "textDocument/didClose",
                        "params": {"textDocument": {"uri": uri}},
                    })

                for uri in uris.values():
                    peer.send({
                        "jsonrpc": "2.0",
                        "method": "textDocument/didClose",
                        "params": {"textDocument": {"uri": uri}},
                    })

                rpc("shutdown", {}, timeout=20.0)
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
                    print("--- PR13 language server stderr ---")
                    print(details)
                raise

    required = [
        item["id"]
        for item in matrix.get("features", [])
        if isinstance(item, dict)
        and item.get("qualification") in ("required", "required_where_applicable")
    ]
    print(
        "KIDE PR13 LSP parity qualified: "
        + ", ".join(required)
        + " across "
        + ", ".join(f".{language['extension']}" for language in languages)
    )


def main() -> int:
    root = Path(__file__).resolve().parents[1]
    parser = argparse.ArgumentParser()
    parser.add_argument("--products", type=Path, required=True)
    parser.add_argument("--registry", type=Path, default=root / "product" / "languages.json")
    parser.add_argument(
        "--matrix", type=Path, default=root / "product" / "lsp-capabilities.json"
    )
    args = parser.parse_args()
    if not args.products.is_dir():
        raise SmokeFailure(f"products directory does not exist: {args.products}")
    run_parity(args.products.resolve(), args.registry.resolve(), args.matrix.resolve())
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
