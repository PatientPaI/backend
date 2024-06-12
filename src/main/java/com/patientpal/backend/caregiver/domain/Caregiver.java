package com.patientpal.backend.caregiver.domain;

import static jakarta.persistence.FetchType.LAZY;

import com.patientpal.backend.common.BaseTimeEntity;
import com.patientpal.backend.matching.domain.Match;
import com.patientpal.backend.member.domain.Address;
import com.patientpal.backend.member.domain.Member;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@Table(name = "caregivers")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Caregiver extends BaseTimeEntity {

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
    private String residentRegistrationNumber;

    @Column(nullable = false, unique = true)
    private String phoneNumber;

    @Embedded
    private Address address;

    private float rating;

    private int experienceYears;

    private String specialization;

    @Lob
    private String caregiverSignificant;

    @Setter
    private Boolean isInMatchList;

    @Lob
    private String profileImageUrl;

    @Builder
    public Caregiver(Member member, String name, String residentRegistrationNumber, String phoneNumber, Address address,
                     float rating, int experienceYears, String specialization, String caregiverSignificant, Boolean isInMatchList, String profileImageUrl) {
        this.member = member;
        this.name = name;
        this.residentRegistrationNumber = residentRegistrationNumber;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.rating = rating;
        this.experienceYears = experienceYears;
        this.specialization = specialization;
        this.caregiverSignificant = caregiverSignificant;
        this.isInMatchList = isInMatchList;
        this.profileImageUrl = profileImageUrl;
    }

    public void updateDetailProfile(final Address address, final float rate, final int experienceYears, final String specialization, final String caregiverSignificant, final String profileImageUrl) {
        this.address = address;
        this.rating = rate;
        this.experienceYears = experienceYears;
        this.specialization = specialization;
        this.caregiverSignificant = caregiverSignificant;
        this.profileImageUrl = profileImageUrl;
    }

    public void deleteProfileImage() {
        this.profileImageUrl = null;
    }
}
