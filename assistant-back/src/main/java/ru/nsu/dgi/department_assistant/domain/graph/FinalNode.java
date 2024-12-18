package ru.nsu.dgi.department_assistant.domain.graph;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@SuperBuilder
public final class FinalNode extends ProcessGraphNode {
    private final boolean isSuccessful;

    @Override
    public List<ProcessGraphNode> next() {
        return List.of();
    }
}
