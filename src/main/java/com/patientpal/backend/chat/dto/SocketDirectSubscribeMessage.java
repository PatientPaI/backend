package com.patientpal.backend.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class SocketDirectSubscribeMessage {

    private String content;
    private Long senderId;
    private MessageType messageType;
}
