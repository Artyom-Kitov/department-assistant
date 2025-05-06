package ru.nsu.dgi.department_assistant.domain.exception;

public class DocumentTypeInUseException extends RuntimeException {
    public DocumentTypeInUseException(String message) {
        super(message);
    }
} 