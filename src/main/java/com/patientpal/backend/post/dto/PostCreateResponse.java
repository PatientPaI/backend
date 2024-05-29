package com.patientpal.backend.post.dto;

import com.patientpal.backend.post.domain.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PostCreateResponse {

    private Long id;
    private String title;
    private String content;
    private String createdAt;
    private String updatedAt;

    public PostCreateResponse(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.createdAt = post.getCreatedAt().toString();
        this.updatedAt = post.getUpdatedAt().toString();
    }
}
