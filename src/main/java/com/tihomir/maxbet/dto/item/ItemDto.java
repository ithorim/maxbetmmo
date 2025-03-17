package com.tihomir.maxbet.dto.item;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto implements Serializable {
    private Integer id;
    private String name;
    private String description;
    private Integer bonusStrength;
    private Integer bonusAgility;
    private Integer bonusIntelligence;
    private Integer bonusFaith;
}
