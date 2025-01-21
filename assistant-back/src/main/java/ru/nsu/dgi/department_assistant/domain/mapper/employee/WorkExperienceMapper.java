package ru.nsu.dgi.department_assistant.domain.mapper.employee;

import org.mapstruct.Mapper;
import ru.nsu.dgi.department_assistant.domain.dto.employee.WorkExperienceResponseDto;
import ru.nsu.dgi.department_assistant.domain.entity.employee.WorkExperience;

@Mapper(componentModel = "spring")
public interface WorkExperienceMapper {
    WorkExperienceResponseDto entityToDto(WorkExperience workExperience);
}
