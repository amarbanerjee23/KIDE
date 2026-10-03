package com.kide.enterprise.server;

import java.net.http.HttpClient;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

import com.kide.codegen.ProjectGenerationService;
import com.kide.enterprise.audit.InMemoryAuditLedger;
import com.kide.enterprise.authorization.AuthorizationEnforcer;
import com.kide.enterprise.authorization.AuthorizationService;
import com.kide.enterprise.authorization.InMemoryAuthorizationPolicyStore;
import com.kide.enterprise.authorization.Role;
import com.kide.enterprise.authorization.RoleBinding;
import com.kide.enterprise.authorization.ServerAuthorizationGate;
import com.kide.enterprise.context.EnterpriseContext;
import com.kide.enterprise.context.EnterpriseContextResult;
import com.kide.enterprise.context.EnterpriseContextStore;
import com.kide.enterprise.context.EnterpriseId;
import com.kide.enterprise.context.EnterpriseScope;
import com.kide.enterprise.identity.FirebaseIdTokenAuthenticator;
import com.kide.enterprise.identity.FirebaseIdTokenConfig;
import com.kide.enterprise.identity.GoogleFirebaseKeyProvider;
import com.kide.enterprise.modelrepo.FileModelRepository;
import com.kide.knowledge.EmbeddedKnowledgeRepository;
import com.kide.knowledge.KnowledgeDataset;
import com.kide.knowledge.KnowledgeProvenance;
import com.kide.knowledge.KnowledgeRepository;
import com.kide.knowledge.KnowledgeTerm;
import com.kide.knowledge.KnowledgeTraceStore;
import com.kide.knowledge.KnowledgeTriple;
import com.kide.knowledge.KnowledgeVocabulary;
import com.kide.synthesis.ProjectSynthesisService;
import com.kide.synthesis.SynthesisVocabulary;

public final class EnterpriseApiApplication implements IApplication {
    private volatile EnterpriseApiServer server;
    private volatile FirebaseIdTokenConfig firebaseConfig;

