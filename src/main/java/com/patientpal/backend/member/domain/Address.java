package com.patientpal.backend.member.domain;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Address {

    private String addr;
    private String addrDetail;
    private String zipCode;

    public Address(String zipCode, String addr, String addrDetail) {
        this.addr = addr;
        this.addrDetail = addrDetail;
        this.zipCode = zipCode;
    }
}
