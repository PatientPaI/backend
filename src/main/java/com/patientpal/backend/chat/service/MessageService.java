package com.patientpal.backend.chat.service;

import com.patientpal.backend.chat.domain.Message;
import com.patientpal.backend.chat.dto.MessageCreateRequest;
import com.patientpal.backend.chat.dto.MessageType;
import com.patientpal.backend.chat.dto.SocketDirectMessage;
import com.patientpal.backend.chat.repository.ChatRepository;
import com.patientpal.backend.chat.repository.MessageRepository;
import com.patientpal.backend.member.domain.Member;
import com.patientpal.backend.member.service.MemberService;
import com.patientpal.backend.socket.publisher.SocketPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageService {

     private final ChatService chatService;
    private final ChatRepository chatRepository;
    private final MemberService memberService;
    private final SocketPublisher socketPublisher;
    private final MessageRepository messageRepository;

    public Message createAndSendMessage(MessageCreateRequest request) {
        final Long chatId = request.getChatId();
        final String content = request.getContent();
        final Long senderId = request.getSenderId();

        chatService.getChat(chatId);

        Member member = memberService.findMember(senderId);
        var directMessage = SocketDirectMessage.builder()
//                .member(member)
                .content(content)
                .build();

        Message message = Message.builder()
                .messageType(MessageType.CHAT)
                .content(content)
                .senderId(senderId)
                .chatId(chatId)
                .build();

        var entity = messageRepository.save(message);

        socketPublisher.sendMessage(chatId, directMessage);

        return entity;
    }

    public Message createMessage(MessageCreateRequest request) {
        return messageRepository.save(request.toEntity());
    }
}
