package com.patientpal.backend.chat.service;

import com.patientpal.backend.chat.domain.Message;
import com.patientpal.backend.chat.dto.MessageCreateRequest;
import com.patientpal.backend.chat.dto.MessageRequestParam;
import com.patientpal.backend.chat.dto.MessageType;
import com.patientpal.backend.chat.repository.MessageRepository;
import com.patientpal.backend.member.domain.Member;
import com.patientpal.backend.member.service.MemberService;
import com.patientpal.backend.socket.dto.SocketDirectMessage;
import com.patientpal.backend.socket.publisher.SocketPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final ChatService chatService;
    private final MemberService memberService;
    private final SocketPublisher socketPublisher;
    private final MessageRepository messageRepository;

    public Message createAndSendMessage(MessageCreateRequest request) {
        final Long chatId = request.getChatId();
        final String content = request.getContent();
        final Long senderId = request.getSenderId();

        chatService.getChat(chatId);

        Message message = Message.builder()
                .messageType(MessageType.CHAT)
                .content(content)
                .senderId(senderId)
                .chatId(chatId)
                .build();

        var entity = messageRepository.save(message);

        Member member = memberService.findMember(senderId);
        var directMessage = SocketDirectMessage.builder()
                .memberId(member.getId())
                .createdAt(entity.getCreatedDate())
                .profileImageUrl(member.getProfileImageUrl())
                .userName(member.getUsername())
                .name(member.getName())
                .content(content)
                .build();

        socketPublisher.sendMessage(chatId, directMessage);

        return entity;
    }

    public Message createMessage(MessageCreateRequest request) {
        return messageRepository.save(request.toEntity());
    }

    public Page<Message> getMessages(MessageRequestParam param) {
        chatRepository.findById(param.getChatId());

        Sort sort = Sort.by(Sort.Direction.DESC, "createdDate");
        Pageable pageable = PageRequest.of(param.getPage(), param.getSize(), sort);
        return messageRepository.findAllByChatId(param.getChatId(), pageable);
    }
}
