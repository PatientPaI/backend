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
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
public class Member extends BaseTimeEntity {
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

    private Integer age;

    @Column(unique = true)
    private String contact;

    private String name;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    //TODO 현재 프로필 등록 시 주소 한글 입력 안됨. 영어만 가능
    //TODO CreatedDate/last~가 현재 시간보다 -9시간 으로 저장되는 문제
    @Embedded
    private Address address;

    @Setter
    private Boolean isProfilePublic;

    @Setter
    private Boolean isCompleteProfile;

    @Column(length = 512)
    @Setter
    private String profileImageUrl;

    private int viewCounts;

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

    public void changePassword(PasswordEncoder passwordEncoder, String newPassword) {
        this.password = newPassword;
        encodePassword(passwordEncoder);
    }

    public void updateIsCompleteProfile() {
        this.isCompleteProfile = true;
    }

    public void updateAddress(Address address) {
        this.address = address;
    }


    public void updateGender(Gender gender) {
        this.gender = gender;
    }

    public void updateContact(String contact) {
        this.contact = contact;
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void updateAge(int age) {
        this.age = age;
    }

    public void delete() {
        this.name = null;
        this.isCompleteProfile = false;
        this.isProfilePublic = false;
        this.address = null;
        this.gender = null;
        this.contact = null;
        this.profileImageUrl = null;
        this.age = 0;
    }

    public void deleteProfileImage() {
        this.profileImageUrl = null;
    }

    public void updateProfileImage(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public void updateWantCareStartDate(final LocalDateTime wantCareStartDate) {
        this.wantCareStartDate = wantCareStartDate;
    }

    public void updateWantCareEndDate(final LocalDateTime wantCareEndDate) {
        this.wantCareEndDate = wantCareEndDate;
    }

    public static boolean isNotOwner(final String username, final Member member) {
        return !Objects.equals(username, member.getUsername());
    }

    public void changeViewCount(Long value) {
        this.viewCounts = Math.toIntExact(value);
    }

}
