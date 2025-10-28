package com.example.LuckyWheel.feature.wheel.logic;

import com.example.LuckyWheel.feature.wheel.dto.GiftRandomDTO;
import com.example.LuckyWheel.feature.wheel.dto.WheelConfigDTO;
import com.example.LuckyWheel.feature.wheel.dto.WheelDTO;
import com.example.LuckyWheel.utils.JsonUtils;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class WheelDataLoader {
    private final ResourceLoader resourceLoader;
    private WheelConfigDTO wheelConfig;

    @PostConstruct
    public void loadWheelConfig() {
        try {
            var resource = resourceLoader.getResource("classpath:Reward.json");
            wheelConfig = JsonUtils.parseJson(resource, WheelConfigDTO.class);
            log.info("Loaded {} wheel configurations", wheelConfig.getWheels().size());
        } catch (Exception e) {
            log.error("Failed to load wheel config", e);
            throw new RuntimeException("Cannot load Reward.json", e);
        }
    }

    public List<GiftRandomDTO> getRewardsForWheel(Long wheelId) {
        return wheelConfig.getWheels().stream()
                .filter(wheel -> wheel.getWheelId().equals(wheelId))
                .findFirst()
                .map(wheel -> wheel.getListGiftRandom().stream()
                        .map(gift -> new GiftRandomDTO(
                                gift.getId(),
                                gift.getNumber(),
                                gift.getProbability()
                        ))
                        .toList())
                .orElseThrow(() -> new RuntimeException("Wheel not found with id: " + wheelId));

    }

    public String getTicketTypeByWheelId(Long wheelId) {
        return wheelConfig.getWheels().stream()
                .filter(wheel -> wheel.getWheelId().equals(wheelId))
                .findFirst()
                .map(WheelDTO::getTicketType)
                .orElseThrow(() -> new RuntimeException("Wheel not found with id: " + wheelId));
    }

    public boolean isActiveWheel(Long wheelId) {
        return wheelConfig.getWheels().stream()
                .filter(wheel -> wheel.getWheelId().equals(wheelId))
                .findFirst()
                .map(WheelDTO::isActive)
                .orElseThrow(() -> new RuntimeException("Wheel not found with id: " + wheelId));
    }

    public List<WheelDTO> getAllWheels() {
        return wheelConfig.getWheels();
    }

    public WheelDTO getWheelById(Long wheelId) {
        return wheelConfig.getWheels().stream()
                .filter(wheel -> wheel.getWheelId().equals(wheelId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Wheel not found with id: " + wheelId));
    }
}

