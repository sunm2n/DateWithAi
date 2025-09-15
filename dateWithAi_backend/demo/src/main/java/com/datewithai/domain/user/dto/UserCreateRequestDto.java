package com.datewithai.domain.user.dto;

import com.datewithai.domain.user.entity.User;

public class UserCreateRequestDto {
    private String username;
    private String password;
    private String email;
    
    public UserCreateRequestDto() {}
    
    public User toEntity() {
        User user = new User();
        user.setUsername(this.username);
        user.setPassword(this.password);
        user.setEmail(this.email);
        return user;
    }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}