package com.patientpal.backend.caregiver.domain;

import com.patientpal.backend.member.domain.Address;
import com.patientpal.backend.member.domain.Gender;
import com.patientpal.backend.member.domain.Member;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity(name = "caregivers")
@Getter
@DiscriminatorValue("CAREGIVER")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
public class Caregiver extends Member {

    private float rating;

    private int experienceYears;

    private String specialization;

    @Lob
    private String caregiverSignificant;

    public Caregiver(float rating, int experienceYears, String specialization, String caregiverSignificant) {
        this.rating = rating;
        this.experienceYears = experienceYears;
        this.specialization = specialization;
        this.caregiverSignificant = caregiverSignificant;
    }

    public void updateDetailProfile(final Address address, final float rate, final int experienceYears, final String specialization, final int age, final String caregiverSignificant,
                                    final LocalDateTime wantCareStartDate, final LocalDateTime wantCareEndDate) {
        updateAddress(address);
        updateWantCareStartDate(wantCareStartDate);
        updateWantCareEndDate(wantCareEndDate);
        updateAge(age);
        this.rating = rate;
        this.experienceYears = experienceYears;
        this.specialization = specialization;
        this.caregiverSignificant = caregiverSignificant;
    }

    public void registerDetailProfile(final String name, final Address address, final String contact, final int age, final Gender gender,
                                      final int experienceYears, final String specialization, final String caregiverSignificant,
                                      final LocalDateTime wantCareStartDate, final LocalDateTime wantCareEndDate, String profileImageUrl) {
        updateName(name);
        updateAddress(address);
        updateContact(contact);
        updateAge(age);
        updateGender(gender);
        updateProfileImage(profileImageUrl);
        updateIsCompleteProfile();
        updateWantCareStartDate(wantCareStartDate);
        updateWantCareEndDate(wantCareEndDate);
        this.experienceYears = experienceYears;
        this.specialization = specialization;
        this.caregiverSignificant = caregiverSignificant;
    }

}
