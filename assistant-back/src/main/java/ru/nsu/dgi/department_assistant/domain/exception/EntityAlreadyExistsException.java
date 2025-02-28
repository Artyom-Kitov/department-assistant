package ru.nsu.dgi.department_assistant.domain.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class EntityAlreadyExistsException extends RuntimeException {
    private final String message;
}
