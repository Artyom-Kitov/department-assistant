package ru.nsu.dgi.department_assistant.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.nsu.dgi.department_assistant.domain.dto.documents.DocumentTypeCreationRequestDto;
import ru.nsu.dgi.department_assistant.domain.dto.documents.DocumentTypeDto;
import ru.nsu.dgi.department_assistant.domain.dto.documents.EmployeeMissingDocumentsDto;
import ru.nsu.dgi.department_assistant.domain.dto.documents.MissingDocumentsDto;
import ru.nsu.dgi.department_assistant.domain.service.impl.DocumentServiceImpl;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
@Tag(name = "Document", description = "Document management APIs")
public class DocumentController {

    private final DocumentServiceImpl documentService;

    @Operation(
            summary = "Get all document types",
            description = "Retrieves a list of all document types in the system"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved document types"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public ResponseEntity<List<DocumentTypeDto>> getAllDocumentTypes() {
        return ResponseEntity.ok(documentService.getAllDocumentTypes());
    }

    @Operation(
            summary = "Create a new document type",
            description = "Creates a new document type with the provided information"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully created document type"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    public ResponseEntity<DocumentTypeDto> createDocumentType(
            @RequestBody DocumentTypeCreationRequestDto request) {
        return ResponseEntity.ok(documentService.createDocumentType(request));
    }

    @Operation(
            summary = "Update a document type",
            description = "Updates an existing document type with the provided information"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated document type"),
            @ApiResponse(responseCode = "404", description = "Document type not found"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/{id}")
    public ResponseEntity<DocumentTypeDto> updateDocumentType(
            @PathVariable Long id,
            @RequestBody DocumentTypeCreationRequestDto request) {
        return ResponseEntity.ok(documentService.updateDocumentType(id, request));
    }

    @Operation(
            summary = "Delete a document type",
            description = "Deletes a document type by its ID if it's not in use"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted document type"),
            @ApiResponse(responseCode = "404", description = "Document type not found"),
            @ApiResponse(responseCode = "400", description = "Document type is in use"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocumentType(@PathVariable Long id) {
        documentService.deleteDocumentType(id);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Get missing documents for a step",
            description = "Retrieves a list of missing documents for all employees in a specific step"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved missing documents"),
            @ApiResponse(responseCode = "404", description = "Process or step not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/missing/process/{processId}/step/{stepId}")
    public ResponseEntity<List<MissingDocumentsDto>> getMissingDocumentsForStep(
            @PathVariable UUID processId,
            @PathVariable int stepId) {
        return ResponseEntity.ok(documentService.getMissingDocumentsForStep(processId, stepId));
    }

    @Operation(
            summary = "Get missing documents for a process",
            description = "Retrieves a list of missing documents for all employees in a specific process"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved missing documents"),
            @ApiResponse(responseCode = "404", description = "Process not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/missing/process/{processId}")
    public ResponseEntity<List<MissingDocumentsDto>> getMissingDocumentsForProcess(
            @PathVariable UUID processId) {
        return ResponseEntity.ok(documentService.getMissingDocumentsForProcess(processId));
    }

    @Operation(
            summary = "Get missing documents for an employee",
            description = "Retrieves a list of missing documents for a specific employee across all their processes"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved missing documents"),
            @ApiResponse(responseCode = "404", description = "Employee not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/missing/employee/{employeeId}")
    public ResponseEntity<List<EmployeeMissingDocumentsDto>> getEmployeeMissingDocuments(
            @PathVariable UUID employeeId) {
        return ResponseEntity.ok(documentService.getEmployeeMissingDocuments(employeeId));
    }

    @Operation(
            summary = "Get missing documents for an employee in a specific process",
            description = "Retrieves a list of missing documents for a specific employee in a specific process"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved missing documents"),
            @ApiResponse(responseCode = "404", description = "Employee or process not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/missing/employee/{employeeId}/process/{processId}")
    public ResponseEntity<List<EmployeeMissingDocumentsDto>> getEmployeeMissingDocumentsForProcess(
            @PathVariable UUID employeeId,
            @PathVariable UUID processId) {
        return ResponseEntity.ok(documentService.getEmployeeMissingDocumentsForProcess(employeeId, processId));
    }
}

