package ru.nsu.dgi.department_assistant.domain.repository.process;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.nsu.dgi.department_assistant.domain.entity.process.EmployeeAtProcess;
import ru.nsu.dgi.department_assistant.domain.entity.process.id.EmployeeAtProcessId;

import java.util.List;
import java.util.UUID;

public interface EmployeeAtProcessRepository extends JpaRepository<EmployeeAtProcess, EmployeeAtProcessId> {
    boolean existsByProcessId(UUID processId);
    List<EmployeeAtProcess> findByEmployeeId(UUID employeeId);
}
