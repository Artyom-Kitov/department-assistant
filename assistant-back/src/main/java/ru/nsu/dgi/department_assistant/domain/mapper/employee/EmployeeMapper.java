package ru.nsu.dgi.department_assistant.domain.mapper.employee;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.nsu.dgi.department_assistant.domain.dto.employee.EmployeeRequestDto;
import ru.nsu.dgi.department_assistant.domain.dto.employee.EmployeeResponseDto;
import ru.nsu.dgi.department_assistant.domain.dto.employee.EmployeeWithAllInfoRequestDto;
import ru.nsu.dgi.department_assistant.domain.dto.employee.EmployeeWithAllInfoResponseDto;
import ru.nsu.dgi.department_assistant.domain.entity.employee.Employee;

@Mapper(componentModel = "spring", uses = {
        ContactsMapper.class,
        AcademicDegreeMapper.class,
        EmploymentStatusMapper.class,
        EmploymentRecordMapper.class,
        PassportInfoMapper.class,
        WorkExperienceMapper.class,
        CertificateOfNoCriminalRecordMapper.class,
        EmployeeEmploymentMapper.class
})
public interface EmployeeMapper {
    EmployeeResponseDto entityToResponseDto(Employee employee);

    @Mapping(target = "id", expression = "java(java.util.UUID.randomUUID())")
    @Mapping(target = "contacts", ignore = true)
    @Mapping(target = "passportInfo", ignore = true)
    @Mapping(target = "employmentStatus", ignore = true)
    @Mapping(target = "employmentRecord", ignore = true)
    @Mapping(target = "academicDegree", ignore = true)
    @Mapping(target = "certificateOfNoCriminalRecord", ignore = true)
    @Mapping(target = "workExperience", ignore = true)
    @Mapping(target = "employments", ignore = true)
    Employee requestToEntity(EmployeeRequestDto employeeRequestDto);

    @Mapping(target = "id", expression = "java(java.util.UUID.randomUUID())")
    Employee requestWithInfoToEntity(EmployeeWithAllInfoRequestDto employeeWithAllInfoRequestDto);

    EmployeeWithAllInfoResponseDto entityToWithInfoResponse(Employee employee);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "contacts", ignore = true)
    @Mapping(target = "passportInfo", ignore = true)
    @Mapping(target = "employmentStatus", ignore = true)
    @Mapping(target = "employmentRecord", ignore = true)
    @Mapping(target = "academicDegree", ignore = true)
    @Mapping(target = "certificateOfNoCriminalRecord", ignore = true)
    @Mapping(target = "workExperience", ignore = true)
    @Mapping(target = "employments", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateRequestToEntity(EmployeeRequestDto employeeRequestDto, @MappingTarget Employee employee);
}
