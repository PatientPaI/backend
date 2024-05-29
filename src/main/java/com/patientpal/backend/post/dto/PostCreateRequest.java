package com.patientpal.backend.post.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PostCreateRequest {

    @NotEmpty
    private String title;
    @NotEmpty
    private String content;
}
