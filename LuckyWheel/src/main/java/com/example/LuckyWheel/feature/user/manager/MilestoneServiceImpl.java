package com.example.LuckyWheel.feature.user.manager;

import com.example.LuckyWheel.controller.response.WheelInfoResponse;
import com.example.LuckyWheel.feature.items.dto.ItemDTO;
import com.example.LuckyWheel.feature.items.logic.ItemDataLoader;
import com.example.LuckyWheel.feature.user.dto.MilestoneDTO;
import com.example.LuckyWheel.feature.user.entity.User;
import com.example.LuckyWheel.feature.user.enums.ResourceType;
import com.example.LuckyWheel.feature.user.logic.MilestoneDataLoader;
import com.example.LuckyWheel.feature.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
@Slf4j
public class MilestoneServiceImpl implements MilestoneService {
    private final UserRepository userRepository;
    private final MilestoneDataLoader milestoneDataLoader;
    private final ItemDataLoader itemDataLoader;
    private final ResourceService resourceService;


    @Override
    public List<WheelInfoResponse.MilestoneInfo> getAvailableMilestones(String userId, Long wheelId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        int currentCount = user.getWheelSpinCounts().getOrDefault(wheelId, 0);
        Set<Long> claimedMilestones = user.getMilestoneRewardsClaimed().getOrDefault(wheelId, new HashSet<>());
        List<WheelInfoResponse.MilestoneInfo> availableMilestones = new ArrayList<>();

        // Lấy milestone config cho wheel này
        MilestoneDTO milestoneConfig = milestoneDataLoader.getMilestoneByWheelId(wheelId);

        // Kiểm tra từng milestone
        for (MilestoneDTO.MilestoneReward milestoneReward : milestoneConfig.getMilestoneRewards()) {
            // Đủ điều kiện VÀ chưa claim
            if (currentCount >= milestoneReward.getMilestone() && !claimedMilestones.contains(milestoneReward.getId())) {
                // Map rewards với item name
                List<WheelInfoResponse.RewardInfo> rewardInfos = new ArrayList<>();
                for (MilestoneDTO.Reward reward : milestoneReward.getRewards()) {
                    ItemDTO item = itemDataLoader.getItemById(reward.getInfoId());
                    WheelInfoResponse.RewardInfo rewardInfo = WheelInfoResponse.RewardInfo.builder()
                            .itemName(item.getName())
                            .number(reward.getNumber())
                            .build();
                    rewardInfos.add(rewardInfo);
                }

                WheelInfoResponse.MilestoneInfo milestoneInfo = WheelInfoResponse.MilestoneInfo.builder()
                        .id(milestoneReward.getId())
                        .milestone(milestoneReward.getMilestone())
                        .rewards(rewardInfos)
                        .build();

                availableMilestones.add(milestoneInfo);
            }
        }

        return availableMilestones;
    }

    @Override
    @Transactional
    public List<WheelInfoResponse.RewardInfo> claimMilestoneReward(String userId, Long wheelId, Long milestoneId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        int currentCount = user.getWheelSpinCounts().getOrDefault(wheelId, 0);
        Set<Long> claimedMilestones = user.getMilestoneRewardsClaimed().getOrDefault(wheelId, new HashSet<>());

        // Kiểm tra đã claim chưa
        if (claimedMilestones.contains(milestoneId)) {
            throw new RuntimeException("Milestone already claimed: " + milestoneId);
        }

        // Lấy milestone config
        MilestoneDTO milestoneConfig = milestoneDataLoader.getMilestoneByWheelId(wheelId);
        MilestoneDTO.MilestoneReward milestoneReward = milestoneConfig.getMilestoneRewards().stream()
                .filter(m -> m.getId().equals(milestoneId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Milestone not found: " + milestoneId));

        // Kiểm tra đủ điều kiện chưa
        if (currentCount < milestoneReward.getMilestone()) {
            throw new RuntimeException("Milestone not reached yet. Current: " + currentCount + ", Required: " + milestoneReward.getMilestone());
        }

        // Trao thưởng và map sang RewardInfo
        List<WheelInfoResponse.RewardInfo> grantedRewards = new ArrayList<>();
        for (MilestoneDTO.Reward reward : milestoneReward.getRewards()) {
            grantReward(user.getUsername(), reward, wheelId, milestoneReward.getId());

            // Map sang RewardInfo với item name
            ItemDTO item = itemDataLoader.getItemById(reward.getInfoId());
            WheelInfoResponse.RewardInfo rewardInfo = WheelInfoResponse.RewardInfo.builder()
                    .itemName(item.getName())
                    .number(reward.getNumber())
                    .build();
            grantedRewards.add(rewardInfo);
        }

        log.info("User {} claimed milestone {} for wheel {}", userId, milestoneId, wheelId);
        return grantedRewards;
    }

    private void grantReward(String username, MilestoneDTO.Reward reward, Long wheelId, Long milestoneId) {
        ResourceType resourceType = ResourceType.fromValue(itemDataLoader.getItemById(reward.getInfoId()).getItemType());
        resourceService.addResource(username, resourceType, reward.getNumber());
        resourceService.updateMilestone(username, wheelId, milestoneId);
        log.info("Granting reward to user {} with id: {} x{}", username, reward.getInfoId(), reward.getNumber());
    }


}



