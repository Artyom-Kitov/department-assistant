package ru.nsu.dgi.department_assistant.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.nsu.dgi.department_assistant.domain.dto.process.ProcessStepDto;
import ru.nsu.dgi.department_assistant.domain.dto.process.stepdata.StepData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class StepDataDeserializer extends JsonDeserializer<List<ProcessStepDto>> {

    @Override
    public List<ProcessStepDto> deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
        ObjectMapper mapper = (ObjectMapper) p.getCodec();
        JsonNode root = mapper.readTree(p);
        List<ProcessStepDto> steps = new ArrayList<>();

        for (JsonNode node : root) {
            UUID id = UUID.fromString(node.get("id").asText());
            StepType type = StepType.of(node.get("type").intValue());
            String description = node.get("description").toString();
            int duration = node.get("duration") == null ? 1 : node.get("duration").intValue();
            String metaInfo = node.get("metaInfo").toString();

            JsonNode dataNode = node.get("data");
            StepData data = mapper.treeToValue(dataNode, type.getTargetType());
            steps.add(ProcessStepDto.builder()
                    .id(id)
                    .type(type.getValue())
                    .duration(duration)
                    .metaInfo(metaInfo)
                    .description(description)
                    .data(data)
                    .build()
            );
        }
        return steps;
    }
}
