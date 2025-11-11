package com.example.LuckyWheel.feature.user.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

@Getter
@RequiredArgsConstructor
public enum ResourceType {
    NONE("None", 0L),
    GOLD("Gold", 1L),
    DIAMOND("Diamond", 2L),
    NORMAL("Normal Ticket", 3L),
    PREMIUM("Premium Ticket", 4L);

    private final String name;
    private final Long value;

    public static ResourceType fromValue(Long value) {
        for (ResourceType type : ResourceType.values()) {
            if (Objects.equals(type.value, value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid ResourceType value: " + value);
    }
}

