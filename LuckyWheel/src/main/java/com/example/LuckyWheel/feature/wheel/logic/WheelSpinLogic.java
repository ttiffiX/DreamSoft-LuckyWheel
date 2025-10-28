package com.example.LuckyWheel.feature.wheel.logic;

import com.example.LuckyWheel.feature.wheel.dto.GiftRandomDTO;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class WheelSpinLogic {
    private final Random random = new Random();

    public List<Long> calculateWinningRewardId(List<GiftRandomDTO> giftRandomDTOList, int quantity) {
        List<Long> winningRewardIds = new ArrayList<>();

        int totalProbability = giftRandomDTOList.stream()
                .mapToInt(GiftRandomDTO::getProbability)
                .sum();

        for (int i = 0; i < quantity; i++) {
            int randomValue = random.nextInt(totalProbability);
            int cumulative = 0;

            for (GiftRandomDTO gift : giftRandomDTOList) {
                cumulative += gift.getProbability();
                if (randomValue < cumulative) {
                    winningRewardIds.add(gift.getId());
                    break;
                }
            }
        }
        return winningRewardIds;
    }
}
