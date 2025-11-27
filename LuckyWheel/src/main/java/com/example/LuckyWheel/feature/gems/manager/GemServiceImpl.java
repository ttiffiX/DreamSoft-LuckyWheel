package com.example.LuckyWheel.feature.gems.manager;

import com.example.LuckyWheel.feature.gems.entity.Gems;
import com.example.LuckyWheel.feature.gems.repository.GemsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GemServiceImpl implements GemsService {

    private final GemsRepository gemsRepository;

    @Override
    @Transactional
    public Gems addGem(String userId, Long gemId) {
        Gems gem = Gems.builder()
                .userId(userId)
                .gemId(gemId)
                .isSocketed(false)
                .build();

        Gems savedGem = gemsRepository.save(gem);
        log.info("Gem {} added successfully with instance ID {}", gemId, savedGem.getId());

        return savedGem;
    }

    @Override
    @Transactional
    public Gems removeGem(String userId, String gemInstanceId) {
        Gems gem = gemsRepository.findByIdAndUserId(gemInstanceId, userId)
                .orElseThrow(() -> new RuntimeException("Gem not found or does not belong to user"));

        // Kiểm tra gem có đang được khảm vào trang bị không
        if (gem.getIsSocketed() == true) {
            throw new RuntimeException("Cannot remove gem that is socketed in equipment. Unsocket it first.");
        }

        gemsRepository.delete(gem);
        log.info("Gem instance {} removed successfully", gemInstanceId);
        return gem;
    }

    @Override
    @Transactional
    public Gems socketGem(String userId, String gemInstanceId, boolean action) {
        log.info("Socket operation - User: {}, Gem: {}, Action: {}",
                userId, gemInstanceId, action ? "SOCKET" : "UNSOCKET");

        // Tìm gem
        Gems gem = gemsRepository.findByIdAndUserId(gemInstanceId, userId)
                .orElseThrow(() -> new RuntimeException("Gem not found or does not belong to user"));

        if (gem.getIsSocketed() == action) {
            throw new RuntimeException("Gem is already in the desired state: " + (action ? "SOCKETED" : "UNSOCKETED"));
        }

        gem.setIsSocketed(action);
        return gemsRepository.save(gem);
    }

    @Override
    public List<Gems> getUserGems(String userId) {
        log.info("Getting all gems for user {}", userId);
        return gemsRepository.findByUserId(userId);
    }

    @Override
    public List<Gems> getAvailableGems(String userId) {
        log.info("Getting available (unsocketed) gems for user {}", userId);
        return gemsRepository.findByUserIdAndIsSocketed(userId, false);
    }
}
