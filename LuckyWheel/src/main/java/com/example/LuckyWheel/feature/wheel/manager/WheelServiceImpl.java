package com.example.LuckyWheel.feature.wheel.manager;

import com.example.LuckyWheel.controller.response.SpinResultResponse;
import com.example.LuckyWheel.feature.user.enums.ResourceType;
import com.example.LuckyWheel.feature.wheel.dto.GiftRandomDTO;
import com.example.LuckyWheel.feature.items.dto.ItemDTO;
import com.example.LuckyWheel.feature.items.manager.ItemDataLoader;
import com.example.LuckyWheel.feature.wheel.logic.WheelDataLoader;
import com.example.LuckyWheel.feature.wheel.logic.WheelSpinLogic;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class WheelServiceImpl implements WheelService {
    private final WheelDataLoader wheelDataLoader;
    private final ItemDataLoader itemDataLoader;
    private final WheelSpinLogic wheelSpinLogic;

    @Override
    public List<SpinResultResponse> spin(Long wheelId, int quantity) {
        List<GiftRandomDTO> giftRandomDTOList = wheelDataLoader.getRewardsForWheel(wheelId);

        if (giftRandomDTOList == null || giftRandomDTOList.isEmpty()) {
            throw new RuntimeException("No rewards configured for wheel: " + wheelId);
        }

        Map<Long, Integer> winningRewardIds = wheelSpinLogic.calculateWinningRewardId(giftRandomDTOList, quantity);

        List<SpinResultResponse> spinRewards = new ArrayList<>();

        LocalDateTime spinTime = LocalDateTime.now();

        winningRewardIds.forEach((rewardId, number) -> {
            ItemDTO item = itemDataLoader.getItemById(rewardId);

            SpinResultResponse spinReward = SpinResultResponse.builder()
                    .rewardId(item.getId().toString())
                    .rewardName(item.getName())
                    .rewardType(ResourceType.fromValue(item.getItemType()))
                    .quantity(number)
                    .spinTime(spinTime)
                    .build();

            spinRewards.add(spinReward);
        });

        log.info("Successfully processed {} rewards", spinRewards.size());

        return spinRewards;
    }
}
