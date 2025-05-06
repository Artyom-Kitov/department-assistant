package ru.nsu.dgi.department_assistant.domain.service;

import ru.nsu.dgi.department_assistant.domain.dto.employee.OrganizationalUnitRequestDto;
import ru.nsu.dgi.department_assistant.domain.dto.employee.OrganizationalUnitResponseDto;

import java.util.List;

public interface OrganizationalUnitService {
    List<OrganizationalUnitResponseDto> getAll();
    OrganizationalUnitResponseDto getById(Long id);
    OrganizationalUnitResponseDto create(OrganizationalUnitRequestDto request);
    OrganizationalUnitResponseDto update(Long id, OrganizationalUnitRequestDto request);
    void delete(Long id);
}
