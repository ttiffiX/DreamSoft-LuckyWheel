package com.example.LuckyWheel.feature.items.logic;

import com.example.LuckyWheel.feature.items.dto.ItemDTO;
import com.example.LuckyWheel.utils.JsonUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class ItemDataLoader {
    private final ResourceLoader resourceLoader;
    private List<ItemDTO> itemList;

    public ItemDataLoader(
            @Qualifier("webApplicationContext") ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @PostConstruct
    public void loadItems() {
        try {
            var resource = resourceLoader.getResource("classpath:items.json");
            itemList = JsonUtils.parseJson(resource, new TypeReference<List<ItemDTO>>() {
            });
            log.info("Loaded {} items data", itemList.size());
        } catch (Exception e) {
            log.error("Failed to load items data", e);
            throw new RuntimeException("Cannot load items.json", e);
        }
    }

    public List<ItemDTO> getAllItems() {
        return itemList;
    }

    public ItemDTO getItemById(Long itemId) {
        return itemList.stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Item not found with id: " + itemId));
    }

}

