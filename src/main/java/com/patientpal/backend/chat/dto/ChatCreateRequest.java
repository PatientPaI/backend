package com.patientpal.backend.chat.dto;

import com.patientpal.backend.chat.domain.Chat;
import java.util.List;
import com.patientpal.backend.chat.domain.ChatType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ChatCreateRequest {

    private List<Long> memberIds;

    public Chat toEntity() {
        return Chat.builder()
                .memberIds(memberIds)
                .chatType(ChatType.DIRECT)
                .build();
    }
}
