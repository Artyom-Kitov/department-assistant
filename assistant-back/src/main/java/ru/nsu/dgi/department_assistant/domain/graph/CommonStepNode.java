package ru.nsu.dgi.department_assistant.domain.graph;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@SuperBuilder
public final class CommonStepNode extends ProcessGraphNode {
    private ProcessGraphNode next;

    @Override
    public List<ProcessGraphNode> next() {
        return List.of(next);
    }
}
