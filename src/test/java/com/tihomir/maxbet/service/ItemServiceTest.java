package com.tihomir.maxbet.service;

import com.tihomir.maxbet.dto.item.CreateItemDto;
import com.tihomir.maxbet.dto.item.GiftItemDto;
import com.tihomir.maxbet.dto.item.GrantItemDto;
import com.tihomir.maxbet.dto.item.ItemDto;
import com.tihomir.maxbet.entity.Character;
import com.tihomir.maxbet.entity.CharacterClass;
import com.tihomir.maxbet.entity.Item;
import com.tihomir.maxbet.exception.custom.CharacterNotFoundException;
import com.tihomir.maxbet.exception.custom.InvalidItemTransferException;
import com.tihomir.maxbet.exception.custom.ItemNotFoundException;
import com.tihomir.maxbet.repository.CharacterRepository;
import com.tihomir.maxbet.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {
    @Mock
    private ItemRepository itemRepository;

    @Mock
    private CharacterRepository characterRepository;

    @Mock
    private CharacterService characterService;

    @InjectMocks
    private ItemService itemService;

    private Item testItem;
    private Character testCharacter1;
    private Character testCharacter2;
    private CharacterClass testClass;

    @BeforeEach
    void setUp() {
        testClass = CharacterClass.builder()
                .id(1)
                .name("Warrior")
                .description("Melee fighter")
                .build();

        testItem = Item.builder()
                .id(1)
                .name("Sword")
                .description("A sharp blade")
                .bonusStrength(10)
                .bonusAgility(5)
                .bonusIntelligence(0)
                .bonusFaith(3)
                .build();

        List<Item> items1 = new ArrayList<>();
        items1.add(testItem);

        testCharacter1 = Character.builder()
                .id(1)
                .name("Tihomir")
                .health(100)
                .mana(0)
                .baseStrength(10)
                .baseAgility(8)
                .baseIntelligence(2)
                .baseFaith(3)
                .characterClass(testClass)
                .items(items1)
                .build();

        testCharacter2 = Character.builder()
                .id(2)
                .name("Tico")
                .health(80)
                .mana(20)
                .baseStrength(11)
                .baseAgility(7)
                .baseIntelligence(4)
                .baseFaith(5)
                .characterClass(testClass)
                .items(new ArrayList<>())
                .build();
    }

    @Test
    void getAllItemsShouldReturnListOfItems() {
        // arrange
        when(itemRepository.findAll()).thenReturn(List.of(testItem));

        // act
        List<Item> result = itemService.getAllItems();

        // assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Sword", result.get(0).getName());
    }

    @Test
    void getItemByIdWhenItemExistsShouldReturnItemDto() {
        // arrange
        when(itemRepository.findById(1)).thenReturn(Optional.of(testItem));

        // act
        ItemDto result = itemService.getItemById(1);

        // assert
        assertNotNull(result);
        assertEquals("Sword Of The Bear", result.getName()); // itemService should add suffix based on highest stat
        assertEquals("A sharp blade", result.getDescription());
        assertEquals(10, result.getBonusStrength());
        assertEquals(5, result.getBonusAgility());
        assertEquals(0, result.getBonusIntelligence());
        assertEquals(3, result.getBonusFaith());
    }

    @Test
    void getItemByIdWhenItemDoesNotExistShouldThrowException() {
        // arrange
        when(itemRepository.findById(99)).thenReturn(Optional.empty());

        // act & assert
        assertThrows(ItemNotFoundException.class, () -> {
            itemService.getItemById(99);
        });
    }

    @Test
    void createItemShouldReturnCreatedItem() {
        // arrange
        CreateItemDto createDto = new CreateItemDto("Staff", "A magical staff", 1, 1, 5, 2);

        when(itemRepository.save(any(Item.class))).thenAnswer(invocation -> {
            Item savedItem = invocation.getArgument(0);
            savedItem.setId(2);
            return savedItem;
        });

        // act
        Item result = itemService.createItem(createDto);

        //assert
        assertNotNull(result);
        assertEquals(2, result.getId());
        assertEquals("Staff", result.getName());
        assertEquals("A magical staff", result.getDescription());
        assertEquals(1, result.getBonusStrength());
        assertEquals(1, result.getBonusAgility());
        assertEquals(5, result.getBonusIntelligence());
        assertEquals(2, result.getBonusFaith());
    }

    @Test
    void grantItemToCharacterShouldAddItemToCharacter() {
        // arrange
        GrantItemDto grantDto = new GrantItemDto(2, 1);
        Item itemToGrant = testItem;
        Character characterToReceive = testCharacter2;

        when(characterRepository.findById(2)).thenReturn(Optional.of(characterToReceive));
        when(itemRepository.findById(1)).thenReturn(Optional.of(itemToGrant));

        // act
        itemService.grantItemToCharacter(grantDto);

        // assert
        verify(characterRepository).save(characterToReceive);
        assertTrue(characterToReceive.getItems().contains(itemToGrant));
    }

    @Test
    void grantItemToCharacterWhenCharacterDoesNotExistThrowsException() {
        // arrange
        GrantItemDto grantItemDto = new GrantItemDto(99, 1);
        when(characterRepository.findById(99)).thenReturn(Optional.empty());

        // act & assert
        assertThrows(CharacterNotFoundException.class, () -> {
            itemService.grantItemToCharacter(grantItemDto);
        });
    }

    @Test
    void grantItemToCharacterWhenItemDoesNotExistThrowsException() {
        // arrange
        GrantItemDto grantItemDto = new GrantItemDto(2, 99);
        when(characterRepository.findById(2)).thenReturn(Optional.of(testCharacter2));
        when(itemRepository.findById(99)).thenReturn(Optional.empty());

        // act & assert
        assertThrows(ItemNotFoundException.class, () -> {
            itemService.grantItemToCharacter(grantItemDto);
        });
    }

    @Test
    void giftItemBetweenCharactersShouldTransferItemBetweenCharacters() {
        // arrange
        GiftItemDto giftDto = new GiftItemDto(1, 2, 1);

        when(characterRepository.findById(1)).thenReturn(Optional.of(testCharacter1));
        when(characterRepository.findById(2)).thenReturn(Optional.of(testCharacter2));
        when(itemRepository.findById(1)).thenReturn(Optional.of(testItem));

        // act
        itemService.giftItemBetweenCharacters(giftDto);

        // assert
        verify(characterRepository).saveAll(List.of(testCharacter1, testCharacter2));

        assertFalse(testCharacter1.getItems().contains(testItem));
        assertTrue(testCharacter2.getItems().contains(testItem));
    }

    @Test
    void giftItemBetweenCharactersWhenSourceCharacterDoesNotOwnItThrowsException() {
        // arrange
        GiftItemDto giftItemDto = new GiftItemDto(2, 1, 1);

        when(characterRepository.findById(1)).thenReturn(Optional.of(testCharacter1));
        when(characterRepository.findById(2)).thenReturn(Optional.of(testCharacter2));
        when(itemRepository.findById(1)).thenReturn(Optional.of(testItem));

        // act & assert
        assertThrows(InvalidItemTransferException.class, () -> {
            itemService.giftItemBetweenCharacters(giftItemDto);
        });
    }

    @Test
    void determineItemSuffixShouldReturnCorrectSuffix() {
        // test with strength as highest stat
        Item strengthItem = Item.builder()
                .bonusStrength(25)
                .bonusAgility(2)
                .bonusIntelligence(1)
                .bonusFaith(3)
                .build();
        assertEquals(" Of The Bear", itemService.determineItemSuffix(strengthItem));

        // test with agility as highest stat
        Item agilityItem = Item.builder()
                .bonusStrength(2)
                .bonusAgility(23)
                .bonusIntelligence(5)
                .bonusFaith(1)
                .build();
        assertEquals(" Of The Cobra", itemService.determineItemSuffix(agilityItem));

        // test with intelligence as highest stat
        Item intelligenceItem = Item.builder()
                .bonusStrength(2)
                .bonusAgility(3)
                .bonusIntelligence(11)
                .bonusFaith(5)
                .build();
        assertEquals(" Of The Owl", itemService.determineItemSuffix(intelligenceItem));

        // test with faith as highest stat
        Item faithItem = Item.builder()
                .bonusStrength(5)
                .bonusAgility(5)
                .bonusIntelligence(5)
                .bonusFaith(15)
                .build();
        assertEquals(" Of The Unicorn", itemService.determineItemSuffix(faithItem));

        // test with equal stats
        // should use the first highest in the order
        Item equalItem = Item.builder()
                .bonusStrength(5)
                .bonusAgility(5)
                .bonusIntelligence(5)
                .bonusFaith(5)
                .build();
        assertEquals(" Of The Bear", itemService.determineItemSuffix(equalItem));
    }
}
