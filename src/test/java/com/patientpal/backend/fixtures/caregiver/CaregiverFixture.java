package com.patientpal.backend.fixtures.caregiver;

import static com.patientpal.backend.fixtures.member.MemberFixture.DOHYUN_NAME;
import static com.patientpal.backend.fixtures.member.MemberFixture.HUSEONG_NAME;
import static com.patientpal.backend.fixtures.member.MemberFixture.JEONGHYE_NAME;
import static com.patientpal.backend.fixtures.member.MemberFixture.dohyunRoleCaregiver;
import static com.patientpal.backend.fixtures.member.MemberFixture.huseongRoleCaregiver;
import static com.patientpal.backend.fixtures.member.MemberFixture.jeonghyeRoleCaregiver;

import com.patientpal.backend.caregiver.domain.Caregiver;
import com.patientpal.backend.caregiver.dto.request.CaregiverProfileCreateRequest;
import com.patientpal.backend.caregiver.dto.request.CaregiverProfileUpdateRequest;
import com.patientpal.backend.caregiver.dto.response.CaregiverProfileResponse;
import com.patientpal.backend.member.domain.Address;

public class CaregiverFixture {

    public static final String PHONE_NUMBER = "010-1234-5678";
    public static final Address ADDRESS = new Address("Seoul Street", "ho", "hi");

    public static final float RATING = 4.5f;
    public static final int EXPERIENCE_YEARS = 5;
    public static final String RESIDENT_REGISTRATION_NUMBER = "121212-121212";
    public static final String SPECIALIZATION = "노인 간병";
    public static final String CAREGIVER_SIGNIFICANT = "다양한 경험 보유";

    public static final Address UPDATE_ADDRESS = new Address("Suwon Street", "hoi", "hii");
    public static final float UPDATE_RATING = 5.0f;
    public static final int UPDATE_EXPERIENCE_YEARS = 10;
    public static final String UPDATE_SPECIALIZATION = "업데이트 노인 간병";
    public static final String UPDATE_CAREGIVER_SIGNIFICANT = "업데이트 다양한 경험 보유";

    public static Caregiver huseongCaregiver() {
        return Caregiver.builder()
                .member(huseongRoleCaregiver())
                .name(HUSEONG_NAME)
                .residentRegistrationNumber(RESIDENT_REGISTRATION_NUMBER)
                .phoneNumber(PHONE_NUMBER)
                .address(ADDRESS)
                .rating(RATING)
                .experienceYears(EXPERIENCE_YEARS)
                .specialization(SPECIALIZATION)
                .caregiverSignificant(CAREGIVER_SIGNIFICANT)
                .build();
    }

    public static Caregiver dohyunCaregiver() {
        return Caregiver.builder()
                .member(dohyunRoleCaregiver())
                .name(DOHYUN_NAME)
                .residentRegistrationNumber(RESIDENT_REGISTRATION_NUMBER)
                .phoneNumber(PHONE_NUMBER)
                .address(ADDRESS)
                .rating(RATING)
                .experienceYears(EXPERIENCE_YEARS)
                .specialization(SPECIALIZATION)
                .caregiverSignificant(CAREGIVER_SIGNIFICANT)
                .build();
    }

    public static Caregiver jeonghyeCaregiver() {
        return Caregiver.builder()
                .member(jeonghyeRoleCaregiver())
                .name(JEONGHYE_NAME)
                .residentRegistrationNumber(RESIDENT_REGISTRATION_NUMBER)
                .phoneNumber(PHONE_NUMBER)
                .address(ADDRESS)
                .rating(RATING)
                .experienceYears(EXPERIENCE_YEARS)
                .specialization(SPECIALIZATION)
                .caregiverSignificant(CAREGIVER_SIGNIFICANT)
                .build();
    }

    public static CaregiverProfileCreateRequest createCaregiverProfileRequest() {
        return CaregiverProfileCreateRequest.builder()
                .name(HUSEONG_NAME)
                .residentRegistrationNumber(RESIDENT_REGISTRATION_NUMBER)
                .phoneNumber(PHONE_NUMBER)
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
                .experienceYears(UPDATE_EXPERIENCE_YEARS)
                .specialization(UPDATE_SPECIALIZATION)
                .caregiverSignificant(UPDATE_CAREGIVER_SIGNIFICANT)
                .build();
    }

    public static CaregiverProfileResponse createCaregiverProfileResponse() {
        return CaregiverProfileResponse.builder()
                .memberId(1L)
                .name(HUSEONG_NAME)
                .residentRegistrationNumber(RESIDENT_REGISTRATION_NUMBER)
                .phoneNumber(PHONE_NUMBER)
                .address(ADDRESS)
                .rating(RATING)
                .experienceYears(EXPERIENCE_YEARS)
                .specialization(SPECIALIZATION)
                .caregiverSignificant(CAREGIVER_SIGNIFICANT)
                .build();
    }
}