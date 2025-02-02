package ru.nsu.dgi.department_assistant.domain.dto.process;

import org.jetbrains.annotations.Nullable;

import java.time.LocalDate;
import java.util.UUID;

public record ProcessExecutionRequestDto(
        UUID employeeId,
        UUID processId,
        @Nullable
        LocalDate deadline
) {
}
