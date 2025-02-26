package ru.nsu.dgi.department_assistant.domain.dto.employee;

import java.util.UUID;

public record PassportInfoResponseDto(
        Integer id,
        UUID employeeId,
        String passportInfo
) {
}
