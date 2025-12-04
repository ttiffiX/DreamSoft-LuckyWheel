package com.example.LuckyWheel.feature.monster.logic;

import com.example.LuckyWheel.feature.monster.dto.MonsterLootTableDTO;
import com.example.LuckyWheel.utils.JsonUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Loader để load dữ liệu loot table từ monster_loot_table.json
 */
@Component
@Slf4j
public class MonsterLootTableLoader {
    private final ResourceLoader resourceLoader;
    private Map<String, MonsterLootTableDTO> lootTableMap;

    public MonsterLootTableLoader(
            @Qualifier("webApplicationContext") ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @PostConstruct
    public void loadLootTables() {
        try {
            var resource = resourceLoader.getResource("classpath:monster_loot_table.json");
            lootTableMap = JsonUtils.parseJson(resource, new TypeReference<Map<String, MonsterLootTableDTO>>() {});
            log.info("Loaded {} monster loot tables", lootTableMap.size());
        } catch (Exception e) {
            log.error("Failed to load monster loot table data", e);
            throw new RuntimeException("Cannot load monster_loot_table.json", e);
        }
    }

    public Map<String, MonsterLootTableDTO> getAllLootTables() {
        return lootTableMap;
    }

    public MonsterLootTableDTO getLootTableByMonsterId(Long monsterId) {
        MonsterLootTableDTO lootTable = lootTableMap.get(monsterId.toString());
        if (lootTable == null) {
            throw new RuntimeException("Loot table not found for monster id: " + monsterId);
        }
        return lootTable;
    }
}

