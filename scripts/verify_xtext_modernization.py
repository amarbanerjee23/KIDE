#!/usr/bin/env python3
"""PR15 contract gate for deterministic Xtext generation preparation."""

from __future__ import annotations

import hashlib
import json
import re
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
CONTRACT = ROOT / "product" / "xtext-generation.json"
BASELINE = ROOT / "product" / "xtext-generated-baseline.json"
LANGUAGES = ROOT / "product" / "languages.json"
TARGET = ROOT / "releng" / "com.kide.target" / "com.kide.target.target"
POM = ROOT / "pom.xml"


class ContractError(RuntimeError):
    pass


def require(condition: bool, message: str) -> None:
    if not condition:
        raise ContractError(message)


def read(path: Path) -> str:
    require(path.is_file(), f"missing file: {path.relative_to(ROOT)}")
    return path.read_text(encoding="utf-8")


def git_blob_sha(path: Path) -> str:
    data = path.read_bytes()
    return hashlib.sha1(b"blob " + str(len(data)).encode("ascii") + b"\0" + data).hexdigest()


def verify_generated_baseline(contract: dict) -> None:
    baseline = json.loads(read(BASELINE)).get("git_blob_sha1", {})
    expected_paths = set(baseline)
    actual_paths = set()
    for row in contract["languages"]:
        for bundle_key in ("runtime_bundle", "ide_bundle", "ui_bundle"):
            bundle = ROOT / row[bundle_key]
            for generated_root in ("src-gen", "xtend-gen"):
                root = bundle / generated_root
                if not root.is_dir():
                    continue
                for path in root.rglob("*"):
                    if path.is_file():
                        actual_paths.add(path.relative_to(ROOT).as_posix())
    require(actual_paths == expected_paths,
            "generator-owned file set drifted; regenerate/review and update the baseline deliberately")
    for rel, sha in baseline.items():
        path = ROOT / rel
        require(git_blob_sha(path) == sha, f"generator-owned bytes drifted: {rel}")


def verify() -> None:
    contract = json.loads(read(CONTRACT))
    registry = json.loads(read(LANGUAGES))

    require(contract.get("schema_version") == 1, "unsupported xtext generation contract")
    rows = contract.get("languages")
    require(isinstance(rows, list) and rows, "contract has no languages")

    contract_ids = {row.get("id") for row in rows}
    registry_ids = {row.get("id") for row in registry.get("languages", [])}
    require(contract_ids == registry_ids, "generation contract language set differs from product truth")

    baseline = contract.get("baseline", {})
    require(baseline.get("xtext") == "2.25.0", "PR15 must keep Xtext 2.25.0")
    require(baseline.get("java") == 11, "PR15 must keep Java 11")
    require(baseline.get("tycho") == "4.0.8", "PR15 must keep Tycho 4.0.8")

    target = read(TARGET)
    require("2.25.0.v20210301-1429" in target, "PR15 Xtext target pin changed")
    pom = read(POM)
    require("<tycho.version>4.0.8</tycho.version>" in pom, "PR15 Tycho baseline changed")
    require("<maven.compiler.release>11</maven.compiler.release>" in pom, "PR15 Java baseline changed")

    forbidden_generation_tokens = (
        'encoding = "windows-1252"',
        'lineDelimiter = "\\r\\n"',
        "webSupport",
        "generateServlet",
        'framework = "Ace"',
        "web = {",
    )

    for row in rows:
        lang_id = row["id"]
        runtime = ROOT / row["runtime_bundle"]
        ide = ROOT / row["ide_bundle"]
        ui = ROOT / row["ui_bundle"]
        workflow = ROOT / row["workflow"]

        for project in (runtime, ide, ui):
            require(project.is_dir(), f"{lang_id}: missing bundle {project.name}")
            require(not (project / "bin").exists(), f"{lang_id}: bin output present in {project.name}")

        workflow_text = read(workflow)
        require('encoding = "UTF-8"' in workflow_text, f"{lang_id}: generator is not UTF-8")
        require('lineDelimiter = "\\n"' in workflow_text, f"{lang_id}: generator is not LF-normalized")
        for token in forbidden_generation_tokens:
            require(token not in workflow_text, f"{lang_id}: legacy generator token remains: {token}")

        for project in (runtime, ide, ui):
            manifest = project / "META-INF" / "MANIFEST.MF"
            require("org.apache.log4j" not in read(manifest),
                    f"{lang_id}: direct Log4j 1 dependency remains in {manifest.relative_to(ROOT)}")
            build = project / "build.properties"
            if build.is_file():
                require("org.apache.log4j" not in read(build),
                        f"{lang_id}: Log4j 1 remains in {build.relative_to(ROOT)}")
            for source_root in ("src", "src-gen", "xtend-gen"):
                root = project / source_root
                if not root.is_dir():
                    continue
                for path in root.rglob("*"):
                    if path.is_file() and path.suffix.lower() in {".java", ".xtend", ".mwe2", ".mf"}:
                        text = path.read_text(encoding="utf-8", errors="replace")
                        require("org.apache.log4j" not in text,
                                f"{lang_id}: Log4j 1 API remains in {path.relative_to(ROOT)}")

        handwritten = ROOT / row["handwritten_ui_activator"]
        generated = ROOT / row["forbidden_generated_ui_activator"]
        activator = read(handwritten)
        require(not generated.exists(), f"{lang_id}: UI activator returned to generator-owned src-gen")
        require("org.eclipse.core.runtime.Status" in activator,
                f"{lang_id}: hand-written activator does not use Eclipse logging")
        ui_manifest = read(ui / "META-INF" / "MANIFEST.MF")
        require("Bundle-Activator:" in ui_manifest, f"{lang_id}: UI bundle lost its activator declaration")
        require("org.eclipse.core.runtime" in ui_manifest, f"{lang_id}: UI bundle does not import Eclipse runtime logging")

    aggregate = read(ROOT / "com.dsep.dsl.generator" / "src" / "GenerateDsls.mwe2")
    expected_modules = {
        "com.dml.dsl.GenerateDml",
        "com.operation.dsl.GenerateOperation",
        "com.mncml.dsl.GenerateMnc",
        "com.capability.GenerateCapability",
        "com.smr.activity.GenerateActivityDsl",
    }
    actual_modules = set(re.findall(r"component\s*=\s*@([A-Za-z0-9_.]+)", aggregate))
    require(actual_modules == expected_modules,
            f"aggregate MWE2 language set mismatch: {sorted(actual_modules)}")

    verify_generated_baseline(contract)
    print("PR15 XTEXT MODERNIZATION CONTRACT OK")


def main() -> int:
    try:
        verify()
    except (ContractError, json.JSONDecodeError, UnicodeError) as exc:
        print(f"ERROR: {exc}", file=sys.stderr)
        return 2
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
