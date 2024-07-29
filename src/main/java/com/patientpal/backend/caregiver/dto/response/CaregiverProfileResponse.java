package com.patientpal.backend.caregiver.dto.response;

import com.patientpal.backend.member.domain.Address;
import com.patientpal.backend.member.domain.Gender;
import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CaregiverProfileResponse {

    private Long id;

    private String name;

    private Integer age;

    private Gender gender;

    private Address address;

    private float rating;

    private int experienceYears;

    private String specialization;

    private String image;

    private Integer viewCounts;

    @Builder
    @QueryProjection
    public CaregiverProfileResponse(Long id, String name, Integer age, Gender gender, Address address, float rating,
                                    int experienceYears, String specialization, String image, Integer viewCounts) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.address = address;
        this.rating = rating;
        this.experienceYears = experienceYears;
        this.specialization = specialization;
        this.image = image;
        this.viewCounts = viewCounts;
    }
}
