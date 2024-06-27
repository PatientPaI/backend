package com.patientpal.backend.patient.domain;

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
@DiscriminatorValue("PATIENT")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
public class Patient extends Member {

    private String nokName; //보호자 이름


    private String nokContact;

    @Lob
    private String patientSignificant;

    @Lob
    private String careRequirements;

    public Patient(String nokName, String nokContact, String patientSignificant, String careRequirements) {
        this.nokName = nokName;
        this.nokContact = nokContact;
        this.patientSignificant = patientSignificant;
        this.careRequirements = careRequirements;
    }

    public void registerDetailProfile(final String name, final Address address, final String contact, final String residentRegistrationNumber, final Gender gender,
                                      final String nokName, final String nokContact, final String patientSignificant, final String careRequirements, String profileImageUrl) { // 이후 쪼개서 넣어야됨. domain에서 dto 참조 ㄴㄴ
        updateName(name);
        updateAddress(address);
        updateContact(contact);
        updateResidentRegistrationNumber(residentRegistrationNumber);
        updateGender(gender);
        updateProfileImage(profileImageUrl);
        this.nokName = nokName;
        this.nokContact = nokContact;
        this.patientSignificant = patientSignificant;
        this.careRequirements = careRequirements;
    }

    public void updateDetailProfile(final Address address, final String nokName, final String nokContact, final String patientSignificant, final String careRequirements) {
        updateAddress(address);
        this.nokName = nokName;
        this.nokContact = nokContact;
        this.patientSignificant = patientSignificant;
        this.careRequirements = careRequirements;
    }

    public String generatePatientProfileSnapshot() {
        return String.format("Patient Snapshot - Name: %s, Address: %s, PatientSignificant: %s, CareRequirements : %s",
                this.getName(),
                this.getAddress(),
                this.getPatientSignificant(),
                this.getCareRequirements());
    }
}
