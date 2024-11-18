package com.messaging_service.Repository;

import com.messaging_service.Model.Conversation;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ConversationRepository extends MongoRepository<Conversation, String> {

    @Query("{ 'participants': { $all: [?0, ?1] }, 'type': 'DIRECTA' }")
    Optional<Conversation> findDirectConversationByParticipants(String participant1, String participant2);
}

    
