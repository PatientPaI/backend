package com.patientpal.backend.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class WriteMessageRequest {

    private String content;
    private Long chatId;
    private Long senderId;
}
