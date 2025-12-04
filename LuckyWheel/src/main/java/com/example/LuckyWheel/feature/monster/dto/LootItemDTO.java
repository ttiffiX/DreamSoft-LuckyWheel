package com.example.LuckyWheel.feature.monster.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO cho từng item trong bảng loot
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LootItemDTO {
    @JsonProperty("itemInfoId")
    private Long itemInfoId;

    @JsonProperty("dropRate")
    private Integer dropRate;  // Tỷ lệ rơi (0-100)

    @JsonProperty("quantity")
    private Integer quantity;  // Số lượng rơi
}

