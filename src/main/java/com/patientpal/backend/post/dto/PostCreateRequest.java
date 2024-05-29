package com.patientpal.backend.post.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class PostCreateRequest {

    @NotEmpty
    private String title;
    @NotEmpty
    private String content;

    public PostCreateRequest(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
