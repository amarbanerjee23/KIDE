#!/usr/bin/env python3
"""Apply the KIDE visual language to the Sirius design files.

The stock designs use Eclipse system colours, so a capability, an activity and a
data element all look alike. This script installs one named KIDE palette per
design file and rewires every colour reference to it, so the same concept has
the same colour in every diagram. Container titles are also set in bold.

The rewrite is textual on purpose: Sirius .odesign files are XMI with
prefix-sensitive xsi:type values, which a generic XML writer would rename.

Run from the repository root:  python3 tools/apply_visual_language.py
"""
from pathlib import Path
import re
import sys

ROOT = Path(__file__).resolve().parents[1]

PALETTE = [
    ("CapabilityBlue", 82, 132, 226),
    ("CapabilityBlueLight", 219, 230, 250),
    ("ActivityAmber", 226, 152, 46),
    ("ActivityAmberLight", 252, 238, 214),
    ("DataGreen", 62, 166, 118),
    ("DataGreenLight", 219, 242, 231),
    ("StructureSlate", 96, 110, 133),
    ("StructureSlateLight", 233, 237, 243),
    ("EdgeGrey", 138, 149, 168),
    ("InkDark", 31, 36, 48),
    ("Paper", 253, 253, 255),
]

MAPPING = {
    "blue": "CapabilityBlue",
    "light_blue": "CapabilityBlueLight",
    "yellow": "ActivityAmber",
    "light_yellow": "ActivityAmberLight",
    "orange": "ActivityAmber",
    "green": "DataGreen",
    "light_green": "DataGreenLight",
    "chocolate": "StructureSlate",
    "purple": "StructureSlate",
    "light_purple": "StructureSlateLight",
    "gray": "EdgeGrey",
    "light_gray": "StructureSlateLight",
    "dark_gray": "StructureSlate",
    "black": "InkDark",
    "white": "Paper",
    "red": "StructureSlate",
    "light_red": "StructureSlateLight",
    "dark_blue": "CapabilityBlue",
    "dark_green": "DataGreen",
    "dark_red": "StructureSlate",
    "light_orange": "ActivityAmberLight",
}

INDEX = {name: i for i, (name, *_r) in enumerate(PALETTE)}

DESIGNS = [
    "com.model.domain.activity.design/description/activity.odesign",
    "com.model.domain.mnc.design/description/mnc.odesign",
]

PALETTE_MARKER = "<!-- KIDE visual language palette -->"

COLOR_RE = re.compile(
    r'xsi:type="description:SystemColor"\s+href="environment:/viewpoint#//@systemColors/@entries\[name=\'([^\']+)\'\]"'
)
STYLE_RE = re.compile(r'<style xsi:type="style:FlatContainerStyleDescription"(?![^>]*labelFormat=)')


def palette_block() -> str:
    entries = "\n".join(
        f'    <entries xsi:type="description:UserFixedColor" red="{r}" green="{g}" blue="{b}" name="{n}"/>'
        for n, r, g, b in PALETTE
    )
    return f"  {PALETTE_MARKER}\n  <userColorsPalettes name=\"KIDE\">\n{entries}\n  </userColorsPalettes>\n"


def retheme(path: Path) -> int:
    text = path.read_text(encoding="utf-8")
    changes = 0

    def replace_color(match: "re.Match[str]") -> str:
        nonlocal changes
        target = MAPPING.get(match.group(1))
        if not target:
            return match.group(0)
        changes += 1
        return (
            'xsi:type="description:UserFixedColor" '
            f'href="#//@userColorsPalettes.0/@entries.{INDEX[target]}"'
        )

    text = COLOR_RE.sub(replace_color, text)

    text, bolded = STYLE_RE.subn(
        '<style xsi:type="style:FlatContainerStyleDescription" labelFormat="bold"', text
    )
    changes += bolded

    if PALETTE_MARKER not in text:
        closing = text.rstrip().rsplit("\n", 1)[-1]
        if not closing.startswith("</"):
            raise SystemExit(f"unexpected end of {path}")
        text = text.rstrip()[: -len(closing)] + palette_block() + closing + "\n"

    path.write_text(text, encoding="utf-8")
    return changes


def main() -> int:
    total = 0
    for rel in DESIGNS:
        path = ROOT / rel
        if not path.exists():
            print(f"skipped (missing): {rel}", file=sys.stderr)
            continue
        count = retheme(path)
        total += count
        print(f"{rel}: {count} style values aligned to the KIDE palette")
    return 0 if total else 1


if __name__ == "__main__":
    raise SystemExit(main())
