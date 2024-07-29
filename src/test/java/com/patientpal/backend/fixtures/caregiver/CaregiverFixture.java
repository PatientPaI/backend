package com.patientpal.backend.fixtures.caregiver;

import com.patientpal.backend.caregiver.domain.Caregiver;
import com.patientpal.backend.caregiver.dto.request.CaregiverProfileCreateRequest;
import com.patientpal.backend.caregiver.dto.request.CaregiverProfileUpdateRequest;
import com.patientpal.backend.caregiver.dto.response.CaregiverProfileDetailResponse;
import com.patientpal.backend.member.domain.Address;
import com.patientpal.backend.member.domain.Gender;

public class CaregiverFixture {

    public static final String PHONE_NUMBER = "010-1234-5678";
    public static final Address ADDRESS = new Address("Seoul Street", "ho", "hi");
    public static final String CAREGIVER_NAME = "간병";
    public static final String CAREGIVER_USERNAME = "간병";
    public static final float RATING = 4.5f;
    public static final int EXPERIENCE_YEARS = 5;
    public static final Integer AGE = 50;
    public static final String SPECIALIZATION = "노인 간병";
    public static final String CAREGIVER_SIGNIFICANT = "다양한 경험 보유";

    public static final Address UPDATE_ADDRESS = new Address("Suwon Street", "hoi", "hii");
    public static final float UPDATE_RATING = 5.0f;
    public static final int UPDATE_EXPERIENCE_YEARS = 10;
    public static final String UPDATE_SPECIALIZATION = "업데이트 노인 간병";
    public static final String UPDATE_CAREGIVER_SIGNIFICANT = "업데이트 다양한 경험 보유";

    public static Caregiver defaultCaregiver() {
        return Caregiver.builder()
                .username(CAREGIVER_USERNAME)
                .name(CAREGIVER_NAME)
                .age(AGE)
                .contact(PHONE_NUMBER)
                .address(ADDRESS)
                .rating(RATING)
                .experienceYears(EXPERIENCE_YEARS)
                .specialization(SPECIALIZATION)
                .caregiverSignificant(CAREGIVER_SIGNIFICANT)
                .build();
    }

    public static CaregiverProfileCreateRequest createCaregiverProfileRequest() {
        return CaregiverProfileCreateRequest.builder()
                .name(CAREGIVER_NAME)
                .age(AGE)
                .gender(Gender.MALE)
                .contact(PHONE_NUMBER)
                .address(ADDRESS)
                .rating(RATING)
                .experienceYears(EXPERIENCE_YEARS)
                .specialization(SPECIALIZATION)
                .caregiverSignificant(CAREGIVER_SIGNIFICANT)
                .build();
    }

    public static CaregiverProfileUpdateRequest updateCaregiverProfileRequest() {
        return CaregiverProfileUpdateRequest.builder()
                .address(UPDATE_ADDRESS)
                .rating(UPDATE_RATING)
                .age(AGE)
                .experienceYears(UPDATE_EXPERIENCE_YEARS)
                .specialization(UPDATE_SPECIALIZATION)
                .caregiverSignificant(UPDATE_CAREGIVER_SIGNIFICANT)
                .build();
    }

    public static CaregiverProfileDetailResponse createCaregiverProfileResponse() {
        return CaregiverProfileDetailResponse.builder()
                .memberId(1L)
                .name(CAREGIVER_NAME)
                .age(AGE)
                .contact(PHONE_NUMBER)
                .address(ADDRESS)
                .rating(RATING)
                .experienceYears(EXPERIENCE_YEARS)
                .specialization(SPECIALIZATION)
                .caregiverSignificant(CAREGIVER_SIGNIFICANT)
                .build();
    }
}
