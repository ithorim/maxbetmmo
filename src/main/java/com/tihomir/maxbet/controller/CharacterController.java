package com.tihomir.maxbet.controller;

import com.tihomir.maxbet.dto.character.CharacterDto;
import com.tihomir.maxbet.dto.character.CharacterListDto;
import com.tihomir.maxbet.dto.character.CreateCharacterDto;
import com.tihomir.maxbet.service.CharacterService;
import com.tihomir.maxbet.entity.Character;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/character")
@RequiredArgsConstructor
public class CharacterController {
    private final CharacterService characterService;

    @GetMapping
    public ResponseEntity<List<CharacterListDto>> getAllCharacters() {
        return ResponseEntity.ok(characterService.getAllCharacters());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CharacterDto> getCharacterById(@PathVariable Integer id) {
        return ResponseEntity.ok((characterService.getCharacterById(id)));
    }

    @PostMapping
    public ResponseEntity<Character> createCharacter(@Valid @RequestBody CreateCharacterDto createCharacterDto) {
        return new ResponseEntity<>(characterService.createCharacter(createCharacterDto), HttpStatus.CREATED);
    }
}
