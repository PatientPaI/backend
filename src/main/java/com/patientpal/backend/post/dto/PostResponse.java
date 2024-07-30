package com.patientpal.backend.post.dto;

import com.patientpal.backend.post.domain.Post;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PostResponse {
    private Long id;
    private String name;
    private Long memberId;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String postType;
    private int views;


    public PostResponse(Post post) {
        this.id = post.getId();
        this.name = post.getMember().getName();
        this.memberId = post.getMember().getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.createdAt = post.getCreatedDate();
        this.updatedAt = post.getLastModifiedDate();
        this.postType = post.getPostType().toString();
        this.views = post.getViews();
    }
}
