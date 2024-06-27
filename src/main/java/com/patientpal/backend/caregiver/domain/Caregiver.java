package com.patientpal.backend.caregiver.domain;

import com.patientpal.backend.member.domain.Address;
import com.patientpal.backend.member.domain.Gender;
import com.patientpal.backend.member.domain.Member;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
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

    public void updateDetailProfile(final Address address, final float rate, final int experienceYears, final String specialization, final String caregiverSignificant) {
        updateAddress(address);
        this.rating = rate;
        this.experienceYears = experienceYears;
        this.specialization = specialization;
        this.caregiverSignificant = caregiverSignificant;
    }

    //TODO 나이 예외 처리
    //EX. 011012-1408293 => 126살로 나옴.

    public void registerDetailProfile(final String name, final Address address, final String contact, final String residentRegistrationNumber, final Gender gender,
                                      final int experienceYears, final String specialization, final String caregiverSignificant, String profileImageUrl) {
        updateName(name);
        updateAddress(address);
        updateContact(contact);
        updateResidentRegistrationNumber(residentRegistrationNumber);
        updateGender(gender);
        updateProfileImage(profileImageUrl);
        this.experienceYears = experienceYears;
        this.specialization = specialization;
        this.caregiverSignificant = caregiverSignificant;
    }

    public String generateCaregiverProfileSnapshot() {
        return String.format("Caregiver Snapshot - Name: %s, Address: %s, Experience: %d years, CaregiverSignificant: %s, Specialization: %s",
                this.getName(),
                this.getAddress(),
                this.getExperienceYears(),
                this.getCaregiverSignificant(),
                this.getSpecialization());
    }
}
