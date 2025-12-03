package com.example.LuckyWheel.feature.inventory.entity;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;
import lombok.NoArgsConstructor;
import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * Entity cho Inventory - 1 user có 1 inventory duy nhất
 * Lưu stackable items (materials, consumables, quest items) dưới dạng Map
 * KHÔNG bao gồm: Resources (trong User.resources), Equips (collection riêng), Gems (collection riêng)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Document(collection = "user_inventory")
public class InventoryItem {

    @Id
    private String id;

    private String userId;

    // Map lưu items: Key = itemId, Value = quantity
    @Builder.Default
    private Map<Long, Integer> items = new HashMap<>();
}