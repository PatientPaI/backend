package com.patientpal.backend.review.fixtures;

import com.patientpal.backend.caregiver.domain.Caregiver;
import com.patientpal.backend.member.domain.Address;

public class CaregiverFixture {
    public static Caregiver createCaregiver() {
        return Caregiver.builder()
                .id(1L)
                .name("Caregiver A")
                .address(new Address("12345", "Seoul", "Gangnam"))
                .build();
    }
}
