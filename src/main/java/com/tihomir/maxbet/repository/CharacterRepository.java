package com.tihomir.maxbet.repository;

import com.tihomir.maxbet.entity.Character;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CharacterRepository extends JpaRepository<Character, Integer> {
}
