package com.example.LuckyWheel.feature.equip.logic;

import com.example.LuckyWheel.feature.equip.entity.Equip;
import com.example.LuckyWheel.feature.gems.logic.GemsDataLoader;
import com.example.LuckyWheel.utils.DataParser;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Slf4j
@Component
@AllArgsConstructor
public class UpgradeLogic {
    private final Random random = new Random();
    private final DataParser dataParser;
    private final GemsDataLoader gemsDataLoader;

    public Map<Long, Long> calculatePropsMainForLevel(Equip equip) {
        // Với level 1, trả về giá trị base
        if (equip.getLevel() == 1) {
            return new HashMap<>(equip.getPropsMain());
        }

        Map<Long, Long> calculatedProps = new HashMap<>();


        // Tính multiplier
        double multiplier = Math.pow(1.1, equip.getLevel() - 1);

        // Áp dụng multiplier cho từng stat
        for (Map.Entry<Long, Long> entry : equip.getPropsMain().entrySet()) {
            Long statId = entry.getKey();
            Long baseValue = entry.getValue();
            Long calculatedValue = Math.round(baseValue * multiplier);
            calculatedProps.put(statId, calculatedValue);
        }

        List<Long> gemIds = equip.getListGemIds();
        List<String> gemsDataBuffs = gemsDataLoader.getGemsDataBuffs(gemIds);

        for (String gemBuff : gemsDataBuffs) {
            Map<Long, Long> gemBuffMap = dataParser.parseInfoBuff(gemBuff);

            for (Map.Entry<Long, Long> entry : gemBuffMap.entrySet()) {
                Long statId = entry.getKey();
                Long gemValue = entry.getValue();

                calculatedProps.merge(
                        statId,
                        gemValue,
                        Long::sum
                );
            }
        }

        return calculatedProps;
    }

    public boolean rollUpgradeSuccess(Integer successRate) {
        int roll = random.nextInt(100) + 1; // 1-100
        return roll <= successRate;
    }
}

