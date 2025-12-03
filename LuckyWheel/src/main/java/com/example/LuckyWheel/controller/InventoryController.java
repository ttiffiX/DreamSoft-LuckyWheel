package com.example.LuckyWheel.controller;

import com.example.LuckyWheel.feature.inventory.dto.InventoryItemDTO;
import com.example.LuckyWheel.feature.inventory.dto.UserInventoryDTO;
import com.example.LuckyWheel.feature.inventory.manager.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
@Slf4j
public class InventoryController {

    private final InventoryService inventoryService;

    /**
     * Lấy toàn bộ inventory của user
     */
    @GetMapping("/{userId}")
    public ResponseEntity<UserInventoryDTO> getUserInventory(@PathVariable String userId) {
        log.info("Getting inventory for user: {}", userId);
        UserInventoryDTO inventory = inventoryService.getUserInventory(userId);
        return ResponseEntity.ok(inventory);
    }

    /**
     * Lấy items theo type
     */
    @GetMapping("/{userId}/type/{itemType}")
    public ResponseEntity<List<InventoryItemDTO>> getItemsByType(
            @PathVariable String userId,
            @PathVariable Long itemType) {
        log.info("Getting items of type {} for user: {}", itemType, userId);
        List<InventoryItemDTO> items = inventoryService.getItemsByType(userId, itemType);
        return ResponseEntity.ok(items);
    }

    /**
     * Thêm item vào inventory (for testing/admin)
     */
    @PostMapping("/add")
    public ResponseEntity<InventoryItemDTO> addItem(@RequestBody Map<String, Object> request) {
        String userId = (String) request.get("userId");
        Long itemId = Long.valueOf(request.get("itemId").toString());
        Integer quantity = Integer.valueOf(request.get("quantity").toString());

        log.info("Adding item {} (qty: {}) to user {}", itemId, quantity, userId);
        InventoryItemDTO result = inventoryService.addItem(userId, itemId, quantity);
        return ResponseEntity.ok(result);
    }

    /**
     * Xóa item khỏi inventory
     */
    @PostMapping("/remove")
    public ResponseEntity<String> removeItem(@RequestBody Map<String, Object> request) {
        String userId = (String) request.get("userId");
        Long itemId = Long.valueOf(request.get("itemId").toString());
        Integer quantity = Integer.valueOf(request.get("quantity").toString());

        log.info("Removing item {} (qty: {}) from user {}", itemId, quantity, userId);
        inventoryService.removeItem(userId, itemId, quantity);
        return ResponseEntity.ok("Item removed successfully");
    }

    /**
     * Sử dụng consumable item
     */
    @PostMapping("/use/{userId}/{itemId}")
    public ResponseEntity<String> useConsumable(
            @PathVariable String userId,
            @PathVariable Long itemId) {
        log.info("User {} using consumable item {}", userId, itemId);
        inventoryService.useConsumable(userId, itemId);
        return ResponseEntity.ok("Item used successfully");
    }

    /**
     * Kiểm tra có đủ item không
     */
    @GetMapping("/check/{userId}/{itemId}/{quantity}")
    public ResponseEntity<Boolean> checkItem(
            @PathVariable String userId,
            @PathVariable Long itemId,
            @PathVariable Integer quantity) {
        boolean hasEnough = inventoryService.hasEnoughItem(userId, itemId, quantity);
        return ResponseEntity.ok(hasEnough);
    }

    /**
     * Lấy số lượng item
     */
    @GetMapping("/quantity/{userId}/{itemId}")
    public ResponseEntity<Integer> getItemQuantity(
            @PathVariable String userId,
            @PathVariable Long itemId) {
        int quantity = inventoryService.getItemQuantity(userId, itemId);
        return ResponseEntity.ok(quantity);
    }
}

