package ru.nsu.dgi.department_assistant.domain.repository.process;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.nsu.dgi.department_assistant.domain.entity.process.SubstepStatus;
import ru.nsu.dgi.department_assistant.domain.entity.process.id.SubstepStatusId;

import java.util.Optional;
import java.util.UUID;

public interface SubstepStatusRepository extends JpaRepository<SubstepStatus, SubstepStatusId> {
    @Query(value = """
            SELECT ss FROM SubstepStatus ss
            WHERE ss.employeeId = ?1 AND ss.startProcessId = ?2 AND ss.substepId = ?3
            """)
    Optional<SubstepStatus> findAllByEmployeeStartProcessAndId(UUID employeeId, UUID startProcessId, UUID substepId);
}