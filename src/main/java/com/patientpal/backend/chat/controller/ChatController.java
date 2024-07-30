package com.patientpal.backend.chat.controller;


import com.patientpal.backend.chat.domain.Chat;
import com.patientpal.backend.chat.dto.ChatCreateRequest;
import com.patientpal.backend.chat.dto.ChatResponse;
import com.patientpal.backend.chat.service.ChatService;
import com.patientpal.backend.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/chats")
@RequiredArgsConstructor
public class ChatController {

    private final MemberService memberService;
    private final ChatService chatService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ChatResponse create(@RequestBody ChatCreateRequest request, @AuthenticationPrincipal UserDetails user) {
        Chat chat = chatService.create(request);
        return new ChatResponse(chat);
    }

    @GetMapping("/{id}")
    public ChatResponse detail(@PathVariable Long id, @AuthenticationPrincipal UserDetails user) {
        Chat chat = chatService.getChat(id);
        return new ChatResponse(chat);
    }

    @GetMapping
    public List<ChatResponse> list(@AuthenticationPrincipal UserDetails user) {
        Long memberId = memberService.getUserByUsername(user.getUsername()).getId();
        List<Chat> chats = chatService.getMembersChat(memberId);
        return chats.stream()
                .map(ChatResponse::new)
                .collect(Collectors.toList());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void leave(@PathVariable Long id, @AuthenticationPrincipal UserDetails user) {
        Long memberId = memberService.getUserByUsername(user.getUsername()).getId();
        chatService.leaveChat(id, memberId);
    }
}
