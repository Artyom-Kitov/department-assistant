package ru.nsu.dgi.department_assistant.domain.graph.stepdata;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import ru.nsu.dgi.department_assistant.domain.graph.SubtaskLike;

import java.util.Comparator;
import java.util.List;

@Getter
@RequiredArgsConstructor
@ToString
public final class SubtasksStepData implements StepData {

    private final List<SubtaskLike> subtasks;
    private final int next;

    @Override
    public List<Integer> next() {
        return List.of(next);
    }

    @Override
    public int duration() {
        return subtasks.stream()
                    .max(Comparator.comparingInt(SubtaskLike::duration))
                    .orElseThrow()
                    .duration();
    }
}
