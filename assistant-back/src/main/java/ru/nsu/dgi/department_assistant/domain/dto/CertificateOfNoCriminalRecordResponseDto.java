package ru.nsu.dgi.department_assistant.domain.dto;

public record CertificateOfNoCriminalRecordResponseDto(
        Integer id,
        String dateOfReceipt,
        String expirationDate
) {}
