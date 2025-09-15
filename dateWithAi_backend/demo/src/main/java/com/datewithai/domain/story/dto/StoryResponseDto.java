package com.datewithai.domain.story.dto;

import com.datewithai.domain.story.entity.Story;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public class StoryResponseDto {
    private Long id;
    private Long characterId;
    private String characterName;
    private String title;
    private String content;
    private boolean hasEmbedding;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
    
    public StoryResponseDto() {}
    
    public StoryResponseDto(Long id, Long characterId, String characterName, String title, 
                           String content, boolean hasEmbedding, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.characterId = characterId;
        this.characterName = characterName;
        this.title = title;
        this.content = content;
        this.hasEmbedding = hasEmbedding;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    public static StoryResponseDto fromEntity(Story story) {
        return new StoryResponseDto(
            story.getId(),
            story.getCharacter().getId(),
            story.getCharacter().getName(),
            story.getTitle(),
            story.getContent(),
            story.getEmbeddingVector() != null && !story.getEmbeddingVector().trim().isEmpty(),
            story.getCreatedAt(),
            story.getUpdatedAt()
        );
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getCharacterId() { return characterId; }
    public void setCharacterId(Long characterId) { this.characterId = characterId; }
    
    public String getCharacterName() { return characterName; }
    public void setCharacterName(String characterName) { this.characterName = characterName; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public boolean isHasEmbedding() { return hasEmbedding; }
    public void setHasEmbedding(boolean hasEmbedding) { this.hasEmbedding = hasEmbedding; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}