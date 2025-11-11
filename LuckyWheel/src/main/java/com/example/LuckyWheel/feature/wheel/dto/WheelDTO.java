package com.example.LuckyWheel.feature.wheel.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class WheelDTO {
    @JsonProperty("wheelId")
    private Long wheelId;

    @JsonProperty("name")
    private String name;

    @JsonProperty("isActive")
    private boolean active;

    @JsonProperty("resourceRequire")
    private Long resourceRequire;

    @JsonProperty("listGiftRandom")
    private List<GiftRandomDTO> listGiftRandom;
}
