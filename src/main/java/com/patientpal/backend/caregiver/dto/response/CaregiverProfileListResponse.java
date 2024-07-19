package com.patientpal.backend.caregiver.dto.response;

import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CaregiverProfileListResponse {

    private List<CaregiverProfileResponse> caregiverProfileList;
    private int currentPage;
    private boolean hasNext;

    public CaregiverProfileListResponse(List<CaregiverProfileResponse> caregiverProfileList, int currentPage, boolean hasNext) {
        this.caregiverProfileList = caregiverProfileList;
        this.currentPage = currentPage;
        this.hasNext = hasNext;
    }

    public static CaregiverProfileListResponse from(Slice<CaregiverProfileResponse> search) {
        return new CaregiverProfileListResponse(
                search.getContent(),
                search.getNumber(),
                search.hasNext()
        );
    }
}
