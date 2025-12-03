package com.example.LuckyWheel.feature.inventory.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Enum cho ItemType trong inventory
 * Phân biệt với ResourceType (1-4) trong User.resources
 */
@Getter
@RequiredArgsConstructor
public enum InventoryItemType {
    MATERIAL("Material", 100L),      // Materials từ combat
    CONSUMABLE("Consumable", 200L),  // Potions, buffs
    QUEST_ITEM("Quest Item", 300L);  // Quest-specific items

    private final String name;
    private final Long value;

    public static InventoryItemType fromValue(Long value) {
        for (InventoryItemType type : InventoryItemType.values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid InventoryItemType value: " + value);
    }

    public static boolean isInventoryItem(Long itemType) {
        return itemType >= 100L && itemType <= 300L;
    }
}

