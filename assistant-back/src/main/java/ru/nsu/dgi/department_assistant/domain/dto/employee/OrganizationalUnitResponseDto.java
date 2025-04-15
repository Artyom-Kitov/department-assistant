package ru.nsu.dgi.department_assistant.domain.dto.employee;

public record OrganizationalUnitResponseDto(
        Long id,
        String name,
        OrganizationResponseDto organization,
        ContactsResponseDto contacts
) {

}
