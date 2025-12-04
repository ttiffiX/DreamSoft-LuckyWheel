package com.example.LuckyWheel.feature.monster.logic;

import com.example.LuckyWheel.feature.monster.dto.MonsterDTO;
import com.example.LuckyWheel.utils.JsonUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Loader để load dữ liệu quái vật từ monsters.json
 */
@Component
@Slf4j
public class MonsterDataLoader {
    private final ResourceLoader resourceLoader;
    private List<MonsterDTO> monsterList;

    public MonsterDataLoader(
            @Qualifier("webApplicationContext") ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @PostConstruct
    public void loadMonsters() {
        try {
            var resource = resourceLoader.getResource("classpath:monsters.json");
            monsterList = JsonUtils.parseJson(resource, new TypeReference<List<MonsterDTO>>() {});
            log.info("Loaded {} monsters data", monsterList.size());
        } catch (Exception e) {
            log.error("Failed to load monsters data", e);
            throw new RuntimeException("Cannot load monsters.json", e);
        }
    }

    public List<MonsterDTO> getAllMonsters() {
        return monsterList;
    }

    public MonsterDTO getMonsterById(Long monsterId) {
        return monsterList.stream()
                .filter(monster -> monster.getId().equals(monsterId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Monster not found with id: " + monsterId));
    }
}

