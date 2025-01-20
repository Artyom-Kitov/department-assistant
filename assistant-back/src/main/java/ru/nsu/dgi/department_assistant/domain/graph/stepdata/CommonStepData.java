package ru.nsu.dgi.department_assistant.domain.graph.stepdata;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.nsu.dgi.department_assistant.domain.graph.ProcessGraphNode;

import java.util.List;

@Getter
@RequiredArgsConstructor
public final class CommonStepData extends StepData {

    private final ProcessGraphNode next;

    @Override
    public List<ProcessGraphNode> next() {
        return List.of(next);
    }
}
