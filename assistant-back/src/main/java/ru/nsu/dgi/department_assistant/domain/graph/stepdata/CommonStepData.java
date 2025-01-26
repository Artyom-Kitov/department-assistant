package ru.nsu.dgi.department_assistant.domain.graph.stepdata;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public final class CommonStepData extends StepData {

    private final int next;

    @Override
    public List<Integer> next() {
        return List.of(next);
    }
}
