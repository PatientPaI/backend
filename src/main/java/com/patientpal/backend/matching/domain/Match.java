package com.patientpal.backend.matching.domain;

import com.patientpal.backend.common.BaseEntity;
import com.patientpal.backend.common.BaseTimeEntity;
import com.patientpal.backend.member.domain.Caregiver;
import com.patientpal.backend.member.domain.Patient;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

import static jakarta.persistence.FetchType.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Table(name = "matches")
public class Match extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "match_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "caregiver_id")
    private Caregiver caregiver;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdDate;

    @Enumerated(EnumType.STRING)
    private MatchStatus matchStatus;

    @Enumerated(EnumType.STRING)
    private ReadStatus readStatus;

    @Lob
    @Column(nullable = false)
    private String patientProfileSnapshot;

    @Lob
    @Column(nullable = false)
    private String caregiverProfileSnapshot;

    @Builder
    public Match(@NonNull Patient patient, @NonNull Caregiver caregiver, @NonNull MatchStatus matchStatus, @NonNull ReadStatus readStatus,
                 String patientProfileSnapshot, String caregiverProfileSnapshot) {
        this.patient = patient;
        this.caregiver = caregiver;
        this.matchStatus = matchStatus;
        this.readStatus = readStatus;
        this.patientProfileSnapshot = patientProfileSnapshot;
        this.caregiverProfileSnapshot = caregiverProfileSnapshot;
    }
}
