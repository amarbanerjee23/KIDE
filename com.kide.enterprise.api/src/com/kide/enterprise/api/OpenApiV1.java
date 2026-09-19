package com.kide.enterprise.api;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class OpenApiV1 {
    private static final Pattern PATH_PARAMETER = Pattern.compile("\\{([A-Za-z][A-Za-z0-9]*)\\}");

    private OpenApiV1() { }

    public static String generateJson() {
        StringBuilder json = new StringBuilder(16384);
        json.append("{\n");
        json.append("  \"openapi\": \"3.1.0\",\n");
        json.append("  \"info\": {\"title\": \"KIDE Enterprise API\", \"version\": \"v1\"},\n");
        json.append("  \"paths\": {\n");

        Map<String, List<ApiOperation>> byPath = new LinkedHashMap<>();
        for (ApiOperation operation : ApiContractRegistry.v1()) {
            byPath.computeIfAbsent(operation.path(), ignored -> new ArrayList<>()).add(operation);
        }

        int pathIndex = 0;
        for (Map.Entry<String, List<ApiOperation>> pathEntry : byPath.entrySet()) {
            json.append("    \"").append(escape(pathEntry.getKey())).append("\": {\n");
            List<ApiOperation> operations = pathEntry.getValue();
            for (int opIndex = 0; opIndex < operations.size(); opIndex++) {
                ApiOperation op = operations.get(opIndex);
                json.append("      \"").append(op.method().name().toLowerCase()).append("\": {");
                json.append("\"operationId\": \"").append(escape(op.operationId())).append("\"");

                List<String> parameters = pathParameters(op.path());
                if (!parameters.isEmpty()) {
                    json.append(",\"parameters\": [");
                    for (int i = 0; i < parameters.size(); i++) {
                        String parameter = parameters.get(i);
                        json.append("{\"name\":\"").append(escape(parameter))
                                .append("\",\"in\":\"path\",\"required\":true,\"schema\":{\"type\":\"string\"}}");
                        if (i + 1 < parameters.size()) json.append(',');
                    }
                    json.append(']');
                }

                if (!op.requestSchema().isEmpty()) {
                    json.append(",\"requestBody\": {\"required\": true, \"content\": {\"application/json\": {\"schema\": {\"$ref\": \"#/components/schemas/")
                            .append(escape(op.requestSchema())).append("\"}}}}");
                }
                json.append(",\"responses\": {\"200\": {\"description\": \"Success\", \"content\": {\"application/json\": {\"schema\": {\"$ref\": \"#/components/schemas/")
                        .append(escape(op.responseSchema())).append("\"}}}},");
                json.append("\"default\": {\"description\": \"Error\", \"content\": {\"application/json\": {\"schema\": {\"$ref\": \"#/components/schemas/ApiError\"}}}}}");
                json.append('}');
                if (opIndex + 1 < operations.size()) json.append(',');
                json.append('\n');
            }
            json.append("    }");
            if (++pathIndex < byPath.size()) json.append(',');
            json.append('\n');
        }

        json.append("  },\n");
        json.append("  \"components\": {\"schemas\": {\n");
        List<ApiSchema> schemas = new ArrayList<>(ApiSchemaCatalog.v1().values());
        schemas.sort(Comparator.comparing(ApiSchema::name));
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
            if (!op.path().equals(ApiVersion.V1.basePath())
                    && !op.path().startsWith(ApiVersion.V1.basePath() + "/")) {
                issues.add("route outside /api/v1: " + op.path());
            }
            for (String parameter : pathParameters(op.path())) {
                if (parameter.isBlank()) issues.add("blank path parameter: " + op.path());
            }
        }

        String document = generateJson();
        if (!document.startsWith("{\n  \"openapi\": \"3.1.0\"")) issues.add("OpenAPI version missing");
        if (!document.contains("\"ApiError\"")) issues.add("ApiError schema missing");
        if (!document.endsWith("}\n")) issues.add("OpenAPI document is incomplete");
        for (String path : ApiContractRegistry.v1().stream().map(ApiOperation::path).distinct().toList()) {
            String token = "\"" + escape(path) + "\":";
            if (count(document, token) != 1) issues.add("path must be emitted exactly once: " + path);
        }
        return List.copyOf(issues);
    }

    static List<String> pathParameters(String path) {
        List<String> parameters = new ArrayList<>();
        Matcher matcher = PATH_PARAMETER.matcher(path);
        while (matcher.find()) parameters.add(matcher.group(1));
        return List.copyOf(parameters);
    }

    private static int count(String value, String needle) {
        int total = 0;
        int offset = 0;
        while ((offset = value.indexOf(needle, offset)) >= 0) {
            total++;
            offset += needle.length();
        }
        return total;
    }

    private static String escape(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
