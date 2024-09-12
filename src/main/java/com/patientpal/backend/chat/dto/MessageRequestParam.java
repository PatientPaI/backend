package com.patientpal.backend.chat.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor
@Getter
@Setter
public class MessageRequestParam {

    private Long chatId;
    private int page;
    private int size;
}
