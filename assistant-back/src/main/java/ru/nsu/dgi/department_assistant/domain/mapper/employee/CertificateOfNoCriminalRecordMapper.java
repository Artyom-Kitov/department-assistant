package ru.nsu.dgi.department_assistant.domain.mapper.employee;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.nsu.dgi.department_assistant.domain.dto.employee.CertificateOfNoCriminalRecordRequestDto;
import ru.nsu.dgi.department_assistant.domain.dto.employee.CertificateOfNoCriminalRecordResponseDto;
import ru.nsu.dgi.department_assistant.domain.entity.employee.CertificateOfNoCriminalRecord;
import ru.nsu.dgi.department_assistant.domain.entity.employee.Employee;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface CertificateOfNoCriminalRecordMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "employee", ignore = true)
    CertificateOfNoCriminalRecord toEntity(
            CertificateOfNoCriminalRecordRequestDto certificateOfNoCriminalRecordRequestDto
    );

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "employee", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateRequestToEntity(
            CertificateOfNoCriminalRecordRequestDto dto,
            @MappingTarget CertificateOfNoCriminalRecord entity
    );

    @Mapping(target = "employeeId", source = "employee", qualifiedByName = "employeeToId")
    CertificateOfNoCriminalRecordResponseDto toResponse(CertificateOfNoCriminalRecord entity);

    @Named("employeeToId")
    default UUID employeeToId(Employee employee) {
        return employee == null ? null : employee.getId();
    }
}
