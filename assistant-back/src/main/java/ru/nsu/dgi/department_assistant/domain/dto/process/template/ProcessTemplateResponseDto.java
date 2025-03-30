package ru.nsu.dgi.department_assistant.domain.dto.process.template;

import ru.nsu.dgi.department_assistant.domain.graph.ProcessGraphNode;

import java.util.List;
import java.util.UUID;

public record ProcessTemplateResponseDto(
        UUID id,
        String name,
        int duration,
        List<ProcessGraphNode> steps
) {
}
