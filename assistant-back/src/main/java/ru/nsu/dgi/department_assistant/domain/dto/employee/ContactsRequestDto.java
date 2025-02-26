package ru.nsu.dgi.department_assistant.domain.dto.employee;

import java.util.UUID;

public record ContactsRequestDto(
        UUID employeeId,
        Long organizationalUnitId,
        String phoneNumber,
        String email,
        String nsuEmail,
        String additionalInfo
) {}
