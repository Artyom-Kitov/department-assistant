package ru.nsu.dgi.department_assistant.domain.service;

import ru.nsu.dgi.department_assistant.domain.dto.employee.EmployeeWithAllInfoResponseDto;

import java.util.Map;

public interface MapBuilderService {
    Map<String, String> buildMapForPerson(EmployeeWithAllInfoResponseDto employee);
    Map<String, String> buildEmptyMap();
}
