package ru.nsu.dgi.department_assistant.domain.exception;

public class StorageCreationFailureException extends RuntimeException {
    public StorageCreationFailureException(String message, Throwable cause) {
        super(message, cause);
    }
}
