package com.datewithai.domain.story.controller;

import com.datewithai.domain.character.service.CharacterService;
import com.datewithai.domain.story.dto.StoryEmbedRequest;
import com.datewithai.domain.story.dto.StoryResponseDto;
import com.datewithai.domain.story.dto.StoryCreateRequestDto;
import com.datewithai.domain.story.service.StoryService;
import com.datewithai.domain.character.entity.Character;
import com.datewithai.domain.story.entity.Story;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/stories")
public class StoryController {
    
    private final StoryService storyService;
    private final CharacterService characterService;
    
    @Autowired
    public StoryController(StoryService storyService, CharacterService characterService) {
        this.storyService = storyService;
        this.characterService = characterService;
    }
    
    @GetMapping
    public String storyList(@RequestParam(required = false) Long characterId, Model model) {
        List<Story> stories;
        
        if (characterId != null) {
            stories = storyService.findByCharacterId(characterId);
            Optional<Character> character = characterService.findById(characterId);
            character.ifPresent(c -> model.addAttribute("selectedCharacter", c));
        } else {
            stories = storyService.findStoriesWithoutEmbedding();
        }
        
        model.addAttribute("stories", stories);
        model.addAttribute("characters", characterService.findAll());
        return "story/list";
    }
    
    @GetMapping("/{id}")
    public String storyDetail(@PathVariable Long id, Model model) {
        Optional<Story> story = storyService.findById(id);
        if (story.isEmpty()) {
            return "redirect:/stories";
        }
        model.addAttribute("story", story.get());
        return "story/detail";
    }
    
    @GetMapping("/new")
    public String storyForm(@RequestParam(required = false) Long characterId, Model model) {
        model.addAttribute("story", new Story());
        model.addAttribute("characters", characterService.findAll());
        
        if (characterId != null) {
            Optional<Character> character = characterService.findById(characterId);
            character.ifPresent(c -> model.addAttribute("selectedCharacter", c));
        }
        
        return "story/form";
    }
    
    @PostMapping
    public String createStory(@RequestParam Long characterId,
                             @RequestParam String title,
                             @RequestParam String content) {
        Optional<Character> character = characterService.findById(characterId);
        if (character.isEmpty()) {
            return "redirect:/stories/new";
        }
        
        storyService.createStory(character.get(), title, content);
        return "redirect:/stories?characterId=" + characterId;
    }
    
    @PostMapping("/{id}/delete")
    public String deleteStory(@PathVariable Long id) {
        storyService.deleteById(id);
        return "redirect:/stories";
    }
    
    @ResponseBody
    @GetMapping("/api")
    public ResponseEntity<List<StoryResponseDto>> getStories(@RequestParam(required = false) Long characterId) {
        List<Story> stories;
        
        if (characterId != null) {
            stories = storyService.findByCharacterId(characterId);
        } else {
            stories = storyService.findStoriesWithoutEmbedding();
        }
        
        List<StoryResponseDto> storyDtos = stories.stream()
                .map(StoryResponseDto::fromEntity)
                .toList();
        return ResponseEntity.ok(storyDtos);
    }
    
    @ResponseBody
    @GetMapping("/api/{id}")
    public ResponseEntity<StoryResponseDto> getStory(@PathVariable Long id) {
        Optional<Story> story = storyService.findById(id);
        return story.map(s -> ResponseEntity.ok(StoryResponseDto.fromEntity(s)))
                   .orElse(ResponseEntity.notFound().build());
    }
    
    @ResponseBody
    @PostMapping("/api")
    public ResponseEntity<StoryResponseDto> createStoryApi(@RequestBody StoryCreateRequestDto requestDto) {
        Optional<Character> character = characterService.findById(requestDto.getCharacterId());
        if (character.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        Story story = requestDto.toEntity(character.get());
        Story savedStory = storyService.save(story);
        return ResponseEntity.ok(StoryResponseDto.fromEntity(savedStory));
    }
    
    @ResponseBody
    @PostMapping("/api/embed")
    public ResponseEntity<String> embedStory(@RequestBody StoryEmbedRequest request) {
        Optional<Story> storyOpt = storyService.findById(request.getStoryId());
        if (storyOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        Story story = storyOpt.get();
        storyService.embedStoryAsync(story);
        
        return ResponseEntity.ok("Embedding process started");
    }
    
    @ResponseBody
    @DeleteMapping("/api/{id}")
    public ResponseEntity<Void> deleteStoryApi(@PathVariable Long id) {
        if (!storyService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        storyService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}