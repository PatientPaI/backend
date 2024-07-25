package com.patientpal.backend.chat.service;

import com.patientpal.backend.chat.domain.Chat;
import com.patientpal.backend.chat.dto.ChatCreateRequest;
import com.patientpal.backend.chat.repository.ChatRepository;
import com.patientpal.backend.common.exception.EntityNotFoundException;
import com.patientpal.backend.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;

    @Transactional(readOnly = true)
    public Chat getChat(Long chatId) {
        return findChat(chatId);
    }

    private Chat findChat(Long chatId) {
        return chatRepository.findById(chatId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.CHAT_NOT_FOUND));
    }

    @Transactional
    public Chat create(ChatCreateRequest request) {
        return chatRepository.save(request.toEntity());
    }
}

