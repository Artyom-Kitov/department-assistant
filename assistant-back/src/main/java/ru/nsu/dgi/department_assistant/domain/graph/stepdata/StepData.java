package ru.nsu.dgi.department_assistant.domain.graph.stepdata;

import java.util.List;

public abstract sealed class StepData permits CommonStepData, ConditionalStepData, FinalData, ProcessTransitionStepData, SubtasksStepData {
    public abstract List<Integer> next();
}
