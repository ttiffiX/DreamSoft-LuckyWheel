package com.example.LuckyWheel.feature.monster.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * DTO cho Monster từ monsters.json
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonsterDTO {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("monsterType")
    private Long monsterType;  // 0: Normal, 1: Elite, 2: Boss

    @JsonProperty("level")
    private Integer level;

    @JsonProperty("baseStats")
    private Map<Long, Long> baseStats;
}

