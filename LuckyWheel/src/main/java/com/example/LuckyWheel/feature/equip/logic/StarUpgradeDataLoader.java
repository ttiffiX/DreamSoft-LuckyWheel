package com.example.LuckyWheel.feature.equip.logic;

import com.example.LuckyWheel.feature.equip.dto.StarUpgradeDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class StarUpgradeDataLoader {

    private final Map<Integer, StarUpgradeDTO> starUpgradeMap = new HashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostConstruct
    public void loadStarUpgradeData() {
        try {
            ClassPathResource resource = new ClassPathResource("star_upgrade.json");
            List<StarUpgradeDTO> starUpgrades = objectMapper.readValue(
                    resource.getInputStream(),
                    new TypeReference<List<StarUpgradeDTO>>() {
                    }
            );

            for (StarUpgradeDTO upgrade : starUpgrades) {
                starUpgradeMap.put(upgrade.getStarNumber(), upgrade);
            }

            log.info("Loaded {} star upgrade configurations", starUpgradeMap.size());
        } catch (IOException e) {
            log.error("Failed to load star_upgrade.json", e);
            throw new RuntimeException("Failed to load star upgrade data", e);
        }
    }

    /**
     * Lấy thông tin nâng cấp sao theo số sao
     */
    public StarUpgradeDTO getStarUpgradeInfo(Integer starNumber) {
        return starUpgradeMap.get(starNumber);
    }

    /**
     * Kiểm tra có thể nâng sao không
     */
    public boolean canUpgrade(Integer currentStar, Integer equipLevel) {
        Integer nextStar = currentStar + 1;
        StarUpgradeDTO upgradeInfo = starUpgradeMap.get(nextStar);

        if (upgradeInfo == null) {
            return false; // Đã đạt max star
        }

        return equipLevel >= upgradeInfo.getRequiredEquipLevel();
    }

    /**
     * Lấy số sao tối đa
     */
    public int getMaxStar() {
        return starUpgradeMap.keySet().stream()
                .max(Integer::compareTo)
                .orElse(0);
    }
}

