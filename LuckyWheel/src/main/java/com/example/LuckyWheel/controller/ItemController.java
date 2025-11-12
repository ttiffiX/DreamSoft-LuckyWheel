package com.example.LuckyWheel.controller;

import com.example.LuckyWheel.feature.items.dto.ItemDTO;
import com.example.LuckyWheel.feature.items.dto.UserItemDTO;
import com.example.LuckyWheel.feature.items.manager.ItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemService itemService;

    /**
     * Get all items (basic info)
     */
    @GetMapping()
    public ResponseEntity<List<ItemDTO>> getAllItems() {
        log.info("Get all items");
        List<ItemDTO> items = itemService.getAllItems();
        return ResponseEntity.ok(items);
    }

    /**
     * Get user's items with quantities from resources
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserItemDTO>> getUserItems(@PathVariable String userId) {
        log.info("Get items for user: {}", userId);
        List<UserItemDTO> userItems = itemService.getUserItemsWithQuantity(userId);
        return ResponseEntity.ok(userItems);
    }
}

