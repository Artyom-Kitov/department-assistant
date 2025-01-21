package ru.nsu.dgi.department_assistant.domain.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class EntityNotFoundDto extends RuntimeException {
    private final String id;
}
