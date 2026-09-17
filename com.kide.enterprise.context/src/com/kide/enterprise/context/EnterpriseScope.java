package com.kide.enterprise.context;

/** Stable hierarchy levels used by KIDE enterprise context identities. */
public enum EnterpriseScope {
    ORGANIZATION("organization"),
    PORTFOLIO("portfolio"),
    PROJECT("project"),
    WORKSPACE("workspace");

    private final String token;

    EnterpriseScope(String token) {
        this.token = token;
    }

    public String token() {
        return token;
    }
}
