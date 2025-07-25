package ru.nsu.dgi.department_assistant.domain.dto.employee;

import java.time.LocalDate;
import java.util.UUID;

public record EmploymentRecordResponseDto(
        Integer id,
        UUID employeeId,
        LocalDate dateOfReceipt
) {
}
