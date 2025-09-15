package com.datewithai.domain.character.service;

import com.datewithai.domain.character.repository.CharacterRepository;
import com.datewithai.domain.character.entity.Character;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class CharacterService {
    
    private final CharacterRepository characterRepository;
    
    @Autowired
    public CharacterService(CharacterRepository characterRepository) {
        this.characterRepository = characterRepository;
    }
    
    @Cacheable(value = "characters", key = "#id")
    public Optional<Character> findById(Long id) {
        return characterRepository.findById(id);
    }
    
    @Cacheable(value = "characters", key = "#name")
    public Optional<Character> findByName(String name) {
        return characterRepository.findByName(name);
    }
    
    @Cacheable(value = "characterList")
    public List<Character> findAll() {
        return characterRepository.findAllByOrderByCreatedAtDesc();
    }
    
    public List<Character> findByAgeRange(Integer minAge, Integer maxAge) {
        return characterRepository.findByAgeRange(minAge, maxAge);
    }
    
    public List<Character> findByOccupation(String occupation) {
        return characterRepository.findByOccupation(occupation);
    }
    
    @Transactional
    public Character save(Character character) {
        return characterRepository.save(character);
    }
    
    @Transactional
    public Character createCharacter(String name, String description, String personality, 
                                   String speakingStyle, Integer age, String occupation, String background) {
        Character character = new Character(name, description, personality, speakingStyle, age);
        character.setOccupation(occupation);
        character.setBackground(background);
        return characterRepository.save(character);
    }
    
    @Transactional
    public void deleteById(Long id) {
        characterRepository.deleteById(id);
    }
    
    public boolean existsById(Long id) {
        return characterRepository.existsById(id);
    }
}