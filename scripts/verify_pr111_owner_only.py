#!/usr/bin/env python3
"""PR111: fail closed on unsafe Firebase owner-onboarding policy changes.

This is source/contract verification. It is NOT proof that a Cloud Run
registry volume has been qualified or that staging Firebase works.
"""
from __future__ import annotations
from pathlib import Path
import sys

ROOT = Path(__file__).resolve().parents[1]


def read(path: str) -> str:
    return (ROOT / path).read_text(encoding="utf-8")


def verify() -> list[str]:
    failures: list[str] = []
    required: dict[str, tuple[str, ...]] = {
        "com.kide.enterprise.identity/src/com/kide/enterprise/identity/FirebaseIdTokenConfig.java": (
            'env.getOrDefault("KIDE_FIREBASE_OWNER_ONLY_ENGINEERS", "false")',
            '"registry-posix".equals(env.get("KIDE_STORAGE_MODE"))',
            '"/data/hosted-projects".equals(env.get("KIDE_HOSTED_PROJECTS_ROOT"))',
            '"KIDE_SHARED_FIREBASE_ENGINEERS"',
            'new FirebaseIdTokenConfig(projectId, timeout, "true".equals(owner))',
        ),
        "com.kide.enterprise.identity/src/com/kide/enterprise/identity/FirebaseIdTokenAuthenticator.java": (
            'config.requireVerifiedEmail()',
            'claims.get("email")',
            'claims.get("email_verified")',
            '!verified.getAsJsonPrimitive().isBoolean()',
            '!verified.getAsBoolean()',
        ),
        "com.kide.enterprise.server/src/com/kide/enterprise/server/EnterpriseApiApplication.java": (
            'FirebaseIdTokenConfig.fromEnvironment(', 'SharedFirebaseEngineeringPolicy.fromEnvironment(env)',
        ),
        "com.kide.languageserver.gateway/src/com/kide/languageserver/gateway/GatewayApplication.java": (
            'FirebaseIdTokenConfig.fromEnvironment(', 'SharedFirebaseEngineeringPolicy.fromEnvironment(env)',
        ),
        "com.kide.glsp/src/com/kide/glsp/GlspGatewayApplication.java": (
            'FirebaseIdTokenConfig.fromEnvironment(', 'SharedFirebaseEngineeringPolicy.fromEnvironment(env)',
        ),
        "com.kide.enterprise.authorization/src/com/kide/enterprise/authorization/AuthorizationService.java": (
            'if (binding.effect() == BindingEffect.DENY)',
            'boolean allowed = sharedFirebase.grants(principal, permission)',
        ),
        "com.kide.enterprise.authorization/src/com/kide/enterprise/authorization/HostedRegistryAuthorizationPolicyStore.java": (
            'HostedProjectOwnership.principalId(project)',
            'RoleBinding.allow(',
            'Role.ADMINISTRATOR, project.context().project().id()',
        ),
        "com.kide.enterprise.context/src/com/kide/enterprise/context/FileHostedProjectRegistry.java": (
            'HostedProjectOwnership.OWNER_KEY',
            'Files.move(staging, destination, StandardCopyOption.ATOMIC_MOVE)',
        ),
        "com.kide.enterprise.server/src/com/kide/enterprise/server/EnterpriseApiServer.java": (
            'hostedProjects.list(session)', 'hostedProjects.create(', 'hostedProjects.resolve(',
        ),
        "deploy/gcp/entrypoint.sh": (
            'KIDE_FIREBASE_OWNER_ONLY_ENGINEERS',
            'KIDE_SHARED_FIREBASE_ENGINEERS',
            'registry-posix',
            'KIDE-POSIX-REGISTRY-STAGING-v1',
        ),
        "deploy/gcp/deploy-registry-staging.sh": (
            'KIDE_FIREBASE_OWNER_ONLY_ENGINEERS=true',
            'KIDE_SHARED_FIREBASE_ENGINEERS=false',
            '--no-traffic',
        ),
        "deploy/gcp/cloudbuild-owner-registry-staging.yaml": (
            "_STAGING_APPROVAL: disabled",
            "_STORAGE_PREFLIGHT_APPROVAL: disabled",
            'isolated-staging-only',
            'staging-only-offline',
            'COMMIT_SHA',
            'image_summary.digest',
            'sha256:[0-9a-f]{64}',
            'bash deploy/gcp/deploy-registry-staging.sh',
            'Deploy isolated no-traffic owner-only staging revision',
        ),
        "deploy/gcp/configure-auto-deploy.sh": (
            '_KIDE_FIREBASE_OWNER_ONLY_ENGINEERS',
            'KIDE_FIREBASE_OWNER_ONLY_ENGINEERS',
            'normal Git-push trigger deploys legacy GCS FUSE',
        ),
        "deploy/gcp/deploy-cloud-run.sh": (
            'KIDE_FIREBASE_OWNER_ONLY_ENGINEERS',
            'not this legacy GCS release',
        ),
        "web/src/firebaseAuth.ts": (
            'async sendEmailVerification()',
            'requestType: "VERIFY_EMAIL"',
            'async idToken(forceRefresh = false)',
        ),
        "web/src/LandingPage.tsx": (
            'Send verification email',
            "I've verified my email",
        ),
        "web/src/App.tsx": (
            'await firebaseAuth.sendEmailVerification()',
            'await firebaseAuth.idToken(true)',
            'onRefreshVerification',
        ),
        "com.kide.enterprise.identity.tests/src/com/kide/enterprise/identity/tests/FirebaseIdTokenAuthenticationTest.java": (
            'verifiedEmailRequiredOnlyInOwnerOnlyMode',
            'ownerOnlyModeFailsClosedOnSharedOrUnqualifiedStorage',
        ),
        "com.kide.enterprise.authorization.tests/src/com/kide/enterprise/authorization/tests/AuthorizationServiceTest.java": (
            'ownerOnlyRegistryGrantsJustTheCreatorAndPreservesExplicitDeny',
            'assertThrows(AccessDeniedException.class',
        ),
        "web/tests/firebaseAuth.test.ts": (
            'sends Firebase\'s built-in email verification',
            'forces a fresh Firebase JWT',
        ),
    }
    for path, markers in required.items():
        if not (ROOT / path).is_file():
            failures.append(f"required owner-only policy resource missing: {path}")
            continue
        data = read(path)
        for marker in markers:
            if marker not in data:
                failures.append(f"owner-only / verified-email / DENY invariant missing: {path}: {marker}")

    canonical = [read(path) for path in (
        "cloudbuild.yaml",
        "deploy/gcp/cloudbuild.yaml",
        "deploy/gcp/cloudbuild-release.yaml",
    )]
    if len(set(canonical)) != 1:
        failures.append("canonical GCP Cloud Build manifests drifted")
    for path in (
        "cloudbuild.yaml",
        "deploy/gcp/cloudbuild.yaml",
        "deploy/gcp/cloudbuild-release.yaml",
        "deploy/gcp/cloudbuild-deploy.yaml",
    ):
        data = read(path)
        for token in (
            "_KIDE_FIREBASE_OWNER_ONLY_ENGINEERS: 'false'",
            "KIDE_FIREBASE_OWNER_ONLY_ENGINEERS=${_KIDE_FIREBASE_OWNER_ONLY_ENGINEERS}",
            'if [[ "${_KIDE_FIREBASE_OWNER_ONLY_ENGINEERS}" == true ]]',
            'exit 2',
        ):
            if token not in data:
                failures.append(f"legacy Git-push deployment may broaden access or overwrite owner-only registry: {path}: {token}")
        guard = 'if [[ "${_KIDE_FIREBASE_OWNER_ONLY_ENGINEERS}" == true ]]'
        deploy = 'gcloud run deploy "${_KIDE_SERVICE_NAME}"'
        if guard not in data or deploy not in data or data.index(guard) >= data.index(deploy):
            failures.append(f"legacy backend rollout happens before owner-only refusal: {path}")

    # Git-push staging must never target the legacy production service, nor
    # run a Cloud Run deployment before explicit operator approvals.
    staging = read("deploy/gcp/cloudbuild-owner-registry-staging.yaml")
    for absent in ('--no-traffic', '_STAGING_SERVICE:'):
        # The reviewed shell deployment owns the no-traffic flag; its invoking
        # Cloud Build manifest must at least declare the staging service.
        if absent == '_STAGING_SERVICE:' and absent not in staging:
            failures.append("staging Git-push trigger cannot select a dedicated service")
    if not (staging.index('id: Validate private-registry staging approval') <
            staging.index('id: Build source-pinned registry backend') <
            staging.index('id: Deploy isolated no-traffic owner-only staging revision')):
        failures.append("staging Git-push must check approvals before any build/deploy")

    return failures


def main() -> int:
    failures = verify()
    for issue in failures:
        print("ERROR:", issue, file=sys.stderr)
    if failures:
        return 2
    print("PR111 VERIFIED FIREBASE OWNER-ONLY POLICIES GUARDED (GCP LIVE NOT TESTED)")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
