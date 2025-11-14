package com.example.LuckyWheel.feature.equip.manager;

import com.example.LuckyWheel.controller.response.EquipResponse;
import com.example.LuckyWheel.feature.equip.dto.EquipDTO;
import com.example.LuckyWheel.feature.equip.entity.Equip;
import com.example.LuckyWheel.feature.equip.logic.DataParser;
import com.example.LuckyWheel.feature.equip.logic.EquipItemDataLoader;
import com.example.LuckyWheel.feature.equip.logic.UpgradeLogic;
import com.example.LuckyWheel.feature.equip.repository.EquipRepository;
import com.example.LuckyWheel.feature.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class EquipServiceImpl implements EquipService {

    private final EquipRepository equipRepository;
    private final EquipItemDataLoader equipItemDataLoader;
    private final DataParser dataParser;
    private final UserRepository userRepository;
    private final UpgradeLogic upgradeLogic;

    @Override
    public EquipResponse getEquipById(String equipId) {
        Equip equip = equipRepository.findById(equipId)
                .orElseThrow(() -> new RuntimeException("Equip not found with id: " + equipId));

        return EquipResponse.builder()
                .equip(equip)
                .propsMain(upgradeLogic.calculatePropsMainForLevel(equip))
                .build();
    }

    @Override
    public List<EquipResponse> getEquipByUserId(String userId) {
        List<Equip> equips = equipRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Equip not found with userId: " + userId));

        return equips.stream()
                .map(equip -> EquipResponse.builder()
                        .equip(equip)
                        .propsMain(upgradeLogic.calculatePropsMainForLevel(equip))
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public List<Equip> getEquipByUserIdAndState(String userId, int state) {
        List<Equip> allEquips = equipRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Equip not found with userId: " + userId));

        List<Equip> filteredEquips = new ArrayList<>();
        for (Equip equip : allEquips) {
            if (equip.getState() == state) {
                filteredEquips.add(equip);
            }
        }
        return filteredEquips;
    }

    @Override
    public Equip addEquipToUser(String userId, Long equipInfoId) {
        EquipDTO equipDTO = equipItemDataLoader.getEquipByInfoId(equipInfoId);
        Map<Long, Long> propsMain = new HashMap<>(dataParser.parseInfoBuff(equipDTO.getInfoBuff()));

        userRepository.existsById(userId);

        Equip equip = Equip.builder()
                .userId(userId)
                .infoId(equipInfoId)
                .state(0)
                .level(1)
                .star(0)
                .propsMain(propsMain)
                .listGemIds(new ArrayList<>())
                .build();

        return equipRepository.save(equip);
    }

    @Override
    public void removeEquip(String userId, String equipId) {
        Equip equip = equipRepository.findById(equipId)
                .orElseThrow(() -> new RuntimeException("Equip not found with id: " + equipId));
        userRepository.existsById(userId);
        if (!equip.getUserId().equals(userId)) {
            throw new RuntimeException("Equip does not belong to user: " + userId);
        }

        if (equip.getState() == 1) {
            throw new RuntimeException("Cannot remove equipped item. Please unequip it first.");
        }

        equipRepository.delete(equip);
    }

    @Override
    public Equip equipItemToUser(String userId, String equipId) {
        Equip equip = equipRepository.findById(equipId)
                .orElseThrow(() -> new RuntimeException("Equip not found with id: " + equipId));
        userRepository.existsById(userId);

        if (!equip.getUserId().equals(userId)) {
            throw new RuntimeException("Equip does not belong to user: " + userId);
        }

        if (equip.getState() == 1) {
            throw new RuntimeException("Equip item is already equipped.");
        }
        equip.setState(1);
        return equipRepository.save(equip);
    }

    @Override
    public Equip unequipItemFromUser(String userId, String equipId) {
        Equip equip = equipRepository.findById(equipId)
                .orElseThrow(() -> new RuntimeException("Equip not found with id: " + equipId));
        userRepository.existsById(userId);
        if (!equip.getUserId().equals(userId)) {
            throw new RuntimeException("Equip does not belong to user: " + userId);
        }

        if (equip.getState() == 0) {
            throw new RuntimeException("Equip item is already unequipped.");
        }

        equip.setState(0);
        return equipRepository.save(equip);
    }

}
