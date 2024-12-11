package ru.nsu.dgi.department_assistant.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.nsu.dgi.department_assistant.domain.entity.CertificateOfNoCriminalRecord;

@Repository
public interface CertificateOfNoCriminalRecordRepository extends JpaRepository<CertificateOfNoCriminalRecord, Integer> {
}

