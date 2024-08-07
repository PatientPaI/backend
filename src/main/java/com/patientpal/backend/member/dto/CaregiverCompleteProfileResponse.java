package com.patientpal.backend.member.dto;

import com.patientpal.backend.member.domain.Address;
import com.patientpal.backend.member.domain.Gender;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CaregiverCompleteProfileResponse {

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

    private Boolean isCompleteProfile;

    private Boolean isProfilePublic;

    private String image;

    private long viewCount;

    private LocalDateTime wantCareStartDate;

    private LocalDateTime wantCareEndDate;

    @Builder
    public CaregiverCompleteProfileResponse(Long memberId, String name, Integer age, String contact, Gender gender,
                                            Address address, float rating, int experienceYears, String specialization,
                                            String caregiverSignificant, Boolean isCompleteProfile, Boolean isProfilePublic, String image,
                                            long viewCount, LocalDateTime wantCareStartDate, LocalDateTime wantCareEndDate) {
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
        this.isCompleteProfile = isCompleteProfile;
        this.isProfilePublic = isProfilePublic;
        this.image = image;
        this.viewCount = viewCount;
        this.wantCareStartDate = wantCareStartDate;
        this.wantCareEndDate = wantCareEndDate;
    }

    public static CaregiverCompleteProfileResponse of(Long memberId, String name, Integer age, String contact, Gender gender,
                                                   Address address, float rating, int experienceYears, String specialization,
                                                   String caregiverSignificant, Boolean isCompleteProfile, Boolean isProfilePublic, String image,
                                                   long viewCount, LocalDateTime wantCareStartDate, LocalDateTime wantCareEndDate) {
        return CaregiverCompleteProfileResponse.builder()
                .memberId(memberId)
                .name(name)
                .age(age)
                .contact(contact)
                .gender(gender)
                .address(address)
                .rating(rating)
                .experienceYears(experienceYears)
                .specialization(specialization)
                .caregiverSignificant(caregiverSignificant)
                .isCompleteProfile(isCompleteProfile)
                .isProfilePublic(isProfilePublic)
                .image(image)
                .viewCount(viewCount)
                .wantCareStartDate(wantCareStartDate)
                .wantCareEndDate(wantCareEndDate)
                .build();
    }
}
