package com.patientpal.backend.member.domain;

import com.patientpal.backend.matching.domain.Match;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Patient {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "patient_id")
    private Long id;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "patient")
    private List<Match> matches = new ArrayList<>();

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String residentRegistrationNumber; // 주민등록번호, 중복 검사용

    @Embedded
    private Address address;

    private String nokName; //보호자 이름

    private String nokContact;

    @Lob
    private String patientSignificant; //환자 특이사항

    @Lob
    private String careRequirements; //간병 요구사항

    @Builder
    public Patient(@NonNull Member member, @NonNull String name, @NonNull String residentRegistrationNumber, Address address,
                   String nokName, String nokContact, String patientSignificant, String careRequirements) {
        this.member = member;
        this.name = name;
        this.residentRegistrationNumber = residentRegistrationNumber;
        this.address = address;
        this.nokName = nokName;
        this.nokContact = nokContact;
        this.patientSignificant = patientSignificant;
        this.careRequirements = careRequirements;
    }
}
