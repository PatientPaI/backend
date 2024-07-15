package com.patientpal.backend.post.repository;

import com.patientpal.backend.post.domain.Post;
import com.patientpal.backend.post.domain.PostType;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {

    Page<Post> findAllByPostType(PostType postType, Pageable pageable);

    Optional<Post> findByIdAndMemberId(long postId, long memberId);

    Page<Post> findAll(Pageable pageable);

}
