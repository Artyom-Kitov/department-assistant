package ru.nsu.dgi.department_assistant.domain.service;

import ru.nsu.dgi.department_assistant.domain.dto.employee.AcademicDegreeRequestDto;
import ru.nsu.dgi.department_assistant.domain.dto.employee.AcademicDegreeResponseDto;

import java.util.List;
import java.util.UUID;

public interface AcademicDegreeService {
    List<AcademicDegreeResponseDto> getAll();

    AcademicDegreeResponseDto create(
            UUID employeeId,
            AcademicDegreeRequestDto academicDegreeRequestDto
    );

    AcademicDegreeResponseDto update(
            UUID employeeId,
            AcademicDegreeRequestDto academicDegreeRequestDto
    );

    AcademicDegreeResponseDto getByEmployeeId(UUID id);

    void deleteByEmployeeId(UUID id);
}
