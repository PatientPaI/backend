package com.patientpal.backend.patient.dto.request;

import com.patientpal.backend.member.domain.Address;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PatientProfileUpdateRequest {

    @NotNull(message = "주소는 필수 입력 사항입니다.")
    private Address address;

    private String nokName;

    private String nokContact;

    @NotNull(message = "특이사항(세부 정보)는 필수 입력 사항입니다.")
    private String patientSignificant;

    private String careRequirements;
}
