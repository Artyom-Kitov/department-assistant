package ru.nsu.dgi.department_assistant.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.nsu.dgi.department_assistant.domain.dto.process.stepdata.CommonStepData;
import ru.nsu.dgi.department_assistant.domain.dto.process.stepdata.ConditionalStepData;
import ru.nsu.dgi.department_assistant.domain.dto.process.stepdata.FinalData;
import ru.nsu.dgi.department_assistant.domain.dto.process.stepdata.ProcessTransitionStepData;
import ru.nsu.dgi.department_assistant.domain.dto.process.stepdata.StepData;
import ru.nsu.dgi.department_assistant.domain.dto.process.stepdata.SubtasksStepData;
import ru.nsu.dgi.department_assistant.domain.exception.InvalidProcessTemplateException;

import java.util.Arrays;

@RequiredArgsConstructor
@Getter
public enum StepType {
    COMMON(1, CommonStepData.class),
    SUBTASKS(2, SubtasksStepData.class),
    CONDITIONAL(3, ConditionalStepData.class),
    FINAL(4, FinalData.class),
    TRANSITION(5, ProcessTransitionStepData.class);

    private final int value;
    private final Class<? extends StepData> targetType;

    public static StepType of(int type) {
        return Arrays.stream(StepType.values())
                .filter(t -> t.value == type)
                .findAny()
                .orElseThrow(() -> new InvalidProcessTemplateException("no step type with id = " + type));
    }
}
