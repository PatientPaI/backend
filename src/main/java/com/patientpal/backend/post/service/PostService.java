package com.patientpal.backend.post.service;

import com.patientpal.backend.common.exception.EntityNotFoundException;
import com.patientpal.backend.common.exception.ErrorCode;
import com.patientpal.backend.member.domain.Member;
import com.patientpal.backend.post.domain.Post;
import com.patientpal.backend.post.domain.PostType;
import com.patientpal.backend.post.dto.PostCreateRequest;
import com.patientpal.backend.post.dto.PostUpdateRequest;
import com.patientpal.backend.post.repository.PostRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
@RequiredArgsConstructor
@Transactional
public class PostService {

    private final PostRepository postRepository;

    public Page <Post> getFreePostList(int page) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createdDate"));
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        // page : 조회할 페이지의 번호
        // 10 : 한 페이지에 보여 줄 게시물의 개수
        return this.postRepository.findAllByPostType(PostType.FREE, pageable);
    }

    public Page <Post> getNoticePostList(int page) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createdDate"));
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        return this.postRepository.findAllByPostType(PostType.NOTICE, pageable);
    }

    @Transactional(readOnly = true)
    public Post getPost(Long id) {
        return findPost(id);
    }

    @Transactional
    public Post createNoticePost(Member member, PostCreateRequest createRequest) {
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

    @Transactional
    public int updateView(Long id){
        return postRepository.updateView(id);
    }
}
