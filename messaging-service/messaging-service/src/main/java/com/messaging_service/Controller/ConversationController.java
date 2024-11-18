package com.messaging_service.Controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.messaging_service.Dto.ConversationRequest;
import com.messaging_service.Model.Conversation;
import com.messaging_service.Model.Group;
import com.messaging_service.Repository.ConversationRepository;
import com.messaging_service.Repository.GroupRepository;

@RestController
@RequestMapping("/conversation")
public class ConversationController {
    private final ConversationRepository conversationRepository;
    private final GroupRepository groupRepository; // Repositorio del grupo

    public ConversationController(ConversationRepository conversationRepository, GroupRepository groupRepository) {
        this.conversationRepository = conversationRepository;
        this.groupRepository = groupRepository;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createConversation(@RequestBody ConversationRequest request) {
        List<String> participants = request.getParticipants();

        // Validar si es una conversación directa y ya existe
        if (participants.size() == 2) {
            Optional<Conversation> existingConversation = conversationRepository.findDirectConversationByParticipants(participants.get(0), participants.get(1));
            if (existingConversation.isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Ya existe una conversación directa entre estos usuarios.");
            }
        }

        // Crear la conversación
        Conversation conversation = new Conversation();
        conversation.setParticipants(participants);
        conversation.setCreatedAt(LocalDateTime.now());
        conversation.setLastMessage(null); // Sin mensaje inicial
        conversation.setLastMessageAt(null); // Sin fecha de último mensaje

        // Si es un grupo, configurar el nombre del grupo y el administrador
        if (participants.size() > 2) {
            conversation.setType("GRUPO");

            Group group = new Group();
            group.setName(request.getGroupName() != null ? request.getGroupName() : "Grupo de " + participants.size() + " personas");
            group.setAdminId(participants.get(0)); // El primer participante es el admin

            // Guardar el grupo en el repositorio de grupos
            Group savedGroup = groupRepository.save(group);
            conversation.setGroupId(savedGroup.getId()); // Usar el ID del grupo creado
        } else {
            conversation.setType("DIRECTA");
        }

        // Guardar en la base de datos de conversaciones
        Conversation savedConversation = conversationRepository.save(conversation);

        // Devolver la conversación creada
        return ResponseEntity.ok(savedConversation);
    }
}
