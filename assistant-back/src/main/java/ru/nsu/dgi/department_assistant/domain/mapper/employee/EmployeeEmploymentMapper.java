package ru.nsu.dgi.department_assistant.domain.mapper.employee;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.nsu.dgi.department_assistant.domain.dto.employee.EmployeeEmploymentRequestDto;
import ru.nsu.dgi.department_assistant.domain.dto.employee.EmployeeEmploymentResponseDto;
import ru.nsu.dgi.department_assistant.domain.entity.employee.EmployeeEmployment;

@Mapper(componentModel = "spring", uses = {
        PostMapper.class,
        EmploymentTypeMapper.class
})
public interface EmployeeEmploymentMapper {

    EmployeeEmploymentResponseDto entityToResponseDto(EmployeeEmployment entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "employee", ignore = true)
    EmployeeEmployment toEntity(EmployeeEmploymentRequestDto dto);
}
