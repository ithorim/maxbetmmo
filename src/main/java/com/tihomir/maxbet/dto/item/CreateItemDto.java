package com.tihomir.maxbet.dto.item;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateItemDto {
    @NotBlank(message = "Item name is required")
    @Size(min = 2, max = 50, message = "Item name must be between 2 and 50 characters")
    private String name;

    @NotBlank(message = "Item description is required")
    @Size(max = 255, message = "Description cannot exceed 255 characters")
    private String description;

    @Min(value = 0, message = "Bonus strength cannot be negative")
    private Integer bonusStrength = 0;

    @Min(value = 0, message = "Bonus agility cannot be negative")
    private Integer bonusAgility = 0;

    @Min(value = 0, message = "Bonus intelligence cannot be negative")
    private Integer bonusIntelligence = 0;

    @Min(value = 0, message = "Bonus faith cannot be negative")
    private Integer bonusFaith = 0;
}
