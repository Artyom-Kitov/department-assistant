package ru.nsu.dgi.department_assistant.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.nsu.dgi.department_assistant.domain.dto.process.ProcessTemplateCreationRequestDto;
import ru.nsu.dgi.department_assistant.domain.dto.process.ProcessTemplateCreationResponseDto;
import ru.nsu.dgi.department_assistant.domain.service.ProcessTemplateService;

@RestController
@RequestMapping("/api/v1/templates")
@RequiredArgsConstructor
public class ProcessTemplateController {

    private final ProcessTemplateService processTemplateService;

    @PostMapping
    public ResponseEntity<ProcessTemplateCreationResponseDto> createTemplate(
            @RequestBody ProcessTemplateCreationRequestDto request) {
        return ResponseEntity.ok(processTemplateService.createProcessTemplate(request));
    }
}
