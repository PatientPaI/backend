package com.patientpal.backend.socket.publisher.subscriber;

import com.patientpal.backend.chat.dto.MessageCreateRequest;
import com.patientpal.backend.chat.dto.SocketDirectSubscribeMessage;
import com.patientpal.backend.chat.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class SocketSubscriber {

    private final MessageService messageService;

    @MessageMapping("/chat/{chatId}")
    public void subscribe(@DestinationVariable("chatId") Long chatId, @Payload SocketDirectSubscribeMessage message) {
        log.info("content: " + message.getContent());
        var request = MessageCreateRequest.builder()
                .messageType(message.getMessageType())
                .content(message.getContent())
                .senderId(message.getSenderId())
                .chatId(chatId)
                .build();

        messageService.createMessage(request);
    }
}
