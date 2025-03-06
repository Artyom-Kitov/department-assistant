package ru.nsu.dgi.department_assistant.domain.graph.stepdata;

import java.util.List;

public sealed interface StepData permits StartStepData, CommonStepData, ConditionalStepData, FinalData,
        ProcessTransitionStepData, SubtasksStepData {
    List<Integer> next();

    int duration();
}
