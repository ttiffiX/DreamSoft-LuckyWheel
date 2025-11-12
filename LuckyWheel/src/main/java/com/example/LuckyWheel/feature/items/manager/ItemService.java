package com.example.LuckyWheel.feature.items.manager;

import com.example.LuckyWheel.feature.items.dto.ItemDTO;
import com.example.LuckyWheel.feature.items.dto.UserItemDTO;

import java.util.List;

public interface ItemService {
    List<ItemDTO> getAllItems();

    List<UserItemDTO> getUserItemsWithQuantity(String userId);
}
