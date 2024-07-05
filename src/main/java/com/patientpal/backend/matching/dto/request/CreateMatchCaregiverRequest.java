package com.patientpal.backend.matching.dto.request;

import jakarta.persistence.Column;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateMatchCaregiverRequest {

    @Column(nullable = false)
    private LocalDateTime careStartDateTime;

    @Column(nullable = false)
    private LocalDateTime careEndDateTime;

    private Long totalAmount;

    private String significant;

    @Builder
    public CreateMatchCaregiverRequest(LocalDateTime careStartDateTime, LocalDateTime careEndDateTime, Long totalAmount,
                                       String significant) {
        this.careStartDateTime = careStartDateTime;
        this.careEndDateTime = careEndDateTime;
        this.totalAmount = totalAmount;
        this.significant = significant;
    }
}
