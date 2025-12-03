package com.example.LuckyWheel.feature.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO trả về toàn bộ inventory của user
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInventoryDTO {
    private String userId;
    private List<InventoryItemDTO> materials;    // itemType = 100
    private List<InventoryItemDTO> consumables;  // itemType = 200
    private List<InventoryItemDTO> questItems;   // itemType = 300
}

