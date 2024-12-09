package ru.nsu.dgi.department_assistant.service;

import ru.nsu.dgi.department_assistant.domain.entity.Employee;

import java.util.List;
import java.util.UUID;

public interface EmployeeService {
    public List<Employee> getAllEmployees();
    public Employee getEmployee(UUID id);
    public void addEmployee(Employee employee);
}
