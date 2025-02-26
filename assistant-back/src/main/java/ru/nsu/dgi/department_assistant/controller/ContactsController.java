package ru.nsu.dgi.department_assistant.controller;

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
@RequiredArgsConstructor
public class ContactsController {
    private final ContactsService contactsService;

    @GetMapping()
    public ResponseEntity<List<ContactsResponseDto>> getAllContacts() {
        return ResponseEntity.ok(contactsService.getAll());
    }

    @GetMapping("/employee")
    public ResponseEntity<ContactsResponseDto> getContactsByEmployee(
            @RequestParam("employeeId") UUID employeeId
    ) {
        return ResponseEntity.ok(contactsService.getContactsByEmployeeId(employeeId));
    }

    @GetMapping("/org-unit")
    public ResponseEntity<ContactsResponseDto> getContactsByOrganizationalUnit(
            @RequestParam("orgUnitId") Long orgUnitId
    ) {
        return ResponseEntity.ok(contactsService.getContactsByOrganizationalUnitId(orgUnitId));
    }

    @GetMapping("/id")
    public ResponseEntity<ContactsResponseDto> getContactsById(
            @RequestParam("id") Long id
    ) {
        return ResponseEntity.ok(contactsService.getById(id));
    }

    @PostMapping("/create/employee")
    public ResponseEntity<ContactsResponseDto> createEmployeeContact(
            @RequestParam("employeeId") UUID employeeId,
            @RequestBody ContactsRequestDto request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(contactsService.createEmployeeContact(employeeId, request));
    }

    @PostMapping("/create/org-unit")
    public ResponseEntity<ContactsResponseDto> createOrganizationalUnitContact(
            @RequestParam("organizationalUnitId") Long organizationalUnitId,
            @RequestBody ContactsRequestDto request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(contactsService.createOrganizationalUnitContact(organizationalUnitId, request));
    }

    @PutMapping("/update/employee")
    public ResponseEntity<ContactsResponseDto> updateEmployeeContacts(
            @RequestParam("employeeId") UUID employeeId,
            @RequestBody ContactsRequestDto request
    ) {
        return ResponseEntity.ok(contactsService.updateEmployeeContact(employeeId, request));
    }

    @PutMapping("/update/org-unit")
    public ResponseEntity<ContactsResponseDto> updateOrganizationalUnitContacts(
            @RequestParam("organizationalUnitId") Long organizationalUnitId,
            @RequestBody ContactsRequestDto request
    ) {
        return ResponseEntity.ok(contactsService.updateOrganizationalUnitContact(organizationalUnitId, request));
    }

    @DeleteMapping("/delete/employee")
    public ResponseEntity<Void> deleteEmployeeContacts(
            @RequestParam("employeeId") UUID employeeId
    ) {
        contactsService.deleteEmployeeContact(employeeId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/delete/org-unit")
    public ResponseEntity<Void> deleteOrganizationalUnitContacts(
            @RequestParam("orgUnitId") Long orgUnitId
    ) {
        contactsService.deleteOrganizationalUnitContact(orgUnitId);
        return ResponseEntity.noContent().build();
    }
}
