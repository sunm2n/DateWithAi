package com.datewithai.global.controller;

import com.datewithai.domain.character.service.CharacterService;
import com.datewithai.domain.character.repository.CharacterRepository;
import com.datewithai.domain.character.entity.Character;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
public class HomeController {
    
    private final CharacterService characterService;
    private final CharacterRepository characterRepository;
    
    @Autowired
    public HomeController(CharacterService characterService, CharacterRepository characterRepository) {
        this.characterService = characterService;
        this.characterRepository = characterRepository;
    }
    
    @GetMapping("/")
    public String home(Model model) {
        try {
            List<Character> characters = characterRepository.findAllByOrderByCreatedAtDesc();
            model.addAttribute("characters", characters);
            return "index";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("characters", new java.util.ArrayList<>());
            return "index";
        }
    }
    
    @GetMapping("/about")
    public String about() {
        return "about";
    }
}