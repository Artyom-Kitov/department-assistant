package ru.nsu.dgi.department_assistant.domain.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.context.ConfigurationPropertiesAutoConfiguration;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.nsu.dgi.department_assistant.domain.dto.employee.ContactsRequestDto;
import ru.nsu.dgi.department_assistant.domain.dto.employee.ContactsResponseDto;
import ru.nsu.dgi.department_assistant.domain.entity.employee.Contacts;
import ru.nsu.dgi.department_assistant.domain.entity.employee.Employee;
import ru.nsu.dgi.department_assistant.domain.entity.employee.OrganizationalUnit;
import ru.nsu.dgi.department_assistant.domain.exception.EntityNotFoundException;
import ru.nsu.dgi.department_assistant.domain.exception.NullPropertyException;
import ru.nsu.dgi.department_assistant.domain.mapper.employee.ContactsMapper;
import ru.nsu.dgi.department_assistant.domain.repository.employee.ContactsRepository;
import ru.nsu.dgi.department_assistant.domain.repository.employee.EmployeeRepository;
import ru.nsu.dgi.department_assistant.domain.repository.employee.OrganizationalUnitRepository;
import ru.nsu.dgi.department_assistant.domain.service.ContactsService;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ContactsServiceImpl implements ContactsService {
    private final EmployeeRepository employeeRepository;
    private final OrganizationalUnitRepository organizationalUnitRepository;
    private final ContactsRepository contactsRepository;
    private final ContactsMapper contactsMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ContactsResponseDto> getAll() {
        log.info("getting all contacts");
        List<Contacts> contacts = contactsRepository.findAll();
        log.info("successfully find {} contacts", contacts.size());

        return contacts.stream()
                .map(contactsMapper::entityToResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ContactsResponseDto getById(Long id) {
        log.info("getting contact by id {}", id);
        Contacts contact = contactsRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.valueOf(id)));
        log.info("successfully find contact by id {}", id);

        return contactsMapper.entityToResponseDto(contact);
    }

    @Override
    @Transactional
    public ContactsResponseDto createEmployeeContact(UUID employeeId, ContactsRequestDto contactsRequestDto) {
        if (employeeId == null) {
            throw new NullPropertyException("EmployeeId must not be null");
        }
        log.info("creating a new contact for employee id {}", employeeId);
        Contacts contact = contactsMapper.toEntity(contactsRequestDto);
        setEmployee(employeeId, contact);
        contactsRepository.save(contact);
        log.info("successfully created a contact for employee id {}", employeeId);

        return contactsMapper.entityToResponseDto(contact);
    }

    @Override
    @Transactional
    public ContactsResponseDto createOrganizationalUnitContact(
            Long organizationalUnitId,
            ContactsRequestDto contactsRequestDto
    ) {
        if (organizationalUnitId == null) {
            throw new NullPropertyException("EmployeeId must not be null");
        }
        log.info("creating a new contact for organizational unit id {}", organizationalUnitId);
        Contacts contact = contactsMapper.toEntity(contactsRequestDto);
        setOrganizationalUnit(organizationalUnitId, contact);
        contactsRepository.save(contact);
        log.info("successfully created a contact for organizational unit id {}", organizationalUnitId);

        return contactsMapper.entityToResponseDto(contact);
    }

    @Override
    @Transactional
    public ContactsResponseDto updateEmployeeContact(UUID employeeId, ContactsRequestDto contactsRequestDto) {
        log.info("updating contact by employee id {}", employeeId);
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException(String.valueOf(employeeId)));
        Contacts contact = employee.getContacts();
        if (contact == null) {
            throw new EntityNotFoundException("Contacts of " + employeeId);
        }
        contactsMapper.updateRequestToEntity(contactsRequestDto, contact);
        log.info("successfully updated contact by employee id {}", employeeId);

        return contactsMapper.entityToResponseDto(contact);
    }

    @Override
    @Transactional
    public ContactsResponseDto updateOrganizationalUnitContact(
            Long organizationalUnitId,
            ContactsRequestDto contactsRequestDto
    ) {
        log.info("updating contact by organizational unit id {}", organizationalUnitId);
        OrganizationalUnit unit = organizationalUnitRepository.findById(organizationalUnitId)
                .orElseThrow(() -> new EntityNotFoundException(String.valueOf(organizationalUnitId)));
        Contacts contact = unit.getContacts();
        if (contact == null) {
            throw new EntityNotFoundException("Contacts of " + organizationalUnitId);
        }
        contactsMapper.updateRequestToEntity(contactsRequestDto, contact);
        log.info("successfully updated contact by organizational unit id {}", organizationalUnitId);

        return contactsMapper.entityToResponseDto(contact);
    }

    @Override
    @Transactional(readOnly = true)
    public ContactsResponseDto getContactsByEmployeeId(UUID id) {
        log.info("getting contact by employee id {}", id);
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.valueOf(id)));
        Contacts contact = employee.getContacts();
        if (contact == null) {
            throw new EntityNotFoundException("Contacts of " + id);
        }
        log.info("successfully got contact by employee id {}", id);

        return contactsMapper.entityToResponseDto(contact);
    }

    @Override
    @Transactional(readOnly = true)
    public ContactsResponseDto getContactsByOrganizationalUnitId(Long id) {
        log.info("getting a contact by an organizational unit id {}", id);
        OrganizationalUnit organizationalUnit = organizationalUnitRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.valueOf(id)));
        Contacts contact = organizationalUnit.getContacts();
        if (contact == null) {
            throw new EntityNotFoundException("Contacts of " + id);
        }
        log.info("successfully got contact by organizational unit id {}", id);

        return contactsMapper.entityToResponseDto(contact);
    }

    @Override
    @Transactional
    public void deleteEmployeeContact(UUID id) {
        if (id == null) {
            throw new NullPropertyException("EmployeeId must not be null");
        }
        log.info("deleting contact by employee id {}", id);
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.valueOf(id)));
        Contacts contact = employee.getContacts();
        if (contact == null) {
            throw new EntityNotFoundException("Contacts of " + id);
        }
        employee.setContacts(null);
        contactsRepository.delete(contact);
        log.info("successfully deleted contact by employee id {}", id);
    }

    @Override
    @Transactional
    public void deleteOrganizationalUnitContact(Long id) {
        if (id == null) {
            throw new NullPropertyException("OrganizationalUnitId must not be null");
        }
        log.info("deleting contact by organizational unit id {}", id);
        OrganizationalUnit organizationalUnit = organizationalUnitRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.valueOf(id)));
        Contacts contact = organizationalUnit.getContacts();
        if (contact == null) {
            throw new EntityNotFoundException("Contacts of " + id);
        }
        if (contact.getOrganizationalUnit() != null && contact.getOrganizationalUnit().getContacts() != null) {
            contact.getOrganizationalUnit().setContacts(null);
        }
        contactsRepository.delete(contact);
        log.info("successfully deleted contact by organizational unit id {}", id);
    }

    private void setEmployee(UUID employeeId, Contacts contactToChange) {
        log.info("finding an employee by id {} for contacts", employeeId);
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException(String.valueOf(employeeId)));
        log.info("found employee with id {} for contacts", employeeId);
        contactToChange.setEmployee(employee);
        employee.setContacts(contactToChange);
    }

    private void setOrganizationalUnit(Long organizationalUnitId, Contacts contactToChange) {
        log.info("finding an organizational unit by id {} for contacts", organizationalUnitId);
        OrganizationalUnit organizationalUnit = organizationalUnitRepository.findById(organizationalUnitId)
                .orElseThrow(() -> new EntityNotFoundException(String.valueOf(organizationalUnitId)));
        log.info("found organizational unit with id {} for contacts", organizationalUnitId);
        contactToChange.setOrganizationalUnit(organizationalUnit);
        organizationalUnit.setContacts(contactToChange);
    }
}
