package ru.nsu.dgi.department_assistant.domain.graph;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.nsu.dgi.department_assistant.domain.graph.stepdata.StepData;

import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class ProcessGraphNode {
    private final int id;
    private final int type;
    private final int duration;
    private final String metaInfo;
    private final String description;

    @Setter
    private StepData data;

    public List<ProcessGraphNode> next() {
        return data.next();
    }
}
