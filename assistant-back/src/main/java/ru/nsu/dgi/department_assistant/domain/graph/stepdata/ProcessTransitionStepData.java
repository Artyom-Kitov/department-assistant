package ru.nsu.dgi.department_assistant.domain.graph.stepdata;

import java.util.List;
import java.util.UUID;

public record ProcessTransitionStepData(UUID nextProcess) implements StepData {

    @Override
    public List<Integer> next() {
        return List.of();
    }

    @Override
    public int duration() {
        return 0;
    }
}
