package com.patientpal.backend.post.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PostUpdateRequestDto {

    @NotEmpty
    private String title;
    @NotEmpty
    private String content;
}
