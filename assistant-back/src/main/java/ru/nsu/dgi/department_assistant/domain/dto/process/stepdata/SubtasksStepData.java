package ru.nsu.dgi.department_assistant.domain.dto.process.stepdata;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.nsu.dgi.department_assistant.domain.graph.Subtask;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Getter
public final class SubtasksStepData extends StepData {
    private final List<Subtask> subtasks;
    private final UUID next;

    @Override
    public List<UUID> next() {
        return List.of(next);
    }
}
