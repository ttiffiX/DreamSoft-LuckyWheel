package com.example.LuckyWheel.feature.wheel.manager;

import com.example.LuckyWheel.controller.response.SpinResultResponse;
import com.example.LuckyWheel.controller.response.WheelInfoResponse;
import com.example.LuckyWheel.feature.user.dto.MilestoneDTO;
import com.example.LuckyWheel.feature.user.entity.User;
import com.example.LuckyWheel.feature.user.enums.ResourceType;
import com.example.LuckyWheel.feature.user.logic.MilestoneDataLoader;
import com.example.LuckyWheel.feature.user.repository.UserRepository;
import com.example.LuckyWheel.feature.wheel.dto.GiftRandomDTO;
import com.example.LuckyWheel.feature.items.dto.ItemDTO;
import com.example.LuckyWheel.feature.items.logic.ItemDataLoader;
import com.example.LuckyWheel.feature.wheel.dto.WheelDTO;
import com.example.LuckyWheel.feature.wheel.logic.WheelDataLoader;
import com.example.LuckyWheel.feature.wheel.logic.WheelSpinLogic;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class WheelServiceImpl implements WheelService {
    private final WheelDataLoader wheelDataLoader;
    private final ItemDataLoader itemDataLoader;
    private final WheelSpinLogic wheelSpinLogic;
    private final UserRepository userRepository;
    private final MilestoneDataLoader milestoneDataLoader;

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

    @Override
    public Integer getSpinCount(String userId, Long wheelId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        return user.getWheelSpinCounts().getOrDefault(wheelId, 0);
    }

    @Override
    public WheelInfoResponse getWheelInfo(Long wheelId) {
        WheelDTO wheel = wheelDataLoader.getWheelById(wheelId);
        MilestoneDTO milestoneConfig = milestoneDataLoader.getMilestoneByWheelId(wheelId);

        // Map gifts with item names
        List<WheelInfoResponse.GiftInfo> gifts = wheel.getListGiftRandom().stream()
                .map(gift -> {
                    ItemDTO item = itemDataLoader.getItemById(gift.getInfoId());
                    return WheelInfoResponse.GiftInfo.builder()
                            .id(gift.getId())
                            .itemName(item.getName())
                            .number(gift.getNumber())
                            .probability(gift.getProbability())
                            .build();
                })
                .collect(Collectors.toList());

        // Map milestones with item names
        List<WheelInfoResponse.MilestoneInfo> milestones = milestoneConfig.getMilestoneRewards().stream()
                .map(milestone -> {
                    List<WheelInfoResponse.RewardInfo> rewardInfos = milestone.getRewards().stream()
                            .map(reward -> {
                                ItemDTO item = itemDataLoader.getItemById(reward.getInfoId());
                                return WheelInfoResponse.RewardInfo.builder()
                                        .itemName(item.getName())
                                        .number(reward.getNumber())
                                        .build();
                            })
                            .collect(Collectors.toList());

                    return WheelInfoResponse.MilestoneInfo.builder()
                            .id(milestone.getId())
                            .milestone(milestone.getMilestone())
                            .rewards(rewardInfos)
                            .build();
                })
                .collect(Collectors.toList());

        return WheelInfoResponse.builder()
                .wheelId(wheel.getWheelId())
                .wheelName(wheel.getName())
                .isActive(wheel.isActive())
                .resourceType(ResourceType.fromValue(wheel.getResourceRequire()))
                .gifts(gifts)
                .milestones(milestones)
                .build();
    }

    @Override
    public List<WheelInfoResponse> getAllWheelInfo() {
        List<WheelDTO> allWheels = wheelDataLoader.getAllWheels();

        return allWheels.stream()
                .map(wheel -> getWheelInfo(wheel.getWheelId()))
                .collect(Collectors.toList());
    }
}
