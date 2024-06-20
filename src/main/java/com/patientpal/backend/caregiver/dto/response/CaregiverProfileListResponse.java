package com.patientpal.backend.caregiver.dto.response;

import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CaregiverProfileListResponse {

    private List<CaregiverProfileResponse> caregiverProfileList;
    private int currentPage;
    private int totalPages;
    private long totalItems;

    public CaregiverProfileListResponse(List<CaregiverProfileResponse> caregiverProfileList, int currentPage, int totalPages, long totalItems) {
        this.caregiverProfileList = caregiverProfileList;
        this.currentPage = currentPage;
        this.totalPages = totalPages;
        this.totalItems = totalItems;
    }

    public static CaregiverProfileListResponse from(Page<CaregiverProfileResponse> search) {
        return new CaregiverProfileListResponse(
                search.getContent(),
                search.getNumber(),
                search.getTotalPages(),
                search.getTotalElements()
        );
    }
}
