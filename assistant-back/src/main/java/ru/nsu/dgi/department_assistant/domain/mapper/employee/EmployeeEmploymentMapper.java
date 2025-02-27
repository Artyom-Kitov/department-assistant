package ru.nsu.dgi.department_assistant.domain.mapper.employee;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.nsu.dgi.department_assistant.domain.dto.employee.EmployeeEmploymentRequestDto;
import ru.nsu.dgi.department_assistant.domain.dto.employee.EmployeeEmploymentResponseDto;
import ru.nsu.dgi.department_assistant.domain.entity.employee.Employee;
import ru.nsu.dgi.department_assistant.domain.entity.employee.EmployeeEmployment;

import java.util.UUID;

@Mapper(componentModel = "spring", uses = {
        PostMapper.class,
        EmploymentTypeMapper.class
})
public interface EmployeeEmploymentMapper {
    @Mapping(target = "employeeId", source = "employee", qualifiedByName = "employeeToId")
    EmployeeEmploymentResponseDto entityToResponseDto(EmployeeEmployment entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "employee", ignore = true)
    EmployeeEmployment toEntity(EmployeeEmploymentRequestDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "employee", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateRequestToEntity(EmployeeEmploymentRequestDto dto, @MappingTarget EmployeeEmployment entity);

    @Named("employeeToId")
    default UUID employeeToId(Employee employee) {
        return employee == null ? null : employee.getId();
    }
}
