#!/usr/bin/env python3
"""PR16 fail-closed current-platform contract for KIDE."""

from __future__ import annotations

import json
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
TARGET = ROOT / "releng" / "com.kide.target" / "com.kide.target.target"
POM = ROOT / "pom.xml"
CONTRACT = ROOT / "product" / "xtext-generation.json"
WORKFLOWS = [
    ROOT / ".github" / "workflows" / "build.yml",
    ROOT / ".github" / "workflows" / "release.yml",
]


class PlatformError(RuntimeError):
    pass


def require(condition: bool, message: str) -> None:
    if not condition:
        raise PlatformError(message)


def read(path: Path) -> str:
    require(path.is_file(), f"missing file: {path.relative_to(ROOT)}")
    return path.read_text(encoding="utf-8")


def verify() -> None:
    pom = read(POM)
    target = read(TARGET)
    contract = json.loads(read(CONTRACT))
    baseline = contract.get("baseline", {})

    require("<tycho.version>5.0.4</tycho.version>" in pom, "Tycho must be 5.0.4")
    require("<maven.compiler.release>21</maven.compiler.release>" in pom, "compiler release must be 21")
    require("<executionEnvironment>JavaSE-21</executionEnvironment>" in pom,
            "Tycho execution environment must be JavaSE-21")
    require("JavaSE-11" not in pom, "JavaSE-11 remains in parent build")

    required_target_tokens = (
        "https://download.eclipse.org/releases/2026-09/",
        "https://download.eclipse.org/modeling/tmf/xtext/updates/releases/2.44.0/",
        "https://download.eclipse.org/sirius/updates/releases/7.6.1/2025-09/",
        "https://download.eclipse.org/tools/orbit/simrel/maven-osgi/2026-09/",
        "https://download.eclipse.org/lsp4j/updates/releases/1.0.0",
        "org.eclipse.lsp4j",
        "org.eclipse.lsp4j.jsonrpc",
        "org.apache.commons.commons-io",
        "JavaSE-21",
    )
    for token in required_target_tokens:
        require(token in target, f"target is missing current-platform token: {token}")

    forbidden_target_tokens = (
        "releases/2021-12",
        "releases/2021-03",
        "xtext/updates/releases/2.25.0",
        "sirius/updates/releases/6.6.1",
        "0.10.0.v20201105-1103",
        "JavaSE-11",
        "R20211213173813",
        '<unit id="org.apache.commons.io"',
    )
    for token in forbidden_target_tokens:
        require(token not in target, f"legacy target dependency remains: {token}")

    require(baseline.get("java") == 21, "generation contract Java baseline is not 21")
    require(baseline.get("tycho") == "5.0.4", "generation contract Tycho baseline is not 5.0.4")
    require(baseline.get("xtext") == "2.44.0", "generation contract Xtext baseline is not 2.44.0")
    require(baseline.get("eclipse") == "2026-09", "generation contract Eclipse baseline is not 2026-09")
    require(baseline.get("sirius") == "7.6.1", "generation contract Sirius baseline is not 7.6.1")

    manifests = sorted(ROOT.glob("*/META-INF/MANIFEST.MF"))
    require(manifests, "no bundle manifests found")
    for manifest in manifests:
        text = read(manifest)
        require("JavaSE-11" not in text,
                f"legacy JavaSE-11 BREE remains: {manifest.relative_to(ROOT)}")
        require("org.eclipse.xtext.generator;bundle-version" not in text,
                f"deprecated Xtext generator bundle dependency remains: {manifest.relative_to(ROOT)}")
        require("org.eclipse.osgi.services;bundle-version" not in text,
                f"obsolete Eclipse OSGi services bundle dependency remains: {manifest.relative_to(ROOT)}")
        if "Bundle-RequiredExecutionEnvironment:" in text:
            require("Bundle-RequiredExecutionEnvironment: JavaSE-21" in text,
                    f"bundle BREE is not JavaSE-21: {manifest.relative_to(ROOT)}")

    for row in contract.get("languages", []):
        build = ROOT / row["runtime_bundle"] / "build.properties"
        build_text = read(build)
        require("additional.bundles = org.eclipse.xtext.xtext.generator.dependencies" in build_text,
                f"legacy Xtext generator dependency list remains: {build.relative_to(ROOT)}")
        require("org.apache.commons.logging" not in build_text,
                f"legacy Commons Logging generator dependency remains: {build.relative_to(ROOT)}")

    for workflow in WORKFLOWS:
        text = read(workflow)
        require("java-version: '17'" not in text, f"JDK 17 remains in {workflow.relative_to(ROOT)}")
        require("java-version: '21'" in text, f"JDK 21 missing from {workflow.relative_to(ROOT)}")

    justj_repository = "https://download.eclipse.org/justj/jres/21/updates/release/21.0.12.v20260826-1216/"
    repository_poms = (
        ROOT / "releng" / "com.kide.repository" / "pom.xml",
        ROOT / "releng" / "com.kide.languageserver.repository" / "pom.xml",
    )
    for repository_pom in repository_poms:
        repository_text = read(repository_pom)
        require(justj_repository in repository_text,
                f"pinned Java 21 JustJ repository missing: {repository_pom.relative_to(ROOT)}")
        require("/justj/jres/17/" not in repository_text,
                f"legacy Java 17 JustJ repository remains: {repository_pom.relative_to(ROOT)}")

    products = (
        ROOT / "releng" / "com.kide.repository" / "kide.product",
        ROOT / "releng" / "com.kide.languageserver.repository" / "kide-language-server.product",
    )
    for product in products:
        product_text = read(product)
        require("-Dosgi.requiredJavaVersion=21" in product_text,
                f"Java 21 runtime requirement missing: {product.relative_to(ROOT)}")
        require("JavaSE-21" in product_text,
                f"JavaSE-21 VM declaration missing: {product.relative_to(ROOT)}")
        require("-Dosgi.requiredJavaVersion=11" not in product_text,
                f"legacy Java 11 runtime requirement remains: {product.relative_to(ROOT)}")
        require("JavaSE-17" not in product_text,
                f"legacy JavaSE-17 VM declaration remains: {product.relative_to(ROOT)}")

    activity_demo_sources = (
        ROOT / "com.smr.activity.activity2mnc" / "src" / "com" / "smr" / "activity" / "activity2mnc" / "handlers" / "Demo.xtend",
        ROOT / "com.smr.activity.activity2mnc" / "xtend-gen" / "com" / "smr" / "activity" / "activity2mnc" / "handlers" / "Demo.java",
    )
    for source in activity_demo_sources:
        require("javax.inject.Inject" not in read(source),
                f"legacy javax.inject annotation remains: {source.relative_to(ROOT)}")

    print("PR16 CURRENT PLATFORM CONTRACT OK")


def main() -> int:
    try:
        verify()
    except (PlatformError, json.JSONDecodeError, UnicodeError) as exc:
        print(f"ERROR: {exc}", file=sys.stderr)
        return 2
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
