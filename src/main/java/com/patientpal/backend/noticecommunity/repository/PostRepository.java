package com.patientpal.backend.noticecommunity.repository;

import com.patientpal.backend.noticecommunity.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}
