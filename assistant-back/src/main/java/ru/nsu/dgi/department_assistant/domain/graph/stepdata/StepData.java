package ru.nsu.dgi.department_assistant.domain.graph.stepdata;

import ru.nsu.dgi.department_assistant.domain.graph.ProcessGraphNode;

import java.util.List;

public abstract sealed class StepData permits CommonStepData, ConditionalStepData, FinalData, ProcessTransitionStepData, SubtasksStepData {

    public abstract List<ProcessGraphNode> next();
}
