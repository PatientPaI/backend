package com.patientpal.backend.chat.dto;

import com.patientpal.backend.chat.domain.Chat;
import com.patientpal.backend.chat.domain.ChatType;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ChatResponse {

    private Long chatId;
    private ChatType chatType;
    private List<Long> managerIds;

    public ChatResponse(Chat chat) {
        this.chatId = chat.getId();
        this.chatType = chat.getChatType();
        this.managerIds = chat.getMemberIds();
    }
}
