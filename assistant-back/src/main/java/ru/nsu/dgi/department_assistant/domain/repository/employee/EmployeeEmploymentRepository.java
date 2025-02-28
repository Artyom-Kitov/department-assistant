package ru.nsu.dgi.department_assistant.domain.repository.employee;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.nsu.dgi.department_assistant.domain.entity.employee.EmployeeEmployment;
import ru.nsu.dgi.department_assistant.domain.entity.id.EmployeeEmploymentId;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmployeeEmploymentRepository extends JpaRepository<EmployeeEmployment, EmployeeEmploymentId> {
    @Query("select e from EmployeeEmployment e where e.employee.id = :employeeId")
    List<EmployeeEmployment> findByEmployeeId(@Param("employeeId") UUID id);
}
