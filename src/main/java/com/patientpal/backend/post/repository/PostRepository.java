package com.patientpal.backend.post.repository;

import com.patientpal.backend.post.domain.Post;
import com.patientpal.backend.post.domain.PostType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findAllByPostType(PostType postType);

    Optional<Post> findByIdAndMemberId(long postId, long memberId);

    @Query("delete from Post p where p.id = :postId and p.member.id = :memberId")
    int deleteByIdAndMemberId(@Param("postId") Long postId, @Param("memberId") Long memberId);
}
