package ru.nsu.dgi.department_assistant.domain.dto.employee;

import java.time.LocalDateTime;
import java.util.UUID;

public record CertificateOfNoCriminalRecordRequestDto(
        UUID employeeId,
        LocalDateTime dateOfReceipt,
        LocalDateTime expirationDate
) {}
