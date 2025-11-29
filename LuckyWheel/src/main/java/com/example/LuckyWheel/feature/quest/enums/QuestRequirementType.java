package com.example.LuckyWheel.feature.quest.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum QuestRequirementType {
    SPIN_WHEEL( "Spin Wheel", 1L),
    UPGRADE_EQUIP("Upgrade Equipment", 2L);
//    KILL_MONSTER("Kill Monster"),
//    COLLECT_ITEM("Collect Item");

    private final String name;
    private final Long value;

}

