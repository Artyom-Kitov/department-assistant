package ru.nsu.dgi.department_assistant.domain.dto.process.execution;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.jetbrains.annotations.Nullable;
import ru.nsu.dgi.department_assistant.config.DateDeserializer;

import java.time.LocalDate;
import java.util.UUID;

public record ProcessExecutionRequestDto(
        UUID employeeId,
        UUID processId,
        @Nullable
        @JsonDeserialize(using = DateDeserializer.class)
        LocalDate deadline
) {
}
