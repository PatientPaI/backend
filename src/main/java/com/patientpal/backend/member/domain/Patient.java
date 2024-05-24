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
    private String ssn; // 주민등록번호, 중복 검사용

    @Embedded
    private Address address;

    private String nok_name; //보호자 이름

    private String nok_contact;

    @Lob
    private String patient_significant; //환자 특이사항

    @Lob
    private String care_requirements; //간병 요구사항

    @Builder
    public Patient(@NonNull Member member, @NonNull String name, @NonNull String ssn, Address address,
                   String nok_name, String nok_contact, String patient_significant, String care_requirements) {
        this.member = member;
        this.name = name;
        this.ssn = ssn;
        this.address = address;
        this.nok_name = nok_name;
        this.nok_contact = nok_contact;
        this.patient_significant = patient_significant;
        this.care_requirements = care_requirements;
    }
}
