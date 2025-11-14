package com.example.LuckyWheel.feature.equip.manager;

import com.example.LuckyWheel.feature.equip.dto.EquipDTO;
import com.example.LuckyWheel.feature.equip.dto.UpgradeConfigDTO;
import com.example.LuckyWheel.feature.equip.entity.Equip;
import com.example.LuckyWheel.feature.equip.logic.EquipItemDataLoader;
import com.example.LuckyWheel.feature.equip.logic.UpgradeConfigLoader;
import com.example.LuckyWheel.feature.equip.logic.UpgradeLogic;
import com.example.LuckyWheel.feature.equip.repository.EquipRepository;
import com.example.LuckyWheel.feature.user.entity.User;
import com.example.LuckyWheel.feature.user.enums.ResourceType;
import com.example.LuckyWheel.feature.user.manager.ResourceService;
import com.example.LuckyWheel.feature.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class EquipActionServiceImpl implements EquipActionService {
    private final EquipRepository equipRepository;
    private final UserRepository userRepository;
    private final ResourceService resourceService;
    private final UpgradeConfigLoader upgradeConfigLoader;
    private final EquipItemDataLoader equipItemDataLoader;
    private final UpgradeLogic upgradeLogic;

    @Override
    public Equip upgradeEquipment(String userId, String equipId) {
        // Verify user exists
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        // Get equipment
        Equip equip = equipRepository.findById(equipId)
                .orElseThrow(() -> new RuntimeException("Equip not found with id: " + equipId));

        // Verify equipment belongs to user
        if (!equip.getUserId().equals(userId)) {
            throw new RuntimeException("Equipment does not belong to user: " + userId);
        }

        Integer currentLevel = equip.getLevel();
        Integer targetLevel = currentLevel + 1;

        UpgradeConfigDTO upgradeConfigDTO = upgradeConfigLoader.getUpgradeConfigForLevel(targetLevel);
        EquipDTO equipDTO = equipItemDataLoader.getEquipByInfoId(equip.getInfoId());

        if (targetLevel > equipDTO.getMaxLevel()) {
            throw new RuntimeException("Equipment has reached max level: " + equipDTO.getMaxLevel());
        }

        // Check if user has enough gold
        if (!resourceService.hasEnoughResource(user.getUsername(), ResourceType.GOLD, upgradeConfigDTO.getGoldRequired().intValue())) {
            throw new RuntimeException("Not enough gold. Required: " + upgradeConfigDTO.getGoldRequired());
        }

        // Deduct gold
        resourceService.removeResource(user.getUsername(), ResourceType.GOLD, upgradeConfigDTO.getGoldRequired().intValue());

        // Attempt upgrade
        boolean success = upgradeLogic.rollUpgradeSuccess(upgradeConfigDTO.getRate());


        // Save updated equipment
        if (success) {
            equip.setLevel(targetLevel);
            equipRepository.save(equip);
            log.info("Equipment has been upgraded successfully.");
        } else {
            equip.setLevel(upgradeConfigDTO.getLoseLevel());
            equipRepository.save(equip);
            log.info("Equipment has been upgraded failed.");
        }

        return equip;
    }
}
