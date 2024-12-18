package ru.nsu.dgi.department_assistant.domain.dto.process;

import lombok.Builder;
import ru.nsu.dgi.department_assistant.domain.dto.process.stepdata.CommonStepData;
import ru.nsu.dgi.department_assistant.domain.dto.process.stepdata.ConditionalStepData;
import ru.nsu.dgi.department_assistant.domain.dto.process.stepdata.FinalData;
import ru.nsu.dgi.department_assistant.domain.dto.process.stepdata.StepData;
import ru.nsu.dgi.department_assistant.domain.dto.process.stepdata.SubtasksStepData;
import ru.nsu.dgi.department_assistant.domain.graph.CommonStepNode;
import ru.nsu.dgi.department_assistant.domain.graph.ConditionalStepNode;
import ru.nsu.dgi.department_assistant.domain.graph.FinalNode;
import ru.nsu.dgi.department_assistant.domain.graph.ProcessGraphNode;
import ru.nsu.dgi.department_assistant.domain.graph.SubtasksStepNode;

import java.util.UUID;

@Builder
public record ProcessStepDto(
        UUID id,
        String description,
        int type,
        int duration,
        String metaInfo,
        StepData data
) {
    public ProcessGraphNode asGraphNode() {
        return switch (data) {
            case CommonStepData ignored -> CommonStepNode.builder()
                    .id(id)
                    .type(type)
                    .description(description)
                    .duration(duration)
                    .metaInfo(metaInfo)
                    .build();
            case ConditionalStepData ignored -> ConditionalStepNode.builder()
                    .id(id)
                    .type(type)
                    .description(description)
                    .duration(duration)
                    .metaInfo(metaInfo)
                    .build();
            case FinalData d -> FinalNode.builder()
                    .id(id)
                    .type(type)
                    .description(description)
                    .duration(duration)
                    .metaInfo(metaInfo)
                    .isSuccessful(d.isSuccessful())
                    .build();
            case SubtasksStepData d -> SubtasksStepNode.builder()
                    .id(id)
                    .type(type)
                    .description(description)
                    .duration(duration)
                    .metaInfo(metaInfo)
                    .subtasks(d.getSubtasks())
                    .build();
        };
    }
}
