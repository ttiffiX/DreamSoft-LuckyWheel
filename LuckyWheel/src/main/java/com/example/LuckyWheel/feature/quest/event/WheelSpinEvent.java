package com.example.LuckyWheel.feature.quest.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Event được publish khi user quay vòng quay
 */
@Getter
public class WheelSpinEvent extends ApplicationEvent {
    private final String userId;
    private final Long wheelId;
    private final Integer spinCount;

    public WheelSpinEvent(Object source, String userId, Long wheelId, Integer spinCount) {
        super(source);
        this.userId = userId;
        this.wheelId = wheelId;
        this.spinCount = spinCount;
    }
}


