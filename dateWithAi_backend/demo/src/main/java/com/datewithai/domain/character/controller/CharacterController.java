package com.datewithai.domain.character.controller;

import com.datewithai.domain.character.service.CharacterService;
import com.datewithai.domain.character.entity.Character;
import com.datewithai.domain.character.dto.CharacterResponseDto;
import com.datewithai.domain.character.dto.CharacterCreateRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/characters")
public class CharacterController {
    
    private final CharacterService characterService;
    
    @Autowired
    public CharacterController(CharacterService characterService) {
        this.characterService = characterService;
    }
    
    @GetMapping
    public String listCharacters(Model model) {
        List<Character> characters = characterService.findAll();
        model.addAttribute("characters", characters);
        return "character/list";
    }
    
    @GetMapping("/{id}")
    public String viewCharacter(@PathVariable Long id, Model model) {
        Optional<Character> character = characterService.findById(id);
        if (character.isPresent()) {
            model.addAttribute("character", character.get());
            return "character/detail";
        }
        return "redirect:/characters";
    }
    
    @GetMapping("/new")
    public String newCharacterForm(Model model) {
        model.addAttribute("character", new Character());
        return "character/form";
    }
    
    @PostMapping
    public String createCharacter(@ModelAttribute Character character) {
        characterService.save(character);
        return "redirect:/characters";
    }
    
    @ResponseBody
    @GetMapping("/api")
    public ResponseEntity<List<CharacterResponseDto>> getCharacters() {
        List<Character> characters = characterService.findAll();
        List<CharacterResponseDto> characterDtos = characters.stream()
                .map(CharacterResponseDto::fromEntity)
                .toList();
        return ResponseEntity.ok(characterDtos);
    }
    
    @ResponseBody
    @GetMapping("/api/{id}")
    public ResponseEntity<CharacterResponseDto> getCharacter(@PathVariable Long id) {
        Optional<Character> character = characterService.findById(id);
        return character.map(c -> ResponseEntity.ok(CharacterResponseDto.fromEntity(c)))
                       .orElse(ResponseEntity.notFound().build());
    }
    
    @ResponseBody
    @PostMapping("/api")
    public ResponseEntity<CharacterResponseDto> createCharacterApi(@RequestBody CharacterCreateRequestDto requestDto) {
        Character character = requestDto.toEntity();
        Character savedCharacter = characterService.save(character);
        return ResponseEntity.ok(CharacterResponseDto.fromEntity(savedCharacter));
    }
    
    @ResponseBody
    @DeleteMapping("/api/{id}")
    public ResponseEntity<Void> deleteCharacterApi(@PathVariable Long id) {
        if (!characterService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        characterService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}