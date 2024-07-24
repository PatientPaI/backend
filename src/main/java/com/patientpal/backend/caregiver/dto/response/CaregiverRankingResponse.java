package com.patientpal.backend.caregiver.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CaregiverRankingResponse {

    private Long id;
    private String name;
    private String address;
    private double rating;

    @Builder
    public CaregiverRankingResponse(Long id, String name, String address, double rating) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.rating = rating;
    }
}
