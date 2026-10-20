package com.kide.enterprise.authorization.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.kide.enterprise.authorization.AccessDeniedException;
import com.kide.enterprise.authorization.AuthorizationAffordances;
import com.kide.enterprise.authorization.AuthorizationEnforcer;
import com.kide.enterprise.authorization.AuthorizationPolicySnapshot;
import com.kide.enterprise.authorization.AuthorizationService;
import com.kide.enterprise.authorization.InMemoryAuthorizationPolicyStore;
import com.kide.enterprise.authorization.HostedRegistryAuthorizationPolicyStore;
import com.kide.enterprise.authorization.Permission;
import com.kide.enterprise.authorization.Role;
import com.kide.enterprise.authorization.RoleBinding;
import com.kide.enterprise.authorization.ServerAuthorizationGate;
import com.kide.enterprise.authorization.SharedFirebaseEngineeringPolicy;
import com.kide.enterprise.context.EnterpriseContext;
import com.kide.enterprise.context.FileHostedProjectRegistry;
import com.kide.enterprise.context.EnterpriseContextResult;
import com.kide.enterprise.context.EnterpriseContextStore;
import com.kide.enterprise.context.EnterpriseScope;
import com.kide.enterprise.identity.AuthenticatedSession;
import com.kide.enterprise.identity.AuthenticationMethod;
import com.kide.enterprise.identity.PrincipalIdentity;
import com.kide.enterprise.identity.PrincipalKind;

public class AuthorizationServiceTest {

    @Test
    public void roleMatrixIsLeastPrivilege() {
        assertTrue(Role.ADMINISTRATOR.grants(Permission.AUTHORIZATION_MANAGE));
        assertTrue(Role.ENGINEER.grants(Permission.MODEL_WRITE));
        assertTrue(Role.ENGINEER.grants(Permission.MODEL_SYNTHESIZE));
        assertTrue(Role.ENGINEER.grants(Permission.COLLABORATION_WRITE));
        assertTrue(Role.ENGINEER.grants(Permission.REVIEW_COMMENT));
        assertTrue(Role.ENGINEER.grants(Permission.KNOWLEDGE_READ));
        assertTrue(Role.ENGINEER.grants(Permission.KNOWLEDGE_TRACE_WRITE));
        assertFalse(Role.ENGINEER.grants(Permission.REVIEW_APPROVE));
        assertFalse(Role.ENGINEER.grants(Permission.AUTHORIZATION_MANAGE));
        assertTrue(Role.REVIEWER.grants(Permission.MODEL_VALIDATE));
        assertTrue(Role.REVIEWER.grants(Permission.REVIEW_COMMENT));
        assertTrue(Role.REVIEWER.grants(Permission.REVIEW_APPROVE));
        assertTrue(Role.REVIEWER.grants(Permission.KNOWLEDGE_READ));
        assertFalse(Role.REVIEWER.grants(Permission.KNOWLEDGE_TRACE_WRITE));
        assertFalse(Role.REVIEWER.grants(Permission.COLLABORATION_WRITE));
        assertFalse(Role.REVIEWER.grants(Permission.MODEL_WRITE));
        assertTrue(Role.VIEWER.grants(Permission.MODEL_READ));
        assertTrue(Role.VIEWER.grants(Permission.COLLABORATION_READ));
        assertTrue(Role.VIEWER.grants(Permission.KNOWLEDGE_READ));
        assertFalse(Role.VIEWER.grants(Permission.KNOWLEDGE_TRACE_WRITE));
        assertFalse(Role.VIEWER.grants(Permission.REVIEW_COMMENT));
        assertFalse(Role.VIEWER.grants(Permission.MODEL_VALIDATE));
        assertTrue(Role.SERVICE_OPERATOR.grants(Permission.LSP_CONNECT));
        assertFalse(Role.SERVICE_OPERATOR.grants(Permission.AUTHORIZATION_MANAGE));
    }

