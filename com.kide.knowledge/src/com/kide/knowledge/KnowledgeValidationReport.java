package com.kide.knowledge;

import java.util.List;

public record KnowledgeValidationReport(boolean conforms, List<KnowledgeValidationIssue> issues) {
    public KnowledgeValidationReport {
        issues = List.copyOf(issues == null ? List.of() : issues);
        if (conforms && !issues.isEmpty()) {
            throw new IllegalArgumentException("conforming report cannot contain issues");
        }
    }
}
