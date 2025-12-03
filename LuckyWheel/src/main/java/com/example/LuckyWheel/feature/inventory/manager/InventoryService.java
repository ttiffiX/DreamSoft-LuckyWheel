package com.example.LuckyWheel.feature.inventory.manager;

import com.example.LuckyWheel.feature.inventory.dto.InventoryItemDTO;
import com.example.LuckyWheel.feature.inventory.dto.UserInventoryDTO;

import java.util.List;

/**
 * Service interface cho Inventory management
 */
public interface InventoryService {
    InventoryItemDTO addItem(String userId, Long itemId, Integer quantity);

    void removeItem(String userId, Long itemId, Integer quantity);

    UserInventoryDTO getUserInventory(String userId);

    List<InventoryItemDTO> getItemsByType(String userId, Long itemType);

    boolean hasEnoughItem(String userId, Long itemId, Integer quantity);

    int getItemQuantity(String userId, Long itemId);

    void useConsumable(String userId, Long itemId);

    void clearInventory(String userId);
}

