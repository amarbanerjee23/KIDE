#!/usr/bin/env python3
"""Normalize generator-owned PR15 artifacts after running the aggregate MWE2 workflow."""

from __future__ import annotations

import json
import shutil
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
CONTRACT = ROOT / "product" / "xtext-generation.json"


def _remove_log4j_metadata(text: str) -> str:
    text = text.replace("Import-Package: org.apache.log4j\n", "")
    text = text.replace("Import-Package: com.mncml.dsl,\n org.apache.log4j\n", "Import-Package: com.mncml.dsl\n")
    text = text.replace(",\n org.apache.log4j\n", "\n")
    text = text.replace("                     org.apache.log4j,\\\n", "")
    return text


def main() -> int:
    contract = json.loads(CONTRACT.read_text(encoding="utf-8"))
    for row in contract["languages"]:
        generated_activator = ROOT / row["forbidden_generated_ui_activator"]
        if generated_activator.exists():
            generated_activator.unlink()

        for bundle_key in ("runtime_bundle", "ide_bundle", "ui_bundle"):
            bundle = ROOT / row[bundle_key]
            bin_dir = bundle / "bin"
            if bin_dir.exists():
                shutil.rmtree(bin_dir)

            manifest = bundle / "META-INF" / "MANIFEST.MF"
            if manifest.is_file():
                updated = _remove_log4j_metadata(manifest.read_text(encoding="utf-8"))
                if bundle_key == "ui_bundle" and " org.eclipse.core.runtime" not in updated:
                    updated = updated.replace(" org.eclipse.ui,\n", " org.eclipse.core.runtime,\n org.eclipse.ui,\n")
                manifest.write_text(updated, encoding="utf-8", newline="\n")

            build = bundle / "build.properties"
            if build.is_file():
                updated = _remove_log4j_metadata(build.read_text(encoding="utf-8"))
                build.write_text(updated, encoding="utf-8", newline="\n")

    print("PR15 XTEXT GENERATION NORMALIZED")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
