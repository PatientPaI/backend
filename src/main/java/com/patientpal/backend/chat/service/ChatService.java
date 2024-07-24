package com.patientpal.backend.chat.service;

import com.patientpal.backend.chat.domain.Chat;
import com.patientpal.backend.chat.repository.ChatRepository;
import com.patientpal.backend.common.exception.EntityNotFoundException;
import com.patientpal.backend.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;


    public Chat getChat(Long chatId) {
        return findChat(chatId);
    }


    private Chat findChat(Long chatId) {
        return chatRepository.findById(chatId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.CHAT_NOT_FOUND));
    }
}

