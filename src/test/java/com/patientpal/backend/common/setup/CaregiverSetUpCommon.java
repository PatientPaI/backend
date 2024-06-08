package com.patientpal.backend.common.setup;

import com.patientpal.backend.caregiver.domain.Caregiver;
import com.patientpal.backend.caregiver.dto.request.CaregiverProfileCreateRequest;
import com.patientpal.backend.caregiver.dto.request.CaregiverProfileUpdateRequest;
import com.patientpal.backend.caregiver.dto.response.CaregiverProfileResponse;
import com.patientpal.backend.member.domain.Address;
import com.patientpal.backend.member.domain.Member;
import com.patientpal.backend.member.domain.Provider;
import com.patientpal.backend.member.domain.Role;

public class CaregiverSetUpCommon {

    public static Caregiver setUpCaregiver() {
        Member member = Member.builder()
                .username("lhs")
                .password("1234")
                .role(Role.CAREGIVER)
                .provider(Provider.LOCAL)
                .build();
        return Caregiver.builder()
                .name("sickLHS")
                .residentRegistrationNumber("123456-7890123")
                .phoneNumber("010-1234-5678")
                .address(new Address("우리", "동네", "근처"))
                .member(member)
                .rating(4.5f)
                .experienceYears(5)
                .specialization("전문 분야")
                .caregiverSignificant("lhsCare")
                .build();
    }

    public static CaregiverProfileCreateRequest setUpCaregiverProfileCreateRequest() {
        return CaregiverProfileCreateRequest.builder()
                .name("caregiverlhs")
                .residentRegistrationNumber("123456-7890123")
                .phoneNumber("010-1234-5678")
                .address(new Address("저기", "나무", "밑"))
                .rating(4.5f)
                .experienceYears(5)
                .specialization("전문 분야")
                .caregiverSignificant("특이사항 많음")
                .build();
    }

    public static CaregiverProfileUpdateRequest setUpCaregiverProfileUpdateRequest() {
        return CaregiverProfileUpdateRequest.builder()
                .address(new Address("여기", "길", "12345"))
                .rating(4.0f)
                .experienceYears(10)
                .specialization("변경된 전문 분야")
                .caregiverSignificant("변경된 특이사항")
                .build();
    }

    public static CaregiverProfileResponse setUpCaregiverProfileResponse() {
        return new CaregiverProfileResponse(1L, "caregiverlhs", "123456-7890123", "010-1234-5678",
                new Address("저기", "나무", "밑"), 4.5f, 5, "전문 분야", "특이사항 많음");
    }
}
