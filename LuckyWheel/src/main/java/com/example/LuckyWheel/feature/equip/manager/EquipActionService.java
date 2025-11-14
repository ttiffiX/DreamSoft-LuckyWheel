package com.example.LuckyWheel.feature.equip.manager;

import com.example.LuckyWheel.feature.equip.entity.Equip;

public interface EquipActionService {
    // Upgrade equipment
    Equip upgradeEquipment(String userId, String equipId);

}
