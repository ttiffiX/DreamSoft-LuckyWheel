package com.example.LuckyWheel.feature.monster.logic;

import com.example.LuckyWheel.feature.monster.dto.LootItemDTO;
import com.example.LuckyWheel.feature.monster.dto.LootedItemDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Logic tính toán combat (không phụ thuộc vào service/repository)
 */
public class CombatLogic {

    /**
     * Tính damage với thông tin crit (dùng cho battle log)
     * Formula: max(1, attack - defense/2)
     * Có 15% tỷ lệ crit (gây 1.5x damage)
     * @return [damage, isCrit (1 nếu crit, 0 nếu không)]
     */
    public static long[] calculateDamageWithCrit(long attack, long defense) {
        long baseDamage = attack - (defense / 2);
        baseDamage = Math.max(1, baseDamage);

        // 15% tỷ lệ crit
        Random random = new Random();
        boolean isCrit = random.nextDouble() < 0.15;

        long finalDamage = isCrit ? (long) (baseDamage * 1.5) : baseDamage;

        return new long[]{finalDamage, isCrit ? 1 : 0};
    }

    /**
     * Roll loot items dựa trên dropRate
     *
     * @param items Danh sách items có thể rơi
     * @return Danh sách items đã rơi
     */
    public static List<LootedItemDTO> rollLoot(List<LootItemDTO> items) {
        List<LootedItemDTO> looted = new ArrayList<>();
        Random random = new Random();

        for (LootItemDTO item : items) {
            double roll = random.nextDouble() * 100; // 0-100
            if (roll <= item.getDropRate()) {
                looted.add(LootedItemDTO.builder()
                        .itemInfoId(item.getItemInfoId())
                        .quantity(item.getQuantity())
                        .build());
            }
        }

        return looted;
    }
}

