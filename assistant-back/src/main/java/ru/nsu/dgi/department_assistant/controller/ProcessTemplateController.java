package ru.nsu.dgi.department_assistant.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.nsu.dgi.department_assistant.domain.dto.EntityNotFoundDto;
import ru.nsu.dgi.department_assistant.domain.dto.process.InvalidProcessTemplateDto;
import ru.nsu.dgi.department_assistant.domain.dto.process.ProcessTemplateCreationRequestDto;
import ru.nsu.dgi.department_assistant.domain.dto.process.ProcessTemplateCreationResponseDto;
import ru.nsu.dgi.department_assistant.domain.dto.process.ProcessTemplateResponseDto;
import ru.nsu.dgi.department_assistant.domain.dto.process.ProcessTemplateShortDto;
import ru.nsu.dgi.department_assistant.domain.service.ProcessTemplateService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/templates")
@RequiredArgsConstructor
@Validated
public class ProcessTemplateController {

    private final ProcessTemplateService processTemplateService;

    @Operation(
            summary = "Create new process template",
            description = """
                    Note that the process shouldn't have loops and
                    must contain at least one final step where isSuccessful == true.
                    
                    The process itself is represented as list of steps with IDs. Step ID must be unique in the process.
                    The "data" field depends on the "type" value.
                    
                    Supported steps:
                    
                    **Basic step**
                    ```
                    {
                        "id": 1,
                        "duration": 1,
                        "metaInfo": {},
                        "type": 1,
                        "description": "Отнести в деканат документ",
                        "data": {
                            "next": 2
                        }
                    }
                    ```
                    
                    **Subtasks step**
                    ```
                    {
                        "id": 2,
                        "duration": 1,
                        "metaInfo": {},
                        "type": 2,
                        "description": "Донести необходимые документы",
                        "data": {
                            "subtasks": [
                                {
                                    "description": "Паспорт",
                                    "duration": 1
                                },
                                {
                                    "description": "Военник",
                                    "duration": 4
                                },
                                {
                                    "Оригинал диплома"
                                }
                            ],
                            "next": 3
                        }
                    }
                    ```
                    
                    **Conditional step**
                    
                    ```
                    {
                        "id": 3,
                        "duration": 8,
                        "metaInfo": {},
                        "type": 3,
                        "description": "Претендент имеет высшее образование?",
                        "data": {
                            "ifTrue": 4,
                            "ifFalse": 5
                        }
                    }
                    ```
                    
                    **Final**
                    
                    ```
                    {
                        "id": 4,
                        "metaInfo": {},
                        "type": 4,
                        "description": "Претендент успешно трудоустроен",
                        "data": {
                            "isSuccessful": true
                        }
                    }
                    ```
                    
                    **Process transition**
                    
                    ```
                    {
                        "id": 5,
                        "metaInfo": {},
                        "type": 5,
                        "description": "Трудоустройство по ДГПХ",
                        "data": {
                            "processId": "723840cb-2be7-4f10-ad0c-368854aa537b"
                        }
                    }
                    ```
                    """
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully created",
                            content = {
                                    @Content(schema = @Schema(implementation = ProcessTemplateCreationResponseDto.class))
                            }
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid process template",
                            content = {
                                    @Content(schema = @Schema(implementation = InvalidProcessTemplateDto.class))
                            }
                    )
            }
    )
    @PostMapping
    public ResponseEntity<ProcessTemplateCreationResponseDto> createTemplate(
            @Valid @RequestBody ProcessTemplateCreationRequestDto request) {
        return ResponseEntity.ok(processTemplateService.createProcessTemplate(request));
    }

    @Operation(summary = "Get all process IDs")
    @GetMapping
    public ResponseEntity<List<ProcessTemplateShortDto>> getAll() {
        return ResponseEntity.ok(processTemplateService.getAllProcesses());
    }

    @Operation(
            summary = "Get process body by ID",
            description = """
                    The process is represented as described in the creation method.
                    
                    See [POST /api/v1/templates](#/process-template-controller/createTemplate) for more info.
                    """
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved",
                            content = {
                                    @Content(schema = @Schema(implementation = ProcessTemplateResponseDto.class))
                            }
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "No process with given ID",
                            content = {
                                    @Content(schema = @Schema(implementation = EntityNotFoundDto.class))
                            }
                    )
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<ProcessTemplateResponseDto> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(processTemplateService.getProcessById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> editById(@PathVariable UUID id, @Valid @RequestBody ProcessTemplateCreationRequestDto request) {
        processTemplateService.updateById(id, request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Delete process template by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable UUID id) {
        processTemplateService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get process duration")
    @GetMapping("/duration/{id}")
    public ResponseEntity<Integer> getDuration(@PathVariable UUID id) {
        return ResponseEntity.ok(processTemplateService.getDurationById(id));
    }
}
