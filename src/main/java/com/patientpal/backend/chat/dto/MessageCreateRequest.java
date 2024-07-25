package com.patientpal.backend.chat.dto;

import com.patientpal.backend.chat.domain.Message;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class MessageCreateRequest {

    private String content;
    private MessageType messageType;
    private Long chatId;
    private Long senderId;

    public Message toEntity() {
       return Message.builder()
               .chatId(this.chatId)
               .senderId(this.senderId)
               .messageType(this.messageType)
               .content(this.content)
               .build();
    }
}
