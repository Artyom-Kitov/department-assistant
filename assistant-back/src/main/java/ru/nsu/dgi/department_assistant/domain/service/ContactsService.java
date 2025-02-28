package ru.nsu.dgi.department_assistant.domain.service;

import ru.nsu.dgi.department_assistant.domain.dto.employee.ContactsRequestDto;
import ru.nsu.dgi.department_assistant.domain.dto.employee.ContactsResponseDto;

import java.util.List;
import java.util.UUID;

public interface ContactsService {
    List<ContactsResponseDto> getAll();

    ContactsResponseDto getById(Long id);

    ContactsResponseDto createEmployeeContact(
            UUID employeeId,
            ContactsRequestDto contactsRequestDto
    );

    ContactsResponseDto createOrganizationalUnitContact(
            Long organizationalUnitId,
            ContactsRequestDto contactsRequestDto
    );

    ContactsResponseDto updateEmployeeContact(
            UUID employeeId,
            ContactsRequestDto contactsRequestDto
    );

    ContactsResponseDto updateOrganizationalUnitContact(
            Long organizationalUnitId,
            ContactsRequestDto contactsRequestDto
    );

    ContactsResponseDto getContactsByEmployeeId(UUID id);

    ContactsResponseDto getContactsByOrganizationalUnitId(Long id);

    void deleteEmployeeContact(UUID id);

    void deleteOrganizationalUnitContact(Long id);
}
