package com.patientpal.backend.socket.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class SocketDirectMessage {

    private Long memberId;
    private String userName;
    private String name;
    private String profileImageUrl;
    private LocalDateTime createdAt;
    private String content;
}
