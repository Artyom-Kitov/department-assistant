package ru.nsu.dgi.department_assistant.domain.mapper.employee;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.nsu.dgi.department_assistant.domain.dto.employee.AcademicDegreeRequestDto;
import ru.nsu.dgi.department_assistant.domain.dto.employee.AcademicDegreeResponseDto;
import ru.nsu.dgi.department_assistant.domain.entity.employee.AcademicDegree;
import ru.nsu.dgi.department_assistant.domain.entity.employee.Employee;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface AcademicDegreeMapper {
    @Mapping(target = "employeeId", source = "employee", qualifiedByName = "employeeToId")
    AcademicDegreeResponseDto entityToResponseDto(AcademicDegree academicDegree);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "employee", ignore = true)
    AcademicDegree toEntity(AcademicDegreeRequestDto academicDegreeRequestDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "employee", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateRequestToEntity(
            AcademicDegreeRequestDto academicDegreeRequestDto,
            @MappingTarget AcademicDegree academicDegree
    );

    @Named("employeeToId")
    default UUID employeeToId(Employee employee) {
        return employee == null ? null : employee.getId();
    }
}
