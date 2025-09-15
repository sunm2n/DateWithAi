package com.datewithai.domain.story.dto;

import com.datewithai.domain.character.entity.Character;
import com.datewithai.domain.story.entity.Story;

public class StoryCreateRequestDto {
    private Long characterId;
    private String title;
    private String content;
    
    public StoryCreateRequestDto() {}
    
    public Story toEntity(Character character) {
        Story story = new Story();
        story.setCharacter(character);
        story.setTitle(this.title);
        story.setContent(this.content);
        return story;
    }
    
    public Long getCharacterId() { return characterId; }
    public void setCharacterId(Long characterId) { this.characterId = characterId; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}