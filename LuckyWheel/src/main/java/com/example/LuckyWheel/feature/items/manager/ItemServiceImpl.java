package com.example.LuckyWheel.feature.items.manager;

import com.example.LuckyWheel.feature.items.dto.ItemDTO;
import com.example.LuckyWheel.feature.items.dto.UserItemDTO;
import com.example.LuckyWheel.feature.items.logic.ItemDataLoader;
import com.example.LuckyWheel.feature.user.entity.User;
import com.example.LuckyWheel.feature.user.manager.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemDataLoader itemDataLoader;
    private final UserService userService;

    @Override
    public List<ItemDTO> getAllItems() {
        return itemDataLoader.getAllItems();
    }

    @Override
    public List<UserItemDTO> getUserItemsWithQuantity(String userId) {
        User user = userService.getUserById(userId);
        Map<Long, Integer> userResources = user.getResources();
        List<UserItemDTO> userItemDTOList = new ArrayList<>();

        for (Map.Entry<Long, Integer> entry : userResources.entrySet()) {
            Long itemId = entry.getKey();
            Integer quantity = entry.getValue();

            ItemDTO itemDTO = itemDataLoader.getItemById(itemId);
            if (itemDTO != null) {
                UserItemDTO userItemDTO = UserItemDTO.builder()
                        .itemId(itemId)
                        .name(itemDTO.getName())
                        .itemType(itemDTO.getItemType())
                        .description(itemDTO.getDescription())
                        .quantity(quantity)
                        .canTrade(itemDTO.getCanTrade())
                        .build();
                userItemDTOList.add(userItemDTO);
            }
        }

        log.info("Fetched {} items for user {}", userItemDTOList.size(), userId);
        return userItemDTOList;
    }
}
