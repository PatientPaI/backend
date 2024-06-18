package com.patientpal.backend.caregiver.dto.response;

import com.patientpal.backend.member.domain.Address;
import com.patientpal.backend.member.domain.Gender;
import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CaregiverProfileResponse {

    private String name;

    private int age;

    private Gender gender;

    private Address address;

    private float rating;

    private int experienceYears;

    private String specialization;

    private String image;

    @QueryProjection
    public CaregiverProfileResponse(String name, int age, Gender gender, Address address, float rating,
                                    int experienceYears, String specialization, String image) {
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.address = address;
        this.rating = rating;
        this.experienceYears = experienceYears;
        this.specialization = specialization;
        this.image = image;
    }
}
