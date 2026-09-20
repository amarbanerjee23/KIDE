#!/usr/bin/env python3
import argparse
import json
import re
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
PRODUCT = ROOT / "product" / "languages.json"
OUTPUT = ROOT / "web" / "src" / "generated" / "language-assets.json"
LITERAL = re.compile(r"'((?:\\.|[^'\\])*)'")
IDENTIFIER = re.compile(r"^[A-Za-z_][A-Za-z0-9_]*$")


def grammar_keywords(text: str) -> list[str]:
    values = set()
    for match in LITERAL.finditer(text):
        value = match.group(1).replace("\\'", "'")
        if IDENTIFIER.fullmatch(value):
            values.add(value)
    return sorted(values)


def build() -> dict:
    registry = json.loads(PRODUCT.read_text(encoding="utf-8"))
    contents = {}
    for language in registry["languages"]:
        contents[language["id"]] = (
            ROOT / language["grammar"]
        ).read_text(encoding="utf-8")
    inherited_dml = grammar_keywords(contents["dml"])

    languages = []
    for language in registry["languages"]:
        own = grammar_keywords(contents[language["id"]])
        inherited = [] if language["id"] == "dml" else inherited_dml
        languages.append({
            "id": language["id"],
            "display_name": language["display_name"],
            "extension": language["extension"],
            "language_id": language["language_id"],
            "scope_name": "source." + language["language_id"],
            "keywords": sorted(set(inherited + own)),
        })
    return {"schema_version": 1, "languages": languages}


def render(value: dict) -> str:
    return json.dumps(value, indent=2, sort_keys=False) + "\n"


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--check", action="store_true")
    args = parser.parse_args()
    expected = render(build())
    if args.check:
        if not OUTPUT.exists() or OUTPUT.read_text(encoding="utf-8") != expected:
            print("WEB LANGUAGE ASSET ERROR: generated language assets are stale")
            return 1
        print("KIDE web language assets verified.")
        return 0
    OUTPUT.parent.mkdir(parents=True, exist_ok=True)
    OUTPUT.write_text(expected, encoding="utf-8")
    print(f"wrote {OUTPUT}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
