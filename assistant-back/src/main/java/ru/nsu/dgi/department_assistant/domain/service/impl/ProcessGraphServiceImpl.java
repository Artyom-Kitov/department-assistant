package ru.nsu.dgi.department_assistant.domain.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.nsu.dgi.department_assistant.domain.exception.ProcessNotFoundException;
import ru.nsu.dgi.department_assistant.domain.graph.stepdata.CommonStepData;
import ru.nsu.dgi.department_assistant.domain.graph.stepdata.ConditionalStepData;
import ru.nsu.dgi.department_assistant.domain.graph.stepdata.FinalData;
import ru.nsu.dgi.department_assistant.domain.graph.stepdata.ProcessTransitionStepData;
import ru.nsu.dgi.department_assistant.domain.graph.stepdata.SubtasksStepData;
import ru.nsu.dgi.department_assistant.domain.exception.InvalidProcessTemplateException;
import ru.nsu.dgi.department_assistant.domain.graph.ProcessGraphNode;
import ru.nsu.dgi.department_assistant.domain.graph.Subtask;
import ru.nsu.dgi.department_assistant.domain.repository.process.ProcessRepository;
import ru.nsu.dgi.department_assistant.domain.service.ProcessGraphService;

@Service
@RequiredArgsConstructor
public class ProcessGraphServiceImpl implements ProcessGraphService {

    private final ProcessRepository processRepository;

    @Override
    public int calculateDuration(ProcessGraphNode node) {
        return switch (node.getData()) {
            case CommonStepData data -> node.getDuration() + calculateDuration(data.getNext());
            case ConditionalStepData data -> node.getDuration() + Math.max(
                    calculateDuration(data.getIfTrue()),
                    calculateDuration(data.getIfFalse())
            );
            case SubtasksStepData data -> node.getDuration() + data.getSubtasks().stream()
                    .mapToInt(Subtask::duration)
                    .max()
                    .orElseThrow(() -> new InvalidProcessTemplateException("no subtasks found"))
                    + calculateDuration(data.getNext());
            case FinalData ignored -> 0;
            case ProcessTransitionStepData data -> processRepository.findById(data.getNextProcess())
                    .orElseThrow(() -> new ProcessNotFoundException(data.getNextProcess()))
                    .getTotalDuration();
        };
    }
}
