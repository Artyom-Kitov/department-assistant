package ru.nsu.dgi.department_assistant.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.nsu.dgi.department_assistant.domain.dto.process.InvalidProcessTemplateDto;
import ru.nsu.dgi.department_assistant.domain.dto.process.ProcessTemplateCreationRequestDto;
import ru.nsu.dgi.department_assistant.domain.dto.process.ProcessTemplateCreationResponseDto;
import ru.nsu.dgi.department_assistant.domain.service.ProcessTemplateService;

@RestController
@RequestMapping("/api/v1/templates")
@RequiredArgsConstructor
public class ProcessTemplateController {

    private final ProcessTemplateService processTemplateService;

    @Operation(
            summary = "Create new process template",
            description = """
                    Note that the following conditions must be satisfied.
                    1) The process must contain at least one final step where isSuccessful == true.
                    2) There shouldn't be any loops.
                    3) Only one step should be the starting one. This is the step which no other step leads to.
                    
                    Steps, as you can see below, are specified in the "steps" list.
                    The main idea is that the data field depends on the type value.
                    The following step types are supported.
                    
                    **Basic step**
                    ```
                    {
                        "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
                        "duration": 1,
                        "metaInfo": {},
                        "type": 1,
                        "description": "Отнести в деканат документ",
                        "data": {
                            "next": "723840cb-2be7-4f10-ad0c-368854aa537b"
                        }
                    }
                    ```
                    
                    **Subtasks step**
                    ```
                    {
                        "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
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
                            "next": "723840cb-2be7-4f10-ad0c-368854aa537b"
                        }
                    }
                    ```
                    
                    **Conditional step**
                    
                    ```
                    {
                        "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
                        "duration": 8,
                        "metaInfo": {},
                        "type": 3,
                        "description": "Претендент имеет высшее образование?",
                        "data": {
                            "ifTrue": "723840cb-2be7-4f10-ad0c-368854aa537b",
                            "ifFalse": "723840cb-2be7-4f10-ad0c-368854aa537b"
                        }
                    }
                    ```
                    
                    **Final**
                    
                    ```
                    {
                        "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
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
                        "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
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
            @RequestBody ProcessTemplateCreationRequestDto request) {
        return ResponseEntity.ok(processTemplateService.createProcessTemplate(request));
    }
}
