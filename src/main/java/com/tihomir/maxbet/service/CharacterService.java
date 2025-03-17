package com.tihomir.maxbet.service;

import com.tihomir.maxbet.dto.character.CharacterDto;
import com.tihomir.maxbet.dto.character.CharacterListDto;
import com.tihomir.maxbet.dto.character.CreateCharacterDto;
import com.tihomir.maxbet.dto.item.ItemDto;
import com.tihomir.maxbet.entity.Character;
import com.tihomir.maxbet.entity.CharacterClass;
import com.tihomir.maxbet.entity.Item;
import com.tihomir.maxbet.exception.custom.CharacterClassNotFoundException;
import com.tihomir.maxbet.exception.custom.CharacterNotFoundException;
import com.tihomir.maxbet.repository.CharacterClassRepository;
import com.tihomir.maxbet.repository.CharacterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CharacterService {
    private final CharacterRepository characterRepository;
    private final CharacterClassRepository characterClassRepository;

    public List<CharacterListDto> getAllCharacters() {
        return characterRepository.findAll().stream()
                .map(character -> new CharacterListDto(
                        character.getId(),
                        character.getName(),
                        character.getHealth(),
                        character.getMana()
                )).toList();
    }

    @Cacheable(value = "characters", key = "#id")
    public CharacterDto getCharacterById(Integer id) {
        Character character = characterRepository.findById(id)
                .orElseThrow(() -> new CharacterNotFoundException(id));

        // calculate all stats
        int totalStrength = character.getBaseStrength() + character.getItems().stream().mapToInt(Item::getBonusStrength).sum();
        int totalAgility = character.getBaseAgility() + character.getItems().stream().mapToInt(Item::getBonusAgility).sum();
        int totalIntelligence = character.getBaseIntelligence() + character.getItems().stream().mapToInt(Item::getBonusIntelligence).sum();
        int totalFaith = character.getBaseFaith() + character.getItems().stream().mapToInt(Item::getBonusFaith).sum();

        // convert items to DTOs to avoid serialization issues
        List<ItemDto> itemDtos = character.getItems().stream()
                .map(item -> new ItemDto(
                        item.getId(),
                        item.getName(),
                        item.getDescription(),
                        item.getBonusStrength(),
                        item.getBonusAgility(),
                        item.getBonusIntelligence(),
                        item.getBonusFaith()
                ))
                .collect(Collectors.toList());

        return new CharacterDto(
                character.getId(),
                character.getName(),
                character.getHealth(),
                character.getMana(),
                character.getBaseStrength(),
                character.getBaseAgility(),
                character.getBaseIntelligence(),
                character.getBaseFaith(),
                totalStrength,
                totalAgility,
                totalIntelligence,
                totalFaith,
                character.getCharacterClass().getName(),
                itemDtos  // use the converted DTOs instead of the entity collection
        );
    }

    @Transactional
    @CacheEvict(value = "characters", key = "#result.id")
    public Character createCharacter(CreateCharacterDto createCharacterDto) {
        CharacterClass characterClass = characterClassRepository.findById(createCharacterDto.getClassId())
                .orElseThrow(() -> new CharacterClassNotFoundException(createCharacterDto.getClassId()));

        Character character = Character.builder()
                .name(createCharacterDto.getName())
                .health(createCharacterDto.getHealth())
                .mana(createCharacterDto.getMana())
                .baseStrength(createCharacterDto.getBaseStrength())
                .baseAgility(createCharacterDto.getBaseAgility())
                .baseIntelligence(createCharacterDto.getBaseIntelligence())
                .baseFaith(createCharacterDto.getBaseFaith())
                .characterClass(characterClass)
                .build();

        return characterRepository.save(character);
    }

    // annotation handles the cache eviction
    // it is called when a character's data changes
    @CacheEvict(value = "characters", key = "#characterId", beforeInvocation = true)
    public void evictCharacterCache(Integer characterId) { }

    @CacheEvict(value = "characters", allEntries = true)
    public void clearCache() {
        // This method will clear all entries in the characters cache
    }
}
