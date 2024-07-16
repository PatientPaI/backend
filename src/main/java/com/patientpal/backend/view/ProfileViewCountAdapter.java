package com.patientpal.backend.view;

import com.patientpal.backend.member.repository.MemberRepository;
import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ProfileViewCountAdapter {

    private final MemberRepository memberRepository;
    private final EntityManager em;

    public void batchUpdateViewCounts(Map<Long, Long> viewCounts) {
        List<Map.Entry<Long, Long>> entries = new ArrayList<>(viewCounts.entrySet());

        for (int i = 0; i < entries.size(); i += 20) {
            List<Entry<Long, Long>> chunk = entries.subList(i, Math.min(entries.size(), i + 20));

            chunk.forEach(entry -> {
                memberRepository.findById(entry.getKey()).ifPresent(
                        entity -> {
                            entity.changeViewCount(entry.getValue());
                        }
                );
            });
        }
        em.flush();
        em.clear();
    }

}
