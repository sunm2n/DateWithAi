package com.datewithai.domain.chat.repository;

import com.datewithai.domain.chat.entity.ChatMessage;
import com.datewithai.domain.character.entity.Character;
import com.datewithai.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    
    List<ChatMessage> findByUserAndCharacterOrderByCreatedAtAsc(User user, Character character);
    
    List<ChatMessage> findBySessionIdOrderByCreatedAtAsc(String sessionId);
    
    Page<ChatMessage> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);
    
    @Query("SELECT cm FROM ChatMessage cm WHERE cm.user = :user AND cm.character = :character AND cm.createdAt >= :since ORDER BY cm.createdAt ASC")
    List<ChatMessage> findRecentConversation(@Param("user") User user, @Param("character") Character character, @Param("since") LocalDateTime since);
    
    @Query("SELECT cm FROM ChatMessage cm WHERE cm.sessionId = :sessionId ORDER BY cm.createdAt DESC")
    List<ChatMessage> findBySessionIdOrderByCreatedAtDesc(String sessionId, Pageable pageable);
    
    @Query("SELECT COUNT(cm) FROM ChatMessage cm WHERE cm.user = :user AND cm.character = :character")
    Long countConversationMessages(@Param("user") User user, @Param("character") Character character);
}