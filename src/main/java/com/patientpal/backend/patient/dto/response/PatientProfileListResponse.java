package com.patientpal.backend.patient.dto.response;

import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Slice;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PatientProfileListResponse {

    private List<PatientProfileResponse> patientProfileList;
    private int currentPage;
    private boolean hasNext;

    public PatientProfileListResponse(List<PatientProfileResponse> patientProfileList, int currentPage, boolean hasNext) {
        this.patientProfileList = patientProfileList;
        this.currentPage = currentPage;
        this.hasNext = hasNext;
    }

    public static PatientProfileListResponse from(Slice<PatientProfileResponse> search) {
        return new PatientProfileListResponse(
                search.getContent(),
                search.getNumber(),
                search.hasNext()
        );
    }
}
