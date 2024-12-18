package ru.nsu.dgi.department_assistant.domain.dto.process.stepdata;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public final class CommonStepData extends StepData {
    private final UUID next;

    @Override
    public List<UUID> next() {
        return List.of(next);
    }
}
