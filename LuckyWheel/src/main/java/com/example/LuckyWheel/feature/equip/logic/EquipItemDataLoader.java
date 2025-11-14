package com.example.LuckyWheel.feature.equip.logic;

import com.example.LuckyWheel.feature.equip.dto.EquipDTO;
import com.example.LuckyWheel.utils.JsonUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class EquipItemDataLoader {
    private final ResourceLoader resourceLoader;
    private List<EquipDTO> equipItemList;

    public EquipItemDataLoader(
            @Qualifier("webApplicationContext") ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @PostConstruct
    public void loadItems() {
        try {
            var resource = resourceLoader.getResource("classpath:equip.json");
            equipItemList = JsonUtils.parseJson(resource, new TypeReference<List<EquipDTO>>() {
            });
            log.info("Loaded {} equip items data", equipItemList.size());
        } catch (Exception e) {
            log.error("Failed to load equip items data", e);
            throw new RuntimeException("Cannot load equip.json", e);
        }
    }

    public EquipDTO getEquipByInfoId(Long equipInfoId) {
        return equipItemList.stream()
                .filter(equip -> equip.getId().equals(equipInfoId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Equip item not found with info id: " + equipInfoId));
    }
}
