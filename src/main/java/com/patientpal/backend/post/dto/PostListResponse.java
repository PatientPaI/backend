package com.patientpal.backend.post.dto;

import com.patientpal.backend.post.domain.Post;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PostListResponse {
    private Long id;
    private Long memberId;
    private String title;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public PostListResponse(Post post) {
        this.id = post.getId();
        this.memberId = post.getMember().getId();
        this.title = post.getTitle();
        this.createdAt = post.getCreatedDate();
        this.updatedAt = post.getLastModifiedDate();
    }
}