package com.patientpal.backend.patient.domain;

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

@Entity(name = "patients")
@Getter
@DiscriminatorValue("PATIENT")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
public class Patient extends Member {

    private Boolean isNok; // 보호자 여부

    private String nokName;

    private String nokContact;

    @Lob
    private String patientSignificant;

    @Lob
    private String careRequirements;

    private String realCarePlace;

    public Patient(Boolean isNok, String nokName, String nokContact, String patientSignificant, String careRequirements, String realCarePlace) {
        this.isNok = isNok;
        this.nokName = nokName;
        this.nokContact = nokContact;
        this.patientSignificant = patientSignificant;
        this.careRequirements = careRequirements;
        this.realCarePlace = realCarePlace;
    }

    public void registerDetailProfile(final String name, final Address address, final String contact,
                                      final String residentRegistrationNumber, final Gender gender,
                                      final String nokName, final String nokContact, final String patientSignificant,
                                      final String careRequirements, final String realCarePlace, final Boolean isNok,
                                      final LocalDateTime wantCareStartDate, final LocalDateTime wantCareEndDate,
                                      String profileImageUrl) {
        updateName(name);
        updateAddress(address);
        updateContact(contact);
        updateResidentRegistrationNumber(residentRegistrationNumber);
        updateGender(gender);
        updateProfileImage(profileImageUrl);
        updateIsCompleteProfile();
        updateWantCareStartDate(wantCareStartDate);
        updateWantCareEndDate(wantCareEndDate);
        this.nokName = nokName;
        this.nokContact = nokContact;
        this.patientSignificant = patientSignificant;
        this.careRequirements = careRequirements;
        this.realCarePlace = realCarePlace;
        this.isNok = isNok;
    }

    public void updateDetailProfile(final Address address, final String nokName, final String nokContact,
                                    final String patientSignificant, final String careRequirements,
                                    final String realCarePlace, final Boolean isNok,
                                    final LocalDateTime wantCareStartDate, final LocalDateTime wantCareEndDate) {
        updateAddress(address);
        updateWantCareStartDate(wantCareStartDate);
        updateWantCareEndDate(wantCareEndDate);
        this.nokName = nokName;
        this.nokContact = nokContact;
        this.patientSignificant = patientSignificant;
        this.careRequirements = careRequirements;
        this.realCarePlace = realCarePlace;
        this.isNok = isNok;
    }

    public void deleteProfile() {
        delete();
        this.nokName = null;
        this.nokContact = null;
        this.patientSignificant = null;
        this.careRequirements = null;
    }
}
