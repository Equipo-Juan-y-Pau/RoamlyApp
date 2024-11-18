package com.messaging_service.Handler;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.messaging_service.Model.Message;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ConversationWebSocketHandler extends TextWebSocketHandler {

    // Mapa para asociar usuarios con sus sesiones WebSocket
    private final Map<String, WebSocketSession> userToSessionMap = new ConcurrentHashMap<>();

    // Mapa para asociar sesiones WebSocket con sus usuarios
    private final Map<WebSocketSession, String> sessionToUserMap = new ConcurrentHashMap<>();

    /**
     * Método para añadir una sesión de WebSocket asociada a un usuario.
     * @param userId El identificador único del usuario.
     * @param session La sesión WebSocket establecida.
     */
    public void addSession(String userId, WebSocketSession session) {
        userToSessionMap.put(userId, session);
        sessionToUserMap.put(session, userId);
        System.out.println("Nueva sesión añadida para el usuario: " + userId);
    }

    /**
     * Método para eliminar una sesión de WebSocket cuando un usuario se desconecta.
     * @param session La sesión WebSocket que debe eliminarse.
     */
    public void removeSession(WebSocketSession session) {
        String userId = sessionToUserMap.remove(session);
        if (userId != null) {
            userToSessionMap.remove(userId);
            System.out.println("Sesión eliminada para el usuario: " + userId);
        }
    }

    /**
     * Sobrescribe el método para manejar cuando se establece una conexión WebSocket.
     * Aquí se registra la sesión del usuario.
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // Extraer el userId de la URI
        String query = session.getUri().getQuery();
        if (query != null && query.startsWith("userId=")) {
            String userId = query.split("=")[1]; // Extrae el userId de la query string
            addSession(userId, session);
            System.out.println("Nueva conexión establecida para el usuario: " + userId);
        } else {
            System.out.println("No se pudo extraer el userId de la URI");
        }
    }

    /**
     * Sobrescribe el método para manejar cuando se cierra una conexión WebSocket.
     * Aquí se elimina la sesión del usuario.
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        removeSession(session);
        System.out.println("Conexión cerrada: " + session.getId());
    }

    /**
     * Método para notificar a todos los participantes de una conversación.
     * @param participants La lista de IDs de los usuarios que participan en la conversación.
     * @param message El mensaje que se debe enviar.
     */
    public void notifyConversationParticipants(List<String> participants, Message message) {
        participants.forEach(userId -> {
            WebSocketSession session = userToSessionMap.get(userId);
            if (session != null && session.isOpen()) {
                try {
                    String payload = new ObjectMapper().writeValueAsString(message);
                    session.sendMessage(new TextMessage(payload));
                    System.out.println("Mensaje enviado a usuario: " + userId);
                } catch (IOException e) {
                    System.err.println("Error al enviar mensaje a usuario: " + userId);
                    e.printStackTrace();
                }
            } else {
                System.out.println("No se encontró una sesión activa para el usuario: " + userId);
            }
        });
    }
}
