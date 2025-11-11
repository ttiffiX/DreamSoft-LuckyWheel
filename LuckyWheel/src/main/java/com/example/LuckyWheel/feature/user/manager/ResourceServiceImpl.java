package com.example.LuckyWheel.feature.user.manager;

import com.example.LuckyWheel.controller.response.SpinResultResponse;
import com.example.LuckyWheel.feature.user.entity.User;
import com.example.LuckyWheel.feature.user.enums.ResourceType;
import com.example.LuckyWheel.feature.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@AllArgsConstructor
@Slf4j
public class ResourceServiceImpl implements ResourceService {
    private final UserService userService;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void addResource(String username, ResourceType resourceType, int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        log.info("Adding {} {} to user {}", amount, resourceType, username);

        User user = userService.getUserByName(username);
        int currentAmount = user.getResources().getOrDefault(resourceType.getValue(), 0);
        user.getResources().put(resourceType.getValue(), currentAmount + amount);

        userRepository.save(user);
    }

    @Override
    @Transactional
    public void addResource(String username, List<SpinResultResponse> spinRewards) {
        log.info("Adding resources to user {}", username);

        User user = userService.getUserByName(username);
        Map<Long, Integer> userResources = user.getResources();

        spinRewards.forEach(spinReward -> userResources.merge(
                spinReward.getRewardType().getValue(),
                spinReward.getQuantity(),
                Integer::sum
        ));

        userRepository.save(user);
    }

    @Override
    @Transactional
    public void updateResource(User user) {
        User existingUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + user.getId()));
        existingUser.setResources(user.getResources());
        userRepository.save(existingUser);
    }

    @Override
    @Transactional
    public void updateMilestone(String username, Long wheelId, Long milestoneId) {
        log.info("Update milestone claimed to user {}", username);

        User user = userService.getUserByName(username);
        Map<Long, Set<Long>> userMilestoneRewardsClaimed = user.getMilestoneRewardsClaimed();
        userMilestoneRewardsClaimed.merge(wheelId, Set.of(milestoneId), (oldSet, newSet) -> {
            oldSet.addAll(newSet);
            return oldSet;
        });
        userRepository.save(user);
    }


    @Override
    @Transactional
    public void removeResource(String username, ResourceType resourceType, int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        log.info("Removing {} {} from user {}", amount, resourceType, username);
        User user = userService.getUserByName(username);
        int currentAmount = user.getResources().getOrDefault(resourceType.getValue(), 0);

        if (currentAmount < amount) {
            throw new RuntimeException(
                    String.format("Not enough %s. Has: %d, Need: %d", resourceType, currentAmount, amount)
            );
        }

        user.getResources().put(resourceType.getValue(), currentAmount - amount);
        userRepository.save(user);
    }

    @Override
    public int getResourceAmount(String username, ResourceType resourceType) {
        User user = userService.getUserByName(username);
        return user.getResources().getOrDefault(resourceType.getValue(), 0);
    }

    @Override
    public boolean hasEnoughResource(String username, ResourceType resourceType, int amount) {
        int currentAmount = getResourceAmount(username, resourceType);
        return currentAmount >= amount;
    }

    @Override
    @Transactional
    public void incrementSpinCount(String userId, Long wheelId, int count) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        Integer currentCount = user.getWheelSpinCounts().getOrDefault(wheelId, 0);
        user.getWheelSpinCounts().put(wheelId, currentCount + count);

        userRepository.save(user);
        log.info("Updated spin count for wheel {}: {} -> {}", wheelId, currentCount, currentCount + count);
    }
}
