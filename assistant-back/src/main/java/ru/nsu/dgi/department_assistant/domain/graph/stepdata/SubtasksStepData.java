package ru.nsu.dgi.department_assistant.domain.graph.stepdata;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.nsu.dgi.department_assistant.domain.graph.ProcessGraphNode;
import ru.nsu.dgi.department_assistant.domain.graph.Subtask;

import java.util.List;

@Getter
@RequiredArgsConstructor
public final class SubtasksStepData extends StepData {

    private final List<Subtask> subtasks;
    private final int next;

    @Override
    public List<Integer> next() {
        return List.of(next);
    }
}
