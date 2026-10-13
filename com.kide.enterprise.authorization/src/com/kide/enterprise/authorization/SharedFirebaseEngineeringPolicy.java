package com.kide.enterprise.authorization;

import java.util.Map;
import java.util.Objects;

import com.kide.enterprise.identity.AuthenticationMethod;
import com.kide.enterprise.identity.PrincipalIdentity;
import com.kide.enterprise.identity.PrincipalKind;

/**
 * Explicit single-tenant shared-workspace policy. When the operator opts in,
 * every Firebase user authenticated by the configured Firebase project
 * receives ENGINEER permissions for every KIDE project. This mode intentionally
 * removes tenant privacy; it is never enabled by signing in or by default.
 *
 * REST, Xtext, GLSP and collaboration all use this same AuthorizationService.
 */
public final class SharedFirebaseEngineeringPolicy {
    public static final String ENV_KEY = "KIDE_SHARED_FIREBASE_ENGINEERS";

    private final String firebaseProjectId;

    private SharedFirebaseEngineeringPolicy(String firebaseProjectId) {
        this.firebaseProjectId = firebaseProjectId;
    }

    public static SharedFirebaseEngineeringPolicy disabled() {
        return new SharedFirebaseEngineeringPolicy(null);
    }

    public static SharedFirebaseEngineeringPolicy fromEnvironment(Map<String, String> env) {
        Objects.requireNonNull(env, "env");
        String setting = env.getOrDefault(ENV_KEY, "false").trim();
        if (setting.isEmpty() || "false".equalsIgnoreCase(setting)) return disabled();
        if (!"true".equalsIgnoreCase(setting)) {
            throw new IllegalArgumentException(ENV_KEY + " must be true or false");
        }
        String project = env.get("KIDE_FIREBASE_PROJECT_ID");
        if (project == null || !project.matches("[a-z][a-z0-9-]{3,62}")) {
            throw new IllegalArgumentException(
                    "Shared Firebase engineering requires a valid Firebase project ID");
        }
        return new SharedFirebaseEngineeringPolicy(project);
    }

    public boolean enabled() {
        return firebaseProjectId != null;
    }

    public boolean grants(PrincipalIdentity principal, Permission permission) {
        if (!enabled() || principal == null || !Role.ENGINEER.grants(permission)) return false;
        if (principal.kind() != PrincipalKind.HUMAN
                || principal.method() != AuthenticationMethod.FIREBASE_ID_TOKEN) return false;
        String uid = principal.subject();
        return uid != null && uid.matches("[A-Za-z0-9_-]{1,128}")
                && ("https://securetoken.google.com/" + firebaseProjectId).equals(principal.issuer())
                && ("firebase:" + firebaseProjectId + "#" + uid).equals(principal.id());
    }
}
