package ru.nsu.dgi.department_assistant.domain.dto.process.stepdata;

import java.util.List;
import java.util.UUID;

public abstract sealed class StepData permits CommonStepData, ConditionalStepData, FinalData, ProcessTransitionStepData, SubtasksStepData {
    public abstract List<UUID> next();
}
