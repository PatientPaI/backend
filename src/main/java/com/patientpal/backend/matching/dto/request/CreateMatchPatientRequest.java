package com.patientpal.backend.matching.dto.request;

import jakarta.persistence.Column;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateMatchPatientRequest {

    @Column(nullable = false)
    private LocalDateTime careStartDateTime;

    @Column(nullable = false)
    private LocalDateTime careEndDateTime;

    private Long totalAmount;

    private String significant;

    private String realCarePlace;

    private boolean isNok;

    @Builder
    public CreateMatchPatientRequest(LocalDateTime careStartDateTime, LocalDateTime careEndDateTime, Long totalAmount,
                                     String significant, String realCarePlace, boolean isNok) {
        this.careStartDateTime = careStartDateTime;
        this.careEndDateTime = careEndDateTime;
        this.totalAmount = totalAmount;
        this.significant = significant;
        this.realCarePlace = realCarePlace;
        this.isNok = isNok;
    }
}
