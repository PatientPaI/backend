package com.patientpal.backend.post.domain;

import com.patientpal.backend.common.BaseTimeEntity;
import com.patientpal.backend.member.domain.Member;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Entity
@Getter
@Table(name = "posts")
@RequiredArgsConstructor
public class Post extends BaseTimeEntity {

    @Id
    @Column(name = "post_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    private PostType postType;

    @Builder
    public Post(Member member, @NonNull String title, @NonNull String content, PostType postType) {
        this.member = member;
        this.title = title;
        this.content = content;
        this.postType = postType;
    }



    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
