package com.example.LuckyWheel.feature.user.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TicketType {
    NORMAL("Normal"),
    PREMIUM("Premium");

    private final String value;

    public static TicketType fromValue(String value) {
        for (TicketType type : TicketType.values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid TicketType value: " + value);
    }


}
