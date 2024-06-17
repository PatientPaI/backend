package com.patientpal.backend.post.controller;

import com.patientpal.backend.member.domain.Member;
import com.patientpal.backend.member.domain.Role;
import com.patientpal.backend.member.service.MemberService;
import com.patientpal.backend.post.domain.Post;
import com.patientpal.backend.post.dto.*;
import com.patientpal.backend.post.libs.RoleType;
import com.patientpal.backend.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/board")
@RequiredArgsConstructor
public class BoardController {

    private final PostService postService;
    private final MemberService memberService;

    // TODO: wjdwwidz paging 처리
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<PostListResponse> list() {
        List<Post> posts = postService.getFreePosts();
        return posts.stream()
                .map(PostListResponse::new)
                .toList();
    }

    // TODO : member 게시판 접근권한 논의 필요
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PostResponse get(@PathVariable("id") Long id) {
        Post post = postService.getPost(id);
        return new PostResponse(post);
    }

    @RoleType(Role.USER)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PostCreateResponse create(@RequestBody PostCreateRequest createRequest, @AuthenticationPrincipal User currentMember) {
        Member member = memberService.getUserByUsername(currentMember.getUsername());
        Post post = postService.createFreePost(member, createRequest);
        return new PostCreateResponse(post);
    }

    @RoleType(Role.USER)
    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PostResponse update(@PathVariable("id") Long id, @RequestBody PostUpdateRequest updateRequest, @AuthenticationPrincipal User currentMember) {
        Member member = memberService.getUserByUsername(currentMember.getUsername());
        Post post = postService.updatePost(id, updateRequest);
        return new PostResponse(post);
    }

    @RoleType(Role.USER)
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id, @AuthenticationPrincipal User currentMember) {
        Member member = memberService.getUserByUsername(currentMember.getUsername());
        postService.deletePost(id);
    }

}
