package com.datewithai.domain.character.dto;

import com.datewithai.domain.character.entity.Character;

public class CharacterResponseDto {
    private Long id;
    private String name;
    private String description;
    private String personality;
    private String speakingStyle;
    private String profileImageUrl;
    private Integer age;
    private String occupation;
    private String background;
    
    public CharacterResponseDto() {}
    
    public CharacterResponseDto(Long id, String name, String description, String personality, 
                               String speakingStyle, String profileImageUrl, Integer age, 
                               String occupation, String background) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.personality = personality;
        this.speakingStyle = speakingStyle;
        this.profileImageUrl = profileImageUrl;
        this.age = age;
        this.occupation = occupation;
        this.background = background;
    }
    
    public static CharacterResponseDto fromEntity(Character character) {
        return new CharacterResponseDto(
            character.getId(),
            character.getName(),
            character.getDescription(),
            character.getPersonality(),
            character.getSpeakingStyle(),
            character.getProfileImageUrl(),
            character.getAge(),
            character.getOccupation(),
            character.getBackground()
        );
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getPersonality() { return personality; }
    public void setPersonality(String personality) { this.personality = personality; }
    
    public String getSpeakingStyle() { return speakingStyle; }
    public void setSpeakingStyle(String speakingStyle) { this.speakingStyle = speakingStyle; }
    
    public String getProfileImageUrl() { return profileImageUrl; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }
    
    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }
    
    public String getOccupation() { return occupation; }
    public void setOccupation(String occupation) { this.occupation = occupation; }
    
    public String getBackground() { return background; }
    public void setBackground(String background) { this.background = background; }
}