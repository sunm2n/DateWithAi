package com.datewithai.domain.chat.service;

import com.datewithai.domain.chat.dto.AiChatRequest;
import com.datewithai.domain.chat.dto.AiChatResponse;
import com.datewithai.domain.chat.dto.ChatHistoryDto;
import com.datewithai.domain.chat.repository.ChatMessageRepository;
import com.datewithai.domain.character.service.CharacterService;
import com.datewithai.domain.character.repository.CharacterRepository;
import com.datewithai.domain.user.service.UserService;
import com.datewithai.domain.chat.entity.ChatMessage;
import com.datewithai.domain.character.entity.Character;
import com.datewithai.domain.user.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ChatService {
    
    private final ChatMessageRepository chatMessageRepository;
    private final CharacterService characterService;
    private final CharacterRepository characterRepository;
    private final UserService userService;
    private final WebClient pythonServiceWebClient;
    private final RedisTemplate<String, Object> redisTemplate;
    
    @Value("${ai.python-service.endpoints.chat}")
    private String chatEndpoint;
    
    @Autowired
    public ChatService(ChatMessageRepository chatMessageRepository,
                      CharacterService characterService,
                      CharacterRepository characterRepository,
                      UserService userService,
                      @Qualifier("pythonServiceWebClient") WebClient pythonServiceWebClient,
                      RedisTemplate<String, Object> redisTemplate) {
        this.chatMessageRepository = chatMessageRepository;
        this.characterService = characterService;
        this.characterRepository = characterRepository;
        this.userService = userService;
        this.pythonServiceWebClient = pythonServiceWebClient;
        this.redisTemplate = redisTemplate;
    }
    
    @Transactional
    public Mono<AiChatResponse> sendMessage(String username, Long characterId, String message, String sessionId) {
        Optional<User> userOpt = userService.findByUsername(username);
        Optional<Character> characterOpt = characterService.findById(characterId);
        
        if (userOpt.isEmpty() || characterOpt.isEmpty()) {
            return Mono.just(new AiChatResponse("User or Character not found"));
        }
        
        User user = userOpt.get();
        Character character = characterOpt.get();
        
        final String finalSessionId = (sessionId == null || sessionId.isEmpty()) ? 
            UUID.randomUUID().toString() : sessionId;
        
        saveUserMessage(user, character, message, finalSessionId);
        
        String characterInfo = buildCharacterInfo(character);
        String characterFileId = getCharacterFileId(character.getName());
        
        AiChatRequest request = new AiChatRequest(message, characterFileId, characterInfo);
        
        return pythonServiceWebClient.post()
                .uri(chatEndpoint)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(AiChatResponse.class)
                .map(response -> {
                    response.setSessionId(finalSessionId);
                    response.setSuccess(true);
                    return response;
                })
                .doOnSuccess(response -> {
                    saveAiMessage(user, character, response.getResponse(), finalSessionId);
                    cacheConversation(finalSessionId, user, character);
                })
                .onErrorReturn(new AiChatResponse("AI 서버 연결에 실패했습니다.", true));
    }
    
    @Transactional
    private void saveUserMessage(User user, Character character, String message, String sessionId) {
        ChatMessage chatMessage = new ChatMessage(user, character, message, ChatMessage.MessageType.USER, sessionId);
        chatMessageRepository.save(chatMessage);
    }
    
    @Transactional
    @CacheEvict(value = "conversation", key = "#sessionId")
    private void saveAiMessage(User user, Character character, String message, String sessionId) {
        ChatMessage chatMessage = new ChatMessage(user, character, message, ChatMessage.MessageType.AI, sessionId);
        chatMessageRepository.save(chatMessage);
    }
    
    @Cacheable(value = "conversation", key = "#user.id + '_' + #character.id")
    public List<ChatHistoryDto> getConversationHistory(User user, Character character, int limit) {
        LocalDateTime since = LocalDateTime.now().minusHours(24);
        List<ChatMessage> messages = chatMessageRepository.findRecentConversation(user, character, since);
        
        return messages.stream()
                .limit(limit)
                .map(ChatHistoryDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    public List<ChatHistoryDto> getSessionHistory(String sessionId) {
        List<ChatMessage> messages = chatMessageRepository.findBySessionIdOrderByCreatedAtAsc(sessionId);
        
        return messages.stream()
                .map(ChatHistoryDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    private void cacheConversation(String sessionId, User user, Character character) {
        String key = "session:" + sessionId;
        String value = user.getId() + ":" + character.getId();
        redisTemplate.opsForValue().set(key, value, 24, TimeUnit.HOURS);
    }
    
    public Long getConversationCount(User user, Character character) {
        return chatMessageRepository.countConversationMessages(user, character);
    }
    
    @Cacheable(value = "userChatHistory", key = "#username")
    public List<ChatHistoryDto> getUserChatHistory(String username, int page, int size) {
        Optional<User> userOpt = userService.findByUsername(username);
        if (userOpt.isEmpty()) {
            return List.of();
        }
        
        return chatMessageRepository.findByUserOrderByCreatedAtDesc(userOpt.get(), PageRequest.of(page, size))
                .stream()
                .map(ChatHistoryDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    private String buildCharacterInfo(Character character) {
        StringBuilder info = new StringBuilder();
        info.append("캐릭터 이름: ").append(character.getName()).append("\n");
        if (character.getDescription() != null) {
            info.append("캐릭터 설명: ").append(character.getDescription()).append("\n");
        }
        if (character.getPersonality() != null) {
            info.append("성격: ").append(character.getPersonality()).append("\n");
        }
        return info.toString();
    }
    
    // 새로운 단순한 메시지 전송 메소드 (세션 관리 없음)
    @Transactional
    public Mono<AiChatResponse> sendSimpleMessage(Long characterId, String message) {
        Optional<Character> characterOpt = characterRepository.findById(characterId);
        
        if (characterOpt.isEmpty()) {
            return Mono.just(new AiChatResponse("Character not found", true));
        }
        
        Character character = characterOpt.get();
        String characterInfo = buildCharacterInfo(character);
        String characterFileId = getCharacterFileId(character.getName());
        
        AiChatRequest request = new AiChatRequest(message, characterFileId, characterInfo);
        
        return pythonServiceWebClient.post()
                .uri(chatEndpoint)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(AiChatResponse.class)
                .map(response -> {
                    response.setSuccess(true);
                    return response;
                })
                .onErrorReturn(new AiChatResponse("AI 서버 연결에 실패했습니다.", true));
    }

    private String getCharacterFileId(String characterName) {
        // 캐릭터 이름을 파이썬에서 사용하는 파일명으로 매핑
        switch (characterName) {
            case "호시노 아이":
                return "hoshino ai_character.txt";
            case "시노부":
                return "shinobu_character.txt";
            default:
                return characterName.toLowerCase().replace(" ", "_") + "_character.txt";
        }
    }
}