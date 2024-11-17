package com.messaging_service.Service;

import org.springframework.stereotype.Service;

import com.messaging_service.Model.Message;
import com.messaging_service.Repository.MessageRepository;

@Service
public class MessageService {

    private final MessageRepository messageRepository;

    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public Message saveMessage(Message message) {
        return messageRepository.save(message);
    }
}

