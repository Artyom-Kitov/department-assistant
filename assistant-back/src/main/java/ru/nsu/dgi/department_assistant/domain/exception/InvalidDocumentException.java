package ru.nsu.dgi.department_assistant.domain.exception;

public class InvalidDocumentException extends RuntimeException {
    public InvalidDocumentException(String message, Throwable cause) {
        super(message, cause);
    }
}
