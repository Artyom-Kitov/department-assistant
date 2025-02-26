package ru.nsu.dgi.department_assistant.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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

    @GetMapping("/employee/{id}")
    public ResponseEntity<ContactsResponseDto> getContactsByEmployee(
            @PathVariable("id") UUID id
    ) {
        return ResponseEntity.ok(contactsService.getContactsByEmployeeId(id));
    }

    @GetMapping("/org-unit/{id}")
    public ResponseEntity<ContactsResponseDto> getContactsByOrganizationalUnit(
            @PathVariable("id") Long id
    ) {
        return ResponseEntity.ok(contactsService.getContactsByOrganizationalUnitId(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContactsResponseDto> getContactsById(
            @PathVariable("id") Long id
    ) {
        return ResponseEntity.ok(contactsService.getById(id));
    }

    @PostMapping()
    public ResponseEntity<ContactsResponseDto> createContact(
            @RequestBody ContactsRequestDto request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(contactsService.create(request));
    }

    @PutMapping("/employee")
    public ResponseEntity<ContactsResponseDto> updateEmployeeContacts(
            @RequestBody ContactsRequestDto request
    ) {
        return ResponseEntity.ok(contactsService.updateEmployeeContact(request));
    }

    @PutMapping("/org-unit")
    public ResponseEntity<ContactsResponseDto> updateOrganizationalUnitContacts(
            @RequestBody ContactsRequestDto request
    ) {
        return ResponseEntity.ok(contactsService.updateOrganizationalUnitContact(request));
    }

    @DeleteMapping("/employee/{id}")
    public ResponseEntity<Void> deleteEmployeeContacts(
        @PathVariable("id") UUID id
    ) {
        contactsService.deleteEmployeeContact(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/org-unit/{id}")
    public ResponseEntity<Void> deleteOrganizationalUnitContacts(
            @PathVariable("id") Long id
    ) {
        contactsService.deleteOrganizationalUnitContact(id);
        return ResponseEntity.noContent().build();
    }
}
