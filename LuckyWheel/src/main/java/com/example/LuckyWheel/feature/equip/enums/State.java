package com.example.LuckyWheel.feature.equip.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum State {
    IN_INVENTORY("In Inventory", 0),
    IN_USE("In Use", 1);

    private final String name;
    private final Integer value;
}
