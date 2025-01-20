package ru.nsu.dgi.department_assistant.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.nsu.dgi.department_assistant.domain.exception.InvalidProcessTemplateException;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum StepType {

    COMMON(1),
    SUBTASKS(2),
    CONDITIONAL(3),
    FINAL(4),
    TRANSITION(5);

    private final int value;

    public static StepType of(int type) {
        return Arrays.stream(StepType.values())
                .filter(t -> t.value == type)
                .findAny()
                .orElseThrow(() -> new InvalidProcessTemplateException("no step type with id = " + type));
    }
}
