package com.example.LuckyWheel.feature.user.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ResourceType {
    NONE("None", 0),
    GOLD("Gold", 1),
    DIAMOND("Diamond", 2),
    NORMAL("Normal Ticket", 3),
    PREMIUM("Premium Ticket", 4);

    private final String name;
    private final int value;

    public static ResourceType fromValue(int value) {
        for (ResourceType type : ResourceType.values()) {
            if (type.value == value) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid ResourceType value: " + value);
    }
}

