package com.patientpal.backend.post.controller;


import com.patientpal.backend.post.domain.Post;
import com.patientpal.backend.post.dto.*;
import com.patientpal.backend.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class NoticeController {

    private final PostService postService;

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
    public PostCreateResponse create(@RequestBody PostCreateRequest createRequest) {
        Post post = postService.createPost(createRequest);
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
