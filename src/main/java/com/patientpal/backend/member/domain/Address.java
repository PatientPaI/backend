package com.patientpal.backend.member.domain;

import jakarta.persistence.*;
import lombok.Getter;

import static jakarta.persistence.FetchType.*;

@Embeddable
@Getter
public class Address {

    private String street;
    private String city;
    private String zipCode;

    protected Address() {}

    public Address(String city, String street, String zipCode) {
        this.city = city;
        this.street = street;
        this.zipCode = zipCode;
    }

}
