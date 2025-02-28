package ru.nsu.dgi.department_assistant.domain.service;

import ru.nsu.dgi.department_assistant.domain.dto.employee.EmploymentTypeRequestDto;
import ru.nsu.dgi.department_assistant.domain.dto.employee.EmploymentTypeResponseDto;

import java.util.List;

public interface EmploymentTypeService {
    List<EmploymentTypeResponseDto> getAll();

    EmploymentTypeResponseDto getById(Integer id);

    EmploymentTypeResponseDto create(EmploymentTypeRequestDto employmentTypeRequestDto);

    EmploymentTypeResponseDto update(Integer id, EmploymentTypeRequestDto employmentTypeRequestDto);

    void delete(Integer id);
}
