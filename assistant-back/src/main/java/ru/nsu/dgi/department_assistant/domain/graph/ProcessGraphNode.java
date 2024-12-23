package ru.nsu.dgi.department_assistant.domain.graph;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.UUID;

@Getter
@SuperBuilder
public abstract sealed class ProcessGraphNode permits CommonStepNode, ConditionalStepNode, FinalNode, ProcessTransitionNode, SubtasksStepNode {
    protected UUID id;
    protected int type;
    protected int duration;
    protected String metaInfo;
    protected String description;

    public abstract List<ProcessGraphNode> next();
}
