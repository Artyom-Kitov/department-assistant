package ru.nsu.dgi.department_assistant.domain.exception;

public class StorageFileException extends RuntimeException {
    public StorageFileException(String message, Throwable cause) {
        super(message, cause);
    }
}
