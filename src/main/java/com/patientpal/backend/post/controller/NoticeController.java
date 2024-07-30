package com.patientpal.backend.post.controller;


import com.patientpal.backend.member.domain.Member;
import com.patientpal.backend.member.domain.Role;
import com.patientpal.backend.member.service.MemberService;
import com.patientpal.backend.post.domain.Post;
import com.patientpal.backend.post.dto.PostCreateRequest;
import com.patientpal.backend.post.dto.PostCreateResponse;
import com.patientpal.backend.post.dto.PostListResponse;
import com.patientpal.backend.post.dto.PostResponse;
import com.patientpal.backend.post.dto.PostUpdateRequest;
import com.patientpal.backend.post.libs.RoleType;
import com.patientpal.backend.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/notice")
@RequiredArgsConstructor
public class NoticeController {

    private final PostService postService;
    private final MemberService memberService;

    @GetMapping
    public Page<PostListResponse> list (@RequestParam(value = "page", defaultValue = "0") int page) {
        Page<Post> posts = this.postService.getNoticePostList(page);
        return posts.map(PostListResponse::new);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PostResponse get(@PathVariable("id") Long id) {
        Post post = postService.getPost(id);
        postService.updateView(id);
        return new PostResponse(post);
    }

    @RoleType(Role.ADMIN)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PostCreateResponse create(@RequestBody PostCreateRequest createRequest, @AuthenticationPrincipal User currentMember) {
        Member member = memberService.getUserByUsername(currentMember.getUsername());
        Post post = postService.createNoticePost(member, createRequest);
        return new PostCreateResponse(post);
    }

    @RoleType(Role.ADMIN)
    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PostResponse update(@PathVariable("id") Long id, @RequestBody PostUpdateRequest updateRequest,@AuthenticationPrincipal User currentMember) {
        Member member = memberService.getUserByUsername(currentMember.getUsername());
        Post post = postService.updatePost(member, id, updateRequest);
        return new PostResponse(post);
    }

    @RoleType(Role.ADMIN)
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id, @AuthenticationPrincipal User currentMember) {
        Member member = memberService.getUserByUsername(currentMember.getUsername());
        postService.deletePost(member, id);
    }
}
