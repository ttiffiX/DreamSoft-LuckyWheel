package com.example.LuckyWheel.feature.equip.manager;

import com.example.LuckyWheel.feature.equip.entity.Equip;

public interface EquipActionService {
    // Upgrade equipment
    Equip upgradeEquipment(String userId, String equipId);

    Equip upgradeStarEquip(String userId, String equipId);

    Equip changeStateGemToEquip(String userId, String equipId, String gemInstanceId, boolean action);

//    Equip unsocketGemFromEquip(String userId, String equipId, String gemInstanceId);
}
