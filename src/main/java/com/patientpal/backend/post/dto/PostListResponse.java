package com.patientpal.backend.post.dto;

import com.patientpal.backend.post.domain.Post;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PostListResponse {
    private Long id;
    private String name;
    private Long memberId;
    private String title;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String postType;
    private int views;

    public PostListResponse(Post post) {
        this.id = post.getId();
        this.name = post.getMember().getName();
        this.memberId = post.getMember().getId();
        this.title = post.getTitle();
        this.createdAt = post.getCreatedDate();
        this.updatedAt = post.getLastModifiedDate();
        this.postType = post.getPostType().toString();
        this.views = post.getViews();
    }
}
