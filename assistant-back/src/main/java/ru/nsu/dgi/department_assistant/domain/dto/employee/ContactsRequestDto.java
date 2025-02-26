package ru.nsu.dgi.department_assistant.domain.dto.employee;

public record ContactsRequestDto(
        String phoneNumber,
        String email,
        String nsuEmail,
        String additionalInfo
) {
}
