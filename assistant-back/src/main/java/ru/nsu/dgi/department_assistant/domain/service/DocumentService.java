package ru.nsu.dgi.department_assistant.domain.service;
import java.util.UUID;

public interface DocumentService {

    byte[] fillAndConvertTemplate(Long templateId, UUID employeeId);

}
