package com.patientpal.backend.caregiver.domain;

import static jakarta.persistence.FetchType.LAZY;

import com.patientpal.backend.common.BaseTimeEntity;
import com.patientpal.backend.matching.domain.Match;
import com.patientpal.backend.member.domain.Address;
import com.patientpal.backend.member.domain.Gender;
import com.patientpal.backend.member.domain.Member;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
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

    private int age;

    @Column(nullable = false, unique = true)
    private String phoneNumber;

    @Embedded
    private Address address;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private float rating;

    private int experienceYears;

    private String specialization;

    @Lob
    private String caregiverSignificant;

    @Setter
    private Boolean isInMatchList;

    @Column(length = 512)
    @Setter
    private String profileImageUrl;

    @Builder
    public Caregiver(Member member, String name, String residentRegistrationNumber, String phoneNumber, Address address, Gender gender, int age,
                     float rating, int experienceYears, String specialization, String caregiverSignificant, Boolean isInMatchList, String profileImageUrl) {
        this.member = member;
        this.name = name;
        this.residentRegistrationNumber = residentRegistrationNumber;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.gender = gender;
        this.age = age;
        this.rating = rating;
        this.experienceYears = experienceYears;
        this.specialization = specialization;
        this.caregiverSignificant = caregiverSignificant;
        this.isInMatchList = isInMatchList;
        this.profileImageUrl = profileImageUrl;
    }

    public void updateDetailProfile(final Address address, final float rate, final int experienceYears, final String specialization, final String caregiverSignificant) {
        this.address = address;
        this.rating = rate;
        this.experienceYears = experienceYears;
        this.specialization = specialization;
        this.caregiverSignificant = caregiverSignificant;
    }

    public void deleteProfileImage() {
        this.profileImageUrl = null;
    }

    public void updateProfileImage(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    //TODO 나이 예외 처리
    //EX. 011012-1408293 => 126살로 나옴.
    public static int getAge(String rrn) {
        String birthDateString = rrn.substring(0, 6);
        int century;

        char genderCode = rrn.charAt(7);
        if (genderCode == '1' || genderCode == '2' || genderCode == '5' || genderCode == '6') {
            century = 1900;
        } else if (genderCode == '3' || genderCode == '4' || genderCode == '7' || genderCode == '8') {
            century = 2000;
        } else {
            throw new IllegalArgumentException("Invalid RRN");
        }

        String birthYear = String.valueOf(century + Integer.parseInt(birthDateString.substring(0, 2)));
        String birthMonthDay = birthDateString.substring(2);
        String fullBirthDateString = birthYear + birthMonthDay;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate birthDate = LocalDate.parse(fullBirthDateString, formatter);

        LocalDate currentDate = LocalDate.now();
        return Period.between(birthDate, currentDate).getYears();
    }
}
