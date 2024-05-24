package com.patientpal.backend.member.domain;

import com.patientpal.backend.matching.domain.Match;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Caregiver {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "caregiver_id")
    private Long id;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "caregiver")
    private List<Match> matches = new ArrayList<>();

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String ssn; // 주민등록번호, 중복 검사용

    @Embedded
    private Address address;

    private float rating;

    private int experience_years; //경력

    private String specialization; //간병 전문분야

    @Lob
    private String caregiver_significant; //간병인 특이사항

    @Builder
    public Caregiver(@NonNull Member member, @NonNull String name, @NonNull String ssn, Address address,
                     float rating, int experience_years, String specialization, String caregiver_significant) {
        this.member = member;
        this.name = name;
        this.ssn = ssn;
        this.address = address;
        this.rating = rating;
        this.experience_years = experience_years;
        this.specialization = specialization;
        this.caregiver_significant = caregiver_significant;
    }
}
