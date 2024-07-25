package com.patientpal.backend.chat.controller;


import com.patientpal.backend.chat.domain.Chat;
import com.patientpal.backend.chat.dto.ChatCreateRequest;
import com.patientpal.backend.chat.dto.ChatResponse;
import com.patientpal.backend.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/chats")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ChatResponse create(@RequestBody ChatCreateRequest request) {
        Chat chat = chatService.create(request);
        return new ChatResponse(chat);
    }
}
