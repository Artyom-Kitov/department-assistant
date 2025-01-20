package ru.nsu.dgi.department_assistant.domain.graph;

import lombok.Builder;

import java.util.UUID;

@Builder
public record ProcessGraph(
        UUID id,
        String name,
        int duration,
        ProcessGraphNode body
) {
}
