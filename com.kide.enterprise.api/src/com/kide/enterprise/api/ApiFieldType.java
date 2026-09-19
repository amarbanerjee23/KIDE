package com.kide.enterprise.api;

public enum ApiFieldType {
    STRING("string"),
    INTEGER("integer"),
    BOOLEAN("boolean"),
    OBJECT("object"),
    ARRAY("array");

    private final String openApiType;

    ApiFieldType(String openApiType) {
        this.openApiType = openApiType;
    }

    public String openApiType() {
        return openApiType;
    }
}
