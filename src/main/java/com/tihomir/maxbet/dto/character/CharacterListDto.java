package com.tihomir.maxbet.dto.character;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CharacterListDto {
    private Integer id;
    private String name;
    private Integer health;
    private Integer mana;
}
