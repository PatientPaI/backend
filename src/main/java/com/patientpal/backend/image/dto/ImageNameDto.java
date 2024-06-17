package com.patientpal.backend.image.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(force = true)
public class ImageNameDto {
    private final String imageName;

    public ImageNameDto(String imageName) {
        this.imageName = imageName;
    }
}
