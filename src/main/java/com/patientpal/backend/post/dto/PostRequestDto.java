package com.patientpal.backend.post.dto;
import com.patientpal.backend.member.domain.Member;
import com.patientpal.backend.post.domain.Post;
import lombok.*;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class PostRequestDto {
    private String content;
    private Member member;


    @Builder
    public PostRequestDto(String content, Member member) {
        this.content = content;
        this.member = member;
    }

    public Post toEntity() {
        return Post.builder()
                .content(content)
                .member(member)
                .build();
    }


}
