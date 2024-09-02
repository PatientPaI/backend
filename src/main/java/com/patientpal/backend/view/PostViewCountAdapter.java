package com.patientpal.backend.view;

import com.patientpal.backend.post.repository.PostRepository;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostViewCountAdapter {

    private final PostRepository postRepository;

    @Transactional
    public void batchUpdatePostViewCounts(Map<Long, Long> viewCounts) {
        viewCounts.forEach((key, value) -> {
            final var post = postRepository.findById(key)
                    .orElse(null);

            if (post == null) {
                return;
            }
            postRepository.incrementViewCountsById(post.getId(), value);
        });
    }
}
