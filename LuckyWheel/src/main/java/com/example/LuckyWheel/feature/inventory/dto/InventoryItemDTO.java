package com.example.LuckyWheel.feature.inventory.dto;

import java.util.Map;

import lombok.NoArgsConstructor;
import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;

/**
 * DTO trả về FE cho 1 item trong inventory
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryItemDTO {
    private Long itemId;
    private Long itemType;
    private String name;
    private String description;
    private Integer quantity;
    private Integer maxStack;
    private Boolean canTrade;
    private Map<String, Object> effect;  // Cho consumable
}