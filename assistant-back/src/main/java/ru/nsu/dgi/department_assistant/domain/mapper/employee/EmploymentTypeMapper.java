package ru.nsu.dgi.department_assistant.domain.mapper.employee;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.nsu.dgi.department_assistant.domain.dto.employee.EmploymentTypeRequestDto;
import ru.nsu.dgi.department_assistant.domain.dto.employee.EmploymentTypeResponseDto;
import ru.nsu.dgi.department_assistant.domain.entity.employee.EmploymentType;

@Mapper(componentModel = "spring")
public interface EmploymentTypeMapper {
    EmploymentTypeResponseDto entityToResponseDto(EmploymentType employmentType);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "employments", ignore = true)
    EmploymentType toEntity(EmploymentTypeRequestDto employmentTypeRequestDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "employments", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateRequestToEntity(
            EmploymentTypeRequestDto employmentTypeRequestDto,
            @MappingTarget EmploymentType employmentType
    );
}
