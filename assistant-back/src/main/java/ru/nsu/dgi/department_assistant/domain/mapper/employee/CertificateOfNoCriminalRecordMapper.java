package ru.nsu.dgi.department_assistant.domain.mapper.employee;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.nsu.dgi.department_assistant.domain.dto.employee.CertificateOfNoCriminalRecordRequestDto;
import ru.nsu.dgi.department_assistant.domain.dto.employee.CertificateOfNoCriminalRecordResponseDto;
import ru.nsu.dgi.department_assistant.domain.entity.employee.CertificateOfNoCriminalRecord;

@Mapper(componentModel = "spring")
public interface CertificateOfNoCriminalRecordMapper {
    CertificateOfNoCriminalRecordResponseDto entityToDto(CertificateOfNoCriminalRecord certificateOfNoCriminalRecord);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "employee", ignore = true)
    CertificateOfNoCriminalRecord toEntity(
            CertificateOfNoCriminalRecordRequestDto certificateOfNoCriminalRecordRequestDto
    );
}
