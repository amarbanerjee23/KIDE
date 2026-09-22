package com.kide.knowledge;

public class KnowledgeRepositoryException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public KnowledgeRepositoryException(String message) { super(message); }
    public KnowledgeRepositoryException(String message, Throwable cause) { super(message, cause); }
}
