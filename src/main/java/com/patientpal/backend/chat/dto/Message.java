package com.patientpal.backend.chat.dto;

import com.patientpal.backend.common.BaseTimeEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Message extends BaseTimeEntity {
    private MessageType messageType;
    private String content;
    private Long senderId;
    private String chatId;
}
