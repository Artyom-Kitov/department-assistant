package ru.nsu.dgi.department_assistant.domain.dto.process.stepdata;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Getter
public final class FinalData extends StepData {
    @JsonProperty("isSuccessful")
    private final boolean isSuccessful;

    @Override
    public List<UUID> next() {
        return List.of();
    }
}
