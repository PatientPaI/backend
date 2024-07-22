package com.patientpal.backend.chat;

import com.patientpal.backend.chat.dto.ChatMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {

    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public ChatMessage greeting(@Payload ChatMessage message) throws Exception {
        return new ChatMessage("Hello, " + message.getContent());
    }
}
