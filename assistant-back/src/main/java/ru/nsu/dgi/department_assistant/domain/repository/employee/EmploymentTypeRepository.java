package ru.nsu.dgi.department_assistant.domain.repository.employee;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.nsu.dgi.department_assistant.domain.entity.employee.EmploymentType;

@Repository
public interface EmploymentTypeRepository extends JpaRepository<EmploymentType, Integer> {
}

