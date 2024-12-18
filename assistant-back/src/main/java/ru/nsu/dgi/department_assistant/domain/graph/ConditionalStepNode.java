package ru.nsu.dgi.department_assistant.domain.graph;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@SuperBuilder
public final class ConditionalStepNode extends ProcessGraphNode {
    private ProcessGraphNode ifTrue;
    private ProcessGraphNode ifFalse;

    @Override
    public List<ProcessGraphNode> next() {
        return List.of(ifTrue, ifFalse);
    }
}
