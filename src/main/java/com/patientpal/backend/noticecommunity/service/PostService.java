package com.patientpal.backend.noticecommunity.service;

import com.patientpal.backend.noticecommunity.domain.Post;
import com.patientpal.backend.noticecommunity.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    // TODO: wjdwwidz paging 처리
    public List<Post> getPosts() {
        return postRepository.findAll();
    }
}
