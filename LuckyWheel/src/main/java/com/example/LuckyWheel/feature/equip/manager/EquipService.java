package com.example.LuckyWheel.feature.equip.manager;


import com.example.LuckyWheel.controller.response.EquipResponse;
import com.example.LuckyWheel.feature.equip.entity.Equip;

import java.util.List;

public interface EquipService {
    EquipResponse getEquipById(String equipId);
    List<EquipResponse> getEquipByUserId(String userId);


    List<Equip> getEquipByUserIdAndState(String userId, int state);

    Equip addEquipToUser(String userId, Long equipInfoId);
    void removeEquip(String userId, String equipId);

    Equip changeStateEquip(String userId, String equipId, int state);
}
