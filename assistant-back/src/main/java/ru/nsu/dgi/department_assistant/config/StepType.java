package ru.nsu.dgi.department_assistant.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.nsu.dgi.department_assistant.domain.exception.InvalidProcessTemplateException;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public enum StepType {

    COMMON(1),
    SUBTASKS(2),
    CONDITIONAL(3),
    FINAL(4),
    TRANSITION(5);

    private final int value;

    private static final Map<Integer, StepType> TYPE_MAP = Arrays.stream(values())
            .collect(Collectors.toMap(StepType::getValue, Function.identity()));

    public static StepType of(int type) {
        StepType value = TYPE_MAP.get(type);
        if (value == null) {
            throw new InvalidProcessTemplateException("no step type with id = " + type);
        }
        return value;
    }
}
