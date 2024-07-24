package com.patientpal.backend.chat.controller;

import com.patientpal.backend.chat.dto.WriteMessageRequest;
import com.patientpal.backend.chat.service.MessageService;
import lombok.RequiredArgsConstructor;
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
    public void create(@RequestBody WriteMessageRequest message) {
        messageService.send(message);
    }


}
