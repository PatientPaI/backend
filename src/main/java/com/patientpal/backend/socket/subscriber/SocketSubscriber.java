package com.patientpal.backend.socket.subscriber;

import com.patientpal.backend.chat.dto.MessageCreateRequest;
import com.patientpal.backend.chat.dto.MessageType;
import com.patientpal.backend.socket.dto.SocketDirectMessage;
import com.patientpal.backend.chat.service.MessageService;
import com.patientpal.backend.socket.publisher.SocketPublisher;
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
    private final SocketPublisher socketPublisher;

    @MessageMapping("/chat/{chatId}")
    public void subscribe(@DestinationVariable("chatId") Long chatId, @Payload SocketDirectMessage message) {
        log.info("content: " + message.getContent());
        var request = MessageCreateRequest.builder()
                .messageId(message.getMessageId())
                .messageType(MessageType.CHAT)
                .content(message.getContent())
                .senderId(message.getMemberId())
                .chatId(chatId)
                .build();

        messageService.createMessage(request);

        var directMessage = SocketDirectMessage.builder()
                .messageId(message.getMessageId())
                .memberId(message.getMemberId())
                .createdAt(message.getCreatedAt())
                .profileImageUrl(message.getProfileImageUrl())
                .userName(message.getName())
                .name(message.getName())
                .content(message.getContent())
                .build();

        socketPublisher.sendMessage(chatId, directMessage);
    }
}
