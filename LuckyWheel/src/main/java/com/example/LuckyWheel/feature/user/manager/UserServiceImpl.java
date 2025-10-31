package com.example.LuckyWheel.feature.user.manager;

import com.example.LuckyWheel.controller.response.SpinResultResponse;
import com.example.LuckyWheel.feature.user.entity.User;
import com.example.LuckyWheel.feature.user.enums.ResourceType;
import com.example.LuckyWheel.feature.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<User> getAllUser() {
        return userRepository.findAll();
    }

    @Override
    public User getUserByName(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
    }

    @Override
    @Transactional
    public void createUser(User user) {

        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("User with username: " + user.getUsername() + " already exists");
        }

        log.info("Creating user with username: {}", user.getUsername());

        Map<ResourceType, Integer> initialResources = createInitialResources();

        User newUser = User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .resources(initialResources)
                .build();

        userRepository.save(newUser);
    }

    @Override
    @Transactional
    public void updateUser(User user) {
        User existingUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + user.getId()));

        log.info("Updating user with id: {}", user.getId());

        existingUser.setUsername(user.getUsername());
        existingUser.setPassword(user.getPassword());

        userRepository.save(existingUser);
    }

    // ==================== RESOURCE MANAGEMENT ====================

    @Override
    @Transactional
    public void addResource(String username, ResourceType resourceType, int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        log.info("Adding {} {} to user {}", amount, resourceType, username);

        User user = getUserByName(username);
        int currentAmount = user.getResources().getOrDefault(resourceType, 0);
        user.getResources().put(resourceType, currentAmount + amount);

        userRepository.save(user);
    }

    @Override
    @Transactional
    public void addResource(String username, List<SpinResultResponse> spinRewards) {
        log.info("Adding resources to user {}", username);

        User user = getUserByName(username);
        Map<ResourceType, Integer> userResources = user.getResources();

        spinRewards.forEach(spinReward -> {
            userResources.merge(
                    spinReward.getRewardType(),
                    spinReward.getQuantity(),
                    Integer::sum
            );
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
        User user = getUserByName(username);
        int currentAmount = user.getResources().getOrDefault(resourceType, 0);

        if (currentAmount < amount) {
            throw new RuntimeException(
                    String.format("Not enough %s. Has: %d, Need: %d", resourceType, currentAmount, amount)
            );
        }

        user.getResources().put(resourceType, currentAmount - amount);
        userRepository.save(user);
    }

    @Override
    public int getResourceAmount(String username, ResourceType resourceType) {
        User user = getUserByName(username);
        return user.getResources().getOrDefault(resourceType, 0);
    }

    @Override
    public boolean hasEnoughResource(String username, ResourceType resourceType, int amount) {
        int currentAmount = getResourceAmount(username, resourceType);
        return currentAmount >= amount;
    }

    @Override
    public Map<ResourceType, Integer> createInitialResources() {

        // Số lượng vàng ban đầu
        final int INITIAL_GOLD_AMOUNT = 10_000_000;

        return Arrays.stream(ResourceType.values())
                .filter(type -> !type.equals(ResourceType.NONE)) // Loại bỏ NONE
                .collect(Collectors.toMap(
                        Function.identity(), // Key: Đối tượng ResourceType

                        // 🔥 Value: Dùng Ternary Operator để kiểm tra loại Vàng
                        type -> type.equals(ResourceType.GOLD) ? INITIAL_GOLD_AMOUNT : 0
                ));
    }
}
