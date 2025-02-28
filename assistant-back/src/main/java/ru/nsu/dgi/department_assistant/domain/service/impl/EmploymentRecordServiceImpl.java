package ru.nsu.dgi.department_assistant.domain.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.nsu.dgi.department_assistant.domain.dto.employee.EmploymentRecordRequestDto;
import ru.nsu.dgi.department_assistant.domain.dto.employee.EmploymentRecordResponseDto;
import ru.nsu.dgi.department_assistant.domain.entity.employee.Employee;
import ru.nsu.dgi.department_assistant.domain.entity.employee.EmploymentRecord;
import ru.nsu.dgi.department_assistant.domain.exception.EntityAlreadyExistsException;
import ru.nsu.dgi.department_assistant.domain.exception.EntityNotFoundException;
import ru.nsu.dgi.department_assistant.domain.exception.NullPropertyException;
import ru.nsu.dgi.department_assistant.domain.mapper.employee.EmploymentRecordMapper;
import ru.nsu.dgi.department_assistant.domain.repository.employee.EmployeeRepository;
import ru.nsu.dgi.department_assistant.domain.repository.employee.EmploymentRecordRepository;
import ru.nsu.dgi.department_assistant.domain.service.EmploymentRecordService;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
@Service
public class EmploymentRecordServiceImpl implements EmploymentRecordService {
    private final EmploymentRecordRepository employmentRecordRepository;
    private final EmploymentRecordMapper employmentRecordMapper;
    private final EmployeeRepository employeeRepository;

    @Override
    @Transactional(readOnly = true)
    public List<EmploymentRecordResponseDto> getAll() {
        log.info("getting all employee records");
        List<EmploymentRecord> records = employmentRecordRepository.findAll();
        log.info("successfully found {} employee records", records.size());

        return records.stream()
                .map(employmentRecordMapper::entityToResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public EmploymentRecordResponseDto getByEmployeeId(UUID id) {
        if (id == null) {
            throw new NullPropertyException("EmployeeId must not be null");
        }
        log.info("getting record by employee id {}", id);
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.valueOf(id)));
        EmploymentRecord record = employee.getEmploymentRecord();
        if (record == null) {
            throw new EntityNotFoundException("Could not find record by employee id " + id);
        }
        log.info("successfully found record by employee id {}", id);

        return employmentRecordMapper.entityToResponseDto(record);
    }

    @Override
    @Transactional
    public EmploymentRecordResponseDto create(UUID employeeId, EmploymentRecordRequestDto employmentRecordRequestDto) {
        if (employeeId == null) {
            throw new NullPropertyException("EmployeeId must not be null");
        }
        log.info("creating record with employee id {}", employeeId);
        EmploymentRecord record = employmentRecordMapper.toEntity(employmentRecordRequestDto);
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException(String.valueOf(employeeId)));
        if (employee.getEmploymentRecord() != null) {
            throw new EntityAlreadyExistsException(
                    "Employment record with employee id " + employeeId + " already exists"
            );
        }
        record.setEmployee(employee);
        employee.setEmploymentRecord(record);
        employmentRecordRepository.save(record);
        log.info("successfully created record with employee id {}", employeeId);

        return employmentRecordMapper.entityToResponseDto(record);
    }

    @Override
    @Transactional
    public EmploymentRecordResponseDto update(UUID employeeId, EmploymentRecordRequestDto employmentRecordRequestDto) {
        if (employeeId == null) {
            throw new NullPropertyException("EmployeeId must not be null");
        }
        log.info("updating record with employee id {}", employeeId);
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException(String.valueOf(employeeId)));
        EmploymentRecord record = employee.getEmploymentRecord();
        if (record == null) {
            throw new EntityNotFoundException("Could not find record by employee id " + employeeId);
        }
        employmentRecordMapper.updateRequestToEntity(employmentRecordRequestDto, record);
        employmentRecordRepository.save(record);
        log.info("successfully updated record with employee id {}", employeeId);

        return employmentRecordMapper.entityToResponseDto(record);
    }

    @Override
    @Transactional
    public void deleteByEmployeeId(UUID employeeId) {
        if (employeeId == null) {
            throw new NullPropertyException("EmployeeId must not be null");
        }
        log.info("deleting record by employee id {}", employeeId);
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException(String.valueOf(employeeId)));
        EmploymentRecord record = employee.getEmploymentRecord();
        if (record == null) {
            throw new EntityNotFoundException("Could not find record by employee id " + employeeId);
        }
        employee.setEmploymentRecord(null);
        employmentRecordRepository.delete(record);
    }
}
