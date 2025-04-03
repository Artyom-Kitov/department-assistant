package ru.nsu.dgi.department_assistant.domain.dto.process.execution;

import java.util.List;
import java.util.UUID;

public record ProcessExecutionStatusDto(
        UUID processId,
        String name,
        List<StepStatusDto> statuses
) {
}
