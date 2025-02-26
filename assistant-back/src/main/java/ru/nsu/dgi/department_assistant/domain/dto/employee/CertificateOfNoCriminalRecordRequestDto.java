package ru.nsu.dgi.department_assistant.domain.dto.employee;

import java.time.LocalDate;

public record CertificateOfNoCriminalRecordRequestDto(
        LocalDate dateOfReceipt,
        LocalDate expirationDate
) {
}
