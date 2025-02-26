package ru.nsu.dgi.department_assistant.domain.mapper.employee;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.nsu.dgi.department_assistant.domain.dto.employee.WorkExperienceRequestDto;
import ru.nsu.dgi.department_assistant.domain.dto.employee.WorkExperienceResponseDto;
import ru.nsu.dgi.department_assistant.domain.entity.employee.Employee;
import ru.nsu.dgi.department_assistant.domain.entity.employee.WorkExperience;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface WorkExperienceMapper {
    @Mapping(target = "employeeId", source = "employee", qualifiedByName = "employeeToId")
    WorkExperienceResponseDto toResponse(WorkExperience workExperience);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "employee", ignore = true)
    WorkExperience toEntity(WorkExperienceRequestDto workExperienceRequestDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "employee", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateRequestToEntity(WorkExperienceRequestDto dto, @MappingTarget WorkExperience workExperience);

    @Named("employeeToId")
    default UUID employeeToId(Employee employee) {
        return employee == null ? null : employee.getId();
    }
}
