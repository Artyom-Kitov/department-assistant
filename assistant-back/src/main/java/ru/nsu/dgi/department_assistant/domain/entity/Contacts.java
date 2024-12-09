package ru.nsu.dgi.department_assistant.domain.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "contacts", schema = "public")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Contacts {
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "email")
    private String email;

    @Column(name = "nsu_email")
    private String nsuEmail;

    @Column(name = "additional_info", columnDefinition = "TEXT")
    private String additionalInfo;

    @OneToOne(mappedBy = "contacts", fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    private Employee employee;

    @OneToOne(mappedBy = "contacts", fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    private OrganizationalUnit organizationalUnit;
}
