package com.patientpal.backend.member.domain;

import com.patientpal.backend.caregiver.domain.Caregiver;
import com.patientpal.backend.patient.domain.Patient;
import jakarta.persistence.*;

import java.time.LocalDate;

import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

@Getter
@Entity
@Table(name = "members")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(nullable = false, unique = true, length = 32)
    private String username;

    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Provider provider;

    @OneToOne(mappedBy = "member")
    private Patient patient;

    @OneToOne(mappedBy = "member")
    private Caregiver caregiver;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false, length = 20)
    private String contact;

    private Boolean isInMatchList;

    private String name;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String address;

    private LocalDate birthDate;

    @Builder
    public Member(String username, String password, Provider provider, Role role, String contact, Patient patient, Caregiver caregiver) {
        this.username = username;
        this.password = password;
        this.provider = provider;
        this.role = role;
        this.contact = contact;
        this.patient = patient;
        this.caregiver = caregiver;
    }

    public void encodePassword(PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(this.password);
    }

    public void changePassword(PasswordEncoder passwordEncoder, String newPassword) {
        this.password = newPassword;
        encodePassword(passwordEncoder);
    }
}
