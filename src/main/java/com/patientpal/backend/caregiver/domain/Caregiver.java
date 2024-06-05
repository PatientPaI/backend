package com.patientpal.backend.caregiver.domain;

import com.patientpal.backend.caregiver.dto.request.CaregiverProfileUpdateRequest;
import com.patientpal.backend.matching.domain.Match;
import com.patientpal.backend.member.domain.Address;
import com.patientpal.backend.member.domain.Member;
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

    @OneToMany(mappedBy = "caregiver", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Match> matches = new ArrayList<>();

    private String name;

    @Column(unique = true)
    private String residentRegistrationNumber; // 주민등록번호, 중복 검사용 (암호화 로직 추가 필요)

    @Embedded
    private Address address;

    private float rating;

    private int experienceYears;

    private String specialization; //간병 전문분야

    @Lob
    private String caregiverSignificant; //간병인 특이사항

    @Builder
    public Caregiver(Member member, String name, String residentRegistrationNumber, Address address,
                     float rating, int experienceYears, String specialization, String caregiverSignificant) {
        this.member = member;
        this.name = name;
        this.residentRegistrationNumber = residentRegistrationNumber;
        this.address = address;
        this.rating = rating;
        this.experienceYears = experienceYears;
        this.specialization = specialization;
        this.caregiverSignificant = caregiverSignificant;
    }

    public void updateDetailProfile(final CaregiverProfileUpdateRequest caregiverProfileUpdateRequest) {
        //validate추가
        this.address = caregiverProfileUpdateRequest.getAddress();
        this.rating = caregiverProfileUpdateRequest.getRating();
        this.experienceYears = caregiverProfileUpdateRequest.getExperienceYears();
        this.specialization = caregiverProfileUpdateRequest.getSpecialization();
        this.caregiverSignificant = caregiverProfileUpdateRequest.getCaregiverSignificant();
    }
}
