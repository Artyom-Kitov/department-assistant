package ru.nsu.dgi.department_assistant.domain.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.nsu.dgi.department_assistant.domain.dto.employee.WorkExperienceRequestDto;
import ru.nsu.dgi.department_assistant.domain.dto.employee.WorkExperienceResponseDto;
import ru.nsu.dgi.department_assistant.domain.entity.employee.Employee;
import ru.nsu.dgi.department_assistant.domain.entity.employee.WorkExperience;
import ru.nsu.dgi.department_assistant.domain.exception.EntityNotFoundException;
import ru.nsu.dgi.department_assistant.domain.exception.NullPropertyException;
import ru.nsu.dgi.department_assistant.domain.mapper.employee.WorkExperienceMapper;
import ru.nsu.dgi.department_assistant.domain.repository.employee.EmployeeRepository;
import ru.nsu.dgi.department_assistant.domain.repository.employee.WorkExperienceRepository;
import ru.nsu.dgi.department_assistant.domain.service.WorkExperienceService;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class WorkExperienceServiceImpl implements WorkExperienceService {
    private final WorkExperienceRepository workExperienceRepository;
    private final WorkExperienceMapper workExperienceMapper;
    private final EmployeeRepository employeeRepository;

    @Override
    public List<WorkExperienceResponseDto> getAll() {
        log.info("finding all work experiences");
        List<WorkExperience> workExperiences = workExperienceRepository.findAll();
        log.info("successfully found {} work experiences", workExperiences.size());

        return workExperiences.stream()
                .map(workExperienceMapper::toResponse)
                .toList();
    }

    @Override
    public WorkExperienceResponseDto getByEmployeeId(UUID employeeId) {
        if (employeeId == null) {
            throw new NullPropertyException("EmployeeId must not be null");
        }
        log.info("finding work experience by employee id {}", employeeId);
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException(String.valueOf(employeeId)));
        WorkExperience workExperience = employee.getWorkExperience();
        if (workExperience == null) {
            throw new EntityNotFoundException("Could not work experience for employee id " + employeeId);
        }
        log.info("successfully found work experience for employee id {}", employeeId);

        return workExperienceMapper.toResponse(workExperience);
    }

    @Override
    public WorkExperienceResponseDto create(UUID employeeId, WorkExperienceRequestDto workExperienceRequestDto) {
        if (employeeId == null) {
            throw new NullPropertyException("EmployeeId must not be null");
        }
        log.info("creating work experience by employee id {}", employeeId);
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException(String.valueOf(employeeId)));
        WorkExperience workExperience = workExperienceMapper.toEntity(workExperienceRequestDto);
        workExperience.setEmployee(employee);
        employee.setWorkExperience(workExperience);
        workExperienceRepository.save(workExperience);
        log.info("successfully created work experience for employee id {}", employeeId);

        return workExperienceMapper.toResponse(workExperience);
    }

    @Override
    public WorkExperienceResponseDto update(UUID employeeId, WorkExperienceRequestDto workExperienceRequestDto) {
        if (employeeId == null) {
            throw new NullPropertyException("EmployeeId must not be null");
        }
        log.info("updating work experience by employee id {}", employeeId);
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException(String.valueOf(employeeId)));
        WorkExperience workExperience = employee.getWorkExperience();
        if (workExperience == null) {
            throw new EntityNotFoundException("Could not work experience for employee id " + employeeId);
        }
        workExperienceMapper.updateRequestToEntity(workExperienceRequestDto, workExperience);
        workExperienceRepository.save(workExperience);
        log.info("successfully updated work experience for employee id {}", employeeId);

        return workExperienceMapper.toResponse(workExperience);
    }

    @Override
    public void delete(UUID employeeId) {
        if (employeeId == null) {
            throw new NullPropertyException("EmployeeId must not be null");
        }
        log.info("deleting work experience by employee id {}", employeeId);
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException(String.valueOf(employeeId)));
        WorkExperience workExperience = employee.getWorkExperience();
        if (workExperience == null) {
            throw new EntityNotFoundException("Could not work experience for employee id " + employeeId);
        }
        employeeRepository.delete(employee);
        log.info("successfully deleted work experience for employee id {}", employeeId);
    }
}
