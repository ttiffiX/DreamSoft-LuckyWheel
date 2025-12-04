package com.example.LuckyWheel.feature.monster.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO cho item đã rơi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LootedItemDTO {
    private Long itemInfoId;
    private Integer quantity;
}

