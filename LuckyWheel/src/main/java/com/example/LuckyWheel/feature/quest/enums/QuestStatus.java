package com.example.LuckyWheel.feature.quest.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum QuestStatus {
    ACTIVE(0L),
    COMPLETED(1L),
    CLAIMED(2L);

    private final Long value;
}
