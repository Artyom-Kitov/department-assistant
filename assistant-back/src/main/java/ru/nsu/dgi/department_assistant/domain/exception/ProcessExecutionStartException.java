package ru.nsu.dgi.department_assistant.domain.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
@Getter
public class ProcessExecutionStartException extends RuntimeException {
    private final UUID employeeId;
    private final UUID processId;
}
