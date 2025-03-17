package com.tihomir.maxbet.dto.item;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GiftItemDto {
    @NotNull(message = "Source character ID is required")
    private Integer fromCharacterId;

    @NotNull(message = "Target character ID is required")
    private Integer toCharacterId;

    @NotNull(message = "Item ID is required")
    private Integer itemId;
}
