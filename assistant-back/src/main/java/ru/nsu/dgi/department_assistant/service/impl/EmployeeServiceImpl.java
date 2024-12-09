package ru.nsu.dgi.department_assistant.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.nsu.dgi.department_assistant.domain.entity.Employee;
import ru.nsu.dgi.department_assistant.repository.EmployeeRepository;
import ru.nsu.dgi.department_assistant.service.EmployeeService;

import java.util.List;
import java.util.UUID;

@Service
public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeRepository employeeRepository;

    @Autowired
    public EmployeeServiceImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    @Override
    public Employee getEmployee(UUID id) {
        return employeeRepository.getReferenceById(id);
    }

    @Override
    public void addEmployee(Employee employee) {
        employeeRepository.save(employee);
    }
}
