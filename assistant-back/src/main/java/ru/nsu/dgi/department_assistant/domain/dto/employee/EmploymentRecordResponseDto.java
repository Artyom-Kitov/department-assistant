package ru.nsu.dgi.department_assistant.domain.dto.employee;

import java.time.LocalDate;

public record EmploymentRecordResponseDto(
        Integer id,
        LocalDate dateOfReceipt
) {}
