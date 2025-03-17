package com.tihomir.maxbet.service;

import com.tihomir.maxbet.dto.item.CreateItemDto;
import com.tihomir.maxbet.dto.item.GiftItemDto;
import com.tihomir.maxbet.dto.item.GrantItemDto;
import com.tihomir.maxbet.dto.item.ItemDto;
import com.tihomir.maxbet.entity.Character;
import com.tihomir.maxbet.entity.Item;
import com.tihomir.maxbet.exception.custom.CharacterNotFoundException;
import com.tihomir.maxbet.exception.custom.InvalidItemTransferException;
import com.tihomir.maxbet.repository.CharacterRepository;
import com.tihomir.maxbet.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.tihomir.maxbet.exception.custom.ItemNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final CharacterRepository characterRepository;
    private final CharacterService characterService;

    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    public String determineItemSuffix(Item item) {
        int strength = item.getBonusStrength() != null ? item.getBonusStrength() : 0;
        int agility = item.getBonusAgility() != null ? item.getBonusAgility() : 0;
        int intelligence = item.getBonusIntelligence() != null ? item.getBonusIntelligence() : 0;
        int faith = item.getBonusFaith() != null ? item.getBonusFaith() : 0;

        int maxStat = Math.max(Math.max(strength, agility), Math.max(intelligence, faith));

        if (maxStat == strength && strength > 0) {
            return " Of The Bear";
        } else if (maxStat == agility && agility > 0) {
            return " Of The Cobra";
        } else if (maxStat == intelligence && intelligence > 0) {
            return " Of The Owl";
        } else if (maxStat == faith && faith > 0) {
            return " Of The Unicorn";
        }

        // return empty string if none match the criteria
        return "";
    }

    public ItemDto getItemById(Integer id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException(id));

        String suffix = determineItemSuffix(item);
        String fullItemName = item.getName() + suffix;

        return new ItemDto(
                item.getId(),
                fullItemName,
                item.getDescription(),
                item.getBonusStrength(),
                item.getBonusAgility(),
                item.getBonusIntelligence(),
                item.getBonusFaith()
        );
    }

    @Transactional
    public Item createItem(CreateItemDto createItemDto) {
        Item item = Item.builder()
                .name(createItemDto.getName())
                .description(createItemDto.getDescription())
                .bonusStrength(createItemDto.getBonusStrength())
                .bonusAgility(createItemDto.getBonusAgility())
                .bonusIntelligence(createItemDto.getBonusIntelligence())
                .bonusFaith(createItemDto.getBonusFaith())
                .build();

        return itemRepository.save(item);
    }

    @Transactional
    public void grantItemToCharacter(GrantItemDto grantItemDto) {
        Character character = characterRepository.findById(grantItemDto.getCharacterId())
                .orElseThrow(() -> new CharacterNotFoundException(grantItemDto.getCharacterId()));

        Item item = itemRepository.findById(grantItemDto.getItemId())
                .orElseThrow(() -> new ItemNotFoundException(grantItemDto.getItemId()));

        character.getItems().add(item);
        characterRepository.save(character);

        // evict cache for this character
        characterService.evictCharacterCache(grantItemDto.getCharacterId());
    }

    @Transactional
    public void giftItemBetweenCharacters(GiftItemDto giftItemDto) {
        Character fromCharacter = characterRepository.findById(giftItemDto.getFromCharacterId())
                .orElseThrow(() -> new CharacterNotFoundException(giftItemDto.getFromCharacterId()));

        Character toCharacter = characterRepository.findById(giftItemDto.getToCharacterId())
                .orElseThrow(() -> new CharacterNotFoundException(giftItemDto.getToCharacterId()));

        Item item = itemRepository.findById(giftItemDto.getItemId())
                .orElseThrow(() -> new ItemNotFoundException(giftItemDto.getItemId()));

        if (!fromCharacter.getItems().contains(item)) {
            throw new InvalidItemTransferException("Source character does not own this item");
        }

        fromCharacter.getItems().remove(item);
        toCharacter.getItems().add(item);

        characterRepository.saveAll(List.of(fromCharacter, toCharacter));

        // evict cache for both characters
        characterService.evictCharacterCache(giftItemDto.getFromCharacterId());
        characterService.evictCharacterCache(giftItemDto.getToCharacterId());
    }
}
