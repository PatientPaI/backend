package com.patientpal.backend.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class SocketDirectSubscribeMessage {

    private String content;
    private Long senderId;
    private String userName;
    private String name;
    private String profileImageUrl;
    private LocalDateTime createdAt;
    private MessageType messageType;
}
