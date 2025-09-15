package com.datewithai.domain.chat.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class AiChatResponse {
    private String response;
    
    @JsonProperty("context_used")
    private List<Object> contextUsed;
    
    @JsonProperty("similarity_scores")
    private List<Double> similarityScores;
    
    // Spring 내부 사용을 위한 필드들
    private String sessionId;
    private boolean success = true;
    private String error;
    
    public AiChatResponse() {}
    
    public AiChatResponse(String response) {
        this.response = response;
        this.success = true;
    }
    
    public AiChatResponse(String error, boolean isError) {
        if (isError) {
            this.error = error;
            this.success = false;
        } else {
            this.response = error;
            this.success = true;
        }
    }
    
    public String getResponse() { return response; }
    public void setResponse(String response) { this.response = response; }
    
    public List<Object> getContextUsed() { return contextUsed; }
    public void setContextUsed(List<Object> contextUsed) { this.contextUsed = contextUsed; }
    
    public List<Double> getSimilarityScores() { return similarityScores; }
    public void setSimilarityScores(List<Double> similarityScores) { this.similarityScores = similarityScores; }
    
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    
    public String getError() { return error; }
    public void setError(String error) { this.error = error; }
}