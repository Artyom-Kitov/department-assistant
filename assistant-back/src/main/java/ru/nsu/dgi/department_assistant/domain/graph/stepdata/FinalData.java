package ru.nsu.dgi.department_assistant.domain.graph.stepdata;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public final class FinalData extends StepData {

    private final boolean isSuccessful;

    @Override
    public List<Integer> next() {
        return List.of();
    }
}
