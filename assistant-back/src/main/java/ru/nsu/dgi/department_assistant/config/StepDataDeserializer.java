package ru.nsu.dgi.department_assistant.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.nsu.dgi.department_assistant.domain.dto.process.ProcessStepDto;
import ru.nsu.dgi.department_assistant.domain.dto.process.stepdata.CommonStepData;
import ru.nsu.dgi.department_assistant.domain.dto.process.stepdata.ConditionalStepData;
import ru.nsu.dgi.department_assistant.domain.dto.process.stepdata.FinalData;
import ru.nsu.dgi.department_assistant.domain.dto.process.stepdata.StepData;
import ru.nsu.dgi.department_assistant.domain.dto.process.stepdata.SubtasksStepData;
import ru.nsu.dgi.department_assistant.domain.exception.InvalidProcessTemplateException;

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
            int type = node.get("type").intValue();
            String description = node.get("description").toString();
            int duration = node.get("duration") == null ? 1 : node.get("duration").intValue();
            String metaInfo = node.get("metaInfo").toString();

            JsonNode dataNode = node.get("data");
            StepData data = switch (type) {
                case 1 -> mapper.treeToValue(dataNode, CommonStepData.class);
                case 2 -> mapper.treeToValue(dataNode, SubtasksStepData.class);
                case 3 -> mapper.treeToValue(dataNode, ConditionalStepData.class);
                case 4 -> mapper.treeToValue(dataNode, FinalData.class);
                default -> throw new InvalidProcessTemplateException("no step type with id = " + type);
            };
            steps.add(ProcessStepDto.builder()
                    .id(id)
                    .type(type)
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
