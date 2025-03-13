package ru.nsu.dgi.department_assistant.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.nsu.dgi.department_assistant.domain.dto.employee.ContactsRequestDto;
import ru.nsu.dgi.department_assistant.domain.dto.employee.ContactsResponseDto;
import ru.nsu.dgi.department_assistant.domain.service.ContactsService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/contacts")
@Tag(
        name = "Contacts",
        description = "Provides basic operations for creating, updating, deleting and retrieving " +
                "information about contacts of employees and organizational units."
)
@RequiredArgsConstructor
public class ContactsController {
    private final ContactsService contactsService;

    @Operation(
            summary = "Returns all contacts",
            description = "Returns contacts of all employees and organizational units."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved"
                    )
            }
    )
    @GetMapping()
    public ResponseEntity<List<ContactsResponseDto>> getAllContacts() {
        return ResponseEntity.ok(contactsService.getAll());
    }

    @Operation(
            summary = "Returns contacts of an employee",
            description = "Returns contacts of a certain employee by employee id."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Entity not found"
                    )
            }
    )
    @GetMapping("/employee")
    public ResponseEntity<ContactsResponseDto> getContactsByEmployee(
            @RequestParam("employeeId") UUID employeeId
    ) {
        return ResponseEntity.ok(contactsService.getContactsByEmployeeId(employeeId));
    }

    @Operation(
            summary = "Returns contacts of an organizational unit",
            description = "Returns contacts of a certain organizational unit by organizational unit id."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Entity not found"
                    )
            }
    )
    @GetMapping("/org-unit")
    public ResponseEntity<ContactsResponseDto> getContactsByOrganizationalUnit(
            @RequestParam("orgUnitId") Long orgUnitId
    ) {
        return ResponseEntity.ok(contactsService.getContactsByOrganizationalUnitId(orgUnitId));
    }

    @Operation(
            summary = "Returns contacts by id",
            description = "Returns contacts by its id."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Entity not found"
                    )
            }
    )
    @GetMapping("/id")
    public ResponseEntity<ContactsResponseDto> getContactsById(
            @RequestParam("id") Long id
    ) {
        return ResponseEntity.ok(contactsService.getById(id));
    }

    @Operation(
            summary = "Creates a new employee contacts",
            description =
                    "Creates new contacts for an employee by employee id. " +
                            "Operation may not be possible if contacts are already exist for this employee."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Successfully created"
                    ),
                    @ApiResponse(
                            responseCode = "412",
                            description = "Entity already exists"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Entity not found"
                    )
            }
    )
    @PostMapping("/create/employee")
    public ResponseEntity<ContactsResponseDto> createEmployeeContact(
            @RequestParam("employeeId") UUID employeeId,
            @RequestBody ContactsRequestDto request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(contactsService.createEmployeeContact(employeeId, request));
    }

    @Operation(
            summary = "Creates a new organizational unit contacts",
            description =
                    "Creates new contacts for an organizational unit by organizational unit id. " +
                            "Operation may not be possible if contacts are already exist for this organizational unit."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Successfully created"
                    ),
                    @ApiResponse(
                            responseCode = "412",
                            description = "Entity already exists"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Entity not found"
                    )
            }
    )
    @PostMapping("/create/org-unit")
    public ResponseEntity<ContactsResponseDto> createOrganizationalUnitContact(
            @RequestParam("organizationalUnitId") Long organizationalUnitId,
            @RequestBody ContactsRequestDto request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(contactsService.createOrganizationalUnitContact(organizationalUnitId, request));
    }

    @Operation(
            summary = "Updates employee contacts",
            description =
                    "Updates contacts of an employee by employee id. " +
                            "Does nothing if there are no contacts for chosen employee."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully updated"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Entity not found"
                    )
            }
    )
    @PutMapping("/update/employee")
    public ResponseEntity<ContactsResponseDto> updateEmployeeContacts(
            @RequestParam("employeeId") UUID employeeId,
            @RequestBody ContactsRequestDto request
    ) {
        return ResponseEntity.ok(contactsService.updateEmployeeContact(employeeId, request));
    }

    @Operation(
            summary = "Updates organizational unit contacts",
            description =
                    "Updates contacts of an organizational unit by organizational unit id. " +
                            "Does nothing if there are no contacts for chosen organizational unit."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully updated"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Entity not found"
                    )
            }
    )
    @PutMapping("/update/org-unit")
    public ResponseEntity<ContactsResponseDto> updateOrganizationalUnitContacts(
            @RequestParam("organizationalUnitId") Long organizationalUnitId,
            @RequestBody ContactsRequestDto request
    ) {
        return ResponseEntity.ok(contactsService.updateOrganizationalUnitContact(organizationalUnitId, request));
    }

    @Operation(
            summary = "Deletes employee contacts",
            description = "Deletes contacts of an employee by employee id. " +
                    "Returns no content response."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Successfully deleted"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Entity not found"
                    )
            }
    )
    @DeleteMapping("/delete/employee")
    public ResponseEntity<Void> deleteEmployeeContacts(
            @RequestParam("employeeId") UUID employeeId
    ) {
        contactsService.deleteEmployeeContact(employeeId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Deletes organizational unit contacts",
            description = "Deletes contacts of an organizational unit by organizational unit id. " +
                    "Returns no content response."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Successfully deleted"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Entity not found"
                    )
            }
    )
    @DeleteMapping("/delete/org-unit")
    public ResponseEntity<Void> deleteOrganizationalUnitContacts(
            @RequestParam("orgUnitId") Long orgUnitId
    ) {
        contactsService.deleteOrganizationalUnitContact(orgUnitId);
        return ResponseEntity.noContent().build();
    }
}
