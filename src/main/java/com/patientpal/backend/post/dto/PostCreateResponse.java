package com.patientpal.backend.post.dto;

import com.patientpal.backend.post.domain.Post;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class PostCreateResponse {

    private Long id;
    private String name;
    private Long memberId;
    private String title;
    private String content;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private String postType;

    public PostCreateResponse(Post post) {
        this.id = post.getId();
        this.name = post.getMember().getName();
        this.memberId = post.getMember().getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.createdDate = post.getCreatedDate();
        this.updatedDate = post.getLastModifiedDate();
        this.postType = post.getPostType().toString();
    }
}
