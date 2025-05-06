package ru.nsu.dgi.department_assistant.domain.repository.documents;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.nsu.dgi.department_assistant.domain.entity.documents.DocumentType;
import ru.nsu.dgi.department_assistant.domain.entity.employee.Employee;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DocumentTypeRepository extends JpaRepository<DocumentType, Long> {
    Optional<DocumentType> findById(Long documentTypeId);

    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END " +
           "FROM Substep s " +
           "WHERE s.documentType.id = :documentTypeId")
    boolean isDocumentTypeInUse(@Param("documentTypeId") Long documentTypeId);

    @Query("SELECT e FROM Employee e " +
           "WHERE e.id IN (" +
           "    SELECT DISTINCT eap.employeeId " +
           "    FROM EmployeeAtProcess eap " +
           "    WHERE eap.processId = :processId" +
           ")")
    List<Employee> findEmployeesForStep(@Param("processId") UUID processId, @Param("stepId") int stepId);

    @Query("SELECT dt FROM DocumentType dt " +
           "WHERE dt.id IN (" +
           "    SELECT DISTINCT s.documentType.id " +
           "    FROM Substep s " +
           "    WHERE s.step.id = :stepId " +
           "    AND s.step.processId = :processId" +
           ")")
    List<DocumentType> findRequiredDocumentsForStep(@Param("processId") UUID processId, @Param("stepId") int stepId);

    @Query("SELECT e FROM Employee e " +
           "WHERE e.id IN (" +
           "    SELECT DISTINCT eap.employeeId " +
           "    FROM EmployeeAtProcess eap " +
           "    WHERE eap.processId = :processId" +
           ")")
    List<Employee> findEmployeesForProcess(@Param("processId") UUID processId);

    @Query("SELECT dt FROM DocumentType dt " +
           "WHERE dt.id IN (" +
           "    SELECT DISTINCT s.documentType.id " +
           "    FROM Substep s " +
           "    WHERE s.step.processId = :processId" +
           ")")
    List<DocumentType> findRequiredDocumentsForProcess(@Param("processId") UUID processId);

    @Query("SELECT s.documentType FROM SubstepStatus s " +
           "WHERE s.employeeId = :employeeId " +
           "AND s.startProcessId = :processId " +
           "AND s.substep.step.id = :stepId " +
           "AND s.substep.step.processId = :processId " +
           "AND s.isCompleted = true")
    List<DocumentType> findEmployeeDocumentsForStep(
            @Param("employeeId") UUID employeeId,
            @Param("processId") UUID processId,
            @Param("stepId") int stepId
    );

    @Query("SELECT s.documentType FROM SubstepStatus s " +
           "WHERE s.employeeId = :employeeId " +
           "AND s.startProcessId = :processId " +
           "AND s.substep.step.processId = :processId " +
           "AND s.isCompleted = true")
    List<DocumentType> findEmployeeDocumentsForProcess(
            @Param("employeeId") UUID employeeId,
            @Param("processId") UUID processId
    );
}