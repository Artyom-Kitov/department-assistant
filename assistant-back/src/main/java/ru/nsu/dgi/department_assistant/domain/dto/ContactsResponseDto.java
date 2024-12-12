package ru.nsu.dgi.department_assistant.domain.dto;

public record ContactsResponseDto(
        Long id,
        String phoneNumber,
        String email,
        String additionalInfo
) {}
