package ru.nsu.dgi.department_assistant.domain.dto.process;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import ru.nsu.dgi.department_assistant.config.ProcessGraphDeserializer;
import ru.nsu.dgi.department_assistant.domain.graph.ProcessGraphNode;

import java.util.List;

public record ProcessTemplateCreationRequestDto(
        String name,

        @JsonDeserialize(using = ProcessGraphDeserializer.class)
        List<ProcessGraphNode> steps
) {
}
