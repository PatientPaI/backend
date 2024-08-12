package com.patientpal.backend.chat.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.patientpal.backend.chat.domain.Chat;
import com.patientpal.backend.chat.dto.ChatCreateRequest;
import com.patientpal.backend.chat.repository.ChatRepository;
import com.patientpal.backend.common.exception.EntityNotFoundException;
import com.patientpal.backend.common.exception.ErrorCode;
import io.jsonwebtoken.lang.Collections;
import java.util.List;
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
        List<Long> memberIds = request.getMemberIds();

        String memberIdsJson = convertToJson(memberIds);

        List<Chat> chats = chatRepository.findAllByJson(memberIdsJson);

        if(!Collections.isEmpty(chats)) {
            return chats.get(0);
        }

        return chatRepository.save(request.toEntity());
    }

    @Transactional(readOnly = true)
    public List<Chat> getMembersChat(Long memberId) {
        String memberIdJson = String.format("[%d]", memberId);
        return chatRepository.findAllByJson(memberIdJson);
    }

    @Transactional
    public void leaveChat(Long chatId, Long memberId) {
        Chat chat = findChat(chatId);
        if (!chat.alone()) {
            chat.leave(memberId);
            return;
        }

        chatRepository.delete(chat);
    }

    private String convertToJson(List<Long> memberIds) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(memberIds);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Error converting list to JSON", e);
        }
    }
}

