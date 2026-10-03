#!/usr/bin/env python3
import json
import pathlib
import re
import sys
import xml.etree.ElementTree as ET

ROOT = pathlib.Path(__file__).resolve().parents[1]
CONTRACT = ROOT / "product" / "shared-service-kernel.json"
DESKTOP_FEATURE = ROOT / "releng" / "com.kide.feature" / "feature.xml"
HEADLESS_FEATURE = ROOT / "releng" / "com.kide.languageserver.feature" / "feature.xml"
SHARED_SYNTHESIS = (
    ROOT / "com.kide.synthesis" / "src" / "com" / "kide" / "synthesis"
    / "ProjectSynthesisService.java"
)
SERVER_SYNTHESIS = (
    ROOT / "com.kide.enterprise.server" / "src" / "com" / "kide"
    / "enterprise" / "server" / "ProjectSynthesisService.java"
)
SHARED_GENERATION = (
    ROOT / "com.kide.codegen" / "src" / "com" / "kide" / "codegen"
    / "ProjectGenerationService.java"
)
SERVER_GENERATION = (
    ROOT / "com.kide.enterprise.server" / "src" / "com" / "kide"
    / "enterprise" / "server" / "ProjectGenerationService.java"
)
SERVER_ROOT = ROOT / "com.kide.enterprise.server" / "src"


def feature_plugins(path: pathlib.Path) -> set[str]:
    tree = ET.parse(path)
    return {
        node.attrib["id"]
        for node in tree.getroot().findall("plugin")
        if node.attrib.get("id")
    }


def fail(message: str) -> None:
    raise SystemExit(f"shared service kernel violation: {message}")


def main() -> int:
    contract = json.loads(CONTRACT.read_text(encoding="utf-8"))
    required = set(contract["bundles"])
    desktop = feature_plugins(DESKTOP_FEATURE)
    headless = feature_plugins(HEADLESS_FEATURE)

    missing_desktop = sorted(required - desktop)
    missing_headless = sorted(required - headless)
    if missing_desktop:
        fail("desktop feature is missing: " + ", ".join(missing_desktop))
    if missing_headless:
        fail("headless/web feature is missing: " + ", ".join(missing_headless))

    if not SHARED_SYNTHESIS.is_file():
        fail("ProjectSynthesisService must live in com.kide.synthesis")
    source = SHARED_SYNTHESIS.read_text(encoding="utf-8")
    if "package com.kide.synthesis;" not in source:
        fail("ProjectSynthesisService has the wrong package")
    if SERVER_SYNTHESIS.exists():
        fail("server bundle must not own ProjectSynthesisService")
    if not SHARED_GENERATION.is_file():
        fail("ProjectGenerationService must live in com.kide.codegen")
    generation = SHARED_GENERATION.read_text(encoding="utf-8")
    if "package com.kide.codegen;" not in generation:
        fail("ProjectGenerationService has the wrong package")
    if SERVER_GENERATION.exists():
        fail("server bundle must not own ProjectGenerationService")

    forbidden = (
        "new ProjectSynthesisEngine(",
        "new DeterministicSynthesisService(",
        "new ProjectKrlGenerationEngine(",
    )
    for java in SERVER_ROOT.rglob("*.java"):
        text = java.read_text(encoding="utf-8")
        for token in forbidden:
            if token in text:
                fail(f"{java.relative_to(ROOT)} reimplements synthesis via {token}")

    print(
        "KIDE shared service kernel verified: "
        + ", ".join(sorted(required))
    )
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
