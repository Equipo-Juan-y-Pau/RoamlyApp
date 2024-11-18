package com.messaging_service.Repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.messaging_service.Model.Group;

@Repository
public interface GroupRepository extends MongoRepository<Group, String> {
} 
