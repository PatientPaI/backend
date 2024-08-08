package com.patientpal.backend.caregiver.dto.response;

import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CaregiverRecommendListResponse {

    private List<CaregiverProfileResponse> caregivers;

    public CaregiverRecommendListResponse(List<CaregiverProfileResponse> caregivers) {
        this.caregivers = caregivers;
    }
}
