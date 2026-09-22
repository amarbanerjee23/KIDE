package com.kide.codegen;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class KrlTemplateRenderer {
    private static final Pattern PLACEHOLDER =
            Pattern.compile("\\$\\{([A-Za-z_][A-Za-z0-9_.-]*)}");
    private static final int MAX_TEMPLATE_CHARS = 512 * 1024;

    public String render(String template, Map<String, SemanticValue> values) {
        if (template == null) throw new GenerationException("template body is required");
        if (template.length() > MAX_TEMPLATE_CHARS) {
            throw new GenerationException("template exceeds 512 KiB");
        }
        Matcher matcher = PLACEHOLDER.matcher(template);
        StringBuffer rendered = new StringBuffer();
        while (matcher.find()) {
            String name = matcher.group(1);
            SemanticValue value = values.get(name);
            if (value == null) {
                throw new GenerationException("unresolved template placeholder " + name);
            }
            matcher.appendReplacement(rendered, Matcher.quoteReplacement(value.rendered()));
        }
        matcher.appendTail(rendered);
        if (rendered.indexOf("${") >= 0) {
            throw new GenerationException("malformed or unsupported template placeholder");
        }
        return rendered.toString();
    }
}
