package ru.nsu.dgi.department_assistant.domain.graph;

import lombok.Builder;

import java.util.Map;
import java.util.UUID;

@Builder
public record ProcessGraph(
        UUID id,
        String name,
        int duration,
        int start,
        Map<Integer, ProcessGraphNode> nodes
) {
    public ProcessGraphNode getNode(int id) {
        return nodes.get(id);
    }
}
