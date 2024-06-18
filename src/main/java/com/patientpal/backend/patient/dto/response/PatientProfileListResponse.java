package com.patientpal.backend.patient.dto.response;

import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PatientProfileListResponse {

    private List<PatientProfileResponse> patientProfileList;
    private int currentPage;
    private int totalPages;
    private long totalItems;

    public PatientProfileListResponse(List<PatientProfileResponse> patientProfileList, int currentPage, int totalPages, long totalItems) {
        this.patientProfileList = patientProfileList;
        this.currentPage = currentPage;
        this.totalPages = totalPages;
        this.totalItems = totalItems;
    }

    public static PatientProfileListResponse from(Page<PatientProfileResponse> search) {
        return new PatientProfileListResponse(
                search.getContent(),
                search.getNumber(),
                search.getTotalPages(),
                search.getTotalElements()
        );
    }
}
