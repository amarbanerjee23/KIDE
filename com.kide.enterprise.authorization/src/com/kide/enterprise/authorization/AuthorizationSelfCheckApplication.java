package com.kide.enterprise.authorization;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

import com.kide.enterprise.context.EnterpriseContext;
import com.kide.enterprise.context.EnterpriseContextResult;
import com.kide.enterprise.context.EnterpriseContextStore;
import com.kide.enterprise.context.EnterpriseScope;
import com.kide.enterprise.identity.AuthenticatedSession;
import com.kide.enterprise.identity.AuthenticationMethod;
import com.kide.enterprise.identity.PrincipalIdentity;
import com.kide.enterprise.identity.PrincipalKind;

public final class AuthorizationSelfCheckApplication implements IApplication {
    @Override
    public Object start(IApplicationContext applicationContext) {
        Path root = null;
        try {
            root = Files.createTempDirectory("kide-pr18-authz-");
            Path workspace = Files.createDirectories(root.resolve("workspace"));
            Path project = Files.createDirectories(root.resolve("project"));
            EnterpriseContextResult provisioned = new EnterpriseContextStore().provision(
                    workspace, project, "Self Check Org", "Self Check Portfolio",
                    "Self Check Project", "Self Check Workspace");
            if (!provisioned.isReady()) return fail("enterprise context was not ready");
            EnterpriseContext context = provisioned.context().get();

            PrincipalIdentity principal = new PrincipalIdentity(
                    "offline:selfcheck", "Self Check", PrincipalKind.LOCAL_OFFLINE,
                    AuthenticationMethod.LOCAL_OFFLINE, "", "selfcheck", Map.of("offline", "true"));
            InMemoryAuthorizationPolicyStore policies = new InMemoryAuthorizationPolicyStore(
                    List.of(RoleBinding.allow(
                            principal.id(), Role.ENGINEER, context.project().id())));
            AuthorizationEnforcer enforcer =
                    new AuthorizationEnforcer(new AuthorizationService(policies));

            try (AuthenticatedSession session = new AuthenticatedSession(principal, null, Clock.systemUTC())) {
                enforcer.require(session, context, EnterpriseScope.WORKSPACE, Permission.MODEL_WRITE);
                if (enforcer.isAllowed(
                        session, context, EnterpriseScope.PROJECT, Permission.AUTHORIZATION_MANAGE)) {
                    return fail("engineer unexpectedly received authorization administration");
                }

                policies.replace(0, List.of());
                if (enforcer.isAllowed(session, context, EnterpriseScope.WORKSPACE, Permission.MODEL_WRITE)) {
                    return fail("revoked privilege remained active");
                }
            }

            System.out.println("KIDE PR18 AUTHORIZATION SELF-CHECK OK");
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
        System.err.println("KIDE PR18 AUTHORIZATION SELF-CHECK FAILED: " + message);
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
