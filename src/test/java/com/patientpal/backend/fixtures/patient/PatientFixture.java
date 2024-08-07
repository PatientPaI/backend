package com.patientpal.backend.fixtures.patient;

import com.patientpal.backend.member.domain.Address;
import com.patientpal.backend.member.domain.Gender;
import com.patientpal.backend.patient.domain.Patient;
import com.patientpal.backend.patient.dto.request.PatientProfileCreateRequest;
import com.patientpal.backend.patient.dto.request.PatientProfileUpdateRequest;
import com.patientpal.backend.patient.dto.response.PatientProfileDetailResponse;

public class PatientFixture {

    public static final String PATIENT_PHONE_NUMBER = "010-9876-5432";
    public static final Address PATIENT_ADDRESS = new Address("Incheon Street", "ho", "hi");
    public static final String NAME = "환자 이름";
    public static final String NOK_NAME = "보호자이름";
    public static final String NOK_CONTACT = "010-8765-4321";
    public static final Integer AGE = 70;
    public static final String PATIENT_SIGNIFICANT = "환자 특이사항";
    public static final String PATIENT_USERNAME = "환자 아이디";


    public static final Address UPDATE_PATIENT_ADDRESS = new Address("Busan Street", "hoi", "hii");
    public static final String UPDATE_NOK_NAME = "업데이트 보호자이름";
    public static final String UPDATE_NOK_CONTACT = "010-6543-2109";
    public static final String UPDATE_PATIENT_SIGNIFICANT = "업데이트 환자 특이사항";
    public static final String UPDATE_REAL_CARE_PLACE = "업데이트 환자 간병 장소";
    public static final String PATIENT_REAL_CARE_PLACE = "환자 간병 장소";

    public static Patient defaultPatient() {
        return Patient.builder()
                .username(PATIENT_USERNAME)
                .contact(PATIENT_PHONE_NUMBER)
                .age(AGE)
                .address(PATIENT_ADDRESS)
                .isNok(false)
                .realCarePlace(PATIENT_REAL_CARE_PLACE)
                .nokName(NOK_NAME)
                .nokContact(NOK_CONTACT)
                .patientSignificant(PATIENT_SIGNIFICANT)
                .build();
    }

    public static PatientProfileCreateRequest createPatientProfileRequest() {
        return PatientProfileCreateRequest.builder()
                .contact(PATIENT_PHONE_NUMBER)
                .age(AGE)
                .gender(Gender.MALE)
                .isNok(false)
                .realCarePlace(PATIENT_REAL_CARE_PLACE)
                .name(NAME)
                .address(PATIENT_ADDRESS)
                .nokName(NOK_NAME)
                .nokContact(NOK_CONTACT)
                .patientSignificant(PATIENT_SIGNIFICANT)
                .build();
    }

    public static PatientProfileUpdateRequest updatePatientProfileRequest() {
        return PatientProfileUpdateRequest.builder()
                .address(UPDATE_PATIENT_ADDRESS)
                .age(AGE)
                .isNok(true)
                .realCarePlace(UPDATE_REAL_CARE_PLACE)
                .nokName(UPDATE_NOK_NAME)
                .nokContact(UPDATE_NOK_CONTACT)
                .patientSignificant(UPDATE_PATIENT_SIGNIFICANT)
                .build();
    }

    public static PatientProfileDetailResponse createPatientProfileResponse() {
        return PatientProfileDetailResponse.builder()
                .memberId(1L)
                .name(NAME)
                .age(AGE)
                .contact(PATIENT_PHONE_NUMBER)
                .address(PATIENT_ADDRESS)
                .isNok(false)
                .realCarePlace(PATIENT_REAL_CARE_PLACE)
                .nokName(NOK_NAME)
                .nokContact(NOK_CONTACT)
                .patientSignificant(PATIENT_SIGNIFICANT)
                .build();
    }
}
