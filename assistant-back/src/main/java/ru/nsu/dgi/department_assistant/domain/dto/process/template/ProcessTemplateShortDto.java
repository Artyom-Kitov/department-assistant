package ru.nsu.dgi.department_assistant.domain.dto.process.template;

import java.util.UUID;

public record ProcessTemplateShortDto(
        UUID id,
        String name,
        int duration
) {
}
