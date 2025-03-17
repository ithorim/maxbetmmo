package com.tihomir.maxbet.dto.character;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateCharacterDto {
    @NotBlank(message = "Character name is required")
    private String name;

    @NotNull(message = "Health is required")
    @Min(value = 1, message = "Health must be at least 1")
    private Integer health;

    @NotNull(message = "Mana is required")
    @Min(value = 0, message = "Mana cannot be negative")
    private Integer mana;

    @NotNull(message = "Base strength is required")
    @Min(value = 0, message = "Base strength cannot be negative")
    private Integer baseStrength;

    @NotNull(message = "Base agility is required")
    @Min(value = 0, message = "Base agility cannot be negative")
    private Integer baseAgility;

    @NotNull(message = "Base intelligence is required")
    @Min(value = 0, message = "Base intelligence cannot be negative")
    private Integer baseIntelligence;

    @NotNull(message = "Base faith is required")
    @Min(value = 0, message = "Base faith cannot be negative")
    private Integer baseFaith;

    @NotNull(message = "Character class ID is required")
    private Integer classId;
}
