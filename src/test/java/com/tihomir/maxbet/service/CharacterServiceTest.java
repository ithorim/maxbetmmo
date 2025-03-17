package com.tihomir.maxbet.service;

import com.tihomir.maxbet.dto.character.CharacterDto;
import com.tihomir.maxbet.dto.character.CharacterListDto;
import com.tihomir.maxbet.dto.character.CreateCharacterDto;
import com.tihomir.maxbet.entity.Character;
import com.tihomir.maxbet.entity.CharacterClass;
import com.tihomir.maxbet.entity.Item;
import com.tihomir.maxbet.exception.custom.CharacterClassNotFoundException;
import com.tihomir.maxbet.exception.custom.CharacterNotFoundException;
import com.tihomir.maxbet.repository.CharacterClassRepository;
import com.tihomir.maxbet.repository.CharacterRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CharacterServiceTest {
    @Mock
    private CharacterRepository characterRepository;

    @Mock
    private CharacterClassRepository characterClassRepository;

    @InjectMocks
    private CharacterService characterService;

    private Character testCharacter;
    private CharacterClass testClass;
    private Item testItem;
    private Validator validator;

    @BeforeEach
    void setUp() {
        try (var factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }

        testClass = CharacterClass.builder()
                .id(1)
                .name("Warrior")
                .description("Melee fighter")
                .build();

        testItem = Item.builder()
                .id(1)
                .name("Sword")
                .description("A sharp blade")
                .bonusStrength(5)
                .bonusAgility(2)
                .bonusIntelligence(0)
                .bonusFaith(5)
                .build();
        List<Item> items = new ArrayList<>();
        items.add(testItem);

        testCharacter = Character.builder()
                .id(1)
                .name("Testomir")
                .health(100)
                .mana(0)
                .baseStrength(15)
                .baseAgility(5)
                .baseIntelligence(0)
                .baseFaith(0)
                .characterClass(testClass)
                .items(items)
                .build();
    }

    @Test
    void getAllCharactersShouldReturnListOfCharacters() {
        // arrange
        when(characterRepository.findAll()).thenReturn(List.of(testCharacter));

        // act
        List<CharacterListDto> result = characterService.getAllCharacters();

        // assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Testomir", result.get(0).getName());
        assertEquals(100, result.get(0).getHealth());
        assertEquals(0, result.get(0).getMana());
    }

    @Test
    void getCharacterByIdWhenCharacterExistsShouldReturnCharacterDto() {
        // arrange
        when(characterRepository.findById(1)).thenReturn(Optional.of(testCharacter));

        // act
        CharacterDto result = characterService.getCharacterById(1);

        // assert
        assertNotNull(result);
        assertEquals("Testomir", result.getName());
        assertEquals(100, result.getHealth());
        assertEquals(0, result.getMana());
        assertEquals(20, result.getTotalStrength()); // 15 base + 5 from item
        assertEquals(7, result.getTotalAgility()); // 5 base + 2 from item
        assertEquals(0, result.getTotalIntelligence()); // 0 base, 0 item
        assertEquals(5, result.getTotalFaith()); // 0 base + 5 from item
        assertEquals("Warrior", result.getCharacterClass());
        
        // Check that items were converted to DTOs
        assertNotNull(result.getItems());
        assertEquals(1, result.getItems().size());
        assertEquals(testItem.getId(), result.getItems().get(0).getId());
        assertEquals(testItem.getName(), result.getItems().get(0).getName());
    }

    @Test
    void getCharacterByIdWhenCharacterDoesNotExistShouldThrowException() {
        // arrange
        when(characterRepository.findById(99)).thenReturn(Optional.empty());

        // act & assert
        assertThrows(CharacterNotFoundException.class, () -> {
            characterService.getCharacterById(99);
        });
    }

    @Test
    void createCharacterShouldReturnCreatedCharacter() {
        // arrange
        CreateCharacterDto createDto = new CreateCharacterDto("Tihomir", 100, 20, 10, 5, 2, 3, 1);

        when(characterClassRepository.findById(1)).thenReturn(Optional.of(testClass));

        // used thenAnswer and invocation to set the id manually as dto doesn't have id field
        when(characterRepository.save(any(Character.class))).thenAnswer( invocation -> {
            Character savedCharacter = invocation.getArgument(0); // get the first arg passed to save()
            savedCharacter.setId(2); // simulate db auto-generating an id
            return savedCharacter; // return "saved" character
        });

        // act
        Character result = characterService.createCharacter(createDto);

        // assert
        assertNotNull(result);
        assertEquals(2, result.getId());
        assertEquals("Tihomir", result.getName());
        assertEquals(100, result.getHealth());
        assertEquals(20, result.getMana());
        assertEquals(10, result.getBaseStrength());
        assertEquals(5, result.getBaseAgility());
        assertEquals(2, result.getBaseIntelligence());
        assertEquals(3, result.getBaseFaith());
        assertEquals(testClass, result.getCharacterClass());
    }

    @Test
    void createCharacterWhenClassDoesNotExistShouldThrowException() {
        // arrange
        CreateCharacterDto createDto = new CreateCharacterDto("Tihomir", 100, 20, 10, 5, 2, 3, 99);
        when(characterClassRepository.findById(99)).thenReturn(Optional.empty());

        // act & assert
        assertThrows(CharacterClassNotFoundException.class, () -> {
            characterService.createCharacter(createDto);
        });
    }

    @Test
    void createCharacterWithInvalidNameShouldThrowException() {
        // arrange
        // invalid - empty name
        CreateCharacterDto createDto = new CreateCharacterDto("", 100,20, 10, 5, 2, 3, 1);

        // act & assert
        Set<ConstraintViolation<CreateCharacterDto>> violations = validator.validate(createDto);
        assertFalse(violations.isEmpty());
        assertEquals("Character name is required",
            violations.iterator().next().getMessage());
    }

    @Test
    void createCharacterWithNegativeHealthShouldThrowException() {
        // arrange
        // invalid - negative health
        CreateCharacterDto createDto = new CreateCharacterDto("Tihomir", -100,20, 10, 5, 2, 3, 1);

        // act & assert
        Set<ConstraintViolation<CreateCharacterDto>> violations = validator.validate(createDto);
        assertFalse(violations.isEmpty());
        assertEquals("Health must be at least 1",
            violations.iterator().next().getMessage());
    }
}
