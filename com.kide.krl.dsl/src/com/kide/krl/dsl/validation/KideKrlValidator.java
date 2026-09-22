package com.kide.krl.dsl.validation;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.xtext.validation.Check;

import com.kide.krl.dsl.krl.KnowledgeModel;
import com.kide.krl.dsl.krl.KrlPackage;
import com.kide.krl.dsl.krl.Namespace;
import com.kide.krl.dsl.krl.Target;
import com.kide.krl.dsl.krl.Template;

public final class KideKrlValidator extends KrlValidator {
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
            error("Namespace URI must use http, https or urn.",
                    KrlPackage.Literals.NAMESPACE__URI, INVALID_NAMESPACE);
        }
    }

    @Check
    public void safeOutputPath(Target target) {
        String raw = target.getOutputPath() == null ? "" : target.getOutputPath();
        if (raw.isBlank() || raw.indexOf('\\') >= 0) {
            error("Target output must be a non-empty portable relative path.",
                    KrlPackage.Literals.TARGET__OUTPUT_PATH, UNSAFE_OUTPUT);
            return;
        }
        Path value = Path.of(raw);
        if (value.isAbsolute() || value.normalize().startsWith("..")
                || raw.startsWith(".kide/") || raw.equals(".kide")) {
            error("Target output escapes the generation sandbox.",
                    KrlPackage.Literals.TARGET__OUTPUT_PATH, UNSAFE_OUTPUT);
        }
    }

    @Check
    public void matchingTemplateTarget(Target target) {
        Template template = target.getTemplate();
        if (template != null && !template.getTargetType().equals(target.getTargetType())) {
            error("Target type must match its template target type.",
                    KrlPackage.Literals.TARGET__TARGET_TYPE, TEMPLATE_TARGET_MISMATCH);
        }
    }

}
