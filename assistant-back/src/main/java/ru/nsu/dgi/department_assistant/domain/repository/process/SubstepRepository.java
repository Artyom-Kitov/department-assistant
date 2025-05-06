package ru.nsu.dgi.department_assistant.domain.repository.process;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.nsu.dgi.department_assistant.domain.entity.process.Step;
import ru.nsu.dgi.department_assistant.domain.entity.process.Substep;

import java.util.List;
import java.util.UUID;

@Repository
public interface SubstepRepository extends JpaRepository<Substep, UUID> {
    List<Substep> findAllByStep(Step step);
    
//    @Query("SELECT s FROM Substep s WHERE s.step.id = :stepId")
//    List<Substep> findByStepId(@Param("stepId") UUID stepId);
}
