package com.datewithai.domain.user.dto;

public class UserUpdateRequestDto {
    private String username;
    private String email;
    
    public UserUpdateRequestDto() {}
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}