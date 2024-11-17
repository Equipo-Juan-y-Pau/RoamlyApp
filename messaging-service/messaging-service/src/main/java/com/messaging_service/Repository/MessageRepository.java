package com.messaging_service.Repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.messaging_service.Model.Message;

public interface MessageRepository extends MongoRepository<Message, String> {
}

