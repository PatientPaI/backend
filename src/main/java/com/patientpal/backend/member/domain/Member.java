package com.patientpal.backend.member.domain;

import com.patientpal.backend.common.BaseTimeEntity;
import com.patientpal.backend.matching.domain.Match;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Getter
@Entity
@Table(name = "members")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "member_type")
@SuperBuilder
public  class Member extends BaseTimeEntity { //abstract 붙이고 싶은데 CustomOauth2UserService 여기서 Member 빌드할 때 걸리네
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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @OneToMany(mappedBy = "requestMember", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Match> requestedMatches = new ArrayList<>();

    @OneToMany(mappedBy = "receivedMember", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Match> receivedMatches = new ArrayList<>();

    @Column(unique = true)
    private String residentRegistrationNumber;

    private int age;

    @Column(unique = true)
    private String contact;

    private String name;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Embedded
    private Address address;

    @Setter
    private Boolean isProfilePublic;

    @Setter
    private Boolean isCompleteProfile;

    @Column(length = 512)
    @Setter
    private String profileImageUrl;

    public Member(String username, String password, String contact, Provider provider, Role role) {
        this.username = username;
        this.password = password;
        this.contact = contact;
        this.provider = provider;
        this.role = role;
    }

    public void encodePassword(PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(this.password);
    }

    public void chanceProfileVisible(Member member) {
        if (member.getIsProfilePublic()) {
            this.isProfilePublic = false;
        } else {
            this.isProfilePublic = true;
        }
    }

    public void changePassword(PasswordEncoder passwordEncoder, String newPassword) {
        this.password = newPassword;
        encodePassword(passwordEncoder);
    }

    public void updateAddress(Address address) {
        this.address = address;
    }


    public void updateGender(Gender gender) {
        this.gender = gender;
    }

    public void updateResidentRegistrationNumber(String residentRegistrationNumber) {
        this.residentRegistrationNumber = residentRegistrationNumber;
    }

    public void updateContact(String contact) {
        this.contact = contact;
    }

    public void updateName(String name) {
        this.name = name;
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

    public boolean isNotOwner(Long id) {
        return !Objects.equals(this.id, id);
    }
}
