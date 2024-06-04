package com.patientpal.backend.post.controller;


import com.patientpal.backend.member.domain.Member;
import com.patientpal.backend.member.domain.Role;
import com.patientpal.backend.member.dto.MemberResponse;
import com.patientpal.backend.member.service.MemberService;
import com.patientpal.backend.post.domain.Post;
import com.patientpal.backend.post.dto.*;
import com.patientpal.backend.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.nio.file.attribute.UserPrincipal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class NoticeController {

    private final PostService postService;
    private final MemberService memberService;

    // TODO: wjdwwidz paging 처리
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<PostListResponse> list() {
        List<Post> posts = postService.getNotices();
        return posts.stream()
                .map(PostListResponse::new)
                .toList();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PostResponse get(@PathVariable("id") Long id) {
        Post post = postService.getPost(id);
        return new PostResponse(post);
    }

    // TODO: wjdwwidz member 추가
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PostCreateResponse create(@RequestBody PostCreateRequest createRequest, @AuthenticationPrincipal User currentMember) {
        Member member = memberService.getUserByUsername(currentMember.getUsername());
        Role role = member.getRole();
        if (role != Role.ADMIN) {
            throw new IllegalArgumentException("권한이 없습니다.");
        }
        Post post = postService.createPost(member, createRequest);
        return new PostCreateResponse(post);
    }

    // TODO: wjdwwidz member 추가
    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PostResponse update(@PathVariable("id") Long id, @RequestBody PostUpdateRequest updateRequest) {
        Post post = postService.updatePost(id, updateRequest);
        return new PostResponse(post);
    }

    // TODO: wjdwwidz member 추가
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        postService.deletePost(id);
    }
}
