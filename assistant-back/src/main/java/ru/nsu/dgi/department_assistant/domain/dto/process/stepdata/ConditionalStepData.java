package ru.nsu.dgi.department_assistant.domain.dto.process.stepdata;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Getter
public final class ConditionalStepData extends StepData {
    private final UUID ifTrue;
    private final UUID ifFalse;

    @Override
    public List<UUID> next() {
        return List.of(ifTrue, ifFalse);
    }
}
