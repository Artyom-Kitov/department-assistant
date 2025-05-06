package ru.nsu.dgi.department_assistant.domain.exception;

public class StepNotFoundException extends RuntimeException {
    public StepNotFoundException(String message) {
        super(message);
    }
} 