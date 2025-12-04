package com.example.LuckyWheel.feature.monster.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * DTO kết quả combat
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CombatResultDTO {
    private Boolean isVictory;
    private Integer turnsTaken;
    private Long damageDealt;
    private Long damageReceived;
    private Map<Long, Integer> guaranteedRewards;  // Resource rewards (gold, etc)
    private List<LootedItemDTO> lootedItems;  // Items rơi
    private String battleLog;  // Log trận đấu
}


