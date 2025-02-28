package ru.nsu.dgi.department_assistant.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.nsu.dgi.department_assistant.domain.graph.ProcessGraphNode;
import ru.nsu.dgi.department_assistant.domain.graph.Subtask;
import ru.nsu.dgi.department_assistant.domain.graph.stepdata.CommonStepData;
import ru.nsu.dgi.department_assistant.domain.graph.stepdata.ConditionalStepData;
import ru.nsu.dgi.department_assistant.domain.graph.stepdata.FinalData;
import ru.nsu.dgi.department_assistant.domain.graph.stepdata.ProcessTransitionStepData;
import ru.nsu.dgi.department_assistant.domain.graph.stepdata.StepData;
import ru.nsu.dgi.department_assistant.domain.graph.stepdata.SubtasksStepData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProcessGraphDeserializer extends JsonDeserializer<List<ProcessGraphNode>> {

    @Override
    public List<ProcessGraphNode> deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
        ObjectMapper mapper = (ObjectMapper) p.getCodec();
        JsonNode root = mapper.readTree(p);
        List<ProcessGraphNode> nodes = new ArrayList<>();
        for (JsonNode node : root) {
            nodes.add(deserializeNode(node));
        }
        return nodes;
    }

    private ProcessGraphNode deserializeNode(JsonNode jsonNode) {
        int id = jsonNode.get("id").asInt();
        StepType type = StepType.of(jsonNode.get("type").intValue());
        int duration = jsonNode.get("duration") != null ? jsonNode.get("duration").asInt(1) : 1;
        String metaInfo = jsonNode.get("metaInfo").toString();
        String description = jsonNode.get("description").toString();

        JsonNode dataSerialized = jsonNode.get("data");
        StepData data = switch (type) {
            case COMMON -> deserializeCommon(dataSerialized);
            case SUBTASKS -> deserializeSubtasks(dataSerialized);
            case CONDITIONAL -> deserializeConditional(dataSerialized);
            case FINAL -> deserializeFinal(dataSerialized);
            case TRANSITION -> deserializeTransition(dataSerialized);
        };

        return ProcessGraphNode.builder()
                .id(id)
                .type(type.getValue())
                .duration(duration)
                .metaInfo(metaInfo)
                .description(description)
                .data(data)
                .build();
    }

    private CommonStepData deserializeCommon(JsonNode node) {
        int next = node.get("next").asInt();
        return new CommonStepData(next);
    }

    private SubtasksStepData deserializeSubtasks(JsonNode node) {
        JsonNode subtasksNode = node.get("subtasks");
        List<Subtask> subtasks = new ArrayList<>();
        for (JsonNode subtaskNode : subtasksNode) {
            UUID subtaskId = UUID.randomUUID();
            String description = subtaskNode.get("description").asText();
            int duration = subtaskNode.get("duration") != null
                    ? subtaskNode.get("duration").asInt(1)
                    : 1;
            subtasks.add(new Subtask(subtaskId, description, duration));
        }
        int next = node.get("next").asInt();
        return new SubtasksStepData(subtasks, next);
    }

    private ConditionalStepData deserializeConditional(JsonNode node) {
        int ifTrue = node.get("ifTrue").asInt();
        int ifFalse = node.get("ifFalse").asInt();
        return new ConditionalStepData(ifTrue, ifFalse);
    }

    private FinalData deserializeFinal(JsonNode node) {
        boolean isSuccessful = node.get("isSuccessful").asBoolean();
        return new FinalData(isSuccessful);
    }

    private ProcessTransitionStepData deserializeTransition(JsonNode node) {
        UUID nextProcessId = UUID.fromString(node.get("nextProcess").asText());
        return new ProcessTransitionStepData(nextProcessId);
    }
}
