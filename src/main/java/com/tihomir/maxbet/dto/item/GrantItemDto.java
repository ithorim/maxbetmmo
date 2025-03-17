package com.tihomir.maxbet.dto.item;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GrantItemDto {
    @NotNull(message = "Character ID is required")
    private Integer characterId;

    @NotNull(message = "Item ID is required")
    private Integer itemId;
}