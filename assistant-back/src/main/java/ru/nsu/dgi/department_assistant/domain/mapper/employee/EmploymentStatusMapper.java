package ru.nsu.dgi.department_assistant.domain.mapper.employee;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.nsu.dgi.department_assistant.domain.dto.employee.EmploymentStatusRequestDto;
import ru.nsu.dgi.department_assistant.domain.dto.employee.EmploymentStatusResponseDto;
import ru.nsu.dgi.department_assistant.domain.entity.employee.Employee;
import ru.nsu.dgi.department_assistant.domain.entity.employee.EmploymentStatus;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface EmploymentStatusMapper {
    @Mapping(target = "employeeId", source = "employee", qualifiedByName = "employeeToId")
    EmploymentStatusResponseDto entityToResponseDto(EmploymentStatus employmentStatus);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "employee", ignore = true)
    EmploymentStatus toEntity(EmploymentStatusRequestDto employmentStatusRequestDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "employee", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateRequestToEntity(
            EmploymentStatusRequestDto dto,
            @MappingTarget EmploymentStatus employmentStatus
    );

    @Named("employeeToId")
    default UUID employeeToId(Employee employee) {
        return employee == null ? null : employee.getId();
    }
}
