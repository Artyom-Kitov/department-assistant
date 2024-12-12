package ru.nsu.dgi.department_assistant.domain.entity.employee;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "employee", schema = "public")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Employee {
    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "middle_name")
    private String middleName;

    @Column(name = "agreement", nullable = false)
    private Boolean agreement;

    @Column(name = "has_completed_advanced_courses", nullable = false)
    private Boolean hasCompletedAdvancedCourses;

    @Column(name = "needs_mandatory_election", nullable = false)
    private Boolean needsMandatoryElection;

    @Column(name = "has_a_higher_education", nullable = false)
    private Boolean hasHigherEducation;

    @Column(name = "snils", unique = true)
    private String snils;

    @Column(name = "inn", unique = true)
    private String inn;

    @Column(name = "is_archived", nullable = false)
    private Boolean isArchived;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "contacts_id", referencedColumnName = "id")
    private Contacts contacts;

    @OneToOne(mappedBy = "employee", fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    private PassportInfo passportInfo;

    @OneToOne(mappedBy = "employee", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private EmploymentStatus employmentStatus;

    @OneToOne(mappedBy = "employee", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private EmploymentRecord employmentRecord;

    @OneToOne(mappedBy = "employee", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private AcademicDegree academicDegree;

    @OneToOne(mappedBy = "employee", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private CertificateOfNoCriminalRecord certificateOfNoCriminalRecord;

    @OneToMany(mappedBy = "employee", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private List<EmployeeEmployment> employments;
}
