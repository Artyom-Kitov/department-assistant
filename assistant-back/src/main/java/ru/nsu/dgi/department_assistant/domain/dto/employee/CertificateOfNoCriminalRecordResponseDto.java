package ru.nsu.dgi.department_assistant.domain.dto.employee;

public record CertificateOfNoCriminalRecordResponseDto(
        Integer id,
        String dateOfReceipt,
        String expirationDate
) {}
