package com.example.LuckyWheel.feature.user.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Stats {
    HP("Health Points", 1L),
    MP("Mana Points", 2L),
    ATTACK("Attack Power", 3L),
    DEFENSE("Defense Power", 4L),
    SPEED("Speed", 5L);

    private final String name;
    private final Long value;
}
