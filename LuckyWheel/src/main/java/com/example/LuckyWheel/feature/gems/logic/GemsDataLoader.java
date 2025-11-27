package com.example.LuckyWheel.feature.gems.logic;

import com.example.LuckyWheel.feature.gems.dto.GemsDTO;
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
public class GemsDataLoader {
    private final ResourceLoader resourceLoader;
    private List<GemsDTO> gemsList;

    public GemsDataLoader(@Qualifier("webApplicationContext") ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @PostConstruct
    public void loadItems() {
        try {
            var resource = resourceLoader.getResource("classpath:gem_stats.json");
            gemsList = JsonUtils.parseJson(resource, new TypeReference<List<GemsDTO>>() {
            });
            log.info("Loaded {} gems data", gemsList.size());
        } catch (Exception e) {
            log.error("Failed to load gems data", e);
            throw new RuntimeException("Cannot load gem_stats.json", e);
        }
    }

    public GemsDTO getGemsById(Long gemId) {
        return gemsList.stream()
                .filter(gem -> gem.getGemId().equals(gemId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Gems not found with id: " + gemId));
    }

    public List<String> getGemsDataBuffs(List<Long> gemIds) {
        return gemsList.stream()
                .filter(gem -> gemIds.contains(gem.getGemId()))
                .map(GemsDTO::getBuffs)
                .toList();
    }
}
