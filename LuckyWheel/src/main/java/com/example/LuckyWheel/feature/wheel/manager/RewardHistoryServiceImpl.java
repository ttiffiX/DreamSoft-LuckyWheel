package com.example.LuckyWheel.feature.wheel.manager;

import com.example.LuckyWheel.controller.response.SpinResultResponse;
import com.example.LuckyWheel.feature.wheel.dto.GiftRandomDTO;
import com.example.LuckyWheel.feature.wheel.entity.RewardHistory;
import com.example.LuckyWheel.feature.wheel.logic.WheelDataLoader;
import com.example.LuckyWheel.feature.wheel.logic.WheelSpinLogic;
import com.example.LuckyWheel.feature.wheel.repository.RewardHistoryRepository;
import com.example.LuckyWheel.feature.rewards.entity.Reward;
import com.example.LuckyWheel.feature.rewards.repository.RewardRepository;
import com.example.LuckyWheel.feature.user.manager.TicketService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RewardHistoryServiceImpl implements RewardHistoryService {
    private final RewardHistoryRepository rewardHistoryRepository;
    private final TicketService ticketService;
    private final WheelDataLoader wheelDataLoader;
    private final WheelSpinLogic wheelSpinLogic;
    private final RewardRepository rewardRepository;

    @Override
    public Page<SpinResultResponse> getRewardHistory(Long wheelId, Pageable pageable) {
        log.info("Get Reward History with pagination: page {}, size {}, wheelId: {}",
                pageable.getPageNumber(), pageable.getPageSize(), wheelId);

        Page<RewardHistory> rewardHistoryPage = rewardHistoryRepository.findByWheelId(wheelId, pageable);

        // Khắc phục N+1: Lấy tất cả reward IDs từ page hiện tại
        List<Long> rewardIds = rewardHistoryPage.getContent().stream()
                .map(RewardHistory::getRewardId)
                .distinct()
                .toList();

        // Fetch tất cả rewards một lần duy nhất
        Map<Long, Reward> rewardMap = rewardRepository.findAllById(rewardIds).stream()
                .collect(Collectors.toMap(Reward::getId, reward -> reward));

        String ticketType = wheelDataLoader.getTicketTypeByWheelId(wheelId);

        return rewardHistoryPage.map(rewardHistory -> {
            Reward reward = rewardMap.get(rewardHistory.getRewardId());
            if (reward == null) {
                throw new RuntimeException("Reward not found: " + rewardHistory.getRewardId());
            }

            return SpinResultResponse.builder()
                    .rewardId(reward.getId())
                    .rewardName(reward.getName())
                    .rewardType(reward.getType())
                    .ticketType(ticketType)
                    .quantity(1)
                    .spinTime(rewardHistory.getSpinTime())
                    .build();
        });
    }

    @Override
    @Transactional
    public List<SpinResultResponse> saveRewardHistory(Long userId, Long wheelId, int quantity) {
        if (!wheelDataLoader.isActiveWheel(wheelId)) {
            throw new RuntimeException("Wheel is not active");
        }

        String ticketType = wheelDataLoader.getTicketTypeByWheelId(wheelId);

        if (!ticketService.hasEnoughTickets(userId, ticketType, quantity)) {
            throw new RuntimeException("Not enough tickets. Required: " + quantity);
        }

        List<GiftRandomDTO> giftRandomDTOList = wheelDataLoader.getRewardsForWheel(wheelId);
        List<Long> winningRewardIds = wheelSpinLogic.calculateWinningRewardId(giftRandomDTOList, quantity);

        LocalDateTime spinTime = LocalDateTime.now();

        // Nhóm và đếm số lượng từng loại reward
        Map<Long, Long> rewardCountMap = winningRewardIds.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        // Batch insert: Tạo tất cả RewardHistory trước rồi saveAll một lần
        List<RewardHistory> histories = new ArrayList<>();
        rewardCountMap.forEach((rewardId, count) -> {
            for (int i = 0; i < count; i++) {
                RewardHistory history = RewardHistory.builder()
                        .userId(userId)
                        .wheelId(wheelId)
                        .rewardId(rewardId)
                        .spinTime(spinTime)
                        .build();
                histories.add(history);
            }
        });
        rewardHistoryRepository.saveAll(histories);

        // Fetch tất cả rewards một lần thay vì từng cái
        Map<Long, Reward> rewardMap = rewardRepository.findAllById(rewardCountMap.keySet()).stream()
                .collect(Collectors.toMap(Reward::getId, reward -> reward));

        // Build response
        List<SpinResultResponse> spinResults = new ArrayList<>();
        rewardCountMap.forEach((rewardId, count) -> {
            Reward reward = rewardMap.get(rewardId);
            if (reward == null) {
                throw new RuntimeException("Reward not found: " + rewardId);
            }

            SpinResultResponse spinResult = SpinResultResponse.builder()
                    .rewardId(rewardId)
                    .rewardName(reward.getName())
                    .rewardType(reward.getType())
                    .ticketType(ticketType)
                    .quantity(count.intValue())
                    .spinTime(spinTime)
                    .build();

            spinResults.add(spinResult);
        });

        log.info("User {} spun wheel {} {} times and won {} different rewards",
                userId, wheelId, quantity, spinResults.size());

        return spinResults;
    }


}
