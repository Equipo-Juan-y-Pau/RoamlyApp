package com.messaging_service.Controller;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.messaging_service.Handler.ConversationWebSocketHandler;
import com.messaging_service.Model.Conversation;
import com.messaging_service.Model.Message;
import com.messaging_service.Repository.ConversationRepository;
import com.messaging_service.Repository.MessageRepository;

@RestController
@RequestMapping("/message")
public class MessageController {

    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;
    private final ConversationWebSocketHandler conversationWebSocketHandler;

    public MessageController(
            MessageRepository messageRepository,
            ConversationRepository conversationRepository,
            ConversationWebSocketHandler conversationWebSocketHandler) {
        this.messageRepository = messageRepository;
        this.conversationRepository = conversationRepository;
        this.conversationWebSocketHandler = conversationWebSocketHandler;
    }

    // Enviar un mensaje a una conversación
    @PostMapping("/send")
    public ResponseEntity<String> sendMessage(@RequestBody Message message) {
        // Verificar si la conversación asociada existe
        Optional<Conversation> optionalConversation = conversationRepository.findById(message.getConversationId());

        if (optionalConversation.isPresent()) {
            Conversation conversation = optionalConversation.get();

            if(message.getRemitenteId() == null || message.getRemitenteId().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El remitente es requerido.");
            }
            else if(conversation.getParticipants().stream().noneMatch(p -> p.equals(message.getRemitenteId()))) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El remitente no es parte de la conversación.");
            }
            // Guardar el mensaje en la base de datos
            message.setFechaEnvio(LocalDateTime.now().toString());
            message.setEstado("ENVIADO");
            message.setRemitenteId(message.getRemitenteId());
            messageRepository.save(message);

            // Actualizar la información de la conversación
            conversation.setLastMessage(message.getContenido());
            conversation.setLastMessageAt(LocalDateTime.now());
            conversationRepository.save(conversation);

            // Notificar a los clientes conectados (solo a los participantes de la conversación)
            conversationWebSocketHandler.notifyConversationParticipants(conversation.getParticipants(), message);

            return ResponseEntity.ok("Mensaje enviado y notificado");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("La conversación no existe.");
        }
    }
}
