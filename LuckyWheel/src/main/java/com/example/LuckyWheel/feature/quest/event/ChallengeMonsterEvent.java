package com.example.LuckyWheel.feature.quest.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Event được publish khi user giet quai
 */
@Getter
public class ChallengeMonsterEvent extends ApplicationEvent {
    private final String userId;
    private final Long monsterInfoId;
    private final Integer killCount;

    public ChallengeMonsterEvent(Object source, String userId, Long monsterInfoId, Integer killCount) {
        super(source);
        this.userId = userId;
        this.monsterInfoId = monsterInfoId;
        this.killCount = killCount;
    }
}

