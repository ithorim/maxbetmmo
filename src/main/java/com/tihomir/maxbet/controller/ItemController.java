package com.tihomir.maxbet.controller;

import com.tihomir.maxbet.dto.item.CreateItemDto;
import com.tihomir.maxbet.dto.item.GiftItemDto;
import com.tihomir.maxbet.dto.item.GrantItemDto;
import com.tihomir.maxbet.dto.item.ItemDto;
import com.tihomir.maxbet.entity.Item;
import com.tihomir.maxbet.service.ItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public ResponseEntity<List<Item>> getAllItems() {
        return ResponseEntity.ok(itemService.getAllItems());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemDto> getItemById(@PathVariable Integer id) {
        return ResponseEntity.ok(itemService.getItemById(id));
    }

    @PostMapping
    public ResponseEntity<Item> createItem(@Valid @RequestBody CreateItemDto createItemDto) {
        return new ResponseEntity<>(itemService.createItem(createItemDto), HttpStatus.CREATED);
    }

    @PostMapping("/grant")
    public ResponseEntity<Void> grantItemToCharacter(@Valid @RequestBody GrantItemDto grantItemDto) {
        itemService.grantItemToCharacter(grantItemDto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/gift")
    public ResponseEntity<Void> giftItemBetweenCharacters(@Valid @RequestBody GiftItemDto giftItemDto) {
        itemService.giftItemBetweenCharacters(giftItemDto);
        return ResponseEntity.ok().build();
    }
}
