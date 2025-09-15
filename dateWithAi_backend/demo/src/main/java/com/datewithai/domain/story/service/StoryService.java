package com.datewithai.domain.story.service;

import com.datewithai.domain.story.dto.StoryEmbedRequest;
import com.datewithai.domain.story.dto.VectorSearchRequest;
import com.datewithai.domain.story.repository.StoryRepository;
import com.datewithai.domain.character.entity.Character;
import com.datewithai.domain.story.entity.Story;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class StoryService {
    
    private final StoryRepository storyRepository;
    private final WebClient pythonServiceWebClient;
    
    @Value("${ai.python-service.endpoints.embed-story}")
    private String embedStoryEndpoint;
    
    @Value("${ai.python-service.endpoints.search-vectors}")
    private String searchVectorsEndpoint;
    
    @Autowired
    public StoryService(StoryRepository storyRepository, 
                       @Qualifier("pythonServiceWebClient") WebClient pythonServiceWebClient) {
        this.storyRepository = storyRepository;
        this.pythonServiceWebClient = pythonServiceWebClient;
    }
    
    public Optional<Story> findById(Long id) {
        return storyRepository.findById(id);
    }
    
    @Cacheable(value = "characterStories", key = "#character.id")
    public List<Story> findByCharacter(Character character) {
        return storyRepository.findByCharacterOrderByCreatedAtDesc(character);
    }
    
    public List<Story> findByCharacterId(Long characterId) {
        return storyRepository.findByCharacterId(characterId);
    }
    
    public List<Story> findStoriesWithoutEmbedding() {
        return storyRepository.findStoriesWithoutEmbedding();
    }
    
    @Transactional
    public Story save(Story story) {
        return storyRepository.save(story);
    }
    
    @Transactional
    public Story createStory(Character character, String title, String content) {
        Story story = new Story(character, title, content);
        Story savedStory = storyRepository.save(story);
        
        embedStoryAsync(savedStory);
        
        return savedStory;
    }
    
    @Transactional
    public void embedStoryAsync(Story story) {
        StoryEmbedRequest request = new StoryEmbedRequest(story.getId(), story.getContent());
        
        pythonServiceWebClient.post()
                .uri(embedStoryEndpoint)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(String.class)
                .doOnSuccess(embedding -> {
                    story.setEmbeddingVector(embedding);
                    storyRepository.save(story);
                })
                .doOnError(error -> {
                    System.err.println("Failed to embed story: " + error.getMessage());
                })
                .subscribe();
    }
    
    public Mono<List<Story>> searchSimilarStories(String userMessage, Long characterId, int limit) {
        return pythonServiceWebClient.post()
                .uri("/api/embed")
                .bodyValue(userMessage)
                .retrieve()
                .bodyToMono(String.class)
                .map(embedding -> {
                    VectorSearchRequest searchRequest = new VectorSearchRequest(embedding, characterId, limit);
                    return storyRepository.findSimilarStoriesByEmbedding(characterId, embedding, limit);
                });
    }
    
    @Transactional
    public void deleteById(Long id) {
        storyRepository.deleteById(id);
    }
    
    public boolean existsById(Long id) {
        return storyRepository.existsById(id);
    }
}