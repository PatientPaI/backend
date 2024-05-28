package com.patientpal.backend.noticecommunity.controller;


import com.patientpal.backend.noticecommunity.domain.Post;
import com.patientpal.backend.noticecommunity.dto.PostListResponseDto;
import com.patientpal.backend.noticecommunity.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// TODO
// Create
// Update
// READ
// DELETE
// READ_ALL
@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping("/list")
    @ResponseStatus(HttpStatus.OK)
    public List<PostListResponseDto> list() {
        List<Post> posts = postService.getPosts();
        return posts.stream()
                .map(PostListResponseDto::new)
                .toList();
    }
}
