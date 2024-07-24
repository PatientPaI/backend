package com.patientpal.backend.chat.dto;

import com.patientpal.backend.member.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class SocketDirectMessage {
    private Member member;
    private String content;
}
