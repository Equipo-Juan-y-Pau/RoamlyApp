package com.messaging_service.Handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private static final Logger logger = LoggerFactory.getLogger(ChatWebSocketHandler.class);

    private final Set<WebSocketSession> sessions = Collections.synchronizedSet(new HashSet<>());

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {

        String clientId = UUID.randomUUID().toString();
        session.getAttributes().put("client-id", clientId);
        sessions.add(session);
        System.out.println("Nueva conexión establecida. ID del cliente: " + clientId);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String clientId = (String) session.getAttributes().get("client-id");
        String messageWithClientInfo = String.format("Cliente %s: %s", clientId, message.getPayload());

        synchronized (sessions) {
            for (WebSocketSession wsSession : sessions) {
                if (wsSession.isOpen()) {
                    wsSession.sendMessage(new TextMessage(messageWithClientInfo));
                }
            }
        }

        System.out.println("Mensaje enviado desde cliente " + clientId + ": " + message.getPayload());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
        String clientId = (String) session.getAttributes().get("client-id");
        System.out.println("Conexión cerrada para cliente: " + clientId);
    }
}

