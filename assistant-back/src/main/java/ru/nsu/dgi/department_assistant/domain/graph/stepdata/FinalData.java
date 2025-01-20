package ru.nsu.dgi.department_assistant.domain.graph.stepdata;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.nsu.dgi.department_assistant.domain.graph.ProcessGraphNode;

import java.util.List;

@Getter
@RequiredArgsConstructor
public final class FinalData extends StepData {

    private final boolean isSuccessful;

    @Override
    public List<ProcessGraphNode> next() {
        return List.of();
    }
}
