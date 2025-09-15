package com.datewithai.domain.story.repository;

import com.datewithai.domain.character.entity.Character;
import com.datewithai.domain.story.entity.Story;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StoryRepository extends JpaRepository<Story, Long> {
    
    List<Story> findByCharacterOrderByCreatedAtDesc(Character character);
    
    List<Story> findByCharacterId(Long characterId);
    
    @Query(value = "SELECT s.* FROM stories s WHERE s.character_id = :characterId AND s.embedding_vector IS NOT NULL ORDER BY s.embedding_vector <-> CAST(:embedding AS vector) LIMIT :limit", nativeQuery = true)
    List<Story> findSimilarStoriesByEmbedding(@Param("characterId") Long characterId, @Param("embedding") String embedding, @Param("limit") int limit);
    
    @Query("SELECT s FROM Story s WHERE s.character = :character AND s.embeddingVector IS NOT NULL")
    List<Story> findByCharacterWithEmbedding(Character character);
    
    @Query("SELECT s FROM Story s WHERE s.embeddingVector IS NULL")
    List<Story> findStoriesWithoutEmbedding();
}