package ru.nsu.dgi.department_assistant.domain.exception;

public class DocumentTypeNotFoundException extends RuntimeException {
    public DocumentTypeNotFoundException(String message) {
        super(message);
    }
} 