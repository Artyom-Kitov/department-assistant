package ru.nsu.dgi.department_assistant.domain.graph.stepdata;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;

@RequiredArgsConstructor
@ToString
public final class CommonStepData implements StepData {

    @Getter
    private final int next;
    private final int duration;

    @Override
    public List<Integer> next() {
        return List.of(next);
    }

    @Override
    public int duration() {
        return duration;
    }
}
