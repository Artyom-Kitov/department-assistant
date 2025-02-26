package ru.nsu.dgi.department_assistant.domain.mapper.employee;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.nsu.dgi.department_assistant.domain.dto.employee.AcademicDegreeRequestDto;
import ru.nsu.dgi.department_assistant.domain.dto.employee.AcademicDegreeResponseDto;
import ru.nsu.dgi.department_assistant.domain.entity.employee.AcademicDegree;

@Mapper(componentModel = "spring")
public interface AcademicDegreeMapper {
    AcademicDegreeResponseDto entityToResponseDto(AcademicDegree academicDegree);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "employee", ignore = true)
    AcademicDegree toEntity(AcademicDegreeRequestDto academicDegreeRequestDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "employee", ignore = true)
    void updateRequestToEntity(
            AcademicDegreeRequestDto academicDegreeRequestDto,
            @MappingTarget AcademicDegree academicDegree
    );
}
