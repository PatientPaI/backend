package com.patientpal.backend.socket.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class SocketDirectMessage {

    private String messageId;
    private Long memberId;
    private String userName;
    private String name;
    private String profileImageUrl;
    private LocalDateTime createdAt;
    private String content;
}
