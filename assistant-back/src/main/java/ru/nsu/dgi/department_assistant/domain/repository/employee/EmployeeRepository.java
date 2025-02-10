package ru.nsu.dgi.department_assistant.domain.repository.employee;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.nsu.dgi.department_assistant.domain.entity.employee.Employee;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, UUID> {
    @Query("SELECT e " +
            "FROM Employee e " +
            "LEFT JOIN FETCH e.employmentStatus " +
            "LEFT JOIN FETCH e.employmentRecord " +
            "LEFT JOIN FETCH e.academicDegree")
    List<Employee> findAllEmployeesWithInfo();
    @Query("SELECT e " +
            "FROM Employee e " +
            "LEFT JOIN FETCH e.employmentStatus " +
            "LEFT JOIN FETCH e.employmentRecord " +
            "LEFT JOIN FETCH e.academicDegree " +
            "WHERE e.id = :id")
    Optional<Employee> findEmployeeWithInfoById(@Param("id") UUID id);
}
