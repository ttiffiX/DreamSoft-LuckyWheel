package com.example.LuckyWheel.feature.rewardhistory.manager;

import com.example.LuckyWheel.controller.response.SpinResultResponse;
import com.example.LuckyWheel.feature.rewardhistory.entity.RewardHistory;
import com.example.LuckyWheel.feature.rewardhistory.repository.RewardHistoryRepository;
import com.example.LuckyWheel.feature.user.entity.User;
import com.example.LuckyWheel.feature.user.enums.ResourceType;
import com.example.LuckyWheel.feature.user.manager.UserService;
import com.example.LuckyWheel.feature.user.repository.UserRepository;
import com.example.LuckyWheel.feature.wheel.logic.WheelDataLoader;
import com.example.LuckyWheel.feature.wheel.manager.WheelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RewardHistoryServiceImpl implements RewardHistoryService {
    private final RewardHistoryRepository rewardHistoryRepository;
    private final WheelService wheelService;
    private final UserRepository userRepository;
    private final WheelDataLoader wheelDataLoader;
    private final UserService userService;

    @Override
    public Page<SpinResultResponse> getRewardHistory(Long wheelId, String userId, Pageable pageable) {
        log.info("Get Item History with pagination: page {}, size {}, wheelId: {}",
                pageable.getPageNumber(), pageable.getPageSize(), wheelId);

        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found with id: " + userId);
        }

        Page<RewardHistory> rewardHistoryPage = rewardHistoryRepository.findByWheelIdAndUserId(wheelId, userId, pageable);

        return rewardHistoryPage.map(history -> {

            // Giả sử bạn có thể lấy RewardInfo (tên, type) từ Document nhúng
            RewardHistory.RewardInfo info = history.getRewardInfo();

            return SpinResultResponse.builder()
                    .rewardId(info.getItemId())
                    .rewardName(info.getItemName())
                    .rewardType(info.getItemType())
                    .quantity(info.getQuantity()) // Vì mỗi record là 1 lần quay (quantity = 1)
                    .spinTime(history.getSpinTime())
                    .build();
        });
    }

    @Override
    @Transactional
    public List<SpinResultResponse> saveRewardHistory(String userId, Long wheelId, int quantity) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        if (!wheelDataLoader.isActiveWheel(wheelId)) {
            throw new RuntimeException("Wheel is not active: " + wheelId);
        }

        ResourceType resourceType = ResourceType.fromValue(wheelDataLoader.getWheelResourceType(wheelId));

        if (!userService.hasEnoughResource(user.getUsername(), resourceType, quantity)) {
            throw new RuntimeException("User does not have enough resources: " + user.getUsername());
        }

        List<SpinResultResponse> spinRewards = wheelService.spin(wheelId, quantity);


        if (spinRewards == null || spinRewards.isEmpty()) {
            throw new RuntimeException("No rewards generated from spin");
        }

        List<RewardHistory> histories = new ArrayList<>();

        for (SpinResultResponse reward : spinRewards) {
            RewardHistory.RewardInfo rewardInfo = RewardHistory.RewardInfo.builder()
                    .itemId(reward.getRewardId())
                    .itemName(reward.getRewardName())
                    .itemType(reward.getRewardType())
                    .quantity(reward.getQuantity())
                    .build();

            RewardHistory history = RewardHistory.builder()
                    .userId(userId)
                    .wheelId(wheelId)
                    .rewardInfo(rewardInfo)
                    .spinTime(reward.getSpinTime())
                    .build();

            histories.add(history);
        }

        userService.addResource(user.getUsername(), spinRewards);
        userService.removeResource(user.getUsername(), resourceType, quantity);
        rewardHistoryRepository.saveAll(histories);

        log.info("User {} spun wheel {} {} times and won {} rewards",
                userId, wheelId, quantity, spinRewards.size());

        return spinRewards;
    }


}
