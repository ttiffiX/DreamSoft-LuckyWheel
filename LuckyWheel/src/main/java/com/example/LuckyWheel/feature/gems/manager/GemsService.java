package com.example.LuckyWheel.feature.gems.manager;

import com.example.LuckyWheel.feature.gems.entity.Gems;

import java.util.List;

public interface GemsService {

    Gems addGem(String userId, Long gemId);

    Gems removeGem(String userId, String gemInstanceId);

    Gems socketGem(String userId, String gemInstanceId, boolean action);

    List<Gems> getUserGems(String userId);

    List<Gems> getAvailableGems(String userId);

}
