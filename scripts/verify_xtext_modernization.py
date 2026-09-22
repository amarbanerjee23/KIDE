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
    require(contract_ids == {"dml", "operation", "mnc", "capability", "activity"},
            "legacy generated-byte contract must remain the original five DSLs")
    require(registry_ids == contract_ids | {"krl"},
            "product truth must contain the frozen legacy five plus KRL")

    baseline = contract.get("baseline", {})
    require(baseline.get("xtext") == "2.44.0", "current Xtext baseline must be 2.44.0")
    require(baseline.get("java") == 21, "current Java baseline must be 21")
    require(baseline.get("tycho") == "5.0.4", "current Tycho baseline must be 5.0.4")
    require(baseline.get("eclipse") == "2026-09", "current Eclipse baseline must be 2026-09")
    require(baseline.get("sirius") == "7.6.1", "current Sirius baseline must be 7.6.1")

    target = read(TARGET)
    require("releases/2026-09/" in target, "current Eclipse target pin changed")
    require("xtext/updates/releases/2.44.0/" in target, "current Xtext target pin changed")
    require("sirius/updates/releases/7.6.1/" in target, "current Sirius target pin changed")
    pom = read(POM)
    require("<tycho.version>5.0.4</tycho.version>" in pom, "current Tycho baseline changed")
    require("<maven.compiler.release>21</maven.compiler.release>" in pom, "current Java baseline changed")

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
        "com.kide.krl.dsl.GenerateKrl",
    }
    aggregate_without_line_comments = re.sub(r"(?m)^\s*//.*$", "", aggregate)
    actual_modules = set(re.findall(
        r"component\s*=\s*@([A-Za-z0-9_.]+)",
        aggregate_without_line_comments,
    ))
    require(actual_modules == expected_modules,
            f"aggregate MWE2 language set mismatch: {sorted(actual_modules)}")

    krl_workflow = ROOT / "com.kide.krl.dsl" / "src" / "com" / "kide" / "krl" / "dsl" / "GenerateKrl.mwe2"
    krl_generator_pom = ROOT / "tools" / "krl-language-generator" / "pom.xml"
    krl_text = read(krl_workflow)
    require('encoding = "UTF-8"' in krl_text, "krl: generator is not UTF-8")
    require('lineDelimiter = "\\n"' in krl_text, "krl: generator is not LF-normalized")
    require('name = "com.kide.krl.dsl.Krl"' in krl_text, "krl: unexpected Xtext language name")
    require('fileExtensions = "krl"' in krl_text, "krl: file extension is not .krl")
    require("createEclipseMetaData = false" in krl_text,
            "krl: build-time generation must not mutate Maven project metadata")
    generator_pom = read(krl_generator_pom)
    require("<version>2.44.0</version>" in generator_pom,
            "krl: Xtext generator must be pinned to 2.44.0")
    require("org.eclipse.emf.mwe2.launch" in generator_pom,
            "krl: MWE2 build generator dependency is missing")

    verify_generated_baseline(contract)
    print("PR15 XTEXT MODERNIZATION CONTRACT OK + PR38 KRL GENERATOR OK")


def main() -> int:
    try:
        verify()
    except (ContractError, json.JSONDecodeError, UnicodeError) as exc:
        print(f"ERROR: {exc}", file=sys.stderr)
        return 2
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
