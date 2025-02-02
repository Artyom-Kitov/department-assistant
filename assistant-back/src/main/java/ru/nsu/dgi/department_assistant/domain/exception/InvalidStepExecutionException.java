package ru.nsu.dgi.department_assistant.domain.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
@Getter
public class InvalidStepExecutionException extends RuntimeException {
    private final int stepId;
    private final UUID processId;
}
