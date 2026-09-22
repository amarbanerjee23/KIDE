package com.kide.krl.dsl;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.xtext.validation.Check;

import com.kide.krl.dsl.krl.KnowledgeModel;
import com.kide.krl.dsl.krl.Namespace;
import com.kide.krl.dsl.krl.Target;
import com.kide.krl.dsl.krl.Template;

public final class KrlValidator extends AbstractKrlValidator {
    public static final String DUPLICATE_DECLARATION = "krl.duplicateDeclaration";
    public static final String UNSAFE_OUTPUT = "krl.unsafeOutput";
    public static final String TEMPLATE_TARGET_MISMATCH = "krl.templateTargetMismatch";
    public static final String INVALID_NAMESPACE = "krl.invalidNamespace";

    @Check
    public void uniqueNamedDeclarations(KnowledgeModel model) {
        Set<String> names = new HashSet<>();
        model.getDeclarations().forEach(declaration -> {
            try {
                var method = declaration.getClass().getMethod("getName");
                Object value = method.invoke(declaration);
                if (value instanceof String name && !name.isBlank() && !names.add(name)) {
                    error("Declaration names must be unique: " + name, declaration, null,
                            DUPLICATE_DECLARATION);
                }
            } catch (ReflectiveOperationException ignored) {
                // Facts intentionally have no declaration name.
            }
        });
    }

    @Check
    public void namespaceUri(Namespace namespace) {
        String uri = namespace.getUri();
        if (uri == null || !(uri.startsWith("http://") || uri.startsWith("https://")
                || uri.startsWith("urn:"))) {
            error("Namespace URI must use http, https or urn.", null, INVALID_NAMESPACE);
        }
    }

    @Check
    public void safeOutputPath(Target target) {
        String raw = unquote(target.getOutputPath());
        if (raw.isBlank() || raw.indexOf('\\') >= 0) {
            error("Target output must be a non-empty portable relative path.", null, UNSAFE_OUTPUT);
            return;
        }
        Path value = Path.of(raw);
        if (value.isAbsolute() || value.normalize().startsWith("..")
                || raw.startsWith(".kide/") || raw.equals(".kide")) {
            error("Target output escapes the generation sandbox.", null, UNSAFE_OUTPUT);
        }
    }

    @Check
    public void matchingTemplateTarget(Target target) {
        Template template = target.getTemplate();
        if (template != null && template.getTargetType() != target.getTargetType()) {
            error("Target type must match its template target type.", null,
                    TEMPLATE_TARGET_MISMATCH);
        }
    }

    private static String unquote(String value) {
        if (value == null) return "";
        if (value.length() >= 2 && value.startsWith("\"") && value.endsWith("\"")) {
            return value.substring(1, value.length() - 1);
        }
        return value;
    }
}
