package com.datewithai.domain.character.dto;

import com.datewithai.domain.character.entity.Character;

public class CharacterCreateRequestDto {
    private String name;
    private String description;
    private String personality;
    private String speakingStyle;
    private String profileImageUrl;
    private Integer age;
    private String occupation;
    private String background;
    
    public CharacterCreateRequestDto() {}
    
    public Character toEntity() {
        Character character = new Character();
        character.setName(this.name);
        character.setDescription(this.description);
        character.setPersonality(this.personality);
        character.setSpeakingStyle(this.speakingStyle);
        character.setProfileImageUrl(this.profileImageUrl);
        character.setAge(this.age);
        character.setOccupation(this.occupation);
        character.setBackground(this.background);
        return character;
    }
    
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