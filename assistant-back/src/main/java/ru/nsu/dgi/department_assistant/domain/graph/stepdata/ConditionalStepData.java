package ru.nsu.dgi.department_assistant.domain.graph.stepdata;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public final class ConditionalStepData extends StepData {

    private final int ifTrue;
    private final int ifFalse;

    @Override
    public List<Integer> next() {
        return List.of(ifTrue, ifFalse);
    }
}