    @Test
    public void denyByDefaultAndAncestorGrantInheritsDownward() throws Exception {
        Fixture fixture = Fixture.create("inherit");
        try {
            PrincipalIdentity principal = principal("user-a");
            InMemoryAuthorizationPolicyStore store = new InMemoryAuthorizationPolicyStore(
                    List.of(RoleBinding.allow(
                            principal.id(), Role.VIEWER, fixture.context.organization().id())));
            AuthorizationService service = new AuthorizationService(store);

            assertTrue(service.decide(
                    principal, fixture.context, EnterpriseScope.ORGANIZATION, Permission.PROJECT_READ).allowed());
            assertTrue(service.decide(
                    principal, fixture.context, EnterpriseScope.WORKSPACE, Permission.MODEL_READ).allowed());
            assertFalse(service.decide(
                    principal, fixture.context, EnterpriseScope.WORKSPACE, Permission.MODEL_WRITE).allowed());

            PrincipalIdentity unbound = principal("user-b");
            assertFalse(service.decide(
                    unbound, fixture.context, EnterpriseScope.PROJECT, Permission.MODEL_READ).allowed());
        } finally {
            fixture.close();
        }
    }

    @Test
    public void projectGrantDoesNotEscapeToAnotherSameNamedProject() throws Exception {
        Fixture first = Fixture.create("same-name");
        Fixture second = Fixture.create("same-name");
        try {
            PrincipalIdentity principal = principal("user-a");
            AuthorizationService service = new AuthorizationService(
                    new InMemoryAuthorizationPolicyStore(List.of(
                            RoleBinding.allow(principal.id(), Role.ENGINEER, first.context.project().id()))));

            assertTrue(service.decide(
                    principal, first.context, EnterpriseScope.WORKSPACE, Permission.MODEL_WRITE).allowed());
            assertFalse(service.decide(
                    principal, second.context, EnterpriseScope.PROJECT, Permission.MODEL_READ).allowed());
            assertFalse(service.decide(
                    principal, second.context, EnterpriseScope.WORKSPACE, Permission.LSP_CONNECT).allowed());
            assertEquals(first.context.project().displayName(), second.context.project().displayName());
            assertFalse(first.context.project().id().equals(second.context.project().id()));
        } finally {
            first.close();
            second.close();
        }
    }

    @Test
    public void explicitDenyOverridesInheritedAllow() throws Exception {
        Fixture fixture = Fixture.create("deny");
        try {
            PrincipalIdentity principal = principal("user-a");
            AuthorizationService service = new AuthorizationService(
                    new InMemoryAuthorizationPolicyStore(List.of(
                            RoleBinding.allow(principal.id(), Role.ENGINEER, fixture.context.organization().id()),
                            RoleBinding.deny(principal.id(), Role.ENGINEER, fixture.context.project().id()))));

            assertTrue(service.decide(
                    principal, fixture.context, EnterpriseScope.PORTFOLIO, Permission.MODEL_WRITE).allowed());
            assertFalse(service.decide(
                    principal, fixture.context, EnterpriseScope.PROJECT, Permission.MODEL_WRITE).allowed());
            assertFalse(service.decide(
                    principal, fixture.context, EnterpriseScope.WORKSPACE, Permission.MODEL_WRITE).allowed());
        } finally {
            fixture.close();
        }
    }

    @Test
    public void privilegeChangesTakeEffectWithoutSessionRestart() throws Exception {
        Fixture fixture = Fixture.create("revoke");
        try {
            PrincipalIdentity principal = principal("user-a");
            InMemoryAuthorizationPolicyStore store = new InMemoryAuthorizationPolicyStore(
                    List.of(RoleBinding.allow(principal.id(), Role.ENGINEER, fixture.context.project().id())));
            AuthorizationEnforcer enforcer =
                    new AuthorizationEnforcer(new AuthorizationService(store));

            try (AuthenticatedSession session = session(principal)) {
                assertTrue(enforcer.isAllowed(
                        session, fixture.context, EnterpriseScope.PROJECT, Permission.MODEL_WRITE));

                AuthorizationPolicySnapshot replacement = store.replace(0, List.of());
                assertEquals(1L, replacement.revision());
                assertFalse(enforcer.isAllowed(
                        session, fixture.context, EnterpriseScope.PROJECT, Permission.MODEL_WRITE));

                assertThrows(IllegalStateException.class,
                        () -> store.replace(0, List.of()));
            }
        } finally {
            fixture.close();
        }
    }

