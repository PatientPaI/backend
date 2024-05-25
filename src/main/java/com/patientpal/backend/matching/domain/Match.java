package com.patientpal.backend.matching.domain;

import com.patientpal.backend.member.domain.Caregiver;
import com.patientpal.backend.member.domain.Patient;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

import static jakarta.persistence.FetchType.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "matches")
public class Match {

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
    private MatchStatus status;

    @Builder
    public Match(@NonNull Patient patient, @NonNull Caregiver caregiver, @NonNull LocalDateTime createdDate, @NonNull MatchStatus status) {
        this.patient = patient;
        this.caregiver = caregiver;
        this.createdDate = createdDate;
        this.status = status;
    }
}
