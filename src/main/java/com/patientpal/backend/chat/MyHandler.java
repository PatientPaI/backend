package com.patientpal.backend.chat;

import com.patientpal.backend.chat.domain.Message;
import com.patientpal.backend.chat.repository.MessageRepository;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

public class MyHandler extends TextWebSocketHandler {
    //새로운 최초 연결시 call
    private final List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        //텍스트 메세지 수신시 호출
        Message newMessage = new Message(message.getPayload());
        MessageRepository.save(newMessage);

        for (WebSocketSession webSocketSession : sessions) {
            // 모든 연결된 세션에 메시지를 브로드캐스트
            if (webSocketSession.isOpen() && !Objects.equals(session.getId(), webSocketSession.getId())) {
                webSocketSession.sendMessage(message);
            }
        }
    }

    //웹소켓 종료시 call
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
    }
}
