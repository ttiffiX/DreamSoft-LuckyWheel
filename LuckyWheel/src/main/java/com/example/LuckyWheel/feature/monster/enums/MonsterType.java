package com.example.LuckyWheel.feature.monster.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MonsterType {
    NORMAL("Normal", 0),
    ELITE("Elite", 1),
    BOSS("Boss", 2);

    private final String name;
    private final Integer value;

    public static MonsterType fromValue(Integer value) {
        for (MonsterType type : MonsterType.values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid MonsterType value: " + value);
    }
}
