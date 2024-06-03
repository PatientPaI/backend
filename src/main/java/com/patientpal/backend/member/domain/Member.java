package com.patientpal.backend.member.domain;

import jakarta.persistence.*;

import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(nullable = false, length = 32)
    private String username;

    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Provider provider;

    @OneToOne(mappedBy = "member")
    private Patient patient;

    @OneToOne(mappedBy = "member")
    private Caregiver caregiver;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false, length = 20)
    private String contact;

    //프로필 세부 등록 완료 시, true로 변경. -> 이후 매칭 요청 전송 or 리스트에 등록 가능.
    private Boolean isCompletedProfile;

    private Boolean isInMatchList;

    private String name;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String address;

    private LocalDate birthDate;

    @Builder
    public Member(String username, String password, Provider provider, Role role, String contact) {
        this.username = username;
        this.password = password;
        this.provider = provider;
        this.role = role;
        this.contact = contact;
    }

    public void encodePassword(PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(this.password);
    }

    public void changePassword(PasswordEncoder passwordEncoder, String newPassword) {
        this.password = newPassword;
        encodePassword(passwordEncoder);
    }
}
