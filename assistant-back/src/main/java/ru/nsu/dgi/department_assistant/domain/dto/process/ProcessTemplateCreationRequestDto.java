package ru.nsu.dgi.department_assistant.domain.dto.process;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import ru.nsu.dgi.department_assistant.config.ProcessGraphDeserializer;
import ru.nsu.dgi.department_assistant.domain.graph.ProcessGraphNode;

import java.util.List;

public record ProcessTemplateCreationRequestDto(
        @NotBlank(message = "name is required")
        String name,

        @NotNull(message = "steps required")
        @JsonDeserialize(using = ProcessGraphDeserializer.class)
        List<ProcessGraphNode> steps
) {
}