    @Test
    public void unauthorizedProtocolBoundariesFailClosed() throws Exception {
        Fixture fixture = Fixture.create("protocol");
        try {
            PrincipalIdentity principal = principal("user-a");
            ServerAuthorizationGate gate = new ServerAuthorizationGate(
                    new AuthorizationEnforcer(new AuthorizationService(
                            new InMemoryAuthorizationPolicyStore(List.of()))));
            try (AuthenticatedSession session = session(principal)) {
                assertThrows(AccessDeniedException.class,
                        () -> gate.requireApiProjectAccess(session, fixture.context));
                assertThrows(AccessDeniedException.class,
                        () -> gate.requireWebSocketWorkspaceAccess(session, fixture.context));
                assertThrows(AccessDeniedException.class,
                        () -> gate.requireLspWorkspaceAccess(session, fixture.context));
                assertThrows(AccessDeniedException.class,
                        () -> gate.requireKnowledgeRead(session, fixture.context));
                assertThrows(AccessDeniedException.class,
                        () -> gate.requireKnowledgeTraceWrite(session, fixture.context));
            }
        } finally {
            fixture.close();
        }
    }

    @Test
    public void uiAffordancesMirrorButDoNotReplaceEnforcement() throws Exception {
        Fixture fixture = Fixture.create("ui");
        try {
            PrincipalIdentity principal = principal("user-a");
            AuthorizationEnforcer enforcer = new AuthorizationEnforcer(new AuthorizationService(
                    new InMemoryAuthorizationPolicyStore(List.of(
                            RoleBinding.allow(principal.id(), Role.REVIEWER, fixture.context.project().id())))));
            AuthorizationAffordances affordances = new AuthorizationAffordances(enforcer);

            try (AuthenticatedSession session = session(principal)) {
                Set<Permission> visible =
                        affordances.visibleActions(session, fixture.context, EnterpriseScope.PROJECT);
                assertTrue(visible.contains(Permission.MODEL_READ));
                assertTrue(visible.contains(Permission.MODEL_VALIDATE));
                assertFalse(visible.contains(Permission.MODEL_WRITE));
                assertThrows(AccessDeniedException.class,
                        () -> enforcer.require(
                                session, fixture.context, EnterpriseScope.PROJECT, Permission.MODEL_WRITE));
            }
        } finally {
            fixture.close();
        }
    }

    @Test
    public void sharedFirebaseModeAutomaticallyAuthorizesEveryProjectForTrustedEngineers() throws Exception {
        Fixture first = Fixture.create("firebase-shared-one");
        Fixture second = Fixture.create("firebase-shared-two");
        try {
            var policy = SharedFirebaseEngineeringPolicy.fromEnvironment(
                    Map.of("KIDE_SHARED_FIREBASE_ENGINEERS", "true",
                            "KIDE_FIREBASE_PROJECT_ID", "kide-eclipse"));
            PrincipalIdentity alice = firebase("kide-eclipse", "alice");
            PrincipalIdentity bob = firebase("kide-eclipse", "bob");
            ServerAuthorizationGate gate = new ServerAuthorizationGate(
                    new AuthorizationEnforcer(new AuthorizationService(
                            new InMemoryAuthorizationPolicyStore(List.of()), policy)));
            for (EnterpriseContext context : List.of(first.context, second.context)) {
                try (AuthenticatedSession session = session(alice);
                     AuthenticatedSession otherSession = session(bob)) {
                    gate.requireApiProjectAccess(session, context);
                    gate.requireWebSocketWorkspaceAccess(session, context);
                    gate.requireLspWorkspaceAccess(session, context);
                    gate.requireModelWrite(session, context);
                    gate.requireCollaborationRead(session, context);
                    gate.requireCollaborationWrite(otherSession, context);
                    assertFalse(new AuthorizationService(
                            new InMemoryAuthorizationPolicyStore(List.of()), policy)
                            .decide(alice, context, EnterpriseScope.PROJECT,
                                    Permission.AUTHORIZATION_MANAGE).allowed());
                }
            }
        } finally {
            first.close();
            second.close();
        }
    }

