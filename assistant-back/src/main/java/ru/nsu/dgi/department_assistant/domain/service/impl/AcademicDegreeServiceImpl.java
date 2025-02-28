package ru.nsu.dgi.department_assistant.domain.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.nsu.dgi.department_assistant.domain.dto.employee.AcademicDegreeRequestDto;
import ru.nsu.dgi.department_assistant.domain.dto.employee.AcademicDegreeResponseDto;
import ru.nsu.dgi.department_assistant.domain.entity.employee.AcademicDegree;
import ru.nsu.dgi.department_assistant.domain.entity.employee.Employee;
import ru.nsu.dgi.department_assistant.domain.exception.EntityAlreadyExistsException;
import ru.nsu.dgi.department_assistant.domain.exception.EntityNotFoundException;
import ru.nsu.dgi.department_assistant.domain.exception.NullPropertyException;
import ru.nsu.dgi.department_assistant.domain.mapper.employee.AcademicDegreeMapper;
import ru.nsu.dgi.department_assistant.domain.repository.employee.AcademicDegreeRepository;
import ru.nsu.dgi.department_assistant.domain.repository.employee.EmployeeRepository;
import ru.nsu.dgi.department_assistant.domain.service.AcademicDegreeService;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AcademicDegreeServiceImpl implements AcademicDegreeService {
    private final AcademicDegreeRepository academicDegreeRepository;
    private final AcademicDegreeMapper academicDegreeMapper;
    private final EmployeeRepository employeeRepository;

    @Override
    @Transactional(readOnly = true)
    public List<AcademicDegreeResponseDto> getAll() {
        log.info("getting all academic degrees");
        List<AcademicDegree> academicDegrees = academicDegreeRepository.findAll();
        log.info("found {} academic degrees", academicDegrees.size());

        return academicDegrees.stream()
                .map(academicDegreeMapper::entityToResponseDto)
                .toList();
    }

    @Override
    @Transactional
    public AcademicDegreeResponseDto create(
            UUID employeeId,
            AcademicDegreeRequestDto academicDegreeRequestDto
    ) {
        if (employeeId == null) {
            throw new NullPropertyException("EmployeeId must not be null");
        }
        log.info("creating academic degree for employee {}", employeeId);
        AcademicDegree academicDegree = academicDegreeMapper.toEntity(academicDegreeRequestDto);
        setEmployee(employeeId, academicDegree);
        academicDegreeRepository.save(academicDegree);
        log.info("successfully created academic degree for employee {}", employeeId);

        return academicDegreeMapper.entityToResponseDto(academicDegree);
    }

    @Override
    @Transactional
    public AcademicDegreeResponseDto update(
            UUID employeeId,
            AcademicDegreeRequestDto academicDegreeRequestDto
    ) {
        if (employeeId == null) {
            throw new NullPropertyException("EmployeeId must not be null");
        }
        log.info("updating academic degree for employee {}", employeeId);
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException(String.valueOf(employeeId)));
        AcademicDegree academicDegree = employee.getAcademicDegree();
        if (academicDegree == null) {
            throw new EntityNotFoundException("Academic degree of" + employeeId);
        }
        academicDegreeMapper.updateRequestToEntity(academicDegreeRequestDto, academicDegree);
        academicDegreeRepository.save(academicDegree);
        log.info("successfully updated academic degree for employee {}", employeeId);

        return academicDegreeMapper.entityToResponseDto(academicDegree);
    }

    @Override
    @Transactional(readOnly = true)
    public AcademicDegreeResponseDto getByEmployeeId(UUID id) {
        if (id == null) {
            throw new NullPropertyException("Id must not be null");
        }
        log.info("getting academic degree by employee id: {}", id);
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.valueOf(id)));
        AcademicDegree academicDegree = employee.getAcademicDegree();
        if (academicDegree == null) {
            throw new EntityNotFoundException(String.valueOf(id));
        }
        log.info("found academic degree by employee id: {}", id);

        return academicDegreeMapper.entityToResponseDto(academicDegree);
    }

    @Override
    @Transactional
    public void deleteByEmployeeId(UUID id) {
        if (id == null) {
            throw new NullPropertyException("Id must not be null");
        }
        log.info("deleting academic degree by employee id: {}", id);
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.valueOf(id)));
        AcademicDegree academicDegree = employee.getAcademicDegree();
        if (academicDegree == null) {
            throw new EntityNotFoundException(String.valueOf(id));
        }
        employee.setAcademicDegree(null);
        academicDegreeRepository.delete(academicDegree);
        log.info("successfully deleted academic degree by employee id: {}", id);
    }

    private void setEmployee(UUID employeeId, AcademicDegree academicDegree) {
        log.info("finding an employee by id {} for academic degree", employeeId);
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException(String.valueOf(employeeId)));
        if (employee.getAcademicDegree() != null) {
            throw new EntityAlreadyExistsException(
                    "Academic degree already exists for employee with id: " + employeeId
            );
        }
        log.info("found employee with id {} for academic degree", employeeId);
        academicDegree.setEmployee(employee);
        employee.setAcademicDegree(academicDegree);
    }
}
