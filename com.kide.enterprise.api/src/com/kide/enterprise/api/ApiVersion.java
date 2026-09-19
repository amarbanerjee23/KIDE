package com.kide.enterprise.api;

public enum ApiVersion {
    V1("v1", "/api/v1");

    private final String token;
    private final String basePath;

    ApiVersion(String token, String basePath) {
        this.token = token;
        this.basePath = basePath;
    }

    public String token() {
        return token;
    }

    public String basePath() {
        return basePath;
    }
}
