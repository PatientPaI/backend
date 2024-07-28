package com.patientpal.backend.chat.controller;

import com.patientpal.backend.chat.dto.MessageCreateRequest;
import com.patientpal.backend.chat.dto.MessageCreateResponse;
import com.patientpal.backend.chat.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/messages")
public class MessageController {

    private final MessageService messageService;

    @PostMapping
    public MessageCreateResponse create(@RequestBody MessageCreateRequest request, @AuthenticationPrincipal UserDetails user) {
        var message = messageService.createAndSendMessage(request);
        return new MessageCreateResponse(message);
    }
}
