package com.example.LuckyWheel.feature.quest.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Event được publish khi user nâng cấp trang bị
 */
@Getter
public class EquipUpgradeEvent extends ApplicationEvent {
    private final String userId;
    private final Long equipInfoId;
    private final Integer upgradeCount;

    public EquipUpgradeEvent(Object source, String userId, Long equipInfoId, Integer upgradeCount) {
        super(source);
        this.userId = userId;
        this.equipInfoId = equipInfoId;
        this.upgradeCount = upgradeCount;
    }
}

