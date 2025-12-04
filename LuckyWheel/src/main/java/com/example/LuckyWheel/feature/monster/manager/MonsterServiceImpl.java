package com.example.LuckyWheel.feature.monster.manager;

import com.example.LuckyWheel.feature.inventory.manager.InventoryService;
import com.example.LuckyWheel.feature.monster.dto.*;
import com.example.LuckyWheel.feature.monster.logic.CombatLogic;
import com.example.LuckyWheel.feature.monster.logic.MonsterDataLoader;
import com.example.LuckyWheel.feature.monster.logic.MonsterLootTableLoader;
import com.example.LuckyWheel.feature.quest.event.ChallengeMonsterEvent;
import com.example.LuckyWheel.feature.user.entity.User;
import com.example.LuckyWheel.feature.user.enums.ResourceType;
import com.example.LuckyWheel.feature.user.enums.Stats;
import com.example.LuckyWheel.feature.user.logic.UserStatsCalculator;
import com.example.LuckyWheel.feature.user.manager.ResourceService;
import com.example.LuckyWheel.feature.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class MonsterServiceImpl implements MonsterService {

    private final MonsterDataLoader monsterDataLoader;
    private final MonsterLootTableLoader lootTableLoader;
    private final UserRepository userRepository;
    private final UserStatsCalculator userStatsCalculator;
    private final ApplicationEventPublisher eventPublisher;
    private final ResourceService resourceService;
    private final InventoryService inventoryService;

    @Override
    @Transactional
    public CombatResultDTO challengeMonster(String userId, Long monsterId) {
        // 1. Lấy thông tin user và monster
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        MonsterDTO monster = monsterDataLoader.getMonsterById(monsterId);

        // 2. Khởi tạo combat stats (dùng Map)
        Map<Long, Long> userStats = userStatsCalculator.calculateTotalStats(user);
        Map<Long, Long> monsterStats = monster.getBaseStats();

        long userHp = userStats.getOrDefault(Stats.HP.getValue(), 1000L);
        long monsterHp = monsterStats.getOrDefault(Stats.HP.getValue(), 500L);

        // 3. Combat loop
        StringBuilder battleLog = new StringBuilder();
        battleLog.append("=== BATTLE START ===\n");

        battleLog.append("--------------------------------------------------\n");
        battleLog.append(String.format("| %-25s | %-25s |\n", "USER STATUS", "MONSTER STATUS"));
        battleLog.append("--------------------------------------------------\n");

        battleLog.append(String.format("| %-25s | %-25s |\n",
                "Tên: " + user.getUsername(),
                "Tên: " + monster.getName()));

// 🚨 BẮT ĐẦU CHÈN 5 CHỈ SỐ Ở ĐÂY 🚨

// 1. HP (Health Points)
        battleLog.append(String.format("| HP: %-21d | HP: %-21d |\n",
                userHp, monsterHp));

// 2. MP (Mana/Magic Points)
        battleLog.append(String.format("| MP: %-21d | MP: %-21d |\n",
                userStats.get(Stats.MP.getValue()), monsterStats.get(Stats.MP.getValue())));

// 3. ATK (Attack - Sức Tấn công)
        battleLog.append(String.format("| ATK: %-20d | ATK: %-20d |\n",
                userStats.get(Stats.ATTACK.getValue()), monsterStats.get(Stats.ATTACK.getValue())));

// 4. DEF (Defense - Sức Phòng thủ)
        battleLog.append(String.format("| DEF: %-20d | DEF: %-20d |\n",
                userStats.get(Stats.DEFENSE.getValue()), monsterStats.get(Stats.DEFENSE.getValue())));

// 5. SPD (Speed - Tốc độ/Nhanh nhẹn)
        battleLog.append(String.format("| SPD: %-20d | SPD: %-20d |\n",
                userStats.get(Stats.SPEED.getValue()), monsterStats.get(Stats.SPEED.getValue())));

// 🚨 KẾT THÚC CHÈN 5 CHỈ SỐ 🚨

        battleLog.append("--------------------------------------------------\n\n");

        int turn = 0;
        long totalDamageDealt = 0;
        long totalDamageReceived = 0;

        // Xác định ai đánh trước (dựa vào speed)
        long userSpeed = userStats.getOrDefault(Stats.SPEED.getValue(), 10L);
        long monsterSpeed = monsterStats.getOrDefault(Stats.SPEED.getValue(), 5L);
        boolean userFirst = userSpeed >= monsterSpeed;

        while (userHp > 0 && monsterHp > 0) {
            turn++;
            battleLog.append(String.format("--- Turn %d ---\n", turn));

            if (userFirst) {
                // User đánh trước
                long[] damageResult = CombatLogic.calculateDamageWithCrit(
                        userStats.getOrDefault(Stats.ATTACK.getValue(), 100L),
                        monsterStats.getOrDefault(Stats.DEFENSE.getValue(), 20L)
                );
                long damage = damageResult[0];
                boolean isCrit = damageResult[1] == 1;

                monsterHp -= damage;
                totalDamageDealt += damage;

                String critText = isCrit ? " [CRITICAL HIT!]" : "";
                battleLog.append(String.format("User deals %d damage%s! Monster HP: %d\n",
                        damage, critText, Math.max(0, monsterHp)));

                if (monsterHp <= 0) break;

                // Monster đánh lại
                damageResult = CombatLogic.calculateDamageWithCrit(
                        monsterStats.getOrDefault(Stats.ATTACK.getValue(), 50L),
                        userStats.getOrDefault(Stats.DEFENSE.getValue(), 50L)
                );
                damage = damageResult[0];
                isCrit = damageResult[1] == 1;

                userHp -= damage;
                totalDamageReceived += damage;

                critText = isCrit ? " [CRITICAL HIT!]" : "";
                battleLog.append(String.format("Monster deals %d damage%s! User HP: %d\n",
                        damage, critText, Math.max(0, userHp)));
            } else {
                // Monster đánh trước
                long[] damageResult = CombatLogic.calculateDamageWithCrit(
                        monsterStats.getOrDefault(Stats.ATTACK.getValue(), 50L),
                        userStats.getOrDefault(Stats.DEFENSE.getValue(), 50L)
                );
                long damage = damageResult[0];
                boolean isCrit = damageResult[1] == 1;

                userHp -= damage;
                totalDamageReceived += damage;

                String critText = isCrit ? " [CRITICAL HIT!]" : "";
                battleLog.append(String.format("Monster deals %d damage%s! User HP: %d\n",
                        damage, critText, Math.max(0, userHp)));

                if (userHp <= 0) break;

                // User đánh lại
                damageResult = CombatLogic.calculateDamageWithCrit(
                        userStats.getOrDefault(Stats.ATTACK.getValue(), 100L),
                        monsterStats.getOrDefault(Stats.DEFENSE.getValue(), 20L)
                );
                damage = damageResult[0];
                isCrit = damageResult[1] == 1;

                monsterHp -= damage;
                totalDamageDealt += damage;

                critText = isCrit ? " [CRITICAL HIT!]" : "";
                battleLog.append(String.format("User deals %d damage%s! Monster HP: %d\n",
                        damage, critText, Math.max(0, monsterHp)));
            }

            battleLog.append("\n");

            // Giới hạn số turn tối đa để tránh vòng lặp vô hạn
            if (turn > 100) {
                battleLog.append("Battle timeout! Draw.\n");
                break;
            }
        }

        // 4. Xác định kết quả
        boolean isVictory = userHp > 0 && monsterHp <= 0;

        battleLog.append("=== BATTLE END ===\n");
        battleLog.append(String.format("Result: %s\n", isVictory ? "VICTORY!" : "DEFEAT!"));

        // 5. Tính loot nếu thắng
        Map<Long, Integer> guaranteedRewards = new HashMap<>();
        List<LootedItemDTO> lootedItems = new ArrayList<>();

        if (isVictory) {
            MonsterLootTableDTO lootTable = lootTableLoader.getLootTableByMonsterId(monsterId);
            if (lootTable != null) {
                // Lấy guaranteed rewards và looted items
                guaranteedRewards.putAll(lootTable.getGuaranteed());
                lootedItems = CombatLogic.rollLoot(lootTable.getItems());

                // Gọi qua proxy để @Transactional hoạt động
                saveRewardsToDatabase(user, guaranteedRewards, lootedItems);
            }

            // Publish event cho quest system
            eventPublisher.publishEvent(
                    new ChallengeMonsterEvent(this, userId, monsterId, 1)
            );
        }

        // 6. Tạo kết quả
        return CombatResultDTO.builder()
                .isVictory(isVictory)
                .turnsTaken(turn)
                .damageDealt(totalDamageDealt)
                .damageReceived(totalDamageReceived)
                .guaranteedRewards(guaranteedRewards)
                .lootedItems(lootedItems)
                .battleLog(battleLog.toString())
                .build();
    }

    /**
     * Lưu rewards vào database (Resources + Inventory)
     * Transactional đảm bảo nếu có lỗi thì rollback toàn bộ
     */
    @Override
    @Transactional
    public void saveRewardsToDatabase(User user, Map<Long, Integer> guaranteedRewards, List<LootedItemDTO> lootedItems) {
        // 1. Lưu guaranteed rewards vào Resources (Gold, Diamond, etc)
        for (Map.Entry<Long, Integer> entry : guaranteedRewards.entrySet()) {
            Long resourceTypeValue = entry.getKey();
            Integer amount = entry.getValue();

            ResourceType resourceType = ResourceType.fromValue(resourceTypeValue);
            resourceService.addResource(user.getUsername(), resourceType, amount);

            log.info("Added {} {} to user {}", amount, resourceType.getName(), user.getId());
        }

        // 2. Lưu looted items vào Inventory
        for (LootedItemDTO lootedItem : lootedItems) {
            inventoryService.addItem(user.getId(), lootedItem.getItemInfoId(), lootedItem.getQuantity());

            log.info("Added item {} x{} to user {} inventory",
                    lootedItem.getItemInfoId(), lootedItem.getQuantity(), user.getId());
        }

        log.info("Successfully saved all rewards for user {}", user.getId());
    }
}
