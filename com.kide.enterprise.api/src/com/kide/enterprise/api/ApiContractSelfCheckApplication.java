package com.kide.enterprise.api;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

public final class ApiContractSelfCheckApplication implements IApplication {
    @Override
    public Object start(IApplicationContext context) {
        try {
            if (!OpenApiV1.validateContract().isEmpty()) {
                return fail("OpenAPI contract validation failed");
            }

            Path openApiOutput = argumentPath(context, "--openapi-output");
            if (openApiOutput != null) {
                Path parent = openApiOutput.toAbsolutePath().normalize().getParent();
                if (parent != null) Files.createDirectories(parent);
                Files.writeString(openApiOutput, OpenApiV1.generateJson());
                System.out.println("KIDE OPENAPI V1 EXPORTED: "
                        + openApiOutput.toAbsolutePath().normalize());
            }

            UUID requestId = UUID.randomUUID();
            ApiErrorEnvelope error = ApiExceptionMapper.map(
                    requestId, new RuntimeException("secret stack detail"));
            if (error.code() != ApiErrorCode.INTERNAL_ERROR) return fail("error mapping failed");
            if (!requestId.equals(error.requestId())) return fail("request ID propagation failed");
            if (error.toString().contains("secret stack detail")) return fail("raw exception leaked");

            ApiErrorEnvelope decoded = ApiErrorEnvelope.fromMap(Map.of(
                    "apiVersion", "v1",
                    "requestId", requestId.toString(),
                    "code", "NOT_FOUND",
                    "message", "Not found",
                    "details", Map.of("resource", "model"),
                    "futureField", "ignored"));
            if (decoded.code() != ApiErrorCode.NOT_FOUND) return fail("unknown-field compatibility failed");

            System.out.println("KIDE PR20 API CONTRACT SELF-CHECK OK");
            return IApplication.EXIT_OK;
        } catch (Exception e) {
            return fail("guarded runtime failure: " + e.getClass().getSimpleName());
        }
    }

    @Override
    public void stop() { }

    private static Path argumentPath(IApplicationContext context, String name) {
        Object raw = context.getArguments().get(IApplicationContext.APPLICATION_ARGS);
        if (!(raw instanceof String[] args)) return null;
        for (int i = 0; i < args.length; i++) {
            if (!name.equals(args[i])) continue;
            if (i + 1 >= args.length || args[i + 1].isBlank()) {
                throw new IllegalArgumentException(name + " requires a path");
            }
            return Path.of(args[i + 1]);
        }
        return null;
    }

    private static Integer fail(String reason) {
        System.err.println("KIDE PR20 API CONTRACT SELF-CHECK FAILED: " + reason);
        return Integer.valueOf(2);
    }
}
