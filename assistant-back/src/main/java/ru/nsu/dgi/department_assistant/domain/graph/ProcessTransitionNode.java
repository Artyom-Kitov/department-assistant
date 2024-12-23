package ru.nsu.dgi.department_assistant.domain.graph;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@SuperBuilder
public final class ProcessTransitionNode extends ProcessGraphNode {
    private final UUID nextProcess;

    @Override
    public List<ProcessGraphNode> next() {
        return List.of();
    }
}
