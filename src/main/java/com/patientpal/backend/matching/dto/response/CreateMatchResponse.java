package com.patientpal.backend.matching.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateMatchResponse {

    private Long id;

    public CreateMatchResponse(Long id) {
        this.id = id;
    }
}
