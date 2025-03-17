package com.tihomir.maxbet.dto.character;

import com.tihomir.maxbet.dto.item.ItemDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CharacterDto implements Serializable {
    private Integer id;
    private String name;
    private Integer health;
    private Integer mana;
    private Integer baseStrength;
    private Integer baseAgility;
    private Integer baseIntelligence;
    private Integer baseFaith;
    private Integer totalStrength;
    private Integer totalAgility;
    private Integer totalIntelligence;
    private Integer totalFaith;
    private String characterClass;
    private List<ItemDto> items;
}
