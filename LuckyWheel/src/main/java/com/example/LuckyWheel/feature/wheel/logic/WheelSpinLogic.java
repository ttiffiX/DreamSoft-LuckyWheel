package com.example.LuckyWheel.feature.wheel.logic;

import com.example.LuckyWheel.feature.wheel.dto.GiftRandomDTO;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class WheelSpinLogic {
    private final Random random = new Random();

    public Map<Long, Integer> calculateWinningRewardId(List<GiftRandomDTO> giftRandomDTOList, int quantity) {
        Map<Long, Integer> winningRewardIds = new HashMap<>();

        int totalProbability = giftRandomDTOList.stream()
                .mapToInt(GiftRandomDTO::getProbability)
                .sum();

        for (int i = 0; i < quantity; i++) {
            int randomValue = random.nextInt(totalProbability);
            int cumulative = 0;

            for (GiftRandomDTO gift : giftRandomDTOList) {
                cumulative += gift.getProbability();
                if (randomValue < cumulative) {
                    winningRewardIds.merge(
                            gift.getInfoId(),
                            gift.getNumber(),
                            Integer::sum
                    );
                    break;
                }
            }
        }
        return winningRewardIds;
    }
}
