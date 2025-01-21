package ru.nsu.dgi.department_assistant.domain.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.nsu.dgi.department_assistant.domain.dto.employee.EmployeeRequestDto;
import ru.nsu.dgi.department_assistant.domain.dto.employee.EmployeeResponseDto;
import ru.nsu.dgi.department_assistant.domain.dto.employee.EmployeeWithAllInfoResponseDto;
import ru.nsu.dgi.department_assistant.domain.entity.employee.Employee;
import ru.nsu.dgi.department_assistant.domain.mapper.employee.AcademicDegreeMapper;
import ru.nsu.dgi.department_assistant.domain.mapper.employee.CertificateOfNoCriminalRecordMapper;
import ru.nsu.dgi.department_assistant.domain.mapper.employee.ContactsMapper;
import ru.nsu.dgi.department_assistant.domain.mapper.employee.EmployeeMapper;
import ru.nsu.dgi.department_assistant.domain.mapper.employee.EmploymentRecordMapper;
import ru.nsu.dgi.department_assistant.domain.mapper.employee.EmploymentStatusMapper;
import ru.nsu.dgi.department_assistant.domain.mapper.employee.PassportInfoMapper;
import ru.nsu.dgi.department_assistant.domain.mapper.employee.WorkExperienceMapper;
import ru.nsu.dgi.department_assistant.domain.repository.employee.EmployeeRepository;
import ru.nsu.dgi.department_assistant.domain.service.EmployeeService;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeRepository employeeRepository;

    private final EmployeeMapper employeeMapper;
    private final ContactsMapper contactsMapper;
    private final AcademicDegreeMapper academicDegreeMapper;
    private final CertificateOfNoCriminalRecordMapper certificateOfNoCriminalRecordMapper;
    private final EmploymentStatusMapper employmentStatusMapper;
    private final PassportInfoMapper passportInfoMapper;
    private final WorkExperienceMapper workExperienceMapper;
    private final EmploymentRecordMapper employmentRecordMapper;

    @Override
    public List<EmployeeResponseDto> getAllEmployees() {
        List<Employee> employees = employeeRepository.findAll();

        return employees.stream()
                .map(employeeMapper::entityToResponseDto)
                .toList();
    }

    @Override
    public EmployeeResponseDto getEmployee(UUID id) {
        Employee employee = employeeRepository.findById(id).orElse(null);
        return employeeMapper.entityToResponseDto(employee);
    }

    @Override
    public void addEmployee(EmployeeRequestDto employeeRequestDto) {
        Employee employee = employeeMapper.requestToEntity(employeeRequestDto);
        employeeRepository.save(employee);
    }

    @Override
    public List<EmployeeWithAllInfoResponseDto> getAllEmployeeWithAllInfos() {
        List<Employee> employees = employeeRepository.findAllEmployeesWithInfo();

        return employees.stream()
                .map(this::createResponseWithInfo)
                .toList();
    }

    @Override
    public EmployeeWithAllInfoResponseDto getEmployeeWithAllInfos(UUID id) {
        Employee employee = employeeRepository.findEmployeeWithInfoById(id).orElse(null);

        return createResponseWithInfo(employee);
    }

    private EmployeeWithAllInfoResponseDto createResponseWithInfo(Employee employee) {
        if (employee == null) {
            return null;
        }

        return new EmployeeWithAllInfoResponseDto(
                employee.getId().toString(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getMiddleName(),
                contactsMapper.entityToResponseDto(employee.getContacts()),
                academicDegreeMapper.entityToResponseDto(employee.getAcademicDegree()),
                employmentStatusMapper.entityToResponseDto(employee.getEmploymentStatus()),
                employmentRecordMapper.entityToResponseDto(employee.getEmploymentRecord()),
                passportInfoMapper.entityToDto(employee.getPassportInfo()),
                workExperienceMapper.entityToDto(employee.getWorkExperience()),
                certificateOfNoCriminalRecordMapper.entityToDto(employee.getCertificateOfNoCriminalRecord()),
                employee.getAgreement(),
                employee.getHasCompletedAdvancedCourses(),
                employee.getNeedsMandatoryElection(),
                employee.getSnils(),
                employee.getInn(),
                employee.getIsArchived()
        );
    }
}
