package ru.nsu.dgi.department_assistant.domain.graph.stepdata;

import java.util.List;

public record FinalData(boolean isSuccessful) implements StepData {

    @Override
    public List<Integer> next() {
        return List.of();
    }

    @Override
    public int duration() {
        return 0;
    }
}
