package com.kide.enterprise.authorization;

import java.util.Objects;

public record AuthorizationDecision(boolean allowed, String reasonCode) {
    public AuthorizationDecision {
        Objects.requireNonNull(reasonCode, "reasonCode");
    }

    public static AuthorizationDecision allow() {
        return new AuthorizationDecision(true, "ALLOW_ROLE_GRANT");
    }

    public static AuthorizationDecision deny(String reasonCode) {
        return new AuthorizationDecision(false, reasonCode);
    }
}
