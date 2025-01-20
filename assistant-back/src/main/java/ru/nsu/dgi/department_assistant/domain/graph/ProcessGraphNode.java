package ru.nsu.dgi.department_assistant.domain.graph;

import lombok.Builder;
import lombok.Getter;
import ru.nsu.dgi.department_assistant.domain.graph.stepdata.StepData;

import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class ProcessGraphNode {
    private final UUID id;
    private final int type;
    private final int duration;
    private final String metaInfo;
    private final String description;

    private final StepData data;

    public List<ProcessGraphNode> next() {
        return data.next();
    }
}
