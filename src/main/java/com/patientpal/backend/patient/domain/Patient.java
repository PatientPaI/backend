package com.patientpal.backend.patient.domain;

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
@Table(name = "patients")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Patient extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "patient_id")
    private Long id;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Match> matches = new ArrayList<>();

    private String name;

    @Column(unique = true)
    private String residentRegistrationNumber;

    @Column(nullable = false, unique = true)
    private String phoneNumber;

    @Embedded
    private Address address;

    private String nokName; //보호자 이름

    private String nokContact;

    @Lob
    private String patientSignificant;

    @Lob
    private String careRequirements;

    @Setter
    private Boolean isInMatchList;

    @Lob
    private String profileImageUrl;

    @Builder
    public Patient(Member member, String name, String residentRegistrationNumber, String phoneNumber, Address address,
                   String nokName, String nokContact, String patientSignificant, String careRequirements, Boolean isInMatchList, String profileImageUrl) {
        this.member = member;
        this.name = name;
        this.residentRegistrationNumber = residentRegistrationNumber;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.nokName = nokName;
        this.nokContact = nokContact;
        this.patientSignificant = patientSignificant;
        this.careRequirements = careRequirements;
        this.isInMatchList = isInMatchList;
        this.profileImageUrl = profileImageUrl;
    }

    public void updateDetailProfile(final Address address, final String nokName, final String nokContact, final String patientSignificant, final String careRequirements, final String profileImageUrl) {
        this.address = address;
        this.nokName = nokName;
        this.nokContact = nokContact;
        this.patientSignificant = patientSignificant;
        this.careRequirements = careRequirements;
        this.profileImageUrl = profileImageUrl;
    }

    public void deleteProfileImage() {
        this.profileImageUrl = null;
    }
}
