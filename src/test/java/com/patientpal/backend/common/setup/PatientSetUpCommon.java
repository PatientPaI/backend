package com.patientpal.backend.common.setup;

import com.patientpal.backend.member.domain.Address;
import com.patientpal.backend.member.domain.Member;
import com.patientpal.backend.member.domain.Provider;
import com.patientpal.backend.member.domain.Role;
import com.patientpal.backend.patient.domain.Patient;
import com.patientpal.backend.patient.dto.request.PatientProfileCreateRequest;
import com.patientpal.backend.patient.dto.request.PatientProfileUpdateRequest;
import com.patientpal.backend.patient.dto.response.PatientProfileResponse;

public class PatientSetUpCommon {

    public static Patient setUpPatient() {
        Member member = Member.builder()
                .username("lhs")
                .password("1234")
                .role(Role.USER)
                .provider(Provider.LOCAL)
                .build();
        return Patient.builder()
                .name("sickLHS")
                .member(member)
                .nokContact("lhsChild")
                .address(new Address("우주", "너머", "어딘가"))
                .build();
    }

    public static PatientProfileCreateRequest setUpPatientProfileCreateRequest() {
        return PatientProfileCreateRequest.builder()
                .name("patientlhs")
                .residentRegistrationNumber("123456-7890123")
                .phoneNumber("010-1234-5678")
                .address(new Address("저기", "나무", "밑"))
                .nokName("보호자")
                .nokContact("보호자연락처")
                .patientSignificant("특이사항 많음")
                .careRequirements("요구사항 매우 많음")
                .build();

    }

    public static PatientProfileUpdateRequest setUpPatientProfileUpdateRequest() {
        return PatientProfileUpdateRequest.builder()
                .address(new Address("수정", "하는", "주소"))
                .nokName("anotherNok")
                .nokContact("12345678")
                .patientSignificant("몸이 더 아파짐")
                .careRequirements("성실한 분만 신청 바람")
                .build();
    }

    public static PatientProfileResponse setUpPatientProfileResponse() {
        return new PatientProfileResponse(1L, "patientlhs", "123456-7890123", "010-1234-5678",
                new Address("저기", "나무", "밑"), "보호자", "보호자연락처", "특이사항 많음", "요구사항 매우 많음");

    }
}
