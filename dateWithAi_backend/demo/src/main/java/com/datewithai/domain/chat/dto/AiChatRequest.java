package com.datewithai.domain.chat.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AiChatRequest {
    private String message;
    
    @JsonProperty("character_id")
    private String characterId;
    
    @JsonProperty("character_info")
    private String characterInfo;
    
    private String emotion;
    
    @JsonProperty("emotion_intensity")
    private Double emotionIntensity = 0.5;
    
    public AiChatRequest() {}
    
    public AiChatRequest(String message, String characterId, String characterInfo) {
        this.message = message;
        this.characterId = characterId;
        this.characterInfo = characterInfo;
    }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public String getCharacterId() { return characterId; }
    public void setCharacterId(String characterId) { this.characterId = characterId; }
    
    public String getCharacterInfo() { return characterInfo; }
    public void setCharacterInfo(String characterInfo) { this.characterInfo = characterInfo; }
    
    public String getEmotion() { return emotion; }
    public void setEmotion(String emotion) { this.emotion = emotion; }
    
    public Double getEmotionIntensity() { return emotionIntensity; }
    public void setEmotionIntensity(Double emotionIntensity) { this.emotionIntensity = emotionIntensity; }
}