package com.patientpal.backend.chat.dto;

import com.patientpal.backend.chat.domain.Message;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class MessageCreateResponse {

    private String content;
    private Long chatId;
    private Long senderId;
    private MessageType messageType;
    private Long messageId;

    public MessageCreateResponse(Message message) {
        this.content = message.getContent();
        this.chatId = message.getChatId();
        this.senderId = message.getSenderId();
        this.messageType = message.getMessageType();
        this.messageId = message.getId();
    }
}
