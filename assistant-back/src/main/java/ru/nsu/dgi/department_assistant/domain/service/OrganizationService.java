package ru.nsu.dgi.department_assistant.domain.service;

import ru.nsu.dgi.department_assistant.domain.dto.employee.OrganizationRequestDto;
import ru.nsu.dgi.department_assistant.domain.dto.employee.OrganizationResponseDto;

import java.util.List;

public interface OrganizationService {
    List<OrganizationResponseDto> getAll();
    OrganizationResponseDto getById(Integer id);
    OrganizationResponseDto create(OrganizationRequestDto request);
    OrganizationResponseDto update(Integer id, OrganizationRequestDto request);
    void delete(Integer id);
}
