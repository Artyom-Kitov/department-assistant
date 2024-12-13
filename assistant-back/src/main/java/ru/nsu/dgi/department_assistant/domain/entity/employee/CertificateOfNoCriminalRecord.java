package ru.nsu.dgi.department_assistant.domain.entity.employee;


import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "certificate_of_no_criminal_record", schema = "public")
public class CertificateOfNoCriminalRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "date_of_receipt", nullable = false)
    @Temporal(TemporalType.DATE)
    private LocalDate dateOfReceipt;

    @Column(name = "active_until", nullable = false)
    @Temporal(TemporalType.DATE)
    private LocalDate expirationDate;

    @OneToOne(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "employee_id", referencedColumnName = "id")
    private Employee employee;
}

