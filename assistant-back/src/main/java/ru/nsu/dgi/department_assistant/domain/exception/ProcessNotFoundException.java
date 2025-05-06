package ru.nsu.dgi.department_assistant.domain.exception;

public class ProcessNotFoundException extends RuntimeException {
    public ProcessNotFoundException(String message) {
        super(message);
    }
} 