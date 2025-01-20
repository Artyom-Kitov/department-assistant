package ru.nsu.dgi.department_assistant.domain.graph.stepdata;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.nsu.dgi.department_assistant.domain.graph.ProcessGraphNode;

import java.util.List;

@Getter
@RequiredArgsConstructor
public final class ConditionalStepData extends StepData {

    private final ProcessGraphNode ifTrue;
    private final ProcessGraphNode ifFalse;

    @Override
    public List<ProcessGraphNode> next() {
        return List.of(ifTrue, ifFalse);
    }
}
