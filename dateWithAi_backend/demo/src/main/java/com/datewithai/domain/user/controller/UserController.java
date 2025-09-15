package com.datewithai.domain.user.controller;

import com.datewithai.domain.user.service.UserService;
import com.datewithai.domain.user.entity.User;
import com.datewithai.domain.user.dto.UserResponseDto;
import com.datewithai.domain.user.dto.UserCreateRequestDto;
import com.datewithai.domain.user.dto.UserUpdateRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/users")
public class UserController {
    
    private final UserService userService;
    
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }
    
    @GetMapping
    public String userList(Model model) {
        List<User> users = userService.findAll();
        model.addAttribute("users", users);
        return "user/list";
    }
    
    @GetMapping("/{id}")
    public String userDetail(@PathVariable Long id, Model model) {
        Optional<User> user = userService.findById(id);
        if (user.isEmpty()) {
            return "redirect:/users";
        }
        model.addAttribute("user", user.get());
        return "user/detail";
    }
    
    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("user", new User());
        return "user/register";
    }
    
    @PostMapping("/register")
    public String register(@ModelAttribute User user) {
        try {
            userService.createUser(user.getUsername(), user.getPassword(), user.getEmail());
            return "redirect:/users/login";
        } catch (IllegalArgumentException e) {
            return "redirect:/users/register?error=" + e.getMessage();
        }
    }
    
    @GetMapping("/login")
    public String loginForm() {
        return "user/login";
    }
    
    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password) {
        if (userService.validateUser(username, password)) {
            return "redirect:/chat";
        } else {
            return "redirect:/users/login?error=Invalid credentials";
        }
    }
    
    @ResponseBody
    @GetMapping("/api")
    public ResponseEntity<List<UserResponseDto>> getUsers() {
        List<User> users = userService.findAll();
        List<UserResponseDto> userDtos = users.stream()
                .map(UserResponseDto::fromEntity)
                .toList();
        return ResponseEntity.ok(userDtos);
    }
    
    @ResponseBody
    @GetMapping("/api/{id}")
    public ResponseEntity<UserResponseDto> getUser(@PathVariable Long id) {
        Optional<User> user = userService.findById(id);
        return user.map(u -> ResponseEntity.ok(UserResponseDto.fromEntity(u)))
                  .orElse(ResponseEntity.notFound().build());
    }
    
    @ResponseBody
    @PostMapping("/api")
    public ResponseEntity<UserResponseDto> createUserApi(@RequestBody UserCreateRequestDto requestDto) {
        try {
            User savedUser = userService.createUser(requestDto.getUsername(), requestDto.getPassword(), requestDto.getEmail());
            return ResponseEntity.ok(UserResponseDto.fromEntity(savedUser));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @ResponseBody
    @PutMapping("/api/{id}")
    public ResponseEntity<UserResponseDto> updateUserApi(@PathVariable Long id, @RequestBody UserUpdateRequestDto requestDto) {
        try {
            User updatedUser = userService.updateUser(id, requestDto.getUsername(), requestDto.getEmail());
            return ResponseEntity.ok(UserResponseDto.fromEntity(updatedUser));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @ResponseBody
    @DeleteMapping("/api/{id}")
    public ResponseEntity<Void> deleteUserApi(@PathVariable Long id) {
        if (!userService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}