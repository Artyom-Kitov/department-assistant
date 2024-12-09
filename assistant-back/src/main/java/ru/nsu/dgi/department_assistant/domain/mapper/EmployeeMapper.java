package ru.nsu.dgi.department_assistant.domain.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.nsu.dgi.department_assistant.domain.dto.EmployeeResponseDTO;
import ru.nsu.dgi.department_assistant.domain.entity.Employee;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {
    EmployeeMapper INSTANCE = Mappers.getMapper(EmployeeMapper.class);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "firstName", target = "firstName")
    @Mapping(source = "lastName", target = "lastName")
    @Mapping(source = "middleName", target = "middleName")
    @Mapping(source = "hasCompletedAdvancedCourses", target = "hasCompletedAdvancedCourses")
    @Mapping(source = "needsMandatoryElection", target = "needsMandatoryElection")
    @Mapping(source = "snils", target = "snils")
    @Mapping(source = "inn", target = "inn")
    @Mapping(source = "isArchived", target = "isArchived")
    EmployeeResponseDTO toResponseDTO(Employee employee);
}
