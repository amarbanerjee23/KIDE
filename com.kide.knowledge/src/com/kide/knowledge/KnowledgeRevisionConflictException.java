package com.kide.knowledge;

public final class KnowledgeRevisionConflictException extends KnowledgeRepositoryException {
    private static final long serialVersionUID = 1L;

    public KnowledgeRevisionConflictException(String message) { super(message); }
}
