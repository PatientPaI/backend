package com.patientpal.backend.matching.domain;

import static jakarta.persistence.FetchType.LAZY;

import com.patientpal.backend.caregiver.domain.Caregiver;
import com.patientpal.backend.common.BaseEntity;
import com.patientpal.backend.patient.domain.Patient;
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
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "caregiver_id")
    private Caregiver caregiver;

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
    public Match(@NonNull Patient patient, @NonNull Caregiver caregiver, @NonNull MatchStatus matchStatus, @NonNull ReadStatus readStatus,
                 FirstRequest firstRequest, String patientProfileSnapshot, String caregiverProfileSnapshot) {
        this.patient = patient;
        this.caregiver = caregiver;
        this.matchStatus = matchStatus;
        this.readStatus = readStatus;
        this.firstRequest = firstRequest;
        this.patientProfileSnapshot = patientProfileSnapshot;
        this.caregiverProfileSnapshot = caregiverProfileSnapshot;
    }
}
