package com.patientpal.backend.patient.dto.request;

import com.patientpal.backend.member.domain.Address;
import com.patientpal.backend.member.domain.Gender;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PatientProfileCreateRequest {

    @NotNull
    private String name;

    @NotNull
    private Integer age;

    @NotNull
    private Gender gender;

    //TODO 인증
    @NotNull
    private String contact;

    @NotNull
    private Address address;

    @NotNull
    private String patientSignificant;

    private String careRequirements;

    @NotNull
    private String realCarePlace;

    @NotNull
    private Boolean isNok;
    //TODO 보호자라면 이름 연락처 기재
    private String nokName;

    private String nokContact;

    private LocalDateTime wantCareStartDate;

    private LocalDateTime wantCareEndDate;

    @Builder
    public PatientProfileCreateRequest(String name, Integer age, Gender gender, String contact,
                                       Address address, String nokName, String nokContact, String patientSignificant,
                                       String careRequirements, String realCarePlace, Boolean isNok,
                                       LocalDateTime wantCareStartDate, LocalDateTime wantCareEndDate) {
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.contact = contact;
        this.address = address;
        this.nokName = nokName;
        this.nokContact = nokContact;
        this.patientSignificant = patientSignificant;
        this.careRequirements = careRequirements;
        this.realCarePlace = realCarePlace;
        this.isNok = isNok;
        this.wantCareStartDate = wantCareStartDate;
        this.wantCareEndDate = wantCareEndDate;
    }
}
