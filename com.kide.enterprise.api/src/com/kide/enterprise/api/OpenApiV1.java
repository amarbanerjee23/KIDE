package com.kide.enterprise.api;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class OpenApiV1 {
    private OpenApiV1() { }

    public static String generateJson() {
        StringBuilder json = new StringBuilder(16384);
        json.append("{\n");
        json.append("  \"openapi\": \"3.1.0\",\n");
        json.append("  \"info\": {\"title\": \"KIDE Enterprise API\", \"version\": \"v1\"},\n");
        json.append("  \"paths\": {\n");

        List<ApiOperation> operations = ApiContractRegistry.v1();
        for (int i = 0; i < operations.size(); i++) {
            ApiOperation op = operations.get(i);
            json.append("    \"").append(escape(op.path())).append("\": {\n");
            json.append("      \"").append(op.method().name().toLowerCase()).append("\": {");
            json.append("\"operationId\": \"").append(escape(op.operationId())).append("\",");
            if (!op.requestSchema().isEmpty()) {
                json.append("\"requestBody\": {\"required\": true, \"content\": {\"application/json\": {\"schema\": {\"$ref\": \"#/components/schemas/")
                    .append(escape(op.requestSchema())).append("\"}}}},");
            }
            json.append("\"responses\": {\"200\": {\"description\": \"Success\", \"content\": {\"application/json\": {\"schema\": {\"$ref\": \"#/components/schemas/")
                .append(escape(op.responseSchema())).append("\"}}}},");
            json.append("\"default\": {\"description\": \"Error\", \"content\": {\"application/json\": {\"schema\": {\"$ref\": \"#/components/schemas/ApiError\"}}}}}");
            json.append("}\n    }");
            if (i + 1 < operations.size()) json.append(',');
            json.append('\n');
        }

        json.append("  },\n");
        json.append("  \"components\": {\"schemas\": {\n");
        List<ApiSchema> schemas = new ArrayList<>(ApiSchemaCatalog.v1().values());
        schemas.sort(java.util.Comparator.comparing(ApiSchema::name));
        for (int i = 0; i < schemas.size(); i++) {
            ApiSchema schema = schemas.get(i);
            json.append("    \"").append(escape(schema.name())).append("\": {\"type\": \"object\", \"properties\": {");
            List<String> fields = schema.allFieldsSorted();
            for (int j = 0; j < fields.size(); j++) {
                String field = fields.get(j);
                json.append("\"").append(escape(field)).append("\": {\"type\": \"string\"}");
                if (j + 1 < fields.size()) json.append(',');
            }
            json.append("}");
            if (!schema.requiredFields().isEmpty()) {
                List<String> required = schema.requiredFields().stream().sorted().toList();
                json.append(", \"required\": [");
                for (int j = 0; j < required.size(); j++) {
                    json.append("\"").append(escape(required.get(j))).append("\"");
                    if (j + 1 < required.size()) json.append(',');
                }
                json.append(']');
            }
            json.append(", \"additionalProperties\": true}");
            if (i + 1 < schemas.size()) json.append(',');
            json.append('\n');
        }
        json.append("  }}}\n");
        return json.toString();
    }

    public static List<String> validateContract() {
        List<String> issues = new ArrayList<>();
        Map<String, ApiSchema> schemas = ApiSchemaCatalog.v1();
        Set<String> operationIds = new HashSet<>();
        Set<String> routeKeys = new HashSet<>();

        for (ApiOperation op : ApiContractRegistry.v1()) {
            if (!operationIds.add(op.operationId())) issues.add("duplicate operationId: " + op.operationId());
            String routeKey = op.method() + " " + op.path();
            if (!routeKeys.add(routeKey)) issues.add("duplicate route: " + routeKey);
            if (!op.requestSchema().isEmpty() && !schemas.containsKey(op.requestSchema())) {
                issues.add("missing request schema: " + op.requestSchema());
            }
            if (!schemas.containsKey(op.responseSchema())) {
                issues.add("missing response schema: " + op.responseSchema());
            }
            if (!op.path().startsWith("/api/v1/")) {
                issues.add("route outside /api/v1: " + op.path());
            }
        }

        String document = generateJson();
        if (!document.startsWith("{\n  \"openapi\": \"3.1.0\"")) issues.add("OpenAPI version missing");
        if (!document.contains("\"ApiError\"")) issues.add("ApiError schema missing");
        if (!document.endsWith("}\n")) issues.add("OpenAPI document is incomplete");
        return List.copyOf(issues);
    }

    private static String escape(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
