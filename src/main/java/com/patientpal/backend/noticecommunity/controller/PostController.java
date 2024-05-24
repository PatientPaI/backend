package com.patientpal.backend.noticecommunity.controller;


import com.patientpal.backend.member.domain.Member;
import com.patientpal.backend.noticecommunity.domain.Post;
import com.patientpal.backend.noticecommunity.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/test")
public class PostController {

    private final PostRepository postRepository;
    @GetMapping
    public String  hello() {
        return "hi";
    }
}
