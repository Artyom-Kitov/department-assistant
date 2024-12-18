package ru.nsu.dgi.department_assistant.domain.graph;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@SuperBuilder
public final class SubtasksStepNode extends ProcessGraphNode {
    private final List<Subtask> subtasks;

    @Setter
    private ProcessGraphNode next;

    @Override
    public List<ProcessGraphNode> next() {
        return List.of(next);
    }
}
