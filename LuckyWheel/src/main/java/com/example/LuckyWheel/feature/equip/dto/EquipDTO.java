package com.example.LuckyWheel.feature.equip.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EquipDTO {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("type")
    private Long type;

    @JsonProperty("maxStar")
    private String maxStar;

    @JsonProperty("maxLevel")
    private String maxLevel;

    @JsonProperty("infoBuff")
    private String infoBuff;
}
