package ru.nsu.dgi.department_assistant.domain.service.impl;

import lombok.RequiredArgsConstructor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Service;
import ru.nsu.dgi.department_assistant.domain.service.DocumentService;


import java.util.UUID;

@Service
@RequiredArgsConstructor

public class DocumentServiceImpl implements DocumentService {
    private final TemplateHandlerDispatcherServiceImpl templateProcessingService;
    private final FileServiceImpl fileService;

    @Override
    public byte[] fillAndConvertTemplate(Long templateId, UUID employeeId){
    XWPFDocument document = templateProcessingService.processTemplate(templateId, employeeId);

    return fileService.convertToBytes(document);
}

}



