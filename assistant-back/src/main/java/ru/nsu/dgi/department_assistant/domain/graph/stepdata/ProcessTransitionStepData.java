package ru.nsu.dgi.department_assistant.domain.graph.stepdata;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.nsu.dgi.department_assistant.domain.graph.ProcessGraphNode;

import java.util.List;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public final class ProcessTransitionStepData extends StepData {

    private final UUID nextProcess;

    @Override
    public List<ProcessGraphNode> next() {
        return List.of();
    }
}
