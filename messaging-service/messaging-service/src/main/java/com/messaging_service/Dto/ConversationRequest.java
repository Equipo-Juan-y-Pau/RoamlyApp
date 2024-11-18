package com.messaging_service.Dto;

import java.util.List;

public class ConversationRequest {
    private List<String> participants;
    private String groupName; // Solo se usa si es un grupo

    // Getters y Setters
    public List<String> getParticipants() {
        return participants;
    }

    public void setParticipants(List<String> participants) {
        this.participants = participants;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
