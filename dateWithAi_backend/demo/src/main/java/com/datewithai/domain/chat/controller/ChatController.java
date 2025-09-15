package com.datewithai.domain.chat.controller;

import com.datewithai.domain.chat.dto.AiChatResponse;
import com.datewithai.domain.chat.dto.ChatHistoryDto;
import com.datewithai.domain.chat.service.ChatService;
import com.datewithai.domain.character.service.CharacterService;
import com.datewithai.domain.character.repository.CharacterRepository;
import com.datewithai.domain.character.entity.Character;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/chat")
public class ChatController {
    
    private final ChatService chatService;
    private final CharacterService characterService;
    private final CharacterRepository characterRepository;
    
    @Autowired
    public ChatController(ChatService chatService, CharacterService characterService, CharacterRepository characterRepository) {
        this.chatService = chatService;
        this.characterService = characterService;
        this.characterRepository = characterRepository;
    }
    
    
    @GetMapping("/{characterId}")
    public String chatRoom(@PathVariable Long characterId, Model model) {
        Optional<Character> character = characterRepository.findById(characterId);
        if (character.isEmpty()) {
            return "redirect:/";
        }
        
        model.addAttribute("character", character.get());
        return "chat/room";
    }
    
    @ResponseBody
    @PostMapping("/api/send")
    public Mono<ResponseEntity<AiChatResponse>> sendMessage(
            @RequestParam Long characterId,
            @RequestParam String message) {
        
        return chatService.sendSimpleMessage(characterId, message)
                .map(ResponseEntity::ok)
                .onErrorReturn(ResponseEntity.internalServerError().build());
    }
    
}