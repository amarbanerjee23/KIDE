package com.kide.enterprise.audit;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.Map;
import java.util.UUID;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

import com.kide.enterprise.context.EnterpriseContext;
import com.kide.enterprise.context.EnterpriseContextResult;
import com.kide.enterprise.context.EnterpriseContextStore;
import com.kide.enterprise.identity.AuthenticationMethod;
import com.kide.enterprise.identity.PrincipalIdentity;
import com.kide.enterprise.identity.PrincipalKind;

public final class AuditSelfCheckApplication implements IApplication {
    @Override
    public Object start(IApplicationContext applicationContext) {
        Path root = null;
        try {
            root = Files.createTempDirectory("kide-pr19-audit-");
            Path workspace = Files.createDirectories(root.resolve("workspace"));
            Path project = Files.createDirectories(root.resolve("project"));
            EnterpriseContextResult provisioned = new EnterpriseContextStore().provision(
                    workspace, project, "Audit Org", "Audit Portfolio",
                    "Audit Project", "Audit Workspace");
            if (!provisioned.isReady()) return fail("enterprise context was not ready");
            EnterpriseContext context = provisioned.context().get();

            PrincipalIdentity principal = new PrincipalIdentity(
                    "offline:audit-selfcheck", "Audit Self Check",
                    PrincipalKind.LOCAL_OFFLINE, AuthenticationMethod.LOCAL_OFFLINE,
                    "", "audit-selfcheck", Map.of("offline", "true"));

            Clock clock = Clock.fixed(Instant.parse("2026-09-19T00:00:00Z"), ZoneOffset.UTC);
            InMemoryAuditLedger ledger = new InMemoryAuditLedger(clock);
            UUID correlation = UUID.randomUUID();

            AuditEvent first = ledger.append(AuditEventDraft.of(
                    principal, "desktop:eclipse", context, "rev-1",
                    "model.read", "model", "selfcheck-model", AuditOutcome.SUCCESS,
                    correlation, null,
                    Map.of("access_token", "must-not-appear", "source", "selfcheck")));
            AuditEvent second = ledger.append(AuditEventDraft.failure(
                    principal, "desktop:eclipse", context, "rev-2",
                    "model.write", "model", "selfcheck-model",
                    correlation, first.eventId(), "SELF_CHECK_FAILURE"));

            if (!ledger.verify()) return fail("hash chain verification failed");
            if (first.toString().contains("must-not-appear")) return fail("secret redaction failed");
            if (!first.hash().equals(second.previousHash())) return fail("hash chain linkage failed");
            if (!first.eventId().equals(second.causationId())) return fail("causation linkage failed");
            if (second.outcome() != AuditOutcome.FAILURE) return fail("failure outcome missing");

            System.out.println("KIDE PR19 AUDIT SELF-CHECK OK");
            return IApplication.EXIT_OK;
        } catch (Exception e) {
            return fail("guarded runtime failure: " + e.getClass().getSimpleName());
        } finally {
            deleteTree(root);
        }
    }

    @Override
    public void stop() { }

    private static Integer fail(String message) {
        System.err.println("KIDE PR19 AUDIT SELF-CHECK FAILED: " + message);
        return Integer.valueOf(2);
    }

    private static void deleteTree(Path root) {
        if (root == null || !Files.exists(root)) return;
        try (java.util.stream.Stream<Path> stream = Files.walk(root)) {
            stream.sorted(Comparator.reverseOrder()).forEach(path -> {
                try {
                    Files.deleteIfExists(path);
                } catch (IOException ignored) {
                    // Temporary self-check cleanup only.
                }
            });
        } catch (IOException ignored) {
            // Temporary self-check cleanup only.
        }
    }
}
