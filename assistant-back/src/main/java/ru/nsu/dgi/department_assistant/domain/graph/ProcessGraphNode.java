package ru.nsu.dgi.department_assistant.domain.graph;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.nsu.dgi.department_assistant.domain.graph.stepdata.StepData;

import java.util.List;

@Getter
@Builder
public class ProcessGraphNode {
    private final int id;
    private final int type;
    private final int duration;
    private final JsonNode metaInfo;
    private final String description;

    @Setter
    private StepData data;

    public List<Integer> next() {
        return data.next();
    }

    public int getDuration() {
        return data.duration();
    }
}
