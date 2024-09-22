package com.patientpal.backend.post.repository;

import com.patientpal.backend.post.domain.Post;
import com.patientpal.backend.post.domain.PostType;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Long> {

    Optional<Post> findPostByIdAndPostType(long id, PostType postType);

    Page<Post> findAllByPostType(PostType postType, Pageable pageable);

    Optional<Post> findByIdAndMemberId(long postId, long memberId);

    Page<Post> findAll(Pageable pageable);

    @Modifying(flushAutomatically = true)
    @Query("UPDATE Post p SET p.views = p.views + :increment WHERE p.id = :id")
    void incrementViewCountsById(@Param("id") Long postId, @Param("increment") Long increment);
}
