package com.patientpal.backend.post.dto;

import com.patientpal.backend.post.domain.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class PostListResponseDto {
    private Long id;
    private Long memberId;
    private String title;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public PostListResponseDto(Post post) {
        this.id = post.getId();
        this.memberId = post.getMember().getId();
        this.title = post.getTitle();
        this.createdAt = post.getCreatedAt();
        this.updatedAt = post.getUpdatedAt();
    }
}