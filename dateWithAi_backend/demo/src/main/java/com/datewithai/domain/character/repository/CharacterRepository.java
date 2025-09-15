package com.datewithai.domain.character.repository;

import com.datewithai.domain.character.entity.Character;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CharacterRepository extends JpaRepository<Character, Long> {
    
    Optional<Character> findByName(String name);
    
    List<Character> findAllByOrderByCreatedAtDesc();
    
    @Query("SELECT c FROM Character c WHERE c.age BETWEEN :minAge AND :maxAge")
    List<Character> findByAgeRange(Integer minAge, Integer maxAge);
    
    @Query("SELECT c FROM Character c WHERE c.occupation = :occupation")
    List<Character> findByOccupation(String occupation);
}