#!/usr/bin/env python3
"""Fail closed when browser GLSP concepts drift from the existing Sirius/EMF model."""
from __future__ import annotations

import json
import re
import sys
import xml.etree.ElementTree as ET
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
MANIFEST = ROOT / "product" / "diagram-semantics.json"

MODEL_ROOTS = {
    "activityDiagramModel": ROOT / "com.smr.activity" / "src" / "activityDiagramModel",
    "mncModel": ROOT / "com.mncml" / "src" / "mncModel",
}


class ParityError(RuntimeError):
    pass


def getter(feature: str) -> str:
    return "get" + feature[:1].upper() + feature[1:]


def java_type_source(qualified: str) -> Path:
    package, name = qualified.rsplit(".", 1)
    root = MODEL_ROOTS.get(package)
    if root is None:
        raise ParityError(f"unsupported EMF package in diagram contract: {package}")
    path = root / f"{name}.java"
    if not path.is_file():
        raise ParityError(f"missing generated EMF type for {qualified}: {path}")
    return path


def sirius_elements(path: Path) -> list[dict[str, str]]:
    try:
        tree = ET.parse(path)
    except (OSError, ET.ParseError) as exc:
        raise ParityError(f"cannot parse Sirius design {path}: {exc}") from exc
    return [dict(element.attrib) for element in tree.iter()]


def verify() -> None:
    data = json.loads(MANIFEST.read_text(encoding="utf-8"))
    if data.get("schema_version") != 1 or data.get("glsp_version") != "2.8.0":
        raise ParityError("diagram semantic manifest must declare schema 1 and GLSP 2.8.0")

    diagrams = data.get("diagrams")
    if not isinstance(diagrams, list) or not diagrams:
        raise ParityError("diagram semantic manifest contains no diagrams")

    seen_ids: set[str] = set()
    for diagram in diagrams:
        diagram_id = diagram.get("id")
        extension = diagram.get("extension")
        if not isinstance(diagram_id, str) or not isinstance(extension, str):
            raise ParityError("diagram id/extension is missing")
        if diagram_id in seen_ids:
            raise ParityError(f"duplicate diagram id: {diagram_id}")
        seen_ids.add(diagram_id)

        sirius_path = ROOT / str(diagram.get("sirius_source", ""))
        if not sirius_path.is_file():
            raise ParityError(f"Sirius source is missing for {diagram_id}: {sirius_path}")
        design = sirius_elements(sirius_path)
        raw_design = sirius_path.read_text(encoding="utf-8")
        if f'modelFileExtension="{extension}"' not in raw_design:
            raise ParityError(
                f"Sirius source for {diagram_id} does not advertise .{extension}"
            )

        concepts = diagram.get("concepts")
        if not isinstance(concepts, list) or not concepts:
            raise ParityError(f"{diagram_id} contains no concept mappings")

        for concept in concepts:
            concept_id = concept.get("id")
            mapping = concept.get("sirius_mapping")
            if not isinstance(concept_id, str) or not concept_id.startswith("kide:"):
                raise ParityError(f"invalid GLSP concept id in {diagram_id}: {concept_id!r}")
            if not isinstance(mapping, str) or not mapping:
                raise ParityError(f"{concept_id} has no explicit Sirius mapping")
            if not any(item.get("name") == mapping for item in design):
                raise ParityError(
                    f"{concept_id} references missing Sirius mapping {mapping!r}"
                )

            emf_class = concept.get("emf_class")
            emf_feature = concept.get("emf_feature")
            if bool(emf_class) == bool(emf_feature):
                raise ParityError(
                    f"{concept_id} must reference exactly one EMF class or feature"
                )
            if emf_class:
                source = java_type_source(str(emf_class))
                text = source.read_text(encoding="utf-8")
                simple = str(emf_class).rsplit(".", 1)[1]
                if not re.search(rf"public\s+interface\s+{re.escape(simple)}\b", text):
                    raise ParityError(f"{concept_id} EMF class is not generated: {emf_class}")
                mapped_domains = {
                    item.get("domainClass")
                    for item in design
                    if item.get("name") == mapping
                }
                if str(emf_class) not in mapped_domains:
                    raise ParityError(
                        f"Sirius mapping {mapping!r} is not typed as {emf_class}"
                    )
            else:
                owner, feature = str(emf_feature).rsplit(".", 1)
                source = java_type_source(owner)
                text = source.read_text(encoding="utf-8")
                if not re.search(rf"\b{re.escape(getter(feature))}\s*\(", text):
                    raise ParityError(
                        f"{concept_id} references missing generated EMF feature {emf_feature}"
                    )
                # A Sirius relation can reference the feature directly or via AQL/service
                # while still naming an explicit desktop mapping in the manifest.
                feature_marker = feature
                if feature_marker not in raw_design:
                    raise ParityError(
                        f"Sirius design does not reference EMF feature {emf_feature}"
                    )

    java_types = (ROOT / "com.kide.glsp" / "src" / "com" / "kide" / "glsp" / "KideDiagramTypes.java")
    java_text = java_types.read_text(encoding="utf-8")
    browser = (ROOT / "web" / "src" / "glspClient.ts").read_text(encoding="utf-8")
    for diagram in diagrams:
        if diagram["id"] not in java_text or diagram["id"] not in browser:
            raise ParityError(
                f"diagram id {diagram['id']} is not shared by Java GLSP and browser boundary"
            )
        for concept in diagram["concepts"]:
            if concept["id"] not in java_text:
                raise ParityError(
                    f"GLSP Java type registry is missing {concept['id']}"
                )

    forbidden = [
        "activityDiagramModel.Activity",
        "mncModel.InterfaceDescription",
        "mncModel.ControlNode",
    ]
    browser_semantics = "\n".join(
        path.read_text(encoding="utf-8")
        for path in (ROOT / "web" / "src").glob("*.ts*")
        if path.name not in {"languageAssets.generated.ts"}
    )
    for semantic_type in forbidden:
        if semantic_type in browser_semantics:
            raise ParityError(
                f"browser source duplicates Java/EMF semantic type {semantic_type}"
            )

    print(
        "GLSP/Sirius semantic parity verified for "
        + ", ".join(diagram["id"] for diagram in diagrams)
    )


if __name__ == "__main__":
    try:
        verify()
    except (ParityError, OSError, json.JSONDecodeError) as exc:
        print(f"ERROR: {exc}", file=sys.stderr)
        raise SystemExit(2)
