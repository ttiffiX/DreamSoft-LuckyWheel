package com.example.LuckyWheel.feature.equip.manager;


import com.example.LuckyWheel.feature.equip.entity.Equip;

import java.util.List;
import java.util.Map;

public interface EquipService {
    Equip getEquipById(String equipId);
    List<Equip> getEquipByUserId(String userId);

    List<Equip> getEquipByUserIdAndState(String userId, int state);

    Equip addEquipToUser(String userId, Long equipInfoId);
    void removeEquip(String userId, String equipId);

    Equip equipItemToUser(String userId, String equipId);
    Equip unequipItemFromUser(String userId, String equipId);
}
