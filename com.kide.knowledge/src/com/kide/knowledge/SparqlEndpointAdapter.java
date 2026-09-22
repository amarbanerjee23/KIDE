package com.kide.knowledge;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Objects;
import java.util.function.Supplier;

public final class SparqlEndpointAdapter {
    private static final int MAX_RESPONSE_BYTES = 4 * 1024 * 1024;
    private final URI endpoint;
    private final HttpClient client;
    private final Supplier<String> bearerToken;
    private final Duration timeout;

    public SparqlEndpointAdapter(
            URI endpoint,
            HttpClient client,
            Supplier<String> bearerToken,
            Duration timeout) {
        this.endpoint = validateEndpoint(endpoint);
        this.client = Objects.requireNonNull(client, "client");
        this.bearerToken = bearerToken == null ? () -> "" : bearerToken;
        this.timeout = Objects.requireNonNull(timeout, "timeout");
        if (timeout.isZero() || timeout.isNegative()) {
            throw new IllegalArgumentException("timeout must be positive");
        }
    }

    public String select(String sparql) {
        return execute("application/sparql-query", sparql,
                "application/sparql-results+json, application/ld+json, application/json");
    }

    public void update(String sparqlUpdate) {
        execute("application/sparql-update", sparqlUpdate, "application/json, text/plain");
    }

    public void replaceDataset(KnowledgeDataset dataset) {
        StringBuilder update = new StringBuilder("CLEAR DEFAULT; INSERT DATA {\n");
        for (KnowledgeTriple triple : dataset.triples()) {
            update.append('<').append(triple.subject()).append("> <")
                    .append(triple.predicate()).append("> ");
            if (triple.object().literal()) {
                update.append('"').append(escape(triple.object().value())).append('"');
            } else {
                update.append('<').append(triple.object().value()).append('>');
            }
            update.append(" .\n");
        }
        update.append('}');
        update(update.toString());
    }

    public URI endpoint() { return endpoint; }

    private String execute(String contentType, String body, String accept) {
        if (body == null || body.isBlank()) throw new IllegalArgumentException("SPARQL document is required");
        HttpRequest.Builder request = HttpRequest.newBuilder(endpoint)
                .timeout(timeout)
                .header("Content-Type", contentType)
                .header("Accept", accept)
                .POST(HttpRequest.BodyPublishers.ofString(body));
        String token = bearerToken.get();
        if (token != null && !token.isBlank()) request.header("Authorization", "Bearer " + token.trim());
        try {
            HttpResponse<byte[]> response = client.send(
                    request.build(), HttpResponse.BodyHandlers.ofByteArray());
            if (response.body().length > MAX_RESPONSE_BYTES) {
                throw new KnowledgeRepositoryException("SPARQL response exceeds supported size");
            }
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new KnowledgeRepositoryException(
                        "SPARQL endpoint returned HTTP " + response.statusCode());
            }
            return new String(response.body(), java.nio.charset.StandardCharsets.UTF_8);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new KnowledgeRepositoryException("SPARQL request interrupted", e);
        } catch (java.io.IOException e) {
            throw new KnowledgeRepositoryException("SPARQL endpoint unavailable", e);
        }
    }

    private static URI validateEndpoint(URI endpoint) {
        Objects.requireNonNull(endpoint, "endpoint");
        String scheme = endpoint.getScheme();
        if (scheme == null || endpoint.getHost() == null || endpoint.getFragment() != null) {
            throw new IllegalArgumentException("SPARQL endpoint must be an absolute HTTP(S) URI");
        }
        if ("https".equalsIgnoreCase(scheme)) return endpoint;
        if ("http".equalsIgnoreCase(scheme) && isLoopback(endpoint.getHost())) return endpoint;
        throw new IllegalArgumentException("SPARQL endpoint must use HTTPS outside loopback");
    }

    private static boolean isLoopback(String host) {
        return "localhost".equalsIgnoreCase(host) || "127.0.0.1".equals(host)
                || "::1".equals(host) || "[::1]".equals(host);
    }

    private static String escape(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"")
                .replace("\n", "\\n").replace("\r", "\\r");
    }
}