    @Test
    public void sharedFirebaseModeRequiresSignedTrustedIssuerAndNeverOverrulesDeny() throws Exception {
        Fixture fixture = Fixture.create("firebase-shared-deny");
        try {
            var policy = SharedFirebaseEngineeringPolicy.fromEnvironment(
                    Map.of("KIDE_SHARED_FIREBASE_ENGINEERS", "true",
                            "KIDE_FIREBASE_PROJECT_ID", "kide-eclipse"));
            PrincipalIdentity alice = firebase("kide-eclipse", "alice");
            PrincipalIdentity outsider = firebase("outsider-project", "alice");
            PrincipalIdentity forged = new PrincipalIdentity(
                    "firebase:kide-eclipse#alice", "alice", PrincipalKind.HUMAN,
                    AuthenticationMethod.OIDC_PKCE, "https://securetoken.google.com/kide-eclipse",
                    "alice", Map.of());
            var denied = new AuthorizationService(
                    new InMemoryAuthorizationPolicyStore(List.of(
                            RoleBinding.deny(alice.id(), Role.ENGINEER,
                                    fixture.context.project().id()))), policy);
            assertFalse(denied.decide(alice, fixture.context, EnterpriseScope.PROJECT,
                    Permission.MODEL_READ).allowed());
            assertFalse(denied.decide(outsider, fixture.context, EnterpriseScope.PROJECT,
                    Permission.MODEL_READ).allowed());
            assertFalse(denied.decide(forged, fixture.context, EnterpriseScope.PROJECT,
                    Permission.MODEL_READ).allowed());
            assertFalse(denied.decide(principal("local"), fixture.context, EnterpriseScope.PROJECT,
                    Permission.MODEL_READ).allowed());

            var privateMode = new AuthorizationService(
                    new InMemoryAuthorizationPolicyStore(List.of()),
                    SharedFirebaseEngineeringPolicy.fromEnvironment(Map.of(
                            "KIDE_FIREBASE_PROJECT_ID", "kide-eclipse")));
            assertFalse(privateMode.decide(alice, fixture.context, EnterpriseScope.PROJECT,
                    Permission.API_ACCESS).allowed());
            assertThrows(IllegalArgumentException.class,
                    () -> SharedFirebaseEngineeringPolicy.fromEnvironment(Map.of(
                            "KIDE_SHARED_FIREBASE_ENGINEERS", "yes",
                            "KIDE_FIREBASE_PROJECT_ID", "kide-eclipse")));
            assertThrows(IllegalArgumentException.class,
                    () -> SharedFirebaseEngineeringPolicy.fromEnvironment(Map.of(
                            "KIDE_SHARED_FIREBASE_ENGINEERS", "true")));
        } finally {
            fixture.close();
        }
    }

