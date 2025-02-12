package ru.nsu.dgi.department_assistant.domain.repository.employee;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.nsu.dgi.department_assistant.domain.entity.employee.EmployeeEmployment;
import ru.nsu.dgi.department_assistant.domain.entity.id.EmployeeEmploymentId;

@Repository
public interface EmployeeEmploymentRepository extends JpaRepository<EmployeeEmployment, EmployeeEmploymentId> {
}
