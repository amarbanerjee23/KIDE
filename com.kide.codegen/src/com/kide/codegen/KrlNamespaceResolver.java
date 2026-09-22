package com.kide.codegen;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;

import com.kide.krl.dsl.krl.KnowledgeModel;
import com.kide.krl.dsl.krl.Namespace;

final class KrlNamespaceResolver {
    private final Map<String, String> prefixes = new LinkedHashMap<>();

    KrlNamespaceResolver(KnowledgeModel model) {
        model.getDeclarations().stream()
                .filter(Namespace.class::isInstance)
                .map(Namespace.class::cast)
                .sorted(java.util.Comparator.comparing(Namespace::getName))
                .forEach(namespace -> {
                    String previous = prefixes.put(namespace.getName(), namespace.getUri());
                    if (previous != null) {
                        throw new GenerationException(
                                "duplicate KRL namespace prefix " + namespace.getName());
                    }
                });
    }

    String resolve(String qualified) {
        if (qualified == null || qualified.isBlank()) {
            throw new GenerationException("KRL qualified name is required");
        }
        int colon = qualified.indexOf(':');
        if (colon < 1 || colon + 1 >= qualified.length()) {
            throw new GenerationException(
                    "knowledge resource must use a declared namespace prefix: " + qualified);
        }
        String prefix = qualified.substring(0, colon);
        String local = qualified.substring(colon + 1);
        String base = prefixes.get(prefix);
        if (base == null) {
            throw new GenerationException("unknown KRL namespace prefix " + prefix);
        }
        String resolved = join(base, local);
        URI uri = URI.create(resolved);
        if (!uri.isAbsolute()) {
            throw new GenerationException("resolved KRL resource is not an absolute IRI");
        }
        return resolved;
    }

    private static String join(String base, String local) {
        if (base.endsWith("#") || base.endsWith("/") || base.endsWith(":")) {
            return base + local;
        }
        return base + "#" + local;
    }
}
