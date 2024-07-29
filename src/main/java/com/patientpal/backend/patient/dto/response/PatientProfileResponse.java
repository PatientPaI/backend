package com.patientpal.backend.patient.dto.response;

import com.patientpal.backend.member.domain.Address;
import com.patientpal.backend.member.domain.Gender;
import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PatientProfileResponse {

    private Long id;

    private String name;

    private int age;

    private Gender gender;

    private Address address;

    private String image;

    private Integer viewCounts;

    @Builder
    @QueryProjection
    public PatientProfileResponse(Long id, String name, int age, Gender gender, Address address, String image,
                                  Integer viewCounts) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.address = address;
        this.image = image;
        this.viewCounts = viewCounts;
    }
}
