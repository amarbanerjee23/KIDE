#!/usr/bin/env python3
"""PR106: forbid false-ready onboarding or removal of keyboard/desktop parity."""
from __future__ import annotations
from pathlib import Path
import sys

ROOT = Path(__file__).resolve().parents[1]


def read(path: str) -> str:
    return (ROOT / path).read_text(encoding="utf8")


def verify() -> list[str]:
    files = [
        "web/src/firstRun.ts",
        "web/src/FirstRunGuide.tsx",
        "web/src/App.tsx",
        "web/src/styles.css",
        "web/tests/firstRun.test.ts",
        "web/e2e/workspace.spec.ts",
        "docs/pr106-first-run-accessibility.md",
        ".github/workflows/build.yml",
        "docs/installation.md",
    ]
    failures = [f"required PR106 first-run resource missing: {p}" for p in files
                if not (ROOT / p).is_file()]
    if failures:
        return failures

    state = read(files[0])
    for token in (
        "apiCompatible: boolean", "projectOpen: boolean",
        "hasProductionDsl: boolean", "xtextConnected: boolean",
        "collaborationConnected: boolean", "canCreateProject: boolean",
        "state.projectOpen && state.hasProductionDsl",
        "state.projectOpen && state.xtextConnected && state.collaborationConnected",
        "No authorized projects are listed.",
        'action: "starter"',
        'action: "services"',
    ):
        if token not in state:
            failures.append("reactive first-run security and state check missing: " + token)
    guide = read(files[1])
    for token in (
        'aria-labelledby="first-run-title"',
        '<ol className="first-run-steps">',
        'aria-live="polite"',
        "firstRunProgress(state)",
        "step.completed",
        "onClick={() => onAction(step.action!)}",
    ):
        if token not in guide:
            failures.append("accessible first-run action control missing: " + token)

    app = read(files[2])
    for token in (
        '<FirstRunGuide state={firstRunState} onAction={handleFirstRunAction} />',
        'source === "remote" && isProductionDslPath(entry.path)',
        "serviceStatus.includes(\"compatible\")",
        'connectionSummary(lspStatus) === "Online"',
        'connectionSummary(collaborationStatus) === "Online"',
        'href="#kide-workbench-editor"',
        'id="kide-workbench-editor" tabIndex={-1}',
        "void createStarterEngineeringModels()",
        "await connectLanguageServices(current)",
    ):
        if token not in app:
            failures.append("live Web integration readiness or keyboard skip control missing: " + token)

    css = read(files[3])
    for token in (
        ".skip-editor-link:focus-visible",
        ".first-run-guide",
        ".first-run-steps",
        "@media (forced-colors: active)",
    ):
        if token not in css:
            failures.append("keyboard/focus or high-contrast styling missing: " + token)

    tests = read(files[4])
    for token in (
        "firstRunProgress(empty)",
        "never treats a signed-out or unauthorized API",
        "gates starter models on an opened project",
        "requires Xtext and collaboration",
        "never enables language readiness on an unopened project",
    ):
        if token not in tests:
            failures.append("unit acceptance removed: " + token)

    browser = read(files[5])
    for token in (
        'getByRole("region", { name: "Your engineering setup" })',
        'getByRole("link", { name: "Skip to editor" })',
        'toBeFocused()',
        '0 of 5 ready',
    ):
        if token not in browser:
            failures.append("keyboard browser test removed: " + token)

    doc = read(files[6])
    for token in ("Web", "Eclipse desktop", "Firebase", "keyboard",
                  "Help", "Welcome", "staging", "screen-reader"):
        if token.lower() not in doc.lower():
            failures.append("cross-platform first-run acceptance incomplete: " + token)

    build = read(files[7])
    if "python3 scripts/verify_pr106_first_run.py" not in build:
        failures.append("mandatory Web/Eclipse onboarding governance absent from CI")
    if "npm run test" not in build or "npx playwright test" not in build:
        failures.append("Web and browser acceptance missing from build")
    desktop = read(files[8])
    if "Help > Welcome" not in desktop or "KIDE Modelling Project" not in desktop:
        failures.append("Eclipse desktop welcome/user guide acceptance missing")
    return failures


def main() -> int:
    failures = verify()
    for failure in failures:
        print("ERROR:", failure, file=sys.stderr)
    if failures:
        return 2
    print("PR106 FIRST-RUN / ACCESSIBILITY CONTRACT VERIFIED; HUMAN STAGING SIGNOFF STILL REQUIRED")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
