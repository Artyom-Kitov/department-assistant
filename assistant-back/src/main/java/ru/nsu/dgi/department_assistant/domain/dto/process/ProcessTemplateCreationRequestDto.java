package ru.nsu.dgi.department_assistant.domain.dto.process;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import ru.nsu.dgi.department_assistant.config.StepDataDeserializer;

import java.util.List;

public record ProcessTemplateCreationRequestDto(
        String name,

        @JsonDeserialize(using = StepDataDeserializer.class)
        List<ProcessStepDto> steps
) {
}
