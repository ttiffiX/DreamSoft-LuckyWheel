package com.example.LuckyWheel.feature.quest.listener;

import com.example.LuckyWheel.feature.quest.enums.QuestRequirementType;
import com.example.LuckyWheel.feature.quest.event.ChallengeMonsterEvent;
import com.example.LuckyWheel.feature.quest.event.EquipUpgradeEvent;
import com.example.LuckyWheel.feature.quest.event.WheelSpinEvent;
import com.example.LuckyWheel.feature.quest.manager.QuestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Listener để lắng nghe các event và cập nhật quest progress
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class QuestEventListener {
    private final QuestService questService;

    /**
     * Lắng nghe event khi user quay vòng quay
     */
    @EventListener
    @Async
    public void onWheelSpin(WheelSpinEvent event) {
        log.info("Received WheelSpinEvent for user {} on wheel {}", event.getUserId(), event.getWheelId());

        try {
            questService.updateProgress(
                    event.getUserId(),
                    QuestRequirementType.SPIN_WHEEL,
                    event.getWheelId(),
                    event.getSpinCount()
            );
        } catch (Exception e) {
            log.error("Error updating quest progress for wheel spin", e);
        }
    }

    /**
     * Lắng nghe event khi user nâng cấp trang bị
     */
    @EventListener
    @Async
    public void onEquipUpgrade(EquipUpgradeEvent event) {
        log.info("Received EquipUpgradeEvent for user {} on equip {}", event.getUserId(), event.getEquipInfoId());

        try {
            questService.updateProgress(
                    event.getUserId(),
                    QuestRequirementType.UPGRADE_EQUIP,
                    event.getEquipInfoId(),
                    event.getUpgradeCount()
            );
        } catch (Exception e) {
            log.error("Error updating quest progress for equip upgrade", e);
        }
    }

    @EventListener
    @Async
    public void onChallengeMonster(ChallengeMonsterEvent event) {
        log.info("Received ChallengeMonsterEvent for user {} on monster {}", event.getUserId(), event.getMonsterInfoId());

        try {
            questService.updateProgress(
                    event.getUserId(),
                    QuestRequirementType.KILL_MONSTER,
                    event.getMonsterInfoId(),
                    event.getKillCount()
            );
        } catch (Exception e) {
            log.error("Error updating quest progress for challenge monster", e);
        }
    }
}

