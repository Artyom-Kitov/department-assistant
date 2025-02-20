package ru.nsu.dgi.department_assistant.domain.repository.process;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.nsu.dgi.department_assistant.domain.entity.process.StepStatus;
import ru.nsu.dgi.department_assistant.domain.entity.process.id.StepStatusId;

import java.util.List;
import java.util.UUID;

public interface StepStatusRepository extends JpaRepository<StepStatus, StepStatusId> {
    @Query("""
            SELECT ss FROM StepStatus ss
            WHERE ss.employeeId = ?1 AND ss.startProcessId = ?2
            """)
    List<StepStatus> findByEmployeeAndStartProcess(UUID employeeId, UUID originalProcessId);
}
