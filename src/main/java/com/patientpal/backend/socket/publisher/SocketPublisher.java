package com.patientpal.backend.socket.publisher;

import com.patientpal.backend.chat.dto.SocketDirectMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@Slf4j
@RequiredArgsConstructor
public class SocketPublisher {

    private final SimpMessagingTemplate simpMessagingTemplate;

    public void sendMessage(@DestinationVariable("chatId") Long chatId, @Payload SocketDirectMessage message) {
        log.info("send message. chatId: "+ chatId + " content: "+ message.getContent());
        simpMessagingTemplate.convertAndSend("/topic/directChat/" + chatId, message);
    }
}
