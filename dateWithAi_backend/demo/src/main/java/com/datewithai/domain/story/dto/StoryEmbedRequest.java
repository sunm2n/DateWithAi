package com.datewithai.domain.story.dto;

public class StoryEmbedRequest {
    private Long storyId;
    private String content;
    
    public StoryEmbedRequest() {}
    
    public StoryEmbedRequest(Long storyId, String content) {
        this.storyId = storyId;
        this.content = content;
    }
    
    public Long getStoryId() { return storyId; }
    public void setStoryId(Long storyId) { this.storyId = storyId; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}