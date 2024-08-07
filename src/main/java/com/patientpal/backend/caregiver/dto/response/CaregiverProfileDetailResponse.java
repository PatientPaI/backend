package com.patientpal.backend.caregiver.dto.response;

import com.patientpal.backend.caregiver.domain.Caregiver;
import com.patientpal.backend.member.domain.Address;
import com.patientpal.backend.member.domain.Gender;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CaregiverProfileDetailResponse {

    private Long memberId;

    private String name;

    private Integer age;

    private String contact;

    private Gender gender;

    private Address address;

    private float rating;

    private int experienceYears;

    private String specialization;

    private String caregiverSignificant;

    private Boolean isProfilePublic;

    private String image;

    private long viewCount;

    private LocalDateTime wantCareStartDate;

    private LocalDateTime wantCareEndDate;

    @Builder
    public CaregiverProfileDetailResponse(Long memberId, String name, int age, String contact, Gender gender, Address address, float rating, int experienceYears,
                                          String specialization, String caregiverSignificant, Boolean isProfilePublic, String image, long viewCount,
                                          LocalDateTime wantCareStartDate, LocalDateTime wantCareEndDate) {
        this.memberId = memberId;
        this.name = name;
        this.age = age;
        this.contact = contact;
        this.gender = gender;
        this.address = address;
        this.rating = rating;
        this.experienceYears = experienceYears;
        this.specialization = specialization;
        this.caregiverSignificant = caregiverSignificant;
        this.isProfilePublic = isProfilePublic;
        this.image = image;
        this.viewCount = viewCount;
        this.wantCareStartDate = wantCareStartDate;
        this.wantCareEndDate = wantCareEndDate;
    }

    public static CaregiverProfileDetailResponse of(Caregiver caregiver) {
        return CaregiverProfileDetailResponse.builder()
                .memberId(caregiver.getId())
                .name(caregiver.getName())
                .age(caregiver.getAge())
                .contact(caregiver.getContact())
                .gender(caregiver.getGender())
                .address(caregiver.getAddress())
                .rating(caregiver.getRating())
                .experienceYears(caregiver.getExperienceYears())
                .specialization(caregiver.getSpecialization())
                .caregiverSignificant(caregiver.getCaregiverSignificant())
                .isProfilePublic(caregiver.getIsProfilePublic())
                .image(caregiver.getProfileImageUrl())
                .viewCount(caregiver.getViewCounts())
                .wantCareStartDate(caregiver.getWantCareStartDate())
                .wantCareEndDate(caregiver.getWantCareEndDate())
                .build();
    }
}
