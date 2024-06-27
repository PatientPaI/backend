package com.patientpal.backend.post.service;

import com.patientpal.backend.common.exception.EntityNotFoundException;
import com.patientpal.backend.common.exception.ErrorCode;
import com.patientpal.backend.member.domain.Member;
import com.patientpal.backend.post.domain.Post;
import com.patientpal.backend.post.domain.PostType;
import com.patientpal.backend.post.dto.PostCreateRequest;
import com.patientpal.backend.post.dto.PostUpdateRequest;
import com.patientpal.backend.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {

    private final PostRepository postRepository;

    // TODO: wjdwwidz paging 처리
    @Transactional(readOnly = true)
    public List<Post> getPosts() {
        return postRepository.findAll();
    }

    public List<Post> getNotices() {
        return postRepository.findAllByPostType(PostType.NOTICE);
    }

    @Transactional(readOnly = true)
    public Post getPost(Long id) {
        return findPost(id);
    }

    @Transactional
    public Post createPost(Member member, PostCreateRequest createRequest) {
        Post post = Post.builder()
                .member(member)
                .title(createRequest.getTitle())
                .content(createRequest.getContent())
                .postType(PostType.NOTICE)
                .build();

        return postRepository.save(post);
    }

    @Transactional
    public Post updatePost(Member member, Long id, PostUpdateRequest updateRequest) {
        Post post = postRepository.findByIdAndMemberId(id, member.getId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.POST_NOT_FOUND));
        post.update(updateRequest.getTitle(), updateRequest.getContent());
        return post;
    }

    public void deletePost(Member member, Long id) {
        postRepository.findByIdAndMemberId(id, member.getId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.POST_NOT_FOUND));
        postRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Post> getFreePosts() {
        return postRepository.findAllByPostType(PostType.FREE);
    }

    @Transactional
    public Post createFreePost(Member member, PostCreateRequest createRequest) {
        Post post = Post.builder()
                .member(member)
                .title(createRequest.getTitle())
                .content(createRequest.getContent())
                .postType(PostType.FREE)
                .build();

        return postRepository.save(post);
    }

    private Post findPost(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.POST_NOT_FOUND));
    }
}
