package com.datewithai.domain.chat.dto;

import com.datewithai.domain.chat.entity.ChatMessage;

import java.time.LocalDateTime;

public class ChatHistoryDto {
    private String message;
    private String messageType;
    private LocalDateTime createdAt;
    
    public ChatHistoryDto() {}
    
    public ChatHistoryDto(String message, String messageType, LocalDateTime createdAt) {
        this.message = message;
        this.messageType = messageType;
        this.createdAt = createdAt;
    }
    
    public static ChatHistoryDto fromEntity(ChatMessage chatMessage) {
        return new ChatHistoryDto(
            chatMessage.getMessage(),
            chatMessage.getMessageType().toString(),
            chatMessage.getCreatedAt()
        );
    }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public String getMessageType() { return messageType; }
    public void setMessageType(String messageType) { this.messageType = messageType; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}