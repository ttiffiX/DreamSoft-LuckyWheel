package com.example.LuckyWheel.feature.equip.logic;

import com.example.LuckyWheel.feature.equip.entity.Equip;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Slf4j
@Component
@AllArgsConstructor
public class UpgradeLogic {
    private final Random random = new Random();

    public Map<Long, Long> calculatePropsMainForLevel(Equip equip) {
        if (equip.getLevel() < 1) {
            throw new IllegalArgumentException("Target level must be at least 1");
        }

        if (equip.getPropsMain() == null || equip.getPropsMain().isEmpty()) {
            return new HashMap<>();
        }

        Map<Long, Long> calculatedProps = new HashMap<>();

        // Với level 1, trả về giá trị base
        if (equip.getLevel() == 1) {
            return new HashMap<>(equip.getPropsMain());
        }

        // Tính multiplier
        double multiplier = Math.pow(1.1, equip.getLevel() - 1);

        // Áp dụng multiplier cho từng stat
        for (Map.Entry<Long, Long> entry : equip.getPropsMain().entrySet()) {
            Long statId = entry.getKey();
            Long baseValue = entry.getValue();
            Long calculatedValue = Math.round(baseValue * multiplier);
            calculatedProps.put(statId, calculatedValue);
        }

        return calculatedProps;
    }

    public boolean rollUpgradeSuccess(Integer successRate) {
        int roll = random.nextInt(100) + 1; // 1-100
        return roll <= successRate;
    }
}

