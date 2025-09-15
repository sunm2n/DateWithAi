package com.datewithai.domain.story.dto;

public class VectorSearchRequest {
    private String embedding;
    private Long characterId;
    private int limit = 5;
    
    public VectorSearchRequest() {}
    
    public VectorSearchRequest(String embedding, Long characterId, int limit) {
        this.embedding = embedding;
        this.characterId = characterId;
        this.limit = limit;
    }
    
    public String getEmbedding() { return embedding; }
    public void setEmbedding(String embedding) { this.embedding = embedding; }
    
    public Long getCharacterId() { return characterId; }
    public void setCharacterId(Long characterId) { this.characterId = characterId; }
    
    public int getLimit() { return limit; }
    public void setLimit(int limit) { this.limit = limit; }
}