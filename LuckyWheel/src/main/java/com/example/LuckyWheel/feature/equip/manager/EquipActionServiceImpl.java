package com.example.LuckyWheel.feature.equip.manager;

import com.example.LuckyWheel.feature.equip.dto.EquipDTO;
import com.example.LuckyWheel.feature.equip.dto.StarUpgradeDTO;
import com.example.LuckyWheel.feature.equip.dto.UpgradeConfigDTO;
import com.example.LuckyWheel.feature.equip.entity.Equip;
import com.example.LuckyWheel.feature.equip.logic.EquipItemDataLoader;
import com.example.LuckyWheel.feature.equip.logic.StarUpgradeDataLoader;
import com.example.LuckyWheel.feature.equip.logic.UpgradeConfigLoader;
import com.example.LuckyWheel.feature.equip.logic.UpgradeLogic;
import com.example.LuckyWheel.feature.equip.repository.EquipRepository;
import com.example.LuckyWheel.feature.gems.entity.Gems;
import com.example.LuckyWheel.feature.gems.repository.GemsRepository;
import com.example.LuckyWheel.feature.user.entity.User;
import com.example.LuckyWheel.feature.user.enums.ResourceType;
import com.example.LuckyWheel.feature.user.manager.ResourceService;
import com.example.LuckyWheel.feature.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final StarUpgradeDataLoader starUpgradeDataLoader;
    private final GemsRepository gemsRepository;

    @Override
    public Equip upgradeEquipment(String userId, String equipId) {
        // Verify user exists
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        // Get equipment
        Equip equip = equipRepository.findById(equipId).orElseThrow(() -> new RuntimeException("Equip not found with id: " + equipId));

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

    @Override
    @Transactional
    public Equip upgradeStarEquip(String userId, String equipId) {
        log.info("Upgrading star for equip {} by user {}", equipId, userId);

        // Tìm trang bị
        Equip equip = equipRepository.findById(equipId).orElseThrow(() -> new RuntimeException("Equipment not found"));

        // Kiểm tra ownership
        if (!equip.getUserId().equals(userId)) {
            throw new RuntimeException("Equipment does not belong to user");
        }

        // Lấy user để check resources
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        Integer currentStar = equip.getStar();
        Integer nextStar = currentStar + 1;

        // Lấy thông tin nâng cấp
        StarUpgradeDTO upgradeInfo = starUpgradeDataLoader.getStarUpgradeInfo(nextStar);
        if (upgradeInfo == null) {
            throw new RuntimeException("Cannot upgrade further. Already at max star level.");
        }

        // Kiểm tra level requirement
        if (equip.getLevel() < upgradeInfo.getRequiredEquipLevel()) {
            throw new RuntimeException(String.format("Equipment level %d is too low. Required level: %d", equip.getLevel(), upgradeInfo.getRequiredEquipLevel()));
        }

        // Kiểm tra tài nguyên
        ResourceType costType = ResourceType.fromValue(upgradeInfo.getCostType());
        if (!resourceService.hasEnoughResource(user.getUsername(), costType, upgradeInfo.getCostAmount())) {
            throw new RuntimeException(String.format("Not enough %s. Required: %d", costType, upgradeInfo.getCostAmount()));
        }

        // Tính tỷ lệ thành công
        boolean success = upgradeLogic.rollUpgradeSuccess(upgradeInfo.getRate());

        // Trừ tài nguyên
        resourceService.removeResource(user.getUsername(), costType, upgradeInfo.getCostAmount());

        if (success) {
            equip.setStar(nextStar);
            equipRepository.save(equip);
            log.info("Star upgrade SUCCESS for equip {}. New star: {}", equipId, nextStar);
        } else {
            log.info("Star upgrade FAILED for equip {}. Star remains: {}", equipId, currentStar);
        }

        return equip;
    }

    @Override
    @Transactional
    public Equip changeStateGemToEquip(String userId, String equipId, String gemInstanceId, boolean action) {
        log.info("equip/unequip gem {} to equip {} by user {}", gemInstanceId, equipId, userId);

        // Tìm trang bị
        Equip equip = equipRepository.findById(equipId).orElseThrow(() -> new RuntimeException("Equipment not found"));

        // Kiểm tra ownership
        if (!equip.getUserId().equals(userId)) {
            throw new RuntimeException("Equipment does not belong to user");
        }

        // Tìm gem
        Gems gem = gemsRepository.findByIdAndUserId(gemInstanceId, userId).orElseThrow(() -> new RuntimeException("Gem not found or does not belong to user"));

        // Kiểm tra gem đã được khảm chưa
        if (gem.getIsSocketed() == action) {
            throw new RuntimeException("Gem is already in the desired state: " + (action ? "SOCKETED" : "UNSOCKETED"));
        }

        if (equip.getListGemIds().size() >= equip.getStar() && action) {
            throw new RuntimeException("Equipment has reached maximum gem slots");
        }

        // Khảm gem
        gem.setIsSocketed(action);

        // Thêm/giảm gemId vào list
        if (action) {
            equip.getListGemIds().add(gem.getGemId());
        } else
            equip.getListGemIds().remove(gem.getGemId());

        gemsRepository.save(gem);
        return equipRepository.save(equip);
    }

//    @Override
//    @Transactional
//    public Equip unsocketGemFromEquip(String userId, String equipId, String gemInstanceId) {
//        log.info("Unsocketing gem {} from equip {} by user {}", gemInstanceId, equipId, userId);
//
//        // Tìm trang bị
//        Equip equip = equipRepository.findById(equipId).orElseThrow(() -> new RuntimeException("Equipment not found"));
//
//        // Kiểm tra ownership
//        if (!equip.getUserId().equals(userId)) {
//            throw new RuntimeException("Equipment does not belong to user");
//        }
//
//        // Tìm gem
//        Gems gem = gemsRepository.findByIdAndUserId(gemInstanceId, userId).orElseThrow(() -> new RuntimeException("Gem not found or does not belong to user"));
//
//        // Kiểm tra gem có đang được khảm không
//        if (!gem.getIsSocketed()) {
//            throw new RuntimeException("Gem is not socketed");
//        }
//
//        // Kiểm tra gem có trong trang bị này không
//        if (!equip.getListGemIds().contains(gem.getGemId())) {
//            throw new RuntimeException("Gem is not socketed in this equipment");
//        }
//
//        // Tháo gem
//        gem.setIsSocketed(false);
//        gemsRepository.save(gem);
//
//        // Xóa gemId khỏi list (chỉ xóa 1 lần xuất hiện đầu tiên)
//        equip.getListGemIds().remove(gem.getGemId());
//        Equip savedEquip = equipRepository.save(equip);
//
//        log.info("Successfully unsocketed gem {} from equip {}. Remaining gems: {}/{}", gemInstanceId, equipId, equip.getListGemIds().size(), equip.getStar());
//
//        return savedEquip;
//    }

}
