package com.example.LuckyWheel.feature.inventory.manager;

import com.example.LuckyWheel.feature.inventory.dto.InventoryItemDTO;
import com.example.LuckyWheel.feature.inventory.dto.UserInventoryDTO;
import com.example.LuckyWheel.feature.inventory.entity.InventoryItem;
import com.example.LuckyWheel.feature.inventory.enums.InventoryItemType;
import com.example.LuckyWheel.feature.inventory.repository.InventoryRepository;
import com.example.LuckyWheel.feature.items.dto.ItemDTO;
import com.example.LuckyWheel.feature.items.logic.ItemDataLoader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ItemDataLoader itemDataLoader;

    @Override
    public InventoryItemDTO addItem(String userId, Long itemId, Integer quantity) {
        // Load item template từ JSON
        ItemDTO itemTemplate = itemDataLoader.getItemById(itemId);
        if (itemTemplate == null) {
            throw new RuntimeException("Item not found: " + itemId);
        }

        // Lấy hoặc tạo inventory của user
        InventoryItem inventory = inventoryRepository.findByUserId(userId)
                .orElseGet(() -> {
                    InventoryItem newInventory = InventoryItem.builder()
                            .userId(userId)
                            .build();
                    return inventoryRepository.save(newInventory);
                });

        // Lấy quantity hiện tại
        int currentQuantity = inventory.getItems().getOrDefault(itemId, 0);
        int newQuantity = currentQuantity + quantity;

        // Check max stack
        if (itemTemplate.getMaxStack() != null && newQuantity > itemTemplate.getMaxStack()) {
            throw new RuntimeException("Stack limit exceeded. Max: " + itemTemplate.getMaxStack());
        }

        // Update Map
        inventory.getItems().put(itemId, newQuantity);
        inventoryRepository.save(inventory);

        log.info("Added item {} (qty: {}) to user {}. New total: {}", itemId, quantity, userId, newQuantity);

        return convertToDTO(itemId, newQuantity, itemTemplate);
    }

    @Override
    public void removeItem(String userId, Long itemId, Integer quantity) {
        InventoryItem inventory = inventoryRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Inventory not found for user: " + userId));

        int currentQuantity = inventory.getItems().getOrDefault(itemId, 0);

        if (currentQuantity < quantity) {
            throw new RuntimeException("Not enough items. Current: " + currentQuantity + ", Required: " + quantity);
        }

        int newQuantity = currentQuantity - quantity;

        if (newQuantity == 0) {
            // Xóa khỏi Map
            inventory.getItems().remove(itemId);
            log.info("Removed item {} completely from user {}", itemId, userId);
        } else {
            // Giảm quantity
            inventory.getItems().put(itemId, newQuantity);
            log.info("Decreased item {} quantity to {} for user {}", itemId, newQuantity, userId);
        }

        inventoryRepository.save(inventory);
    }

    @Override
    public UserInventoryDTO getUserInventory(String userId) {
        InventoryItem inventory = inventoryRepository.findByUserId(userId)
                .orElse(InventoryItem.builder().userId(userId).build());

        Map<Long, Integer> items = inventory.getItems();

        // Phân loại theo itemType
        List<InventoryItemDTO> materials = new ArrayList<>();
        List<InventoryItemDTO> consumables = new ArrayList<>();
        List<InventoryItemDTO> questItems = new ArrayList<>();

        for (Map.Entry<Long, Integer> entry : items.entrySet()) {
            Long itemId = entry.getKey();
            Integer quantity = entry.getValue();

            ItemDTO template = itemDataLoader.getItemById(itemId);
            if (template == null) continue;

            InventoryItemDTO dto = convertToDTO(itemId, quantity, template);

            // Phân loại theo itemType
            if (template.getItemType().equals(InventoryItemType.MATERIAL.getValue())) {
                materials.add(dto);
            } else if (template.getItemType().equals(InventoryItemType.CONSUMABLE.getValue())) {
                consumables.add(dto);
            } else if (template.getItemType().equals(InventoryItemType.QUEST_ITEM.getValue())) {
                questItems.add(dto);
            }
        }

        return UserInventoryDTO.builder()
                .userId(userId)
                .materials(materials)
                .consumables(consumables)
                .questItems(questItems)
                .build();
    }

    @Override
    public List<InventoryItemDTO> getItemsByType(String userId, Long itemType) {
        InventoryItem inventory = inventoryRepository.findByUserId(userId)
                .orElse(InventoryItem.builder().userId(userId).build());

        List<InventoryItemDTO> result = new ArrayList<>();

        for (Map.Entry<Long, Integer> entry : inventory.getItems().entrySet()) {
            Long itemId = entry.getKey();
            Integer quantity = entry.getValue();

            ItemDTO template = itemDataLoader.getItemById(itemId);
            if (template != null && template.getItemType().equals(itemType)) {
                result.add(convertToDTO(itemId, quantity, template));
            }
        }

        return result;
    }

    @Override
    public boolean hasEnoughItem(String userId, Long itemId, Integer quantity) {
        return getItemQuantity(userId, itemId) >= quantity;
    }

    @Override
    public int getItemQuantity(String userId, Long itemId) {
        return inventoryRepository.findByUserId(userId)
                .map(inventory -> inventory.getItems().getOrDefault(itemId, 0))
                .orElse(0);
    }

    @Override
    public void useConsumable(String userId, Long itemId) {
        ItemDTO itemTemplate = itemDataLoader.getItemById(itemId);

        if (itemTemplate == null || !itemTemplate.getItemType().equals(InventoryItemType.CONSUMABLE.getValue())) {
            throw new RuntimeException("Item is not consumable: " + itemId);
        }

        // TODO: Apply effect to user (heal HP, restore MP, buff stats...)
        // This will be implemented when we have combat/stats system

        // Remove 1 item
        removeItem(userId, itemId, 1);

        log.info("User {} used consumable item {}", userId, itemId);
    }

    @Override
    public void clearInventory(String userId) {
        inventoryRepository.deleteByUserId(userId);
        log.info("Cleared inventory for user {}", userId);
    }

    /**
     * Convert to DTO
     */
    private InventoryItemDTO convertToDTO(Long itemId, Integer quantity, ItemDTO template) {
        return InventoryItemDTO.builder()
                .itemId(itemId)
                .itemType(template.getItemType())
                .name(template.getName())
                .description(template.getDescription())
                .quantity(quantity)
                .maxStack(template.getMaxStack())
                .canTrade(template.getCanTrade() == 1)
                .effect(template.getEffect())
                .build();
    }
}