    @Test
    public void ownerOnlyRegistryGrantsJustTheCreatorAndPreservesExplicitDeny() throws Exception {
        Path root = Files.createTempDirectory("kide-pr111-owner-isolation-");
        try {
            FileHostedProjectRegistry registry = new FileHostedProjectRegistry(
                    Files.createDirectory(root.resolve("registry")));
            PrincipalIdentity alice = firebase("kide-eclipse", "owner-alice");
            PrincipalIdentity bob = firebase("kide-eclipse", "owner-bob");
            var aliceProject = registry.createProject("Alice controls", alice.id());
            var bobProject = registry.createProject("Bob controls", bob.id());
            AuthorizationService privatePolicy = new AuthorizationService(
                    new HostedRegistryAuthorizationPolicyStore(registry, List.of()));
            ServerAuthorizationGate gate = new ServerAuthorizationGate(
                    new AuthorizationEnforcer(privatePolicy));
            try (AuthenticatedSession a = session(alice);
                 AuthenticatedSession b = session(bob)) {
                for (var own : List.of(aliceProject.context())) {
                    gate.requireApiProjectAccess(a, own);
                    gate.requireWebSocketWorkspaceAccess(a, own);
                    gate.requireLspWorkspaceAccess(a, own);
                    gate.requireModelWrite(a, own);
                    gate.requireCollaborationWrite(a, own);
                    assertThrows(AccessDeniedException.class,
                            () -> gate.requireApiProjectAccess(b, own));
                    assertThrows(AccessDeniedException.class,
                            () -> gate.requireLspWorkspaceAccess(b, own));
                }
                gate.requireApiProjectAccess(b, bobProject.context());
                gate.requireLspWorkspaceAccess(b, bobProject.context());
                assertThrows(AccessDeniedException.class,
                        () -> gate.requireModelWrite(a, bobProject.context()));
                assertThrows(AccessDeniedException.class,
                        () -> gate.requireCollaborationWrite(a, bobProject.context()));
            }
            // A project-scoped DENY must beat the creator's persisted allow.
            var denied = new AuthorizationService(
                    new HostedRegistryAuthorizationPolicyStore(registry, List.of(
                            RoleBinding.deny(alice.id(), Role.ENGINEER,
                                    aliceProject.context().project().id()))));
            assertFalse(denied.decide(alice, aliceProject.context(),
                    EnterpriseScope.PROJECT, Permission.MODEL_READ).allowed());
            assertFalse(denied.decide(alice, aliceProject.context(),
                    EnterpriseScope.WORKSPACE, Permission.LSP_CONNECT).allowed());
        } finally {
            try (var files = Files.walk(root)) {
                files.sorted(Comparator.reverseOrder()).forEach(item -> {
                    try { Files.deleteIfExists(item); }
                    catch (IOException ignored) { /* temporary test data */ }
                });
            }
        }
    }

    private static PrincipalIdentity firebase(String projectId, String uid) {
        return new PrincipalIdentity("firebase:" + projectId + "#" + uid, uid,
                PrincipalKind.HUMAN, AuthenticationMethod.FIREBASE_ID_TOKEN,
                "https://securetoken.google.com/" + projectId, uid, Map.of());
    }

    private static PrincipalIdentity principal(String id) {
        return new PrincipalIdentity(
                "offline:" + id, id, PrincipalKind.LOCAL_OFFLINE,
                AuthenticationMethod.LOCAL_OFFLINE, "", id, Map.of("offline", "true"));
    }

    private static AuthenticatedSession session(PrincipalIdentity principal) {
        return new AuthenticatedSession(principal, null, Clock.systemUTC());
    }

    private static final class Fixture implements AutoCloseable {
        final Path root;
        final EnterpriseContext context;

        private Fixture(Path root, EnterpriseContext context) {
            this.root = root;
            this.context = context;
        }

        static Fixture create(String name) throws IOException {
            Path root = Files.createTempDirectory("kide-pr18-" + name + "-");
            Path workspace = Files.createDirectories(root.resolve("workspace"));
            Path project = Files.createDirectories(root.resolve("project"));
            EnterpriseContextResult result = new EnterpriseContextStore().provision(
                    workspace, project, "Shared Org", "Shared Portfolio",
                    "Same Project Name", "Same Workspace Name");
            if (!result.isReady()) {
                throw new AssertionError("context provisioning failed: " + result.summary());
            }
            return new Fixture(root, result.context().get());
        }

        @Override
        public void close() {
            if (root == null || !Files.exists(root)) return;
            try (java.util.stream.Stream<Path> stream = Files.walk(root)) {
                stream.sorted(Comparator.reverseOrder()).forEach(path -> {
                    try {
                        Files.deleteIfExists(path);
                    } catch (IOException ignored) {
                        // Test cleanup only.
                    }
                });
            } catch (IOException ignored) {
                // Test cleanup only.
            }
        }
    }
}
