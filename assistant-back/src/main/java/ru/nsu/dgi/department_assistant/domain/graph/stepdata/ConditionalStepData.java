package ru.nsu.dgi.department_assistant.domain.graph.stepdata;

import java.util.List;

public record ConditionalStepData(int ifTrue, int ifFalse, int duration) implements StepData {

    @Override
    public List<Integer> next() {
        return List.of(ifTrue, ifFalse);
    }
}
