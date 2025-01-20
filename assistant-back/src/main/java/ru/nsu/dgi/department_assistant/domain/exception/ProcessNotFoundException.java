package ru.nsu.dgi.department_assistant.domain.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class ProcessNotFoundException extends RuntimeException {
    private final UUID id;
}
