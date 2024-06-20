package com.patientpal.backend.patient.domain;

import static com.patientpal.backend.common.exception.ErrorCode.INVALID_RESIDENT_REGISTRATION_NUMBER;
import static jakarta.persistence.FetchType.LAZY;

import com.patientpal.backend.common.BaseTimeEntity;
import com.patientpal.backend.common.exception.InvalidValueException;
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

    private int age;

    @Column(nullable = false, unique = true)
    private String phoneNumber;

    @Embedded
    private Address address;

    private String nokName; //보호자 이름

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String nokContact;

    @Lob
    private String patientSignificant;

    @Lob
    private String careRequirements;

    @Setter
    private Boolean isInMatchList;

    @Column(length = 512)
    @Setter
    private String profileImageUrl;

    @Builder
    public Patient(Member member, String name, String residentRegistrationNumber, Gender gender, String phoneNumber, int age, Address address,
                   String nokName, String nokContact, String patientSignificant, String careRequirements, Boolean isInMatchList, String profileImageUrl) {
        this.member = member;
        this.name = name;
        this.residentRegistrationNumber = residentRegistrationNumber;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.age = age;
        this.address = address;
        this.nokName = nokName;
        this.nokContact = nokContact;
        this.patientSignificant = patientSignificant;
        this.careRequirements = careRequirements;
        this.isInMatchList = isInMatchList;
        this.profileImageUrl = profileImageUrl;
    }

    public void updateDetailProfile(final Address address, final String nokName, final String nokContact, final String patientSignificant, final String careRequirements) {
        this.address = address;
        this.nokName = nokName;
        this.nokContact = nokContact;
        this.patientSignificant = patientSignificant;
        this.careRequirements = careRequirements;
    }

    public void deleteProfileImage() {
        this.profileImageUrl = null;
    }

    public void updateProfileImage(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public static int getAge(String rrn) {
        String birthDateString = rrn.substring(0, 6);
        int century;

        char genderCode = rrn.charAt(7);
        if (genderCode == '1' || genderCode == '2' || genderCode == '5' || genderCode == '6') {
            century = 1900;
        } else if (genderCode == '3' || genderCode == '4' || genderCode == '7' || genderCode == '8') {
            century = 2000;
        } else {
            throw new InvalidValueException(INVALID_RESIDENT_REGISTRATION_NUMBER);
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
