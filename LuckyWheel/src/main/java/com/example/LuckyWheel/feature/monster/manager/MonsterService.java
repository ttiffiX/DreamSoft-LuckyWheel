package com.example.LuckyWheel.feature.monster.manager;

import com.example.LuckyWheel.feature.monster.dto.CombatResultDTO;
import com.example.LuckyWheel.feature.monster.dto.LootedItemDTO;
import com.example.LuckyWheel.feature.user.entity.User;

import java.util.List;
import java.util.Map;

public interface MonsterService {
    CombatResultDTO challengeMonster(String userId, Long monsterId);

    void saveRewardsToDatabase(User user, Map<Long, Integer> guaranteedRewards, List<LootedItemDTO> lootedItems);
}

