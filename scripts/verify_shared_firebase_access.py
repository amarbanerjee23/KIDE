#!/usr/bin/env python3
"""PR105: ensure automatic engineering grants are opt-in, coherent and test-backed."""
from __future__ import annotations

from pathlib import Path
import sys

ROOT = Path(__file__).resolve().parents[1]


def read(path: str) -> str:
    return (ROOT / path).read_text(encoding="utf-8")


def verify() -> list[str]:
    problems: list[str] = []
    required = [
        "com.kide.enterprise.authorization/src/com/kide/enterprise/authorization/SharedFirebaseEngineeringPolicy.java",
        "com.kide.enterprise.authorization/src/com/kide/enterprise/authorization/AuthorizationService.java",
        "com.kide.enterprise.authorization.tests/src/com/kide/enterprise/authorization/tests/AuthorizationServiceTest.java",
        "com.kide.enterprise.server/src/com/kide/enterprise/server/EnterpriseApiApplication.java",
        "com.kide.languageserver.gateway/src/com/kide/languageserver/gateway/GatewayApplication.java",
        "com.kide.glsp/src/com/kide/glsp/GlspGatewayApplication.java",
        "cloudbuild.yaml",
        "deploy/gcp/cloudbuild.yaml",
        "deploy/gcp/cloudbuild-release.yaml",
        "deploy/gcp/cloudbuild-deploy.yaml",
        "deploy/gcp/enable-shared-firebase-access.sh",
        "deploy/gcp/configure-auto-deploy.sh",
        "deploy/gcp/deploy-cloud-run.sh",
        "docs/pr105-shared-firebase-engineering.md",
        "web/e2e/live-hosted.spec.ts",
        ".github/workflows/build.yml",
    ]
    for path in required:
        if not (ROOT / path).is_file():
            problems.append("shared access required contract missing: " + path)
    if problems:
        return problems

    policy = read(required[0])
    for token in (
        'ENV_KEY = "KIDE_SHARED_FIREBASE_ENGINEERS"',
        'env.getOrDefault(ENV_KEY, "false")',
        '"true".equalsIgnoreCase(setting)',
        '"KIDE_FIREBASE_PROJECT_ID"',
        "Role.ENGINEER.grants(permission)",
        "principal.kind() != PrincipalKind.HUMAN",
        "principal.method() != AuthenticationMethod.FIREBASE_ID_TOKEN",
        "https://securetoken.google.com/",
        '("firebase:" + firebaseProjectId + "#" + uid)',
    ):
        if token not in policy:
            problems.append("Firebase grant trust boundary missing: " + token)

    auth = read(required[1])
    for token in ("SharedFirebaseEngineeringPolicy.disabled()",
                  "boolean allowed = sharedFirebase.grants(principal, permission)",
                  "if (binding.effect() == BindingEffect.DENY)"):
        if token not in auth:
            problems.append("authorization mode or explicit deny precedence missing: " + token)
    tests = read(required[2])
    for token in (
        "sharedFirebaseModeAutomaticallyAuthorizesEveryProjectForTrustedEngineers",
        "sharedFirebaseModeRequiresSignedTrustedIssuerAndNeverOverrulesDeny",
        "requireApiProjectAccess",
        "requireWebSocketWorkspaceAccess",
        "requireLspWorkspaceAccess",
        "requireCollaborationWrite",
        "Permission.AUTHORIZATION_MANAGE",
        "outsider",
        "forged",
        "privateMode",
    ):
        if token not in tests:
            problems.append("cross-service Firebase role regression removed: " + token)

    for path in required[3:6]:
        code = read(path)
        for token in ("SharedFirebaseEngineeringPolicy.fromEnvironment(env)",
                      "new AuthorizationService("):
            if token not in code:
                problems.append("API/LSP/GLSP policy wiring missing: " + path + " " + token)

    canonical = [read(path) for path in required[6:9]]
    if len(set(canonical)) != 1:
        problems.append("canonical Cloud Build files drifted")
    for path in required[6:10]:
        manifest = read(path)
        for token in (
            "_KIDE_SHARED_FIREBASE_ENGINEERS: 'false'",
            "KIDE_SHARED_FIREBASE_ENGINEERS=${_KIDE_SHARED_FIREBASE_ENGINEERS}",
        ):
            if token not in manifest:
                problems.append("backend deployment does not preserve safe shared-authorization default: " + path)

    operator = read(required[10])
    for token in (
        "I_UNDERSTAND_ALL_FIREBASE_USERS_CAN_EDIT_ALL_PROJECTS",
        "gcloud builds triggers update",
        "gcloud run services update",
        "_KIDE_SHARED_FIREBASE_ENGINEERS=true",
        "KIDE_SHARED_FIREBASE_ENGINEERS=true",
        "KIDE_FIREBASE_PROJECT_ID",
    ):
        if token not in operator:
            problems.append("operator activation consent or Firebase project check missing: " + token)
    for path in required[11:13]:
        code = read(path)
        for token in (
            "KIDE_SHARED_FIREBASE_ENGINEERS",
            "_KIDE_SHARED_FIREBASE_ENGINEERS",
            "I_UNDERSTAND_ALL_FIREBASE_USERS_CAN_EDIT_ALL_PROJECTS",
        ):
            if token not in code:
                problems.append("deployment mode retention or consent missing: " + path + " " + token)

    live = read(required[15])
    for token in (
        'KIDE_LIVE_SHARED_FIREBASE_ENGINEERS',
        'if (sharedMode === "true")',
        'expect(otherProjects.items.some((item) => item.id === created.id)).toBe(true)',
        'await assertGateway(page, cfg.api, other.token, created.workspaceId, "lsp")',
        'await assertGateway(page, cfg.api, other.token, created.workspaceId, "glsp")',
        'other.token, "GET", projectPath, 403',
    ):
        if token not in live:
            problems.append("live shared/private transport acceptance missing: " + token)

    doc = read(required[13])
    for token in ("every KIDE project", "single trusted team", "Reconnect API",
                  "KIDE_SHARED_FIREBASE_ENGINEERS=true", "private"):
        if token.lower() not in doc.lower():
            # User-facing wording may differ, but a clear rollout and safety statement is mandatory.
            problems.append("shared project policy operator documentation missing: " + token)
    if "python3 scripts/verify_shared_firebase_access.py" not in read(required[14]):
        problems.append("shared Firebase access contract must run in PR CI")
    return problems


def main() -> int:
    errors = verify()
    for message in errors:
        print("ERROR:", message, file=sys.stderr)
    if errors:
        return 2
    print("PR105 SHARED FIREBASE REST/LSP/GLSP AUTHORIZATION VERIFIED; PRODUCTION OPT-IN REQUIRED")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
