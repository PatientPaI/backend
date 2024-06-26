package com.patientpal.backend.matching.domain;

import static jakarta.persistence.FetchType.LAZY;

import com.patientpal.backend.common.BaseEntity;
import com.patientpal.backend.member.domain.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@Table(name = "matches")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Match extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "match_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "request_member_id")
    private Member requestMember;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "received_member_id")
    private Member receivedMember;

    @Enumerated(EnumType.STRING)
    @Setter
    private MatchStatus matchStatus;

    @Enumerated(EnumType.STRING)
    @Setter
    private ReadStatus readStatus;

    @Enumerated(EnumType.STRING)
    private FirstRequest firstRequest;

    @Lob
    @Setter
    private String patientProfileSnapshot;

    @Lob
    @Setter
    private String caregiverProfileSnapshot;

    @Builder
    public Match(@NonNull Member requestMember, @NonNull Member receivedMember, @NonNull MatchStatus matchStatus, @NonNull ReadStatus readStatus,
                 FirstRequest firstRequest, String patientProfileSnapshot, String caregiverProfileSnapshot) {
        this.requestMember = requestMember;
        this.receivedMember = receivedMember;
        this.matchStatus = matchStatus;
        this.readStatus = readStatus;
        this.firstRequest = firstRequest;
        this.patientProfileSnapshot = patientProfileSnapshot;
        this.caregiverProfileSnapshot = caregiverProfileSnapshot;
    }
}