    @Override
    public Object start(IApplicationContext applicationContext) {
        try {
            Map<String, String> env = System.getenv();
            String bind = env.getOrDefault("KIDE_API_BIND", "127.0.0.1");
            int port = integer(env, "KIDE_API_PORT", 8081);
            boolean trustForwarded = bool(env, "KIDE_API_TRUST_FORWARDED_PROTO", false);
            boolean allowInsecureLoopback = bool(env, "KIDE_API_ALLOW_INSECURE_LOOPBACK", false);
            if (allowInsecureLoopback && !isLoopback(bind)) {
                throw new IllegalArgumentException(
                        "Insecure API transport is permitted only on a loopback bind");
            }

            EnterpriseApiConfig config = new EnterpriseApiConfig(
                    bind,
                    port,
                    integer(env, "KIDE_API_MAX_REQUEST_BYTES", 1024 * 1024),
                    Duration.ofSeconds(integer(env, "KIDE_API_IDLE_SECONDS", 30)),
                    !allowInsecureLoopback,
                    trustForwarded,
                    csv(env.get("KIDE_API_TRUSTED_PROXY_ADDRESSES")),
                    csv(env.get("KIDE_API_ALLOWED_ORIGINS")));

            Path workspaceRoot = Path.of(required(env, "KIDE_API_WORKSPACE_ROOT"));
            Path projectRoot = Path.of(required(env, "KIDE_API_PROJECT_ROOT"));
            EnterpriseContextResult loaded =
                    new EnterpriseContextStore().load(workspaceRoot, projectRoot);
            if (!loaded.isReady()) {
                throw new IllegalStateException(
                        "API enterprise context is not ready: " + loaded.summary());
            }
            EnterpriseContext context = loaded.context().orElseThrow();

            List<RoleBinding> bindings = parseBindings(
                    required(env, "KIDE_API_ROLE_BINDINGS"), context);
            ServerAuthorizationGate authorization = new ServerAuthorizationGate(
                    new AuthorizationEnforcer(
                            new AuthorizationService(
                                    new InMemoryAuthorizationPolicyStore(bindings))));

            firebaseConfig = new FirebaseIdTokenConfig(
                    required(env, "KIDE_FIREBASE_PROJECT_ID"),
                    Duration.ofSeconds(integer(env, "KIDE_FIREBASE_KEYS_TIMEOUT_SECONDS", 10)));
            HttpClient firebaseHttpClient = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(
                            integer(env, "KIDE_FIREBASE_CONNECT_TIMEOUT_SECONDS", 10)))
                    .build();
            FirebaseIdTokenAuthenticator authenticator =
                    new FirebaseIdTokenAuthenticator(
                            firebaseConfig,
                            new GoogleFirebaseKeyProvider(
                                    firebaseConfig,
                                    firebaseHttpClient,
                                    Clock.systemUTC()),
                            Clock.systemUTC());

            FileModelRepository modelRepository = new FileModelRepository(projectRoot);
            ProjectCollaborationService collaboration =
                    new ProjectCollaborationService(
                            projectRoot, modelRepository, Clock.systemUTC());
            EmbeddedKnowledgeRepository knowledgeRepository =
                    new EmbeddedKnowledgeRepository(projectRoot);
            if (bool(env, "KIDE_API_BOOTSTRAP_STARTER_KNOWLEDGE", false)
                    && knowledgeRepository.snapshot().isEmpty()) {
                knowledgeRepository.replace(
                        starterKnowledge(context),
                        KnowledgeRepository.MISSING_ETAG);
            }
            ProjectKnowledgeService knowledge = new ProjectKnowledgeService(
                    knowledgeRepository,
                    new KnowledgeTraceStore(projectRoot, Clock.systemUTC()),
                    modelRepository);
            ProjectSynthesisService synthesis = new ProjectSynthesisService(
                    projectRoot, modelRepository, knowledgeRepository);
            ProjectGenerationService generation = new ProjectGenerationService(
                    projectRoot, modelRepository, knowledgeRepository, synthesis);
            server = new EnterpriseApiServer(
                    config,
                    authenticator::authenticateAuthorizationHeader,
                    context,
                    authorization,
                    modelRepository,
                    collaboration,
                    knowledge,
                    synthesis,
                    generation,
                    new InMemoryAuditLedger(Clock.systemUTC()),
                    Clock.systemUTC());
            server.start();
            System.out.println("KIDE ENTERPRISE API READY port=" + server.localPort());
            server.join();
            return IApplication.EXIT_OK;
        } catch (RuntimeException e) {
            System.err.println("KIDE enterprise API startup failed: " + safe(e.getMessage()));
            return Integer.valueOf(2);
        } catch (Exception e) {
            System.err.println("KIDE enterprise API startup failed");
            return Integer.valueOf(2);
        } finally {
            closeConfig();
        }
    }

    @Override
    public void stop() {
        EnterpriseApiServer current = server;
        if (current != null) {
            try {
                current.close();
            } catch (RuntimeException ignored) {
                // Shutdown must not expose provider or credential diagnostics.
            }
        }
        closeConfig();
    }

    private static KnowledgeDataset starterKnowledge(EnterpriseContext context) {
        String capability = "urn:kide:capability:Observe";
        String device = "urn:kide:device:camera";
        return new KnowledgeDataset(
                KnowledgeDataset.CURRENT_SCHEMA,
                "kide-starter",
                "PROJECT",
                context.project().id().value(),
                new KnowledgeProvenance(
                        "urn:kide:starter:catalogue",
                        "KIDE",
                        "kide-runtime",
                        Clock.systemUTC().millis(),
                        "INTERNAL"),
                List.of(
                        new KnowledgeTriple(
                                capability,
                                KnowledgeVocabulary.RDF_TYPE,
                                KnowledgeTerm.iri(KnowledgeVocabulary.CAPABILITY)),
                        new KnowledgeTriple(
                                capability,
                                KnowledgeVocabulary.LABEL,
                                KnowledgeTerm.literal("Observe")),
                        new KnowledgeTriple(
                                device,
                                KnowledgeVocabulary.RDF_TYPE,
                                KnowledgeTerm.iri(KnowledgeVocabulary.DEVICE)),
                        new KnowledgeTriple(
                                device,
                                KnowledgeVocabulary.LABEL,
                                KnowledgeTerm.literal("Camera")),
                        new KnowledgeTriple(
                                device,
                                SynthesisVocabulary.PROVIDES_CAPABILITY,
                                KnowledgeTerm.iri(capability)),
                        new KnowledgeTriple(
                                device,
                                SynthesisVocabulary.PROVIDES_INTERFACE,
                                KnowledgeTerm.iri("urn:kide:interface:Device")),
                        new KnowledgeTriple(
                                device,
                                SynthesisVocabulary.PRIORITY,
                                KnowledgeTerm.literal("10"))));
    }

    private static List<RoleBinding> parseBindings(String raw, EnterpriseContext context) {
        List<RoleBinding> result = new ArrayList<>();
        for (String entry : raw.split(";")) {
            if (entry.isBlank()) continue;
            String[] parts = entry.split("\\|", -1);
            if (parts.length != 3) {
                throw new IllegalArgumentException(
                        "Each KIDE_API_ROLE_BINDINGS entry must be principal|ROLE|scopeId");
            }
            String principal = parts[0].trim();
            Role role = Role.valueOf(parts[1].trim());
            EnterpriseId id = parseContextId(parts[2].trim(), context);
            result.add(RoleBinding.allow(principal, role, id));
        }
        if (result.isEmpty()) {
            throw new IllegalArgumentException("At least one API role binding is required");
        }
        return List.copyOf(result);
    }

    private static EnterpriseId parseContextId(String raw, EnterpriseContext context) {
        for (EnterpriseScope scope : EnterpriseScope.values()) {
            EnterpriseId id = EnterpriseId.tryParse(scope, raw).orElse(null);
            if (id != null) {
                if (!id.equals(context.node(scope).id())) {
                    throw new IllegalArgumentException(
                            "API role binding scope is outside the configured enterprise context");
                }
                return id;
            }
        }
        throw new IllegalArgumentException("API role binding contains an invalid E04 scope ID");
    }

    private static String required(Map<String, String> env, String key) {
        String value = env.get(key);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(key + " is required");
        }
        return value.trim();
    }

    private static int integer(Map<String, String> env, String key, int fallback) {
        String raw = env.get(key);
        if (raw == null || raw.isBlank()) return fallback;
        try {
            return Integer.parseInt(raw.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(key + " must be an integer");
        }
    }

    private static boolean bool(Map<String, String> env, String key, boolean fallback) {
        String raw = env.get(key);
        if (raw == null || raw.isBlank()) return fallback;
        if ("true".equalsIgnoreCase(raw)) return true;
        if ("false".equalsIgnoreCase(raw)) return false;
        throw new IllegalArgumentException(key + " must be true or false");
    }

    private static Set<String> csv(String raw) {
        if (raw == null || raw.isBlank()) return Set.of();
        return Arrays.stream(raw.split(","))
                .map(String::trim)
                .filter(value -> !value.isEmpty())
                .collect(Collectors.toUnmodifiableSet());
    }

    private static boolean isLoopback(String host) {
        return "127.0.0.1".equals(host) || "::1".equals(host)
                || "localhost".equalsIgnoreCase(host);
    }

    private static String safe(String message) {
        if (message == null || message.isBlank()) return "configuration error";
        String lower = message.toLowerCase(java.util.Locale.ROOT);
        if (lower.contains("token") || lower.contains("secret")
                || lower.contains("password") || lower.contains("credential")) {
            return "authentication configuration error";
        }
        return message.replace('\r', ' ').replace('\n', ' ');
    }

    private synchronized void closeConfig() {
        firebaseConfig = null;
    }
}
