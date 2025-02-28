package ru.nsu.dgi.department_assistant.domain.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.nsu.dgi.department_assistant.domain.dto.employee.EmployeeEmploymentRequestDto;
import ru.nsu.dgi.department_assistant.domain.dto.employee.EmployeeEmploymentResponseDto;
import ru.nsu.dgi.department_assistant.domain.entity.employee.Employee;
import ru.nsu.dgi.department_assistant.domain.entity.employee.EmployeeEmployment;
import ru.nsu.dgi.department_assistant.domain.entity.employee.EmploymentType;
import ru.nsu.dgi.department_assistant.domain.entity.employee.Post;
import ru.nsu.dgi.department_assistant.domain.entity.id.EmployeeEmploymentId;
import ru.nsu.dgi.department_assistant.domain.exception.EntityNotFoundException;
import ru.nsu.dgi.department_assistant.domain.mapper.employee.EmployeeEmploymentMapper;
import ru.nsu.dgi.department_assistant.domain.repository.employee.EmployeeEmploymentRepository;
import ru.nsu.dgi.department_assistant.domain.repository.employee.EmployeeRepository;
import ru.nsu.dgi.department_assistant.domain.repository.employee.EmploymentTypeRepository;
import ru.nsu.dgi.department_assistant.domain.repository.employee.PostRepository;
import ru.nsu.dgi.department_assistant.domain.service.EmployeeEmploymentService;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmployeeEmploymentServiceImpl implements EmployeeEmploymentService {
    private final EmployeeEmploymentRepository employeeEmploymentRepository;
    private final EmployeeEmploymentMapper employeeEmploymentMapper;
    private final EmployeeRepository employeeRepository;
    private final PostRepository postRepository;
    private final EmploymentTypeRepository employmentTypeRepository;

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeEmploymentResponseDto> getAll() {
        log.info("finding all employments");
        List<EmployeeEmployment> employments = employeeEmploymentRepository.findAll();
        log.info("found {} employments", employments.size());

        return employments.stream()
                .map(employeeEmploymentMapper::entityToResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeEmploymentResponseDto> getByEmployeeId(UUID id) {
        if (id == null) {
            throw new NullPointerException("EmployeeId must not be null");
        }
        log.info("finding employments by employee id {}", id);
        List<EmployeeEmployment> employments = employeeEmploymentRepository.findByEmployeeId(id);
        log.info("found {} employments by employee id {}", employments.size(), id);

        return employments.stream()
                .map(employeeEmploymentMapper::entityToResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeEmploymentResponseDto getById(UUID employeeId, Integer postId, Integer employmentTypeId) {
        if (employeeId == null || postId == null || employmentTypeId == null) {
            throw new NullPointerException("EmployeeId and postId and employmentTypeId must not be null");
        }
        EmployeeEmploymentId id = new EmployeeEmploymentId(
                employeeId,
                postId,
                employmentTypeId
        );
        log.info("finding employment with id {}", id);
        EmployeeEmployment employment = employeeEmploymentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(id.toString()));
        log.info("successfully found employment with id {}", id);

        return employeeEmploymentMapper.entityToResponseDto(employment);
    }

    @Override
    @Transactional
    public EmployeeEmploymentResponseDto create(
            UUID employeeId,
            Integer postId,
            Integer employmentTypeId
    ) {
        if (employeeId == null || postId == null || employmentTypeId == null) {
            throw new NullPointerException("EmployeeId and postId and employmentTypeId must not be null");
        }
        log.info("creating a new employment");
        EmployeeEmployment newEmployment = constructEmployment(
                employeeId,
                postId,
                employmentTypeId
        );
        EmployeeEmployment savedEntity = employeeEmploymentRepository.save(newEmployment);
        log.info("successfully created an employment for employee with id {}", employeeId);

        return employeeEmploymentMapper.entityToResponseDto(savedEntity);
    }

    @Override
    public EmployeeEmploymentResponseDto update(
            UUID employeeId,
            Integer postId,
            Integer employmentTypeId,
            EmployeeEmploymentRequestDto dto
    ) {
        if (employeeId == null || postId == null || employmentTypeId == null) {
            throw new NullPointerException("EmployeeId and postId and employmentTypeId must not be null");
        }
        log.info("updating an employment");
        EmployeeEmployment employment = constructEmployment(
                employeeId,
                postId,
                employmentTypeId
        );
        employeeEmploymentMapper.updateRequestToEntity(dto, employment);
        employeeEmploymentRepository.save(employment);
        log.info("successfully updated an employment for employee with id {}", employeeId);

        return employeeEmploymentMapper.entityToResponseDto(employment);
    }

    @Override
    public void delete(
            UUID employeeId,
            Integer postId,
            Integer employmentTypeId
    ) {
        if (employeeId == null || postId == null || employmentTypeId == null) {
            throw new NullPointerException("EmployeeId and postId and employmentTypeId must not be null");
        }
        log.info("deleting an employment");
        EmployeeEmployment employment = constructEmployment(
                employeeId,
                postId,
                employmentTypeId
        );
        if (employment.getEmployee() != null && employment.getEmployee().getEmployments() != null) {
            employment.getEmployee().getEmployments().remove(employment);
        }

        if (employment.getPost() != null && employment.getPost().getEmployments() != null) {
            employment.getPost().getEmployments().remove(employment);
        }

        if (employment.getEmploymentType() != null && employment.getEmploymentType().getEmployments() != null) {
            employment.getEmploymentType().getEmployments().remove(employment);
        }
        employeeEmploymentRepository.delete(employment);
        log.info("successfully deleted an employment for employee with id {}", employeeId);
    }

    private EmployeeEmployment constructEmployment(
            UUID employeeId,
            Integer postId,
            Integer employmentTypeId
    ) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException(String.valueOf(employeeId)));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException(String.valueOf(postId)));

        EmploymentType employmentType = employmentTypeRepository.findById(employmentTypeId)
                .orElseThrow(() -> new EntityNotFoundException(String.valueOf(employmentTypeId)));

        EmployeeEmploymentId id = new EmployeeEmploymentId(
                employeeId,
                postId,
                employmentTypeId
        );

        EmployeeEmployment newEmployment = new EmployeeEmployment();
        newEmployment.setId(id);
        newEmployment.setEmployee(employee);
        newEmployment.setPost(post);
        newEmployment.setEmploymentType(employmentType);

        return newEmployment;
    }
}
