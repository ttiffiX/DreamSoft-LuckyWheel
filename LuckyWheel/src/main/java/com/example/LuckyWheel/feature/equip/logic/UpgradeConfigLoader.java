package com.example.LuckyWheel.feature.equip.logic;

import com.example.LuckyWheel.feature.equip.dto.UpgradeConfigDTO;
import com.example.LuckyWheel.utils.JsonUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Loader for equipment upgrade configuration data
 * Loads upgrade rules from upgrade_equip.json
 */
@Slf4j
@Component
public class UpgradeConfigLoader {
    private final ResourceLoader resourceLoader;
    private List<UpgradeConfigDTO> upgradeConfigs;

    public UpgradeConfigLoader(
            @Qualifier("webApplicationContext") ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @PostConstruct
    public void loadUpgradeConfigs() {
        try {
            var resource = resourceLoader.getResource("classpath:upgrade_equip.json");
            upgradeConfigs = JsonUtils.parseJson(resource, new TypeReference<List<UpgradeConfigDTO>>() {
            });
            log.info("Loaded {} upgrade configurations", upgradeConfigs.size());
        } catch (Exception e) {
            log.error("Failed to load upgrade configurations", e);
            throw new RuntimeException("Cannot load upgrade_equip.json", e);
        }
    }

    public UpgradeConfigDTO getUpgradeConfigForLevel(Integer targetLevel) {
        return upgradeConfigs.stream()
                .filter(config -> config.getLevel().equals(targetLevel))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No upgrade configuration found for level: " + targetLevel));
    }

    public List<UpgradeConfigDTO> getAllUpgradeConfigs() {
        return upgradeConfigs;
    }

}

